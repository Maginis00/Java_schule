package com.example.system;

import com.example.component.UiButton;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.KeyState;
import com.example.input.MouseState;
import com.example.resource.SpawnMode;

import java.awt.event.KeyEvent;

/**
 * Läuft als erstes im InputSet: Hover-Test, Knopf-Klick und Abbruch des Spawn-Modus.
 * Der Knopf setzt nur den Modus; erzeugt wird die Entity erst vom WorldInputSystem.
 */
public class UiInputSystem implements System {
    @Override
    public void update(World world, double dt) {
        MouseState mouse = world.getResource(MouseState.class);
        KeyState keys = world.getResource(KeyState.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);

        boolean cancel = mouse.isRightClickedThisFrame() || keys.isDown(KeyEvent.VK_ESCAPE);

        for (int id : world.query(UiButton.class)) {
            UiButton button = world.get(id, UiButton.class);

            // Hit-Test gegen das Dreieck selbst (Polygon.contains), nicht gegen seine Bounding-Box
            button.hovered = mouse.isInsideWindow() && button.polygon.contains(mouse.getX(), mouse.getY());
            button.armed = button.hovered && mouse.isLeftPressed();

            if (button.hovered && mouse.isLeftClickedThisFrame()) {
                spawn.setEnabled(true);
                button.active = true;
                // Klick verbrauchen, sonst würde das WorldInputSystem ihn gleich noch einmal
                // sehen und direkt unter dem Knopf eine Entity erzeugen
                mouse.consumeLeftClick();
            }
            if (cancel) {
                button.active = false;
            }
        }

        if (cancel) {
            spawn.setEnabled(false);
            mouse.consumeRightClick();
        }
    }
}
