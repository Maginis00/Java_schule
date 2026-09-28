package com.example.component;

import java.awt.Color;
import java.awt.Polygon;

/** Daten eines Knopfes, der selbst ein gefülltes Polygon ist. Koordinaten = Canvas-Pixel. */
public class UiButton implements Component {
    public final String name;
    public final Polygon polygon;
    public final Color normalColor;
    public final Color hoverColor;
    public final Color activeColor;

    public boolean hovered; // Maus liegt im Polygon
    public boolean armed;   // Maus liegt im Polygon und Linkstaste ist gedrückt
    public boolean active;  // Spawn-Modus an, Knopf leuchtet
    public double glow;     // 0..1, vom ButtonVisualSystem berechnet (Pulsieren)

    public UiButton(String name, Polygon polygon, Color normalColor, Color hoverColor, Color activeColor) {
        this.name = name;
        this.polygon = polygon;
        this.normalColor = normalColor;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
    }
}
