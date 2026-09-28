package com.example.component;

/** Position (obere linke Ecke) in Pixeln. Öffentliche Felder: reine Datenhülle, keine Logik. */
public class Transform implements Component {
    public double x;
    public double y;

    public Transform(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
