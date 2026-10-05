package com.example.resource;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource: die Figur, die gerade mit der Maus gezogen wird. Das WorldPickSystem beginnt den Drag
 * beim Klick, das DragSystem bewegt die Figur, solange die linke Taste gehalten wird.
 *
 * Ein Klick auf die schon selektierte Figur an derselben Stelle würde normalerweise eine Ebene
 * weiterblättern. Das passiert erst beim Loslassen ohne Bewegung ("pending cycle"), sonst würde
 * man beim Ziehen überlappender Figuren die darunterliegende erwischen.
 */
public class DragState {
    private int draggedId = Selection.NONE;
    private double offsetX;
    private double offsetY;
    private int startX;
    private int startY;
    private boolean moved;

    private int cycleTargetId = Selection.NONE;
    private List<Integer> cycleHits = new ArrayList<>();
    private int cycleIndex;

    /** Offset = Mausposition minus Transform, damit die Figur beim Greifen nicht springt. */
    public void begin(int entityId, double offsetX, double offsetY, int startX, int startY) {
        draggedId = entityId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.startX = startX;
        this.startY = startY;
        moved = false;
        cycleTargetId = Selection.NONE;
        cycleHits = new ArrayList<>();
        cycleIndex = 0;
    }

    /** Merkt sich den Ebenen-Wechsel, der beim Loslassen ohne Bewegung ausgeführt wird. */
    public void setPendingCycle(int targetId, List<Integer> hits, int index) {
        cycleTargetId = targetId;
        cycleHits = new ArrayList<>(hits);
        cycleIndex = index;
    }

    public void end() {
        draggedId = Selection.NONE;
        cycleTargetId = Selection.NONE;
        cycleHits = new ArrayList<>();
    }

    public boolean isActive() {
        return draggedId != Selection.NONE;
    }

    public int getDraggedId() {
        return draggedId;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean hasPendingCycle() {
        return cycleTargetId != Selection.NONE;
    }

    public int getCycleTargetId() {
        return cycleTargetId;
    }

    public List<Integer> getCycleHits() {
        return cycleHits;
    }

    public int getCycleIndex() {
        return cycleIndex;
    }
}
