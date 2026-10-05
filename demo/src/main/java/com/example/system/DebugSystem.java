package com.example.system;

import com.example.Game.RenderTarget;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.resource.SpawnMode;

import java.awt.Color;
import java.awt.Graphics;

/** Debug-Text unten links (oben links sitzt das Form-Menü): FPS, UPS, Entities, Spawn-Modus. */
public class DebugSystem implements System {
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int TEXT_X = 10;
    private static final int LINE_HEIGHT = 16;
    private static final int BOTTOM_MARGIN = 10;

    @Override
    public void update(World world, double alpha) {
        RenderTarget target = world.getResource(RenderTarget.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        Graphics g = target.getGraphics();
        int secondLineY = target.getHeight() - BOTTOM_MARGIN;
        int firstLineY = secondLineY - LINE_HEIGHT;

        g.setColor(TEXT_COLOR);
        g.drawString("FPS: " + target.getFps() + "  UPS: " + target.getUps()
                + "  Entities: " + world.getEntityCount(), TEXT_X, firstLineY);
        g.drawString("SPAWN: " + (spawn.isEnabled() ? "AN (" + FormFactory.label(spawn.getSelectedForm()) + ")" : "AUS"),
                TEXT_X, secondLineY);
    }
}
