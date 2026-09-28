package com.example.system;

import com.example.Game.RenderTarget;
import com.example.ecs.System;
import com.example.ecs.World;

import java.awt.Color;
import java.awt.Graphics;

/** Debug-Text oben links: FPS, UPS und Entity-Anzahl. Muss nach dem RenderSystem laufen. */
public class DebugSystem implements System {
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int TEXT_X = 10;
    private static final int TEXT_Y = 20;

    @Override
    public void update(World world, double alpha) {
        RenderTarget target = world.getResource(RenderTarget.class);
        Graphics g = target.getGraphics();
        g.setColor(TEXT_COLOR);
        g.drawString("FPS: " + target.getFps() + "  UPS: " + target.getUps()
                + "  Entities: " + world.getEntityCount(), TEXT_X, TEXT_Y);
    }
}
