package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.MouseState;
import com.example.resource.DragState;
import com.example.resource.PropertyDialog;
import com.example.resource.Recorder;
import com.example.resource.Selection;

/**
 * Zieht die beim Klick gegriffene Figur (DragState, gesetzt vom WorldPickSystem), solange die
 * linke Maustaste gehalten wird. Erst ab einer kleinen Mindestbewegung zählt es als Drag, damit
 * ein einfacher Klick die Figur nicht verrutscht. Loslassen ohne Bewegung führt ein vorgemerktes
 * Ebenen-Blättern aus.
 */
public class DragSystem implements System {
    /** Mindestbewegung in Pixeln, ab der aus dem Klick ein Drag wird. */
    private static final int DRAG_THRESHOLD = 4;

    @Override
    public void update(World world, double dt) {
        DragState drag = world.getResource(DragState.class);
        if (!drag.isActive()) {
            return;
        }
        MouseState mouse = world.getResource(MouseState.class);
        Selection selection = world.getResource(Selection.class);
        int id = drag.getDraggedId();

        // Dialog geöffnet (z. B. per E), Wiedergabe gestartet oder Figur weg: Drag abbrechen
        if (world.getResource(PropertyDialog.class).isOpen() || world.getResource(Recorder.class).isPlaying()
                || !world.has(id, Transform.class)) {
            drag.end();
            return;
        }

        int mx = mouse.getX();
        int my = mouse.getY();
        if (mouse.isLeftPressed()) {
            if (!drag.isMoved() && (Math.abs(mx - drag.getStartX()) > DRAG_THRESHOLD
                    || Math.abs(my - drag.getStartY()) > DRAG_THRESHOLD)) {
                drag.setMoved(true);
            }
            if (drag.isMoved()) {
                // Transform ist der Mittelpunkt der Figur; er bleibt im Fenster, damit sie nicht verloren geht
                RenderTarget target = world.getResource(RenderTarget.class);
                Transform t = world.get(id, Transform.class);
                t.x = clamp(mx - drag.getOffsetX(), target.getWidth());
                t.y = clamp(my - drag.getOffsetY(), target.getHeight());
            }
            return;
        }

        // Losgelassen
        if (drag.isMoved()) {
            selection.clearHits(); // Figur liegt jetzt woanders -> Treffer-Stapel veraltet
        } else if (drag.hasPendingCycle()) {
            selection.pick(drag.getCycleTargetId(), drag.getCycleHits(), drag.getCycleIndex(),
                    drag.getStartX(), drag.getStartY());
        }
        drag.end();
    }

    private static double clamp(double value, int max) {
        return Math.max(0, Math.min(max, value));
    }
}
