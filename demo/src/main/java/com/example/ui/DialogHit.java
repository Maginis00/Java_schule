package com.example.ui;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * Layout des Eigenschaften-Dialogs an EINER Stelle: Bounds aller Widgets plus Hit-Test.
 * Input- und Render-System benutzen dieselben Rechtecke, damit Klickzone und Zeichnung nie
 * auseinanderlaufen. Feste Position (oben Mitte), kein Layout-Manager.
 */
public final class DialogHit {
    public enum Target { OUTSIDE, PANEL, CLOSE, SWATCH, SIZE_MINUS, SIZE_PLUS, Z_FORWARD, Z_BACKWARD, APPLY, CANCEL }

    private static final DialogHit NONE = new DialogHit(Target.OUTSIDE, -1);

    // Panel: zentriert, y liegt unterhalb der Menüleiste ("Form>" oben links)
    public static final Rectangle BOUNDS = new Rectangle(230, 50, 340, 292);
    public static final int TITLE_HEIGHT = 30;
    public static final int LABEL_X = 16;   // Beschriftungen, relativ zum Panel
    public static final int VALUE_X = 80;   // Werte/Widgets, relativ zum Panel

    public static final Color[] SWATCH_COLORS = {
        new Color(0x33, 0x88, 0xFF), new Color(0xE0, 0x48, 0x48), new Color(0xF2, 0xB1, 0x34),
        new Color(0x4C, 0xB9, 0x63), new Color(0x9B, 0x5D, 0xE5), new Color(0xF1, 0x5B, 0xB5),
        new Color(0x00, 0xBB, 0xF9), new Color(0xEE, 0xEE, 0xEE)
    };

    private static final int SWATCH_SIZE = 26;
    private static final int SWATCH_GAP = 4;
    private static final int STEP_BUTTON_SIZE = 28;
    private static final int SIZE_VALUE_WIDTH = 70;

    public final Target target;
    /** Nur bei SWATCH: Index in SWATCH_COLORS, sonst -1. */
    public final int index;

    private DialogHit(Target target, int index) {
        this.target = target;
        this.index = index;
    }

    public static DialogHit none() {
        return NONE;
    }

    public boolean is(Target other) {
        return target == other;
    }

    public boolean isSwatch(int i) {
        return target == Target.SWATCH && index == i;
    }

    // --- Widget-Bounds (Canvas-Koordinaten) ---

    public static Rectangle closeButton() {
        return new Rectangle(BOUNDS.x + BOUNDS.width - 26, BOUNDS.y + 4, 22, 22);
    }

    public static Rectangle formRow() {
        return row(40, 24);
    }

    public static Rectangle swatch(int i) {
        return new Rectangle(BOUNDS.x + VALUE_X + i * (SWATCH_SIZE + SWATCH_GAP), BOUNDS.y + 72, SWATCH_SIZE, SWATCH_SIZE);
    }

    public static Rectangle hexRow() {
        return row(104, 20);
    }

    public static Rectangle sizeMinus() {
        return new Rectangle(BOUNDS.x + VALUE_X, BOUNDS.y + 132, STEP_BUTTON_SIZE, STEP_BUTTON_SIZE);
    }

    public static Rectangle sizeValue() {
        return new Rectangle(sizeMinus().x + STEP_BUTTON_SIZE, BOUNDS.y + 132, SIZE_VALUE_WIDTH, STEP_BUTTON_SIZE);
    }

    public static Rectangle sizePlus() {
        return new Rectangle(sizeValue().x + SIZE_VALUE_WIDTH, BOUNDS.y + 132, STEP_BUTTON_SIZE, STEP_BUTTON_SIZE);
    }

    public static Rectangle zRow() {
        return row(170, 20);
    }

    public static Rectangle zForward() {
        return new Rectangle(BOUNDS.x + VALUE_X, BOUNDS.y + 194, 100, 26);
    }

    public static Rectangle zBackward() {
        return new Rectangle(BOUNDS.x + VALUE_X + 108, BOUNDS.y + 194, 100, 26);
    }

    public static Rectangle applyButton() {
        return new Rectangle(BOUNDS.x + 16, BOUNDS.y + 236, 150, 32);
    }

    public static Rectangle cancelButton() {
        return new Rectangle(BOUNDS.x + BOUNDS.width - 16 - 150, BOUNDS.y + 236, 150, 32);
    }

    public static Rectangle hintRow() {
        return row(272, 16);
    }

    private static Rectangle row(int yOffset, int height) {
        return new Rectangle(BOUNDS.x, BOUNDS.y + yOffset, BOUNDS.width, height);
    }

    /** Hit-Test: welches Widget liegt unter dem Punkt? Immer Rechtecke, es gibt keine Formen-Buttons. */
    public static DialogHit at(int x, int y) {
        if (!BOUNDS.contains(x, y)) {
            return NONE;
        }
        if (closeButton().contains(x, y)) return new DialogHit(Target.CLOSE, -1);
        for (int i = 0; i < SWATCH_COLORS.length; i++) {
            if (swatch(i).contains(x, y)) return new DialogHit(Target.SWATCH, i);
        }
        if (sizeMinus().contains(x, y)) return new DialogHit(Target.SIZE_MINUS, -1);
        if (sizePlus().contains(x, y)) return new DialogHit(Target.SIZE_PLUS, -1);
        if (zForward().contains(x, y)) return new DialogHit(Target.Z_FORWARD, -1);
        if (zBackward().contains(x, y)) return new DialogHit(Target.Z_BACKWARD, -1);
        if (applyButton().contains(x, y)) return new DialogHit(Target.APPLY, -1);
        if (cancelButton().contains(x, y)) return new DialogHit(Target.CANCEL, -1);
        return new DialogHit(Target.PANEL, -1);
    }
}
