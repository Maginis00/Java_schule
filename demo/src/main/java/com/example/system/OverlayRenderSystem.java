package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.resource.Selection;
import com.example.ui.ZOrder;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Info-Box oben rechts: Daten der aktuellen Selektion, "Ebene i / n" und Größe/Farbe. */
public class OverlayRenderSystem implements System {
    private static final Color BOX = new Color(20, 26, 42, 200);
    private static final Color BORDER = new Color(15, 20, 35);
    private static final Color TEXT = Color.WHITE;
    private static final Color TEXT_DIM = new Color(150, 160, 185);
    private static final int BOX_WIDTH = 150;
    private static final int MARGIN = 10;
    private static final int PADDING = 8;
    private static final int LINE_HEIGHT = 17;

    private static final String NO_SELECTION = "Keine Auswahl";
    private static final String HEADER = "Auswahl";
    private static final String LAYER = "Ebene ";
    private static final String SIZE = "Größe ";

    @Override
    public void update(World world, double alpha) {
        RenderTarget target = world.getResource(RenderTarget.class);
        Selection selection = world.getResource(Selection.class);
        Graphics2D g2 = (Graphics2D) target.getGraphics();

        List<String> lines = new ArrayList<>();
        int id = selection.getSelectedId();
        if (selection.hasSelection() && world.has(id, Style.class)) {
            Style style = world.get(id, Style.class);
            lines.add(HEADER);
            lines.add(FormFactory.label(world.get(id, ShapeHolder.class).formId));
            lines.add(layerText(world, selection, id));
            lines.add(SIZE + Math.round(style.size));
            lines.add(String.format("#%02X%02X%02X", style.fill.getRed(), style.fill.getGreen(), style.fill.getBlue()));
            lines.add("id: " + id);
        } else {
            lines.add(NO_SELECTION);
        }

        int height = PADDING * 2 + LINE_HEIGHT * lines.size() - (LINE_HEIGHT - g2.getFontMetrics().getHeight());
        int x = target.getWidth() - BOX_WIDTH - MARGIN;
        g2.setColor(BOX);
        g2.fillRect(x, MARGIN, BOX_WIDTH, height);
        g2.setColor(BORDER);
        g2.drawRect(x, MARGIN, BOX_WIDTH, height);

        Font plain = g2.getFont();
        FontMetrics fm = g2.getFontMetrics();
        int y = MARGIN + PADDING + fm.getAscent();
        for (int i = 0; i < lines.size(); i++) {
            boolean header = i == 0 && lines.size() > 1;
            g2.setFont(header ? plain.deriveFont(Font.BOLD) : plain);
            g2.setColor(lines.size() > 1 && i == lines.size() - 1 ? TEXT_DIM : TEXT); // id-Zeile gedämpft
            g2.drawString(lines.get(i), x + PADDING, y + i * LINE_HEIGHT);
        }
        g2.setFont(plain);
    }

    /**
     * "Ebene i / n" (1 = oben). Direkt nach einem Klick ist das der Treffer i von n überlappenden
     * Figuren unter dem Klickpunkt; ist der Treffer-Stapel leer oder veraltet, die Position der
     * Selektion in der globalen Z-Liste.
     */
    private String layerText(World world, Selection selection, int id) {
        List<Integer> stack = selection.getLastHitStack();
        if (!stack.isEmpty() && stack.contains(id)) {
            return LAYER + (stack.indexOf(id) + 1) + " / " + stack.size();
        }
        List<Integer> topDown = ZOrder.bottomToTop(world);
        Collections.reverse(topDown);
        return LAYER + (topDown.indexOf(id) + 1) + " / " + topDown.size();
    }
}
