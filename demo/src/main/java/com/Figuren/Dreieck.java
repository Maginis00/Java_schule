package com.figuren;

import java.awt.Polygon;
import java.awt.Shape;

/**
 * Ein Dreieck, das manipuliert werden kann und sich selbst auf einer Leinwand
 * zeichnet.
 *
 * @author Michael Kölling und David J. Barnes
 * @version 2016.02.29
 */
public class Dreieck extends Form {

    private int hoehe;
    private int breite;

    /**
     * Erzeuge ein Dreieck mit einer Standardfarbe an einer Standardposition.
     */
    public Dreieck() {
        super(210, 140, "gruen");
        hoehe = 60;
        breite = 70;
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
    protected Shape erzeugeShape() {
        int x = getXPosition();
        int y = getYPosition();
        int[] xpoints = { x, x + (breite / 2), x - (breite / 2) };
        int[] ypoints = { y, y + hoehe, y + hoehe };
        return new Polygon(xpoints, ypoints, 3);
    }
}
