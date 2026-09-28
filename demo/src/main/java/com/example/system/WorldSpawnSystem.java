package com.example.system;

import com.example.component.Layer;
import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.input.MouseState;
import com.example.resource.PropertyDialog;
import com.example.resource.Selection;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;
import com.example.ui.ZOrder;

/**
 * Erzeugt im Spawn-Modus eine Figur der gewählten Form, aber NUR ins Leere: Klicks auf Menü,
 * Dialog oder eine vorhandene Figur hat vorher jemand verbraucht. Muss das letzte Maus-System
 * im InputSet sein, weil es am Ende die Klick-Flags löscht.
 */
public class WorldSpawnSystem implements System {
    @Override
    public void update(World world, double dt) {
        MouseState mouse = world.getResource(MouseState.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        UiMenuState menu = world.getResource(UiMenuState.class);
        PropertyDialog dialog = world.getResource(PropertyDialog.class);

        if (spawn.isEnabled() && !dialog.isOpen() && !menu.isFormMenuOpen() && mouse.isLeftClickedThisFrame()) {
            spawn(world, spawn, mouse.getX(), mouse.getY());
            mouse.consumeLeftClick();
        }

        // Ende der Maus-Systems im InputSet: offene Klicks verwerfen
        mouse.endFrame();
    }

    private void spawn(World world, SpawnMode spawn, int x, int y) {
        String formId = spawn.getSelectedForm();
        // Vorgemerkte Dialog-Werte gelten für genau diese eine Figur, sonst die Standardwerte
        Style style = spawn.takePendingStyle();
        if (style == null) {
            style = FormFactory.defaultStyle(formId);
        }

        // Welt-Koordinaten = Bildschirmkoordinaten (keine Kamera); neue Figuren liegen oben (maxZ + 1)
        int z = ZOrder.maxZ(world) + 1;
        int id = world.createEntity();
        world.add(id, new Transform(x, y));
        world.add(id, new ShapeHolder(FormFactory.create(formId), formId));
        world.add(id, style);
        world.add(id, new Layer(z));

        // Neue Figur kann einen alten Treffer-Stapel überdecken
        world.getResource(Selection.class).clearHits();
    }
}
