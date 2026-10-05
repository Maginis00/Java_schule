package com.example.system;

import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.input.KeyState;
import com.example.input.MouseState;
import com.example.resource.DragState;
import com.example.resource.PropertyDialog;
import com.example.resource.Recorder;
import com.example.resource.Selection;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;
import com.example.ui.ZOrder;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Selektion und Ebenen-Durchklicken. Läuft nur, wenn Dialog und Menü den Klick nicht schon
 * verbraucht haben. Ein Klick auf eine Figur wird hier verbraucht (auch im Spawn-Modus: Pick
 * schlägt Spawn) und greift sie für das DragSystem; nur ein Klick ins Leere bleibt für das
 * WorldSpawnSystem übrig.
 */
public class WorldPickSystem implements System {
    /** Innerhalb dieses Abstands zum letzten Klick zählt ein Klick als "dieselbe Stelle". */
    private static final int SAME_SPOT_TOLERANCE = 4;
    /** Größenänderung per Tastatur in Pixel pro Sekunde, solange die Taste gehalten wird. */
    private static final float SIZE_KEY_RATE = 80f;

    @Override
    public void update(World world, double dt) {
        MouseState mouse = world.getResource(MouseState.class);
        KeyState keys = world.getResource(KeyState.class);
        Selection selection = world.getResource(Selection.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        UiMenuState menu = world.getResource(UiMenuState.class);
        PropertyDialog dialog = world.getResource(PropertyDialog.class);
        DragState drag = world.getResource(DragState.class);

        // Dialog offen oder Wiedergabe = weder Pick noch Tastatur-Bearbeitung
        if (dialog.isOpen() || world.getResource(Recorder.class).isPlaying()) {
            return;
        }

        // Letzte Stufe der ESC-Kette: Selektion aufheben
        if (selection.hasSelection() && keys.consumePressed(KeyEvent.VK_ESCAPE)) {
            selection.clear();
        }
        handleSelectionKeys(world, keys, selection, dt);

        if (menu.isFormMenuOpen() || !mouse.isLeftClickedThisFrame()) {
            return;
        }

        int mx = mouse.getX();
        int my = mouse.getY();
        List<Integer> hits = hitsAt(world, mx, my);

        if (hits.isEmpty()) {
            // Klick ins Leere hebt die Selektion auf. Im Spawn-Modus bleibt der Klick für das
            // WorldSpawnSystem stehen, sonst ist er hier erledigt.
            selection.clear();
            if (!spawn.isEnabled()) {
                mouse.consumeLeftClick();
            }
            return;
        }

        int selectedIndex = hits.indexOf(selection.getSelectedId());

        // Doppelklick auf die (vom ersten Klick schon selektierte) Figur: Dialog statt Blättern
        if (mouse.isLeftDoubleClickedThisFrame() && selectedIndex >= 0) {
            dialog.openEdit(world, selection.getSelectedId());
            mouse.consumeLeftClick();
            return;
        }

        // Pick-Cycle: liegt die Selektion schon unter der SELBEN Stelle im Stapel, kommt die
        // nächste Ebene darunter (zyklisch, nach der untersten wieder die oberste).
        // Sonst (neue Stelle oder nichts selektiert) wird die oberste Figur genommen.
        boolean sameSpot = Math.abs(mx - selection.getLastMouseX()) <= SAME_SPOT_TOLERANCE
                && Math.abs(my - selection.getLastMouseY()) <= SAME_SPOT_TOLERANCE;
        if (selectedIndex >= 0 && sameSpot && hits.size() > 1) {
            // Das Blättern passiert erst beim Loslassen ohne Bewegung (DragSystem). Bis dahin ist die
            // schon selektierte Figur gegriffen, damit ein Drag sie bewegt und nicht die darunter.
            beginDrag(world, drag, selection.getSelectedId(), mx, my);
            int nextIndex = (selectedIndex + 1) % hits.size();
            drag.setPendingCycle(hits.get(nextIndex), hits, nextIndex);
        } else {
            selection.pick(hits.get(0), hits, 0, mx, my);
            beginDrag(world, drag, hits.get(0), mx, my);
        }
        mouse.consumeLeftClick();
    }

    /** Offset zwischen Klickpunkt und Figur-Mittelpunkt merken, damit die Figur beim Greifen nicht springt. */
    private void beginDrag(World world, DragState drag, int id, int mx, int my) {
        Transform t = world.get(id, Transform.class);
        drag.begin(id, mx - t.x, my - t.y, mx, my);
    }

    /**
     * Alle Figuren, deren ECHTE Shape den Punkt enthält (nicht nur die Bounding-Box),
     * oberste zuerst. Selbe Z-Sortierung und selbe Shape-Erzeugung wie beim Zeichnen.
     */
    private List<Integer> hitsAt(World world, int x, int y) {
        List<Integer> order = ZOrder.bottomToTop(world);
        Collections.reverse(order); // oben zuerst
        List<Integer> hits = new ArrayList<>();
        for (int id : order) {
            Transform t = world.get(id, Transform.class);
            ShapeHolder holder = world.get(id, ShapeHolder.class);
            Style style = world.get(id, Style.class);
            if (FormFactory.toShape(holder.figur, t, style.size).contains(x, y)) {
                hits.add(id);
            }
        }
        return hits;
    }

    /** Tastatur-Bearbeitung der Selektion: Größe halten (+ - [ ]) und Ebene per PageUp/PageDown. */
    private void handleSelectionKeys(World world, KeyState keys, Selection selection, double dt) {
        if (!selection.hasSelection() || !world.has(selection.getSelectedId(), Style.class)) {
            return;
        }
        int id = selection.getSelectedId();

        int grow = 0;
        if (keys.isDown(KeyEvent.VK_CLOSE_BRACKET) || keys.isDown(KeyEvent.VK_PLUS)
                || keys.isDown(KeyEvent.VK_ADD) || keys.isDown(KeyEvent.VK_EQUALS)) {
            grow++;
        }
        if (keys.isDown(KeyEvent.VK_OPEN_BRACKET) || keys.isDown(KeyEvent.VK_MINUS)
                || keys.isDown(KeyEvent.VK_SUBTRACT)) {
            grow--;
        }
        if (grow != 0) {
            Style style = world.get(id, Style.class);
            style.size = Style.clampSize(style.size + grow * SIZE_KEY_RATE * (float) dt);
        }

        boolean moved = false;
        if (keys.consumePressed(KeyEvent.VK_PAGE_UP)) {
            moved |= ZOrder.moveForward(world, id);
        }
        if (keys.consumePressed(KeyEvent.VK_PAGE_DOWN)) {
            moved |= ZOrder.moveBackward(world, id);
        }
        if (moved) {
            selection.clearHits(); // Ebenen geändert -> Treffer-Stapel veraltet
        }
    }
}
