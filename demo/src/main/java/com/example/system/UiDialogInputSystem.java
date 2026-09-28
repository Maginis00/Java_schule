package com.example.system;

import com.example.component.Layer;
import com.example.component.Style;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.KeyState;
import com.example.input.MouseState;
import com.example.resource.PropertyDialog;
import com.example.resource.Selection;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;
import com.example.ui.DialogHit;
import com.example.ui.ZOrder;

import java.awt.event.KeyEvent;

/**
 * Läuft als erstes im InputSet. Solange der Dialog offen ist, ist er modal: JEDER Klick wird
 * hier verbraucht (auch neben dem Dialog), weder Menü noch Pick noch Spawn sehen ihn.
 * Bei geschlossenem Dialog öffnet nur die Taste E ihn (Selektion vor Pending-Spawn).
 */
public class UiDialogInputSystem implements System {
    private static final float SIZE_STEP = 4f; // Pixel pro Klick auf +/-

    @Override
    public void update(World world, double dt) {
        PropertyDialog dialog = world.getResource(PropertyDialog.class);
        KeyState keys = world.getResource(KeyState.class);

        if (dialog.isOpen()) {
            handleOpenDialog(world, dialog, keys);
        } else if (keys.consumePressed(KeyEvent.VK_E)) {
            openForSelectionOrPending(world, dialog);
        }
    }

    private void openForSelectionOrPending(World world, PropertyDialog dialog) {
        Selection selection = world.getResource(Selection.class);
        SpawnMode spawn = world.getResource(SpawnMode.class);
        if (selection.hasSelection()) {
            dialog.openEdit(world, selection.getSelectedId());
        } else if (spawn.isEnabled()) {
            dialog.openPending(spawn);
        }
        // Ein offenes Dropdown würde unter dem Dialog liegen und Klicks schlucken
        world.getResource(UiMenuState.class).setFormMenuOpen(false);
    }

    private void handleOpenDialog(World world, PropertyDialog dialog, KeyState keys) {
        MouseState mouse = world.getResource(MouseState.class);

        // Ziel-Entity verschwunden -> Dialog schließen
        if (dialog.isEditMode() && !world.has(dialog.getTargetEntityId(), Style.class)) {
            dialog.close();
            return;
        }

        // ESC bricht ab (erste Stufe der ESC-Kette), E übernimmt
        if (keys.consumePressed(KeyEvent.VK_ESCAPE)) {
            cancel(world, dialog);
            return;
        }
        if (keys.consumePressed(KeyEvent.VK_E)) {
            commit(world, dialog);
            return;
        }

        if (mouse.isRightClickedThisFrame()) {
            mouse.consumeRightClick();
        }
        if (mouse.isLeftClickedThisFrame()) {
            // Dialog-Hit-Test: Widget unter dem Punkt bestimmen (Rechteck-Tests in DialogHit).
            // Ein Klick neben den Dialog ist ein Treffer "OUTSIDE" und passiert einfach nichts.
            perform(world, dialog, DialogHit.at(mouse.getX(), mouse.getY()));
            mouse.consumeLeftClick();
        }
        if (dialog.isOpen()) {
            dialog.setHover(mouse.isInsideWindow() ? DialogHit.at(mouse.getX(), mouse.getY()) : DialogHit.none());
        }
    }

    private void perform(World world, PropertyDialog dialog, DialogHit hit) {
        switch (hit.target) {
            case SWATCH:
                dialog.setDraftFill(DialogHit.SWATCH_COLORS[hit.index]);
                applyLive(world, dialog);
                break;
            case SIZE_MINUS:
                dialog.setDraftSize(Style.clampSize(dialog.getDraftSize() - SIZE_STEP));
                applyLive(world, dialog);
                break;
            case SIZE_PLUS:
                dialog.setDraftSize(Style.clampSize(dialog.getDraftSize() + SIZE_STEP));
                applyLive(world, dialog);
                break;
            case Z_FORWARD:
                moveLayer(world, dialog, true);
                break;
            case Z_BACKWARD:
                moveLayer(world, dialog, false);
                break;
            case APPLY:
                commit(world, dialog);
                break;
            case CANCEL:
            case CLOSE:
                cancel(world, dialog);
                break;
            default:
                break; // PANEL / OUTSIDE: nur verbrauchen
        }
    }

    /** Sofort-Vorschau: im Edit-Modus gehen Farbe und Größe direkt auf die Ziel-Entity. */
    private void applyLive(World world, PropertyDialog dialog) {
        if (!dialog.isEditMode()) {
            return;
        }
        Style style = world.get(dialog.getTargetEntityId(), Style.class);
        style.fill = dialog.getDraftFill();
        style.size = dialog.getDraftSize();
    }

    private void moveLayer(World world, PropertyDialog dialog, boolean forward) {
        if (!dialog.isEditMode()) {
            return; // eine neue Figur landet ohnehin ganz oben
        }
        int id = dialog.getTargetEntityId();
        boolean moved = forward ? ZOrder.moveForward(world, id) : ZOrder.moveBackward(world, id);
        if (moved) {
            dialog.addZSteps(forward ? 1 : -1);
            dialog.setDraftZ(world.get(id, Layer.class).zIndex);
            world.getResource(Selection.class).clearHits(); // Ebenen geändert -> Treffer-Stapel veraltet
        }
    }

    /** Übernehmen: Edit-Werte gelten schon, Pending-Werte werden für die nächste Figur vorgemerkt. */
    private void commit(World world, PropertyDialog dialog) {
        if (!dialog.isEditMode()) {
            world.getResource(SpawnMode.class).setPendingStyle(
                    new Style(dialog.getDraftFill(), Style.DEFAULT_STROKE, Style.DEFAULT_STROKE_WIDTH,
                            dialog.getDraftSize()));
        }
        dialog.close();
    }

    /** Abbrechen: Edit-Änderungen (Farbe, Größe, Ebenen-Schritte) zurücknehmen, Pending bleibt unverändert. */
    private void cancel(World world, PropertyDialog dialog) {
        if (dialog.isEditMode()) {
            int id = dialog.getTargetEntityId();
            Style style = world.get(id, Style.class);
            style.fill = dialog.getOriginalFill();
            style.size = dialog.getOriginalSize();
            // Ebenen-Tausche sind umkehrbar: gleich viele Schritte zurück
            while (dialog.getZSteps() > 0) {
                ZOrder.moveBackward(world, id);
                dialog.addZSteps(-1);
            }
            while (dialog.getZSteps() < 0) {
                ZOrder.moveForward(world, id);
                dialog.addZSteps(1);
            }
            world.getResource(Selection.class).clearHits();
        }
        dialog.close();
    }
}
