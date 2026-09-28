package com.example.system;

import com.example.component.PlayerTag;
import com.example.component.Velocity;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.KeyState;

import java.awt.event.KeyEvent;

/** Übersetzt den Tastaturzustand in die Velocity der Entity(s) mit PlayerTag. */
public class InputSystem implements System {
    /** Geschwindigkeit in Pixel pro Sekunde (nicht pro Frame). */
    private static final double PLAYER_SPEED = 300.0;

    @Override
    public void update(World world, double dt) {
        KeyState keys = world.getResource(KeyState.class);

        int dx = 0;
        int dy = 0;
        if (keys.isDown(KeyEvent.VK_LEFT) || keys.isDown(KeyEvent.VK_A)) dx--;
        if (keys.isDown(KeyEvent.VK_RIGHT) || keys.isDown(KeyEvent.VK_D)) dx++;
        if (keys.isDown(KeyEvent.VK_UP) || keys.isDown(KeyEvent.VK_W)) dy--;
        if (keys.isDown(KeyEvent.VK_DOWN) || keys.isDown(KeyEvent.VK_S)) dy++;

        // Diagonale normalisieren, damit sie nicht schneller ist
        double len = Math.sqrt(dx * dx + dy * dy);
        double vx = len == 0 ? 0 : dx / len * PLAYER_SPEED;
        double vy = len == 0 ? 0 : dy / len * PLAYER_SPEED;

        for (int id : world.query(PlayerTag.class, Velocity.class)) {
            Velocity v = world.get(id, Velocity.class);
            v.vx = vx;
            v.vy = vy;
        }
    }
}
