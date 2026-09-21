package com.figuren;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Ein Quadrat, das manipuliert werden kann und sich selbst auf einer Leinwand
 * zeichnet.
 *
 * @author Michael Kölling und David J. Barnes
 * @version 2016.02.29
 */
public class Quadrat extends Form {

    private int groesse;

    /**
     * Erzeuge ein neues Quadrat mit einer Standardfarbe an einer
     * Standardposition.
     */
    public Quadrat() {
        super(310, 120, "rot");
        groesse = 60;
    }

    /**
     * Ändere die Größe dieses Quadrates in 'neueGroesse'. 'neueGroesse' muss
     * groesser gleich null sein.
     */
    public void groesseAendern(int neueGroesse) {
        loeschen();
        groesse = neueGroesse;
        zeichnen();
    }

    @Override
    protected Shape erzeugeShape() {
        return new Rectangle(getXPosition(), getYPosition(), groesse, groesse);
    }
}
