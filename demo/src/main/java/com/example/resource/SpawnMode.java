package com.example.resource;

/**
 * Resource (keine Component): globaler Werkzeugzustand "Figuren per Klick platzieren".
 * Der Modus bleibt an, bis er per Rechtsklick/ESC abgebrochen wird.
 */
public class SpawnMode {
    private boolean enabled;
    private String selectedForm; // formId aus der FormFactory, z. B. "dreieck"

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSelectedForm() {
        return selectedForm;
    }

    public void setSelectedForm(String selectedForm) {
        this.selectedForm = selectedForm;
    }
}
