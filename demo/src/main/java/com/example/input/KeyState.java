package com.example.input;

import java.util.Arrays;

/**
 * Resource: Tastaturzustand. Der Canvas-Listener (Event-Thread) schreibt, die Systems
 * (Loop-Thread) lesen; deshalb sind alle Zugriffe synchronized.
 *
 * Zwei Sichten auf dieselbe Taste:
 * - isDown: solange die Taste gehalten wird (Bewegung, kontinuierliches Skalieren)
 * - isPressed/consumePressed: genau einmal pro Tastendruck (E, ESC, PageUp ...). Das Tasten-
 *   Wiederholen des Betriebssystems löst KEINEN neuen Druck aus, sonst würde ein gehaltenes
 *   ESC in einem Rutsch durch alle Abbruch-Stufen laufen.
 */
public class KeyState {
    private final boolean[] keys = new boolean[256];
    private final boolean[] pressed = new boolean[256];

    public synchronized boolean isDown(int keyCode) {
        return inRange(keyCode) && keys[keyCode];
    }

    public synchronized void set(int keyCode, boolean down) {
        if (!inRange(keyCode)) {
            return;
        }
        if (down && !keys[keyCode]) {
            pressed[keyCode] = true;
        }
        keys[keyCode] = down;
    }

    /** Wurde die Taste seit dem letzten Verbrauchen gedrückt? Verbraucht den Druck nicht. */
    public synchronized boolean isPressed(int keyCode) {
        return inRange(keyCode) && pressed[keyCode];
    }

    /** Wie isPressed, verbraucht den Druck aber, damit spätere Systems ihn nicht mehr sehen. */
    public synchronized boolean consumePressed(int keyCode) {
        if (!inRange(keyCode)) {
            return false;
        }
        boolean result = pressed[keyCode];
        pressed[keyCode] = false;
        return result;
    }

    /** Am Ende des InputSets: nicht verbrauchte Tastendrücke verwerfen. */
    public synchronized void endFrame() {
        Arrays.fill(pressed, false);
    }

    /** Bei Fokusverlust: nichts darf "hängen" bleiben. */
    public synchronized void clear() {
        Arrays.fill(keys, false);
        Arrays.fill(pressed, false);
    }

    private boolean inRange(int keyCode) {
        return keyCode >= 0 && keyCode < keys.length;
    }
}
