package com.example.system;

import com.example.component.UiButton;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.resource.SpawnMode;

/** Berechnet das Leuchten: solange der Spawn-Modus an ist, pulsiert der Knopf sinusförmig. */
public class ButtonVisualSystem implements System {
    private static final double GLOW_PERIOD_SECONDS = 1.2;

    @Override
    public void update(World world, double dt) {
        SpawnMode spawn = world.getResource(SpawnMode.class);
        if (spawn.isEnabled()) {
            spawn.addGlowTime(dt);
        }

        // cos startet bei 1: direkt nach dem Klick leuchtet der Knopf am hellsten
        double pulse = 0.5 + 0.5 * Math.cos(2 * Math.PI * spawn.getGlowTime() / GLOW_PERIOD_SECONDS);

        for (int id : world.query(UiButton.class)) {
            UiButton button = world.get(id, UiButton.class);
            button.glow = button.active ? pulse : 0;
        }
    }
}
