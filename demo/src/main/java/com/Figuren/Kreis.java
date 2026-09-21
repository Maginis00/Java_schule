package com.Figuren;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Ein Kreis, der manipuliert werden kann und sich selbst auf einer Leinwand
 * zeichnet.
 *
 * @author Michael Kölling und David J. Barnes
 * @version 2016.02.29
 */
public class Kreis extends Form {

    private int durchmesser;

    /**
     * Erzeuge einen neuen Kreis an einer Standardposition mit einer
     * Standardfarbe.
     */
    public Kreis() {
        super(230, 90, "blau");
        durchmesser = 68;
    }

    /**
     * Ändere den Durchmesser dieses Kreises in 'neuerDurchmesser' (Angabe in
     * Bildschirmpunkten). 'neuerDurchmesser' muss größer gleich null sein.
     */
    public void groesseAendern(int neuerDurchmesser) {
        loeschen();
        durchmesser = neuerDurchmesser;
        zeichnen();
    }

    @Override
    protected Shape erzeugeShape() {
        return new Ellipse2D.Double(getXPosition(), getYPosition(), durchmesser, durchmesser);
    }
}
