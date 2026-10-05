package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.ShapeHolder;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.resource.PropertyDialog;
import com.example.resource.SpawnMode;
import com.example.ui.DialogHit;
import com.example.ui.DialogHit.Target;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Zeichnet den Eigenschaften-Dialog über allem anderen (nur wenn offen). Nur Zeichnen, keine Logik. */
public class DialogRenderSystem implements System {
    private static final Color DIM = new Color(0, 0, 0, 60);
    private static final Color PANEL = new Color(40, 48, 68);
    private static final Color TITLE_BAR = new Color(28, 34, 52);
    private static final Color BORDER = new Color(15, 20, 35);
    private static final Color TEXT = Color.WHITE;
    private static final Color TEXT_DIM = new Color(150, 160, 185);
    private static final Color BUTTON = new Color(60, 72, 102);
    private static final Color BUTTON_HOVER = new Color(90, 108, 152);
    private static final Color BUTTON_DISABLED = new Color(48, 54, 72);
    private static final Color CLOSE_HOVER = new Color(200, 70, 70);
    private static final Color SELECTED_BORDER = Color.WHITE;

    // Unicode-Escapes, damit die Datei unabhängig von der Compiler-Kodierung lesbar bleibt
    private static final String TITLE = "Eigenschaften";
    private static final String LABEL_FORM = "Form:";
    private static final String LABEL_COLOR = "Farbe:";
    private static final String LABEL_SIZE = "Größe:";
    private static final String LABEL_LAYER = "Ebene:";
    private static final String BTN_FORWARD = "Nach vorn";
    private static final String BTN_BACKWARD = "Nach hinten";
    private static final String BTN_APPLY = "Übernehmen";
    private static final String BTN_CANCEL = "Abbrechen";
    private static final String HINT = "E = Übernehmen    ESC = Abbrechen";

    @Override
    public void update(World world, double alpha) {
        PropertyDialog dialog = world.getResource(PropertyDialog.class);
        if (!dialog.isOpen()) {
            return;
        }
        RenderTarget target = world.getResource(RenderTarget.class);
        Graphics2D g2 = (Graphics2D) target.getGraphics();
        DialogHit hover = dialog.getHover();
        Font plain = g2.getFont();
        Font bold = plain.deriveFont(Font.BOLD);

        // Leichtes Abdunkeln zeigt: die Welt dahinter ist gerade gesperrt (modal)
        g2.setColor(DIM);
        g2.fillRect(0, 0, target.getWidth(), target.getHeight());

        Rectangle b = DialogHit.BOUNDS;
        g2.setColor(PANEL);
        g2.fill(b);
        g2.setColor(TITLE_BAR);
        g2.fillRect(b.x, b.y, b.width, DialogHit.TITLE_HEIGHT);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(BORDER);
        g2.draw(b);

        g2.setFont(bold);
        g2.setColor(TEXT);
        g2.drawString(TITLE, b.x + DialogHit.LABEL_X, b.y + 20);
        drawCloseButton(g2, hover.is(Target.CLOSE));

        // Form (nur Anzeige)
        g2.setFont(plain);
        Rectangle formRow = DialogHit.formRow();
        drawLabel(g2, LABEL_FORM, formRow);
        g2.setColor(TEXT);
        g2.drawString(formLabel(world, dialog), b.x + DialogHit.VALUE_X, baseline(g2, formRow));

        drawColorRow(g2, dialog, hover);
        drawSizeRow(g2, dialog, hover);
        drawLayerRow(g2, dialog, hover);

        drawButton(g2, DialogHit.applyButton(), BTN_APPLY, hover.is(Target.APPLY), true, bold);
        drawButton(g2, DialogHit.cancelButton(), BTN_CANCEL, hover.is(Target.CANCEL), true, bold);

        g2.setFont(plain);
        g2.setColor(TEXT_DIM);
        g2.drawString(HINT, b.x + DialogHit.LABEL_X, baseline(g2, DialogHit.hintRow()));
    }

    private void drawColorRow(Graphics2D g2, PropertyDialog dialog, DialogHit hover) {
        Rectangle first = DialogHit.swatch(0);
        drawLabel(g2, LABEL_COLOR, first);
        for (int i = 0; i < DialogHit.SWATCH_COLORS.length; i++) {
            Rectangle r = DialogHit.swatch(i);
            Color c = DialogHit.SWATCH_COLORS[i];
            g2.setColor(c);
            g2.fill(r);
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(BORDER);
            g2.draw(r);
            if (c.equals(dialog.getDraftFill())) {
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(SELECTED_BORDER);
                g2.drawRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
            } else if (hover.isSwatch(i)) {
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(TEXT_DIM);
                g2.drawRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2);
            }
        }
        g2.setStroke(new BasicStroke(1f));
        // Aktueller Farbwert als Hex-Text
        Color f = dialog.getDraftFill();
        g2.setColor(TEXT_DIM);
        g2.drawString(String.format("#%02X%02X%02X", f.getRed(), f.getGreen(), f.getBlue()),
                DialogHit.BOUNDS.x + DialogHit.VALUE_X, baseline(g2, DialogHit.hexRow()));
    }

    private void drawSizeRow(Graphics2D g2, PropertyDialog dialog, DialogHit hover) {
        Rectangle minus = DialogHit.sizeMinus();
        drawLabel(g2, LABEL_SIZE, minus);
        drawButton(g2, minus, "–", hover.is(Target.SIZE_MINUS), true, g2.getFont());
        drawButton(g2, DialogHit.sizePlus(), "+", hover.is(Target.SIZE_PLUS), true, g2.getFont());

        Rectangle value = DialogHit.sizeValue();
        String text = Math.round(dialog.getDraftSize()) + " px";
        g2.setColor(TEXT);
        g2.drawString(text, value.x + (value.width - g2.getFontMetrics().stringWidth(text)) / 2, baseline(g2, value));
    }

    private void drawLayerRow(Graphics2D g2, PropertyDialog dialog, DialogHit hover) {
        Rectangle row = DialogHit.zRow();
        drawLabel(g2, LABEL_LAYER, row);
        g2.setColor(TEXT);
        // Im Pending-Modus gibt es noch keine Ebene: die neue Figur landet automatisch ganz oben
        String text = dialog.isEditMode() ? "z = " + dialog.getDraftZ() : "neu (ganz oben)";
        g2.drawString(text, DialogHit.BOUNDS.x + DialogHit.VALUE_X, baseline(g2, row));

        boolean enabled = dialog.isEditMode();
        drawButton(g2, DialogHit.zForward(), BTN_FORWARD, hover.is(Target.Z_FORWARD), enabled, g2.getFont());
        drawButton(g2, DialogHit.zBackward(), BTN_BACKWARD, hover.is(Target.Z_BACKWARD), enabled, g2.getFont());
    }

    private void drawCloseButton(Graphics2D g2, boolean hovered) {
        Rectangle r = DialogHit.closeButton();
        if (hovered) {
            g2.setColor(CLOSE_HOVER);
            g2.fill(r);
        }
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(TEXT);
        int pad = 6;
        g2.drawLine(r.x + pad, r.y + pad, r.x + r.width - pad, r.y + r.height - pad);
        g2.drawLine(r.x + r.width - pad, r.y + pad, r.x + pad, r.y + r.height - pad);
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawButton(Graphics2D g2, Rectangle r, String text, boolean hovered, boolean enabled, Font font) {
        g2.setColor(!enabled ? BUTTON_DISABLED : hovered ? BUTTON_HOVER : BUTTON);
        g2.fill(r);
        g2.setColor(BORDER);
        g2.draw(r);
        Font old = g2.getFont();
        g2.setFont(font);
        g2.setColor(enabled ? TEXT : TEXT_DIM);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, r.x + (r.width - fm.stringWidth(text)) / 2, baseline(g2, r));
        g2.setFont(old);
    }

    private void drawLabel(Graphics2D g2, String text, Rectangle row) {
        g2.setColor(TEXT_DIM);
        g2.drawString(text, DialogHit.BOUNDS.x + DialogHit.LABEL_X, baseline(g2, row));
    }

    private String formLabel(World world, PropertyDialog dialog) {
        if (dialog.isEditMode()) {
            return FormFactory.label(world.get(dialog.getTargetEntityId(), ShapeHolder.class).formId);
        }
        return FormFactory.label(world.getResource(SpawnMode.class).getSelectedForm());
    }

    /** Baseline, damit der Text im Rechteck vertikal mittig sitzt. */
    private int baseline(Graphics2D g2, Rectangle r) {
        FontMetrics fm = g2.getFontMetrics();
        return r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
    }
}
