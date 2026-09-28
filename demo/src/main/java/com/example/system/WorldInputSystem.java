package com.example.system;

import com.Figuren.Form;
import com.example.component.ShapeHolder;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.input.MouseState;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;

import java.awt.Color;
import java.util.Random;

/**
 * Erzeugt im Spawn-Modus bei jedem (nicht von der UI verbrauchten) Linksklick eine Entity
 * der aktuell gewählten Form. Muss das letzte Maus-System im InputSet sein, weil es am Ende
 * die Klick-Flags löscht.
 */
public class WorldInputSystem implements System {
    private static final float COLOR_SATURATION = 0.6f;
    private static final float COLOR_BRIGHTNESS = 0.9f;

    private final Random random = new Random();

    @Override
    public void update(World world, double dt) {
        MouseState mouse = world.getResource(MouseState.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        UiMenuState menu = world.getResource(UiMenuState.class);

        // Hat die UI den Klick verbraucht, ist das Flag hier schon false
        if (spawn.isEnabled() && !menu.isFormMenuOpen() && mouse.isLeftClickedThisFrame()) {
            spawn(world, spawn.getSelectedForm(), mouse.getX(), mouse.getY());
        }

        // Ende des InputSets: offene Klicks verwerfen, damit sie nicht im nächsten Schritt nochmal zählen
        mouse.endFrame();
    }

    private void spawn(World world, String formId, int x, int y) {
        // Welt-Koordinaten = Bildschirmkoordinaten (keine Kamera)
        Form figur = FormFactory.create(formId);
        Color color = Color.getHSBColor(random.nextFloat(), COLOR_SATURATION, COLOR_BRIGHTNESS);

        int id = world.createEntity();
        world.add(id, new Transform(x, y));
        world.add(id, new ShapeHolder(figur, color));
    }
}
