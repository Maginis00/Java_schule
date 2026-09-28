package com.example.resource;

/**
 * Resource (keine Component): globaler Werkzeugzustand "Figuren per Klick platzieren".
 * Der Modus bleibt an, bis er per Rechtsklick/ESC abgebrochen wird.
 */
public class SpawnMode {
    public static final String FIGUR_DREIECK = "dreieck";

    private boolean enabled;
    private String figurTyp = FIGUR_DREIECK;
    private double glowTime; // Sekunden seit dem Einschalten, Basis für das Pulsieren des Knopfs

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (enabled && !this.enabled) {
            glowTime = 0; // Leuchten beginnt bei jedem Einschalten neu
        }
        this.enabled = enabled;
    }

    public String getFigurTyp() {
        return figurTyp;
    }

    public double getGlowTime() {
        return glowTime;
    }

    public void addGlowTime(double dt) {
        glowTime += dt;
    }
}
