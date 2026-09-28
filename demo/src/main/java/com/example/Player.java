package com.example;

import java.awt.Rectangle;

/** Der Spieler: Position, Größe und Bewegung in Pixel pro Sekunde. */
class Player {
    /** Geschwindigkeit in Pixel pro Sekunde (nicht pro Frame). */
    static final double PLAYER_SPEED = 300.0;
    static final int SIZE = 40;

    private double x;
    private double y;
    private double dirX; // -1..1
    private double dirY;

    Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** Wird von handleInput() gesetzt; die Bewegung selbst passiert erst in update(). */
    void setDirection(int dx, int dy) {
        double len = Math.sqrt(dx * dx + dy * dy);
        // Diagonale normalisieren, damit sie nicht schneller ist
        dirX = len == 0 ? 0 : dx / len;
        dirY = len == 0 ? 0 : dy / len;
    }

    /** Bewegt den Spieler um PLAYER_SPEED * dt, begrenzt auf die Welt und blockiert durch das Hindernis. */
    void update(double dt, int worldWidth, int worldHeight, Rectangle obstacle) {
        // Achsen getrennt bewegen, so gleitet der Spieler an der Box entlang
        x += dirX * PLAYER_SPEED * dt;
        x = Math.max(0, Math.min(x, worldWidth - SIZE));
        if (getBounds().intersects(obstacle)) {
            x = dirX > 0 ? obstacle.x - SIZE : obstacle.x + obstacle.width;
        }

        y += dirY * PLAYER_SPEED * dt;
        y = Math.max(0, Math.min(y, worldHeight - SIZE));
        if (getBounds().intersects(obstacle)) {
            y = dirY > 0 ? obstacle.y - SIZE : obstacle.y + obstacle.height;
        }
    }

    Rectangle getBounds() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), SIZE, SIZE);
    }
}
