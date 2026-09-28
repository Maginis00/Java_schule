package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.Renderable;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;

import java.awt.Color;
import java.awt.Graphics;

/** Zeichnet Hintergrund und alle Entities mit Transform + Renderable (in ID-Reihenfolge). */
public class RenderSystem implements System {
    private static final Color BACKGROUND = new Color(30, 40, 60);

    /** Der zweite Parameter ist hier der Interpolationsfaktor; ohne Interpolation ungenutzt. */
    @Override
    public void update(World world, double alpha) {
        RenderTarget target = world.getResource(RenderTarget.class);
        Graphics g = target.getGraphics();

        g.setColor(BACKGROUND);
        g.fillRect(0, 0, target.getWidth(), target.getHeight());

        for (int id : world.query(Transform.class, Renderable.class)) {
            Transform t = world.get(id, Transform.class);
            Renderable r = world.get(id, Renderable.class);
            g.setColor(r.color);
            g.fillRect((int) Math.round(t.x), (int) Math.round(t.y),
                    (int) Math.round(r.width), (int) Math.round(r.height));
        }
    }
}
