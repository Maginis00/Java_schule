package com.Figuren;

import java.awt.Polygon;
import java.awt.Shape;

/**
 * Eine Person, die manipuliert werden kann und sich selbst auf einer Leinwand
 * zeichnet. Sie ist keine geometrische Grundform, teilt aber Sichtbarkeit,
 * Farbe und Bewegung mit den übrigen Figuren — daher dieselbe Basisklasse.
 *
 * @author Michael Kölling und David J. Barnes
 * @version 2016.02.29
 */
public class Person extends Form {

    private int hoehe;
    private int breite;

    /**
     * Erzeuge eine Person mit einer Standardfarbe an einer Standardposition.
     */
    public Person() {
        super(280, 190, "schwarz");
        hoehe = 60;
        breite = 30;
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
        int bh = (int) (hoehe * 0.7); // Körpergröße
        int hh = (hoehe - bh) / 2; // halbe Kopfgröße
        int hw = breite / 2; // halbe Breite
        int x = getXPosition();
        int y = getYPosition();

        int[] xpunkte = {
                x - 3, x - hw, x - hw, x - (int) (hw * 0.2) - 1, x - (int) (hw * 0.2) - 1, x - hw,
                x - hw + (int) (hw * 0.4) + 1, x, x + hw - (int) (hw * 0.4) - 1, x + hw, x + (int) (hw * 0.2) + 1,
                x + (int) (hw * 0.2) + 1, x + hw, x + hw, x + 3, x + (int) (hw * 0.6),
                x + (int) (hw * 0.6), x + 3, x - 3, x - (int) (hw * 0.6), x - (int) (hw * 0.6)
        };
        int[] ypunkte = {
                y, y + (int) (bh * 0.2), y + (int) (bh * 0.4), y + (int) (bh * 0.2),
                y + (int) (bh * 0.5), y + bh, y + bh, y + (int) (bh * 0.65), y + bh, y + bh,
                y + (int) (bh * 0.5), y + (int) (bh * 0.2), y + (int) (bh * 0.4), y + (int) (bh * 0.2),
                y, y - hh + 3, y - hh - 3, y - hh - hh, y - hh - hh, y - hh - 3, y - hh + 3
        };
        return new Polygon(xpunkte, ypunkte, 21);
    }
}
