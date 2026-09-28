package com.example.input;

import java.awt.event.MouseEvent;

/**
 * Resource: Mauszustand in Canvas-Koordinaten.
 * Die AWT-Listener (Event-Thread) schreiben, die Systems (Loop-Thread) lesen;
 * deshalb sind alle Zugriffe synchronized.
 *
 * Klicks sind "gelatcht": sie bleiben gesetzt, bis ein System sie konsumiert
 * oder endFrame() am Ende des InputSets sie löscht. So geht ein Klick auch dann
 * nicht verloren, wenn in einem Render-Frame mal kein Update-Schritt läuft.
 */
public class MouseState {
    private int x;
    private int y;
    private boolean leftPressed;
    private boolean leftClickedThisFrame;
    private boolean rightClickedThisFrame;
    private boolean insideWindow;

    // --- Schreibseite (AWT-Listener) ---

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void setInsideWindow(boolean insideWindow) {
        this.insideWindow = insideWindow;
    }

    public synchronized void press(int button) {
        if (button == MouseEvent.BUTTON1) {
            leftPressed = true;
            leftClickedThisFrame = true;
        } else if (button == MouseEvent.BUTTON3) {
            rightClickedThisFrame = true;
        }
    }

    public synchronized void release(int button) {
        if (button == MouseEvent.BUTTON1) {
            leftPressed = false;
        }
    }

    /** Bei Fokusverlust: nichts darf "hängen" bleiben. */
    public synchronized void clear() {
        leftPressed = false;
        leftClickedThisFrame = false;
        rightClickedThisFrame = false;
    }

    // --- Leseseite (Systems) ---

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized boolean isLeftPressed() {
        return leftPressed;
    }

    public synchronized boolean isLeftClickedThisFrame() {
        return leftClickedThisFrame;
    }

    public synchronized boolean isRightClickedThisFrame() {
        return rightClickedThisFrame;
    }

    public synchronized boolean isInsideWindow() {
        return insideWindow;
    }

    /** Markiert den Linksklick als verbraucht; spätere Systems sehen ihn nicht mehr. */
    public synchronized void consumeLeftClick() {
        leftClickedThisFrame = false;
    }

    public synchronized void consumeRightClick() {
        rightClickedThisFrame = false;
    }

    /** Am Ende des InputSets: alle noch offenen Klicks verwerfen. */
    public synchronized void endFrame() {
        leftClickedThisFrame = false;
        rightClickedThisFrame = false;
    }
}
