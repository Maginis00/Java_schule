package com.example.ecs;

/**
 * Ein System enthält Logik und arbeitet ausschließlich über World-Queries.
 * Achtung: verdeckt innerhalb dieses Packages java.lang.System.
 */
public interface System {
    /** Im Input-/Update-Pfad ist dt die feste Schrittweite, im Render-Pfad der Interpolationsfaktor. */
    void update(World world, double dt);
}
