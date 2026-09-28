package com.example.component;

/** Geschwindigkeit in Pixel pro Sekunde. */
public class Velocity implements Component {
    public double vx;
    public double vy;

    public Velocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }
}
