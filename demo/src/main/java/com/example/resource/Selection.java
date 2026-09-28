package com.example.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resource: genau eine Primär-Selektion plus der Treffer-Stapel des letzten Klicks (oben zuerst). */
public class Selection {
    public static final int NONE = -1;

    private int selectedId = NONE;
    private List<Integer> lastHitStack = new ArrayList<>();
    private int lastHitIndex;
    private int lastMouseX;
    private int lastMouseY;

    public boolean hasSelection() {
        return selectedId != NONE;
    }

    public int getSelectedId() {
        return selectedId;
    }

    /** Selektion ohne Klick (z. B. Tastatur): der alte Treffer-Stapel wäre veraltet. */
    public void select(int entityId) {
        selectedId = entityId;
        clearHits();
    }

    public void clear() {
        select(NONE);
    }

    /** Selektion durch einen Klick: merkt sich Stapel, Position darin und Klickpunkt. */
    public void pick(int entityId, List<Integer> hitStack, int hitIndex, int mouseX, int mouseY) {
        selectedId = entityId;
        lastHitStack = new ArrayList<>(hitStack);
        lastHitIndex = hitIndex;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    /** Stapel verwerfen, wenn sich Ebenen oder Figuren geändert haben. */
    public void clearHits() {
        lastHitStack = new ArrayList<>();
        lastHitIndex = 0;
    }

    public List<Integer> getLastHitStack() {
        return Collections.unmodifiableList(lastHitStack);
    }

    public int getLastHitIndex() {
        return lastHitIndex;
    }

    public int getLastMouseX() {
        return lastMouseX;
    }

    public int getLastMouseY() {
        return lastMouseY;
    }
}
