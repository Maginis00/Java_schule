package com.example.component;

import com.Figuren.Form;

/** Verweist auf eine Figur aus com.Figuren (z. B. Dreieck); Farbe und Größe stehen in Style. */
public class ShapeHolder implements Component {
    public final Form figur;
    public final String formId; // Schlüssel in der FormFactory, für Anzeige und Debug

    public ShapeHolder(Form figur, String formId) {
        this.figur = figur;
        this.formId = formId;
    }
}
