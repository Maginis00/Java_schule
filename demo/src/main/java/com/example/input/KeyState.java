package com.example.input;

import java.util.Arrays;

/** Resource: gedrückte Tasten. Wird vom Canvas-Listener geschrieben, von den Input-Systems gelesen. */
public class KeyState {
    private final boolean[] keys = new boolean[256];

    public boolean isDown(int keyCode) {
        return keyCode >= 0 && keyCode < keys.length && keys[keyCode];
    }

    public void set(int keyCode, boolean down) {
        if (keyCode >= 0 && keyCode < keys.length) {
            keys[keyCode] = down;
        }
    }

    public void clear() {
        Arrays.fill(keys, false);
    }
}
