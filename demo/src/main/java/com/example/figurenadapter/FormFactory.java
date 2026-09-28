package com.example.figurenadapter;

import com.Figuren.Dreieck;
import com.Figuren.Form;
import com.Figuren.Kreis;
import com.Figuren.Person;
import com.Figuren.Quadrat;
import com.Figuren.Rechteck;
import com.example.component.Transform;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Einzige Stelle, die com.Figuren.* kennt: bildet eine formId ("dreieck", ...) auf die
 * passende Figur-Klasse ab und macht aus einer Figur eine Shape an einer Bildschirmposition.
 * Alle Figuren teilen die Basisklasse Form; es gibt keinen zweiten Typbaum.
 * Neue Figur = eine register()-Zeile.
 */
public final class FormFactory {
    private static final class Entry {
        final String label;
        final Supplier<Form> supplier;

        Entry(String label, Supplier<Form> supplier) {
            this.label = label;
            this.supplier = supplier;
        }
    }

    // LinkedHashMap: die Reihenfolge hier ist die Reihenfolge im Menü
    private static final Map<String, Entry> ENTRIES = new LinkedHashMap<>();

    static {
        register("dreieck", "Dreieck", Dreieck::new);
        register("rechteck", "Rechteck", Rechteck::new);
        register("quadrat", "Quadrat", Quadrat::new);
        register("kreis", "Kreis", Kreis::new);
        register("person", "Person", Person::new);
    }

    private FormFactory() {
    }

    private static void register(String formId, String label, Supplier<Form> supplier) {
        ENTRIES.put(formId, new Entry(label, supplier));
    }

    /** Alle verfügbaren formIds in Menü-Reihenfolge. */
    public static List<String> formIds() {
        return Collections.unmodifiableList(new ArrayList<>(ENTRIES.keySet()));
    }

    public static String label(String formId) {
        return entry(formId).label;
    }

    /** Erzeugt eine neue Figur-Instanz (Standardgröße) zur formId. */
    public static Form create(String formId) {
        return entry(formId).supplier.get();
    }

    /**
     * Shape der Figur, mit ihrem Mittelpunkt auf dem Transform. Die Figuren tragen eine eigene
     * Standardposition und haben keinen öffentlichen Setter; deshalb wird die Shape aus
     * erzeugeShape() nur verschoben, die Figur bleibt unverändert. Menü-Preview und
     * Welt-Zeichnung nutzen genau diese Methode.
     */
    public static Shape toShape(Form figur, Transform t) {
        Shape local = figur.erzeugeShape();
        Rectangle2D bounds = local.getBounds2D();
        AffineTransform move = AffineTransform.getTranslateInstance(
                t.x - bounds.getCenterX(), t.y - bounds.getCenterY());
        return move.createTransformedShape(local);
    }

    private static Entry entry(String formId) {
        Entry entry = ENTRIES.get(formId);
        if (entry == null) {
            throw new IllegalArgumentException("Unbekannte Form: " + formId);
        }
        return entry;
    }
}
