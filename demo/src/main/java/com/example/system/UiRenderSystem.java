package com.example.system;

import com.example.Game.RenderTarget;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;
import com.example.ui.FormMenuItem;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;

/** Zeichnet das Form-Menü über der Welt: Anker immer, das Dropdown nur wenn geöffnet. */
public class UiRenderSystem implements System {
    private static final String ANCHOR_CLOSED = "Form>";
    private static final String ANCHOR_OPEN = "Form v";

    private static final Color ANCHOR_COLOR = new Color(50, 60, 85);
    private static final Color ANCHOR_HOVER_COLOR = new Color(75, 90, 125);
    private static final Color PANEL_COLOR = new Color(40, 48, 68);
    private static final Color ITEM_HOVER_COLOR = new Color(85, 105, 150);
    private static final Color BORDER_COLOR = new Color(20, 25, 40);
    private static final Color SEPARATOR_COLOR = new Color(70, 80, 105);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color ACTIVE_COLOR = new Color(255, 200, 60); // aktives Spawn-Werkzeug
    private static final Color PREVIEW_COLOR = new Color(140, 190, 240);

    private static final int TEXT_PADDING = 10;
    private static final int PREVIEW_PADDING = 18;   // Abstand linker Rand -> Preview-Mitte
    private static final int LABEL_X_OFFSET = 38;    // Abstand linker Rand -> Text im Eintrag
    private static final int ACTIVE_BAR_WIDTH = 4;
    private static final int SELECTION_GAP = 12;

    @Override
    public void update(World world, double alpha) {
        Graphics2D g2 = (Graphics2D) world.getResource(RenderTarget.class).getGraphics();
        UiMenuState menu = world.getResource(UiMenuState.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        Font bold = g2.getFont().deriveFont(Font.BOLD);
        Font plain = g2.getFont();

        drawAnchor(g2, menu, spawn, plain, bold);
        if (menu.isFormMenuOpen()) {
            drawDropdown(g2, menu, spawn, plain, bold);
        }
    }

    private void drawAnchor(Graphics2D g2, UiMenuState menu, SpawnMode spawn, Font plain, Font bold) {
        Rectangle a = menu.getAnchorBounds();
        g2.setColor(menu.isAnchorHovered() || menu.isFormMenuOpen() ? ANCHOR_HOVER_COLOR : ANCHOR_COLOR);
        g2.fill(a);
        g2.setColor(BORDER_COLOR);
        g2.draw(a);

        g2.setFont(plain);
        g2.setColor(TEXT_COLOR);
        g2.drawString(menu.isFormMenuOpen() ? ANCHOR_OPEN : ANCHOR_CLOSED, a.x + TEXT_PADDING, baseline(g2, a));

        // Aktuelle Auswahl klein daneben; fett + Akzentfarbe, solange der Spawn-Modus aktiv ist
        String selection = FormFactory.label(spawn.getSelectedForm());
        g2.setFont(spawn.isEnabled() ? bold : plain);
        g2.setColor(spawn.isEnabled() ? ACTIVE_COLOR : TEXT_COLOR);
        g2.drawString(selection, a.x + a.width + SELECTION_GAP, baseline(g2, a));
        g2.setFont(plain);
    }

    private void drawDropdown(Graphics2D g2, UiMenuState menu, SpawnMode spawn, Font plain, Font bold) {
        Rectangle d = menu.getDropdownBounds();
        g2.setColor(PANEL_COLOR);
        g2.fill(d);

        List<FormMenuItem> items = menu.getItems();
        for (int i = 0; i < items.size(); i++) {
            FormMenuItem item = items.get(i);
            Rectangle b = item.bounds;
            boolean selected = i == menu.getSelectedIndex();
            boolean activeTool = selected && spawn.isEnabled();

            // Hover hellt genau diesen Eintrag auf
            if (i == menu.getHoveredIndex()) {
                g2.setColor(ITEM_HOVER_COLOR);
                g2.fill(b);
            }
            // Linker Farbbalken markiert das aktive Spawn-Werkzeug
            if (activeTool) {
                g2.setColor(ACTIVE_COLOR);
                g2.fillRect(b.x, b.y, ACTIVE_BAR_WIDTH, b.height);
            }

            // Preview links: dieselbe Shape-Erzeugung wie in der Welt, nur klein skaliert
            AffineTransform old = g2.getTransform();
            g2.translate(b.x + PREVIEW_PADDING, b.getCenterY());
            g2.setColor(PREVIEW_COLOR);
            g2.fill(item.previewShape);
            g2.setTransform(old);

            g2.setFont(activeTool ? bold : plain);
            g2.setColor(TEXT_COLOR);
            g2.drawString(item.label, b.x + LABEL_X_OFFSET, baseline(g2, b));

            // Trennlinie zwischen den Einträgen
            if (i < items.size() - 1) {
                g2.setColor(SEPARATOR_COLOR);
                g2.drawLine(b.x, b.y + b.height - 1, b.x + b.width, b.y + b.height - 1);
            }
        }
        g2.setFont(plain);
        g2.setColor(BORDER_COLOR);
        g2.draw(d);
    }

    /** Baseline, damit der Text im Rechteck vertikal mittig sitzt. */
    private int baseline(Graphics2D g2, Rectangle r) {
        FontMetrics fm = g2.getFontMetrics();
        return r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;
    }
}
