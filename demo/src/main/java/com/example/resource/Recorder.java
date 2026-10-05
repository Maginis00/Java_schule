package com.example.resource;

import com.example.recording.WorldSnapshot;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Resource: Aufnahme und Wiedergabe. Beim Aufnehmen landet pro Logik-Schritt ein WorldSnapshot
 * in der Liste, bei der Wiedergabe wird pro Schritt einer davon zurück in die Welt geschrieben.
 * So läuft die Wiedergabe genau im Tempo der Aufnahme. Vor dem Abspielen wird der Live-Zustand
 * gesichert und danach wiederhergestellt.
 */
public class Recorder {
    public enum State { IDLE, RECORDING, PLAYING }

    /** Obergrenze einer Aufnahme; danach stoppt sie von selbst. */
    public static final double MAX_SECONDS = 300;

    // Oben rechts neben dem "Einstellungen"-Button, gleiche Höhe wie der Menü-Anker
    private static final int BUTTON_Y = 10;
    private static final int RECORD_X = 330;
    private static final int PLAY_X = 450;
    private static final int BUTTON_WIDTH = 110;
    private static final int BUTTON_HEIGHT = 28;

    private final Rectangle recordButtonBounds = new Rectangle(RECORD_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);
    private final Rectangle playButtonBounds = new Rectangle(PLAY_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);

    private State state = State.IDLE;
    private List<WorldSnapshot> frames = new ArrayList<>();
    private double frameDt; // Dauer eines Frames in Sekunden (feste Schrittweite des Loops)
    private int playIndex;
    private WorldSnapshot liveSnapshot;

    private boolean recordHovered;
    private boolean playHovered;

    /** Startet eine neue Aufnahme; eine alte wird verworfen. */
    public void startRecording() {
        frames = new ArrayList<>();
        state = State.RECORDING;
    }

    public void stopRecording() {
        state = State.IDLE;
    }

    /** Hängt einen Frame an; bei Erreichen der Obergrenze endet die Aufnahme. */
    public void addFrame(WorldSnapshot frame, double dt) {
        frames.add(frame);
        frameDt = dt;
        if (getDurationSeconds() >= MAX_SECONDS) {
            stopRecording();
        }
    }

    /** live = Zustand der Welt direkt vor dem Abspielen, kommt bei stopPlayback() zurück. */
    public void startPlayback(WorldSnapshot live) {
        liveSnapshot = live;
        playIndex = 0;
        state = State.PLAYING;
    }

    /** Nächster Frame der Wiedergabe oder null, wenn die Aufnahme zu Ende ist. */
    public WorldSnapshot nextFrame() {
        return playIndex < frames.size() ? frames.get(playIndex++) : null;
    }

    /** Beendet die Wiedergabe und liefert den gesicherten Live-Zustand zum Wiederherstellen. */
    public WorldSnapshot stopPlayback() {
        WorldSnapshot live = liveSnapshot;
        liveSnapshot = null;
        state = State.IDLE;
        return live;
    }

    public boolean isRecording() {
        return state == State.RECORDING;
    }

    public boolean isPlaying() {
        return state == State.PLAYING;
    }

    public boolean hasRecording() {
        return !frames.isEmpty();
    }

    /** Abspielen geht nur mit fertiger Aufnahme. */
    public boolean canPlay() {
        return state == State.IDLE && hasRecording();
    }

    public double getDurationSeconds() {
        return frames.size() * frameDt;
    }

    public double getPlaySeconds() {
        return playIndex * frameDt;
    }

    public Rectangle getRecordButtonBounds() {
        return recordButtonBounds;
    }

    public Rectangle getPlayButtonBounds() {
        return playButtonBounds;
    }

    public boolean isRecordHovered() {
        return recordHovered;
    }

    public void setRecordHovered(boolean recordHovered) {
        this.recordHovered = recordHovered;
    }

    public boolean isPlayHovered() {
        return playHovered;
    }

    public void setPlayHovered(boolean playHovered) {
        this.playHovered = playHovered;
    }
}
