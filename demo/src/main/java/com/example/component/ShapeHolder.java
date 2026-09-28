package com.example.component;

import com.Figuren.Form;

import java.awt.Color;

/** Verweist auf eine Figur aus com.Figuren (z. B. Dreieck), die per erzeugeShape() gezeichnet wird. */
public class ShapeHolder implements Component {
    public Form figur;
    public Color color;

    public ShapeHolder(Form figur, Color color) {
        this.figur = figur;
        this.color = color;
    }
}
