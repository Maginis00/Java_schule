package com.example.system;

import com.Figuren.Dreieck;
import com.example.component.ShapeHolder;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.MouseState;
import com.example.resource.SpawnMode;

import java.awt.Color;
import java.util.Random;

/**
 * Erzeugt im Spawn-Modus bei jedem (nicht von der UI verbrauchten) Linksklick eine Entity.
 * Muss das letzte Maus-System im InputSet sein, weil es am Ende die Klick-Flags löscht.
 */
public class WorldInputSystem implements System {
    private static final int MIN_SIZE = 40;
    private static final int MAX_SIZE = 90;
    private static final float COLOR_SATURATION = 0.6f;
    private static final float COLOR_BRIGHTNESS = 0.9f;

    private final Random random = new Random();

    @Override
    public void update(World world, double dt) {
        MouseState mouse = world.getResource(MouseState.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);

        // Hat die UI den Klick verbraucht, ist das Flag hier schon false
        if (spawn.isEnabled() && mouse.isLeftClickedThisFrame()) {
            spawn(world, spawn.getFigurTyp(), mouse.getX(), mouse.getY());
        }

        // Ende des InputSets: offene Klicks verwerfen, damit sie nicht im nächsten Schritt nochmal zählen
        mouse.endFrame();
    }

    private void spawn(World world, String figurTyp, int x, int y) {
        if (!SpawnMode.FIGUR_DREIECK.equals(figurTyp)) {
            return;
        }
        // Welt-Koordinaten = Bildschirmkoordinaten (keine Kamera)
        Dreieck dreieck = new Dreieck();
        dreieck.groesseAendern(randomSize(), randomSize());
        Color color = Color.getHSBColor(random.nextFloat(), COLOR_SATURATION, COLOR_BRIGHTNESS);

        int id = world.createEntity();
        world.add(id, new Transform(x, y));
        world.add(id, new ShapeHolder(dreieck, color));
    }

    private int randomSize() {
        return MIN_SIZE + random.nextInt(MAX_SIZE - MIN_SIZE + 1);
    }
}
