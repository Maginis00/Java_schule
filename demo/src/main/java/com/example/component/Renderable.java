package com.example.component;

import java.awt.Color;

/** Darstellung als gefülltes Rechteck. */
public class Renderable implements Component {
    public double width;
    public double height;
    public Color color;

    public Renderable(double width, double height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
    }
}
