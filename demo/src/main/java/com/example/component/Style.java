package com.example.component;

import java.awt.Color;

/**
 * Darstellung einer Figur: Farben, Kontur und Größe. Die Figur-Klassen bleiben unberührt;
 * Farbe und Größe werden erst beim Zeichnen im Adapter (FormFactory.toShape) angewendet.
 */
public class Style implements Component {
    public static final Color DEFAULT_FILL = new Color(0x33, 0x88, 0xFF);
    public static final Color DEFAULT_STROKE = new Color(20, 30, 50);
    public static final float DEFAULT_STROKE_WIDTH = 1.5f;
    public static final float MIN_SIZE = 10f;
    public static final float MAX_SIZE = 300f;

    public Color fill;
    public Color stroke;
    public float strokeWidth;
    /** Zielgröße in Pixel: längste Seite der Figur-Bounding-Box. */
    public float size;

    public Style(Color fill, Color stroke, float strokeWidth, float size) {
        this.fill = fill;
        this.stroke = stroke;
        this.strokeWidth = strokeWidth;
        this.size = size;
    }

    /** Standardstil mit der übergebenen Größe. */
    public static Style defaults(float size) {
        return new Style(DEFAULT_FILL, DEFAULT_STROKE, DEFAULT_STROKE_WIDTH, size);
    }

    public Style copy() {
        return new Style(fill, stroke, strokeWidth, size);
    }

    public static float clampSize(float size) {
        return Math.max(MIN_SIZE, Math.min(MAX_SIZE, size));
    }
}
