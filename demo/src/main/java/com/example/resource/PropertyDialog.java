package com.example.resource;

import com.example.component.Layer;
import com.example.component.Style;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.ui.DialogHit;

import java.awt.Color;

/**
 * Resource: Zustand des Eigenschaften-Dialogs.
 * SPAWN_PENDING: Werte für die nächste neue Figur. EDIT_ENTITY: Werte gelten sofort für
 * die Ziel-Entity (Live-Vorschau); die Originalwerte bleiben für "Abbrechen" erhalten.
 */
public class PropertyDialog {
    public enum Mode { SPAWN_PENDING, EDIT_ENTITY }

    private boolean open;
    private Mode mode = Mode.SPAWN_PENDING;
    private int targetEntityId = Selection.NONE;

    private Color draftFill = Style.DEFAULT_FILL;
    private float draftSize;
    private int draftZ;

    private Color originalFill;
    private float originalSize;
    private int zSteps; // netto "nach vorn" verschobene Ebenen, damit Abbrechen sie zurücknehmen kann

    private DialogHit hover = DialogHit.none();

    public void openEdit(World world, int entityId) {
        Style style = world.get(entityId, Style.class);
        Layer layer = world.get(entityId, Layer.class);
        if (style == null || layer == null) {
            return;
        }
        mode = Mode.EDIT_ENTITY;
        targetEntityId = entityId;
        draftFill = style.fill;
        draftSize = style.size;
        draftZ = layer.zIndex;
        originalFill = style.fill;
        originalSize = style.size;
        zSteps = 0;
        open = true;
    }

    /** Startet mit den bereits vorgemerkten Werten oder den Standardwerten der gewählten Form. */
    public void openPending(SpawnMode spawn) {
        Style style = spawn.getPendingStyle() != null
                ? spawn.getPendingStyle() : FormFactory.defaultStyle(spawn.getSelectedForm());
        mode = Mode.SPAWN_PENDING;
        targetEntityId = Selection.NONE;
        draftFill = style.fill;
        draftSize = style.size;
        open = true;
    }

    public void close() {
        open = false;
        hover = DialogHit.none();
    }

    public boolean isOpen() {
        return open;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isEditMode() {
        return mode == Mode.EDIT_ENTITY;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }

    public Color getDraftFill() {
        return draftFill;
    }

    public void setDraftFill(Color draftFill) {
        this.draftFill = draftFill;
    }

    public float getDraftSize() {
        return draftSize;
    }

    public void setDraftSize(float draftSize) {
        this.draftSize = draftSize;
    }

    public int getDraftZ() {
        return draftZ;
    }

    public void setDraftZ(int draftZ) {
        this.draftZ = draftZ;
    }

    public Color getOriginalFill() {
        return originalFill;
    }

    public float getOriginalSize() {
        return originalSize;
    }

    public int getZSteps() {
        return zSteps;
    }

    public void addZSteps(int delta) {
        zSteps += delta;
    }

    public DialogHit getHover() {
        return hover;
    }

    public void setHover(DialogHit hover) {
        this.hover = hover;
    }
}
