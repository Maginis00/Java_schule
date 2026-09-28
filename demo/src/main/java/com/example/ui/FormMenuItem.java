package com.example.ui;

import java.awt.Rectangle;
import java.awt.Shape;

/** Ein Eintrag im Form-Menü. Unveränderlich: Layout wird einmal in der FormMenuFactory berechnet. */
public class FormMenuItem {
    public final String formId;
    public final String label;
    /** Auf Preview-Größe skalierte Shape, um (0,0) zentriert. */
    public final Shape previewShape;
    /** Klickbereich des Eintrags in Canvas-Koordinaten. */
    public final Rectangle bounds;

    public FormMenuItem(String formId, String label, Shape previewShape, Rectangle bounds) {
        this.formId = formId;
        this.label = label;
        this.previewShape = previewShape;
        this.bounds = bounds;
    }
}
