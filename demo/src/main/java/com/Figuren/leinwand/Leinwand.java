package com.Figuren.leinwand;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Leinwand ist eine Klasse, die einfache Zeichenoperationen auf einer
 * leinwandartigen Zeichenfläche ermöglicht. Sie ist eine vereinfachte Version
 * der Klasse Canvas (englisch für Leinwand) des JDK und wurde speziell für das
 * Projekt "Figuren" geschrieben.
 *
 * Die Leinwand kennt keine konkreten Figuren-Typen, nur beliebige Schlüssel
 * und die zugehörigen Shapes.
 *
 * @author Bruce Quig
 * @author Michael Kölling (mik)
 * @version 2016.02.29
 */
public class Leinwand {

    private static final Map<String, Color> FARBEN = Map.of(
            "rot", new Color(235, 25, 25),
            "schwarz", Color.black,
            "blau", new Color(30, 75, 220),
            "gelb", new Color(255, 230, 0),
            "gruen", new Color(80, 160, 60),
            "lila", Color.magenta,
            "weiss", Color.white,
            "orange", Color.ORANGE
    );

    private static Leinwand leinwandSingleton;

    /**
     * Fabrikmethode, die eine Referenz auf das einzige Exemplar dieser Klasse
     * zurückliefert. Wenn es von einer Klasse nur genau ein Exemplar gibt, wird
     * dieses als 'Singleton' bezeichnet.
     */
    public static Leinwand gibLeinwand() {
        if (leinwandSingleton == null) {
            leinwandSingleton = new Leinwand("BlueJ Figuren Demo", 500, 300, Color.white);
        }
        leinwandSingleton.setzeSichtbarkeit(true);
        return leinwandSingleton;
    }

    private final JFrame fenster;
    private final Zeichenflaeche zeichenflaeche;
    private Graphics2D graphic;
    private final Color hintergrundfarbe;
    private Image leinwandImage;
    private final List<Object> figuren;
    private final Map<Object, ShapeMitFarbe> figurZuShape;

    /**
     * Erzeuge eine Leinwand.
     *
     * @param titel      Titel, der im Rahmen der Leinwand angezeigt wird
     * @param breite     die gewünschte Breite der Leinwand
     * @param hoehe      die gewünschte Höhe der Leinwand
     * @param grundfarbe die Hintergrundfarbe der Leinwand
     */
    private Leinwand(String titel, int breite, int hoehe, Color grundfarbe) {
        fenster = new JFrame();
        zeichenflaeche = new Zeichenflaeche();
        fenster.setContentPane(zeichenflaeche);
        fenster.setTitle(titel);
        fenster.setLocation(30, 30);
        zeichenflaeche.setPreferredSize(new Dimension(breite, hoehe));
        hintergrundfarbe = grundfarbe;
        fenster.pack();
        figuren = new ArrayList<>();
        figurZuShape = new HashMap<>();
    }

    /**
     * Setze, ob diese Leinwand sichtbar sein soll oder nicht. Wenn die Leinwand
     * sichtbar gemacht wird, wird ihr Fenster in den Vordergrund geholt. Diese
     * Operation kann auch benutzt werden, um ein bereits sichtbares
     * Leinwandfenster in den Vordergrund (vor andere Fenster) zu holen.
     *
     * @param sichtbar true für sichtbar, false für nicht sichtbar.
     */
    public void setzeSichtbarkeit(boolean sichtbar) {
        if (graphic == null) {
            Dimension size = zeichenflaeche.getSize();
            leinwandImage = zeichenflaeche.createImage(size.width, size.height);
            graphic = (Graphics2D) leinwandImage.getGraphics();
            graphic.setColor(hintergrundfarbe);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        fenster.setVisible(sichtbar);
    }

    /**
     * Zeichne für das gegebene Figur-Objekt eine Java-Figur (einen Shape) auf
     * die Leinwand.
     *
     * @param figur das Figur-Objekt, für das ein Shape gezeichnet werden soll
     * @param farbe die Farbe der Figur
     * @param shape ein Objekt der Klasse Shape, das tatsächlich gezeichnet wird
     */
    public void zeichne(Object figur, String farbe, Shape shape) {
        figuren.remove(figur);
        figuren.add(figur);
        figurZuShape.put(figur, new ShapeMitFarbe(shape, farbe));
        erneutZeichnen();
    }

    /**
     * Entferne die gegebene Figur von der Leinwand.
     *
     * @param figur die Figur, deren Shape entfernt werden soll
     */
    public void entferne(Object figur) {
        figuren.remove(figur);
        figurZuShape.remove(figur);
        erneutZeichnen();
    }

    /**
     * Setze die Zeichenfarbe der Leinwand.
     *
     * @param farbname der Name der neuen Zeichenfarbe.
     */
    public void setzeZeichenfarbe(String farbname) {
        graphic.setColor(FARBEN.getOrDefault(farbname, Color.black));
    }

    /**
     * Warte für die angegebenen Millisekunden. Mit dieser Operation wird eine
     * Verzögerung definiert, die für animierte Zeichnungen benutzt werden kann.
     *
     * @param millisekunden die zu wartenden Millisekunden
     */
    public void warte(int millisekunden) {
        try {
            Thread.sleep(millisekunden);
        } catch (Exception e) {
            // Exception ignorieren (wie bisher)
        }
    }

    /**
     * Zeichne erneut alle Figuren auf der Leinwand.
     */
    private void erneutZeichnen() {
        loeschen();
        // Lambda: durch alle Figuren zeichnen, ohne Typkenntnis (Kreis/Quadrat/…).
        figuren.forEach(figur -> figurZuShape.get(figur).draw(graphic));
        zeichenflaeche.repaint();
    }

    /**
     * Lösche die gesamte Leinwand.
     */
    private void loeschen() {
        Color original = graphic.getColor();
        graphic.setColor(hintergrundfarbe);
        Dimension size = zeichenflaeche.getSize();
        graphic.fill(new Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }

    /**
     * Interne Klasse Zeichenflaeche – GUI-Komponente im Leinwand-Fenster.
     */
    private class Zeichenflaeche extends JPanel {
        private static final long serialVersionUID = 20060330L;

        @Override
        public void paint(Graphics g) {
            g.drawImage(leinwandImage, 0, 0, null);
        }
    }

    /**
     * Interne Klasse ShapeMitFarbe – Shape des JDK plus Zeichenfarbe.
     */
    private class ShapeMitFarbe {
        private final Shape shape;
        private final String farbe;

        public ShapeMitFarbe(Shape shape, String farbe) {
            this.shape = shape;
            this.farbe = farbe;
        }

        public void draw(Graphics2D graphic) {
            setzeZeichenfarbe(farbe);
            graphic.fill(shape);
        }
    }
}
