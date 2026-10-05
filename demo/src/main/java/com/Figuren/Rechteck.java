package com.Figuren;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * FALLBACK: Diese Klasse gehörte nicht zu den vorhandenen Figuren und wurde für das
 * Form-Menü ergänzt. Sie folgt dem Muster von Dreieck/Quadrat (Basis Form, erzeugeShape()).
 * Kann durch eine "echte" Rechteck-Klasse ersetzt werden, solange der Name gleich bleibt.
 */
public class Rechteck extends Form {

    private int hoehe;
    private int breite;

    /**
     * Erzeuge ein Rechteck mit einer Standardfarbe an einer Standardposition.
     */
    public Rechteck() {
        super(310, 200, "gelb");
        hoehe = 50;
        breite = 90;
    }

    /**
     * Ändere die Höhe in 'neueHoehe' und die Breite in 'neueBreite'. Beide
     * Angaben müssen größer gleich null sein.
     */
    public void groesseAendern(int neueHoehe, int neueBreite) {
        loeschen();
        hoehe = neueHoehe;
        breite = neueBreite;
        zeichnen();
    }

    @Override
    public Shape erzeugeShape() {
        return new Rectangle(getXPosition(), getYPosition(), breite, hoehe);
    }
}
