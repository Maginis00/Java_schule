package com.example.component;

/**
 * Marker-Component ohne Daten: die Figur existiert, ist aber gerade unsichtbar (bei der Wiedergabe
 * eine Figur, die zu diesem Zeitpunkt der Aufnahme noch nicht erzeugt war). ZOrder lässt sie aus,
 * damit Zeichnen, Picking und Overlay sie gar nicht erst sehen.
 */
public class Hidden implements Component {
}
