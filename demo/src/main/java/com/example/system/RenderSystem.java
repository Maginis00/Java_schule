package com.example.system;

import com.example.Game.RenderTarget;
import com.example.ecs.System;
import com.example.ecs.World;

import java.awt.Color;
import java.awt.Graphics;

/** Erstes System im RenderSet: löscht den Frame mit der Hintergrundfarbe. */
public class RenderSystem implements System {
    private static final Color BACKGROUND = new Color(30, 40, 60);

    /** Der zweite Parameter ist hier der Interpolationsfaktor; ohne Interpolation ungenutzt. */
    @Override
    public void update(World world, double alpha) {
        RenderTarget target = world.getResource(RenderTarget.class);
        Graphics g = target.getGraphics();

        g.setColor(BACKGROUND);
        g.fillRect(0, 0, target.getWidth(), target.getHeight());
    }
}
