package com.example.system;

import com.example.ecs.System;
import com.example.ecs.World;
import com.example.input.KeyState;
import com.example.input.MouseState;
import com.example.recording.WorldSnapshot;
import com.example.resource.DragState;
import com.example.resource.PropertyDialog;
import com.example.resource.Recorder;
import com.example.resource.Selection;
import com.example.resource.UiMenuState;

import java.awt.Point;
import java.awt.event.KeyEvent;

/**
 * Läuft im InputSet direkt nach dem Dialog: Buttons "Aufnahme/Stopp" und "Abspielen/Stopp".
 * Während der Wiedergabe ist die Welt gesperrt: jeder Klick und ESC werden hier verbraucht,
 * nur der Stopp-Button und ESC beenden sie.
 */
public class UiRecorderInputSystem implements System {
    @Override
    public void update(World world, double dt) {
        Recorder recorder = world.getResource(Recorder.class);
        MouseState mouse = world.getResource(MouseState.class);
        KeyState keys = world.getResource(KeyState.class);

        // Dialog offen: modal, die Buttons reagieren auf nichts
        if (world.getResource(PropertyDialog.class).isOpen()) {
            recorder.setRecordHovered(false);
            recorder.setPlayHovered(false);
            return;
        }

        Point p = new Point(mouse.getX(), mouse.getY());
        if (recorder.isPlaying()) {
            boolean stop = keys.consumePressed(KeyEvent.VK_ESCAPE);
            if (mouse.isLeftClickedThisFrame()) {
                stop |= recorder.getPlayButtonBounds().contains(p);
                mouse.consumeLeftClick();
            }
            if (mouse.isRightClickedThisFrame()) {
                mouse.consumeRightClick();
            }
            if (stop) {
                recorder.stopPlayback().apply(world);
            }
        } else if (mouse.isLeftClickedThisFrame()) {
            handleLeftClick(world, recorder, mouse, p);
        }

        recorder.setRecordHovered(mouse.isInsideWindow() && !recorder.isPlaying()
                && recorder.getRecordButtonBounds().contains(p));
        recorder.setPlayHovered(mouse.isInsideWindow() && (recorder.isPlaying() || recorder.canPlay())
                && recorder.getPlayButtonBounds().contains(p));
    }

    private void handleLeftClick(World world, Recorder recorder, MouseState mouse, Point p) {
        if (recorder.getRecordButtonBounds().contains(p)) {
            if (recorder.isRecording()) {
                recorder.stopRecording();
            } else {
                recorder.startRecording();
            }
        } else if (recorder.getPlayButtonBounds().contains(p)) {
            // Während einer Aufnahme ist "Abspielen" ausgegraut; der Klick wird trotzdem verbraucht
            if (recorder.canPlay()) {
                startPlayback(world, recorder);
            }
        } else {
            return; // kein Treffer, der Klick bleibt für Menü und Welt stehen
        }
        world.getResource(UiMenuState.class).setFormMenuOpen(false);
        mouse.consumeLeftClick();
    }

    /** Live-Zustand sichern; Selektion und Drag würden zu den abgespielten Figuren nicht passen. */
    private void startPlayback(World world, Recorder recorder) {
        recorder.startPlayback(WorldSnapshot.capture(world));
        world.getResource(Selection.class).clear();
        world.getResource(DragState.class).end();
    }
}
