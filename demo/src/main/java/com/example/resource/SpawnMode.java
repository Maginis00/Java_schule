package com.example.resource;

import com.example.component.Style;

/**
 * Resource (keine Component): globaler Werkzeugzustand "Figuren per Klick platzieren".
 * Der Modus bleibt an, bis er per Rechtsklick/ESC abgebrochen wird.
 */
public class SpawnMode {
    private boolean enabled;
    private String selectedForm; // formId aus der FormFactory, z. B. "dreieck"
    private Style pendingStyle;  // Eigenschaften für die NÄCHSTE Figur; null = Standardwerte

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            pendingStyle = null; // Ende der Werkzeug-Sitzung, eingestellte Werte verfallen
        }
    }

    public String getSelectedForm() {
        return selectedForm;
    }

    public void setSelectedForm(String selectedForm) {
        this.selectedForm = selectedForm;
    }

    public Style getPendingStyle() {
        return pendingStyle;
    }

    public void setPendingStyle(Style pendingStyle) {
        this.pendingStyle = pendingStyle;
    }

    /** Liefert die vorgemerkten Eigenschaften genau einmal ab; danach gelten wieder die Standardwerte. */
    public Style takePendingStyle() {
        Style style = pendingStyle;
        pendingStyle = null;
        return style;
    }
}
