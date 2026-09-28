package com.example.system;

import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.KeyState;
import com.example.input.MouseState;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;
import com.example.ui.FormMenuItem;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Läuft als erstes im InputSet: Hit-Test von Anker und Dropdown, Auswahl einer Form,
 * Abbruch per Rechtsklick/ESC. Klicks, die das Menü betreffen, werden konsumiert.
 * Das Menü setzt nur den SpawnMode; erzeugt wird die Entity vom WorldInputSystem.
 */
public class UiMenuInputSystem implements System {
    @Override
    public void update(World world, double dt) {
        MouseState mouse = world.getResource(MouseState.class);
        KeyState keys = world.getResource(KeyState.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        UiMenuState menu = world.getResource(UiMenuState.class);

        if (mouse.isRightClickedThisFrame() || keys.isDown(KeyEvent.VK_ESCAPE)) {
            // Abbruch: Spawn-Modus aus, Menü zu
            spawn.setEnabled(false);
            menu.setFormMenuOpen(false);
            mouse.consumeRightClick();
        } else if (mouse.isLeftClickedThisFrame()) {
            handleLeftClick(new Point(mouse.getX(), mouse.getY()), mouse, menu, spawn);
        }

        // Hover erst nach den Klicks berechnen, damit er zum Zustand nach dem Klick passt
        updateHover(new Point(mouse.getX(), mouse.getY()), mouse.isInsideWindow(), menu);
    }

    /** Reihenfolge des Hit-Tests: erst Anker, dann offene Einträge, dann "außerhalb". */
    private void handleLeftClick(Point p, MouseState mouse, UiMenuState menu, SpawnMode spawn) {
        if (menu.getAnchorBounds().contains(p)) {
            // Anker klappt auf bzw. zu; die Auswahl bleibt unverändert
            menu.setFormMenuOpen(!menu.isFormMenuOpen());
            mouse.consumeLeftClick();
        } else if (menu.isFormMenuOpen() && menu.getDropdownBounds().contains(p)) {
            int index = itemAt(p, menu);
            if (index != UiMenuState.NO_ITEM) {
                FormMenuItem item = menu.getItems().get(index);
                menu.setSelectedIndex(index);
                spawn.setSelectedForm(item.formId);
                spawn.setEnabled(true);
                menu.setFormMenuOpen(false);
            }
            // Auch ein Treffer im Dropdown ohne Eintrag zählt als Menü-Klick
            mouse.consumeLeftClick();
        } else if (menu.isFormMenuOpen()) {
            // Klick daneben schließt nur das Menü. Er wird verbraucht, damit er nicht im
            // selben Schritt als Spawn in die Welt zählt; ein bereits aktiver SpawnMode bleibt an.
            menu.setFormMenuOpen(false);
            mouse.consumeLeftClick();
        }
        // Sonst: kein UI-Treffer, der Klick bleibt für das WorldInputSystem stehen
    }

    private void updateHover(Point p, boolean insideWindow, UiMenuState menu) {
        menu.setAnchorHovered(insideWindow && menu.getAnchorBounds().contains(p));
        boolean overItems = insideWindow && menu.isFormMenuOpen() && menu.getDropdownBounds().contains(p);
        menu.setHoveredIndex(overItems ? itemAt(p, menu) : UiMenuState.NO_ITEM);
    }

    private int itemAt(Point p, UiMenuState menu) {
        List<FormMenuItem> items = menu.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).bounds.contains(p)) {
                return i;
            }
        }
        return UiMenuState.NO_ITEM;
    }
}
