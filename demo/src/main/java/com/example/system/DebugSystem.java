package com.example.system;

import com.example.Game.RenderTarget;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.resource.SpawnMode;

import java.awt.Color;
import java.awt.Graphics;

/** Debug-Text unter dem UI-Knopf: FPS, UPS, Entity-Anzahl und Spawn-Modus. Läuft zuletzt. */
public class DebugSystem implements System {
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int TEXT_X = 10;
    private static final int TEXT_Y = 100; // unterhalb des Knopfes (y = 20..70)
    private static final int LINE_HEIGHT = 16;

    @Override
    public void update(World world, double alpha) {
        RenderTarget target = world.getResource(RenderTarget.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        Graphics g = target.getGraphics();
        g.setColor(TEXT_COLOR);
        g.drawString("FPS: " + target.getFps() + "  UPS: " + target.getUps()
                + "  Entities: " + world.getEntityCount(), TEXT_X, TEXT_Y);
        g.drawString("SPAWN: " + (spawn.isEnabled() ? "AN" : "AUS"), TEXT_X, TEXT_Y + LINE_HEIGHT);
    }
}
