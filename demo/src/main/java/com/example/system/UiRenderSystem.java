package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.UiButton;
import com.example.ecs.System;
import com.example.ecs.World;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/** Zeichnet den Dreiecks-Knopf mit Beschriftung; läuft nach der Welt, damit die UI oben liegt. */
public class UiRenderSystem implements System {
    private static final float NORMAL_STROKE = 1.5f;
    private static final float ACTIVE_STROKE = 3.5f;
    private static final Color NORMAL_OUTLINE = new Color(20, 30, 50);
    private static final Color ACTIVE_OUTLINE = Color.WHITE;
    private static final double MAX_GLOW_MIX = 0.5;  // wie weit die Füllung Richtung Weiß pulsiert
    private static final int LABEL_GAP = 10;
    private static final int LABEL_BASELINE_OFFSET = 5;
    private static final Color LABEL_COLOR = Color.WHITE;

    @Override
    public void update(World world, double alpha) {
        Graphics2D g2 = (Graphics2D) world.getResource(RenderTarget.class).getGraphics();

        for (int id : world.query(UiButton.class)) {
            UiButton b = world.get(id, UiButton.class);

            g2.setColor(fillColor(b));
            g2.fill(b.polygon);

            g2.setStroke(new BasicStroke(b.active ? ACTIVE_STROKE : NORMAL_STROKE));
            g2.setColor(b.active ? ACTIVE_OUTLINE : NORMAL_OUTLINE);
            g2.draw(b.polygon);

            Rectangle bounds = b.polygon.getBounds();
            g2.setColor(LABEL_COLOR);
            g2.drawString(b.name, bounds.x + bounds.width + LABEL_GAP,
                    bounds.y + bounds.height / 2 + LABEL_BASELINE_OFFSET);
        }
    }

    private Color fillColor(UiButton b) {
        if (b.active) {
            return mixWithWhite(b.activeColor, b.glow * MAX_GLOW_MIX);
        }
        if (b.armed) {
            return b.hoverColor.darker(); // gedrückt
        }
        return b.hovered ? b.hoverColor : b.normalColor;
    }

    private static Color mixWithWhite(Color c, double amount) {
        int r = (int) Math.round(c.getRed() + (255 - c.getRed()) * amount);
        int g = (int) Math.round(c.getGreen() + (255 - c.getGreen()) * amount);
        int bl = (int) Math.round(c.getBlue() + (255 - c.getBlue()) * amount);
        return new Color(r, g, bl);
    }
}
