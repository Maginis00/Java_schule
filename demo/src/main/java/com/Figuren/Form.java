package com.figuren;

import java.awt.Shape;

import com.figuren.leinwand.Leinwand;

/**
 * Gemeinsame Basis aller Figuren: Position, Farbe, Sichtbarkeit und Bewegung.
 * Die konkrete Geometrie bleibt in den Unterklassen.
 *
 * @author Michael Kölling und David J. Barnes
 * @version 2016.02.29 (refaktoriert)
 */
public abstract class Form {

    private int xPosition;
    private int yPosition;
    private String farbe;
    private boolean istSichtbar;

    protected Form(int xPosition, int yPosition, String farbe) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.farbe = farbe;
        this.istSichtbar = false;
    }

    /**
     * Mache diese Figur sichtbar. Wenn sie bereits sichtbar ist, tue nichts.
     */
    public void sichtbarMachen() {
        istSichtbar = true;
        zeichnen();
    }

    /**
     * Mache diese Figur unsichtbar. Wenn sie bereits unsichtbar ist, tue nichts.
     */
    public void unsichtbarMachen() {
        loeschen();
        istSichtbar = false;
    }

    /**
     * Bewege diese Figur einige Bildschirmpunkte nach rechts.
     */
    public void nachRechtsBewegen() {
        horizontalBewegen(20);
    }

    /**
     * Bewege diese Figur einige Bildschirmpunkte nach links.
     */
    public void nachLinksBewegen() {
        horizontalBewegen(-20);
    }

    /**
     * Bewege diese Figur einige Bildschirmpunkte nach oben.
     */
    public void nachObenBewegen() {
        vertikalBewegen(-20);
    }

    /**
     * Bewege diese Figur einige Bildschirmpunkte nach unten.
     */
    public void nachUntenBewegen() {
        vertikalBewegen(20);
    }

    /**
     * Bewege diese Figur horizontal um 'entfernung' Bildschirmpunkte.
     */
    public void horizontalBewegen(int entfernung) {
        loeschen();
        xPosition += entfernung;
        zeichnen();
    }

    /**
     * Bewege diese Figur vertikal um 'entfernung' Bildschirmpunkte.
     */
    public void vertikalBewegen(int entfernung) {
        loeschen();
        yPosition += entfernung;
        zeichnen();
    }

    /**
     * Bewege diese Figur langsam horizontal um 'entfernung' Bildschirmpunkte.
     */
    public void langsamHorizontalBewegen(int entfernung) {
        int delta = bewegungsDelta(entfernung);
        int schritte = Math.abs(entfernung);
        for (int i = 0; i < schritte; i++) {
            xPosition += delta;
            zeichnen();
        }
    }

    /**
     * Bewege diese Figur langsam vertikal um 'entfernung' Bildschirmpunkte.
     */
    public void langsamVertikalBewegen(int entfernung) {
        int delta = bewegungsDelta(entfernung);
        int schritte = Math.abs(entfernung);
        for (int i = 0; i < schritte; i++) {
            yPosition += delta;
            zeichnen();
        }
    }

    /**
     * Ändere die Farbe dieser Figur in 'neueFarbe'. Gültige Angaben sind
     * "rot", "gelb", "blau", "gruen", "lila" und "schwarz".
     */
    public void farbeAendern(String neueFarbe) {
        farbe = neueFarbe;
        zeichnen();
    }

    /**
     * Zeichne diese Figur mit ihren aktuellen Werten auf den Bildschirm.
     */
    protected void zeichnen() {
        if (istSichtbar) {
            Leinwand leinwand = Leinwand.gibLeinwand();
            leinwand.zeichne(this, farbe, erzeugeShape());
            leinwand.warte(10);
        }
    }

    /**
     * Lösche diese Figur vom Bildschirm.
     */
    protected void loeschen() {
        if (istSichtbar) {
            Leinwand leinwand = Leinwand.gibLeinwand();
            leinwand.entferne(this);
        }
    }

    /**
     * Liefert die konkrete AWT-Geometrie dieser Figur.
     */
    protected abstract Shape erzeugeShape();

    protected int getXPosition() {
        return xPosition;
    }

    protected int getYPosition() {
        return yPosition;
    }

    private static int bewegungsDelta(int entfernung) {
        return entfernung < 0 ? -1 : 1;
    }
}
