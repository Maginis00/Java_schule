package com.example.system;

import com.example.Game.RenderTarget;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.resource.Recorder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Zeichnet die Aufnahme-Buttons neben dem Form-Menü (Stil wie UiRenderSystem) und darunter eine
 * Statuszeile: blinkendes "REC" beim Aufnehmen, Fortschritt beim Abspielen, sonst die Länge der Aufnahme.
 */
public class RecorderRenderSystem implements System {
    private static final String RECORD_LABEL = "Aufnahme";
    private static final String PLAY_LABEL = "Abspielen";
    private static final String STOP_LABEL = "Stopp";
    private static final String REC_PREFIX = "REC ";
    private static final String TAKE_PREFIX = "Aufnahme ";

    private static final Color RECORD_COLOR = new Color(230, 70, 70);
    private static final Color PLAY_COLOR = new Color(90, 200, 120);
    private static final Color STATUS_DIM_COLOR = new Color(150, 160, 185);

    private static final int ICON_SIZE = 10;
    private static final int ICON_X_OFFSET = 12;   // Abstand linker Rand -> Icon
    private static final int LABEL_X_OFFSET = 30;  // Abstand linker Rand -> Text
    private static final int STATUS_OFFSET_Y = 18; // Abstand Button-Unterkante -> Baseline der Statuszeile
    private static final int STATUS_TEXT_GAP = 6;  // Abstand Status-Icon -> Text
    private static final int BLINKS_PER_SECOND = 2;

    private enum Icon { RECORD, PLAY, STOP }

    @Override
    public void update(World world, double alpha) {
        Graphics2D g2 = (Graphics2D) world.getResource(RenderTarget.class).getGraphics();
        Recorder recorder = world.getResource(Recorder.class);

        boolean recording = recorder.isRecording();
        boolean playing = recorder.isPlaying();
        drawButton(g2, recorder.getRecordButtonBounds(), !playing, recorder.isRecordHovered(),
                recording ? Icon.STOP : Icon.RECORD, recording ? STOP_LABEL : RECORD_LABEL);
        drawButton(g2, recorder.getPlayButtonBounds(), playing || recorder.canPlay(), recorder.isPlayHovered(),
                playing ? Icon.STOP : Icon.PLAY, playing ? STOP_LABEL : PLAY_LABEL);
        drawStatus(g2, recorder);
    }

    private void drawButton(Graphics2D g2, Rectangle r, boolean enabled, boolean hovered, Icon icon, String label) {
        g2.setColor(enabled ? (hovered ? UiRenderSystem.ANCHOR_HOVER_COLOR : UiRenderSystem.ANCHOR_COLOR)
                : UiRenderSystem.DISABLED_COLOR);
        g2.fill(r);
        g2.setColor(UiRenderSystem.BORDER_COLOR);
        g2.draw(r);

        drawIcon(g2, icon, r.x + ICON_X_OFFSET, (int) r.getCenterY() - ICON_SIZE / 2, enabled);
        g2.setColor(enabled ? UiRenderSystem.TEXT_COLOR : UiRenderSystem.DISABLED_TEXT_COLOR);
        g2.drawString(label, r.x + LABEL_X_OFFSET, UiRenderSystem.baseline(g2, r));
    }

    /** Icons als Shapes statt Unicode-Zeichen, die stehen nicht in jeder Schrift zur Verfügung. */
    private void drawIcon(Graphics2D g2, Icon icon, int x, int y, boolean enabled) {
        switch (icon) {
            case RECORD:
                g2.setColor(enabled ? RECORD_COLOR : UiRenderSystem.DISABLED_TEXT_COLOR);
                g2.fillOval(x, y, ICON_SIZE, ICON_SIZE);
                break;
            case PLAY:
                g2.setColor(enabled ? PLAY_COLOR : UiRenderSystem.DISABLED_TEXT_COLOR);
                g2.fillPolygon(new int[] { x, x + ICON_SIZE, x },
                        new int[] { y, y + ICON_SIZE / 2, y + ICON_SIZE }, 3);
                break;
            default:
                g2.setColor(enabled ? UiRenderSystem.TEXT_COLOR : UiRenderSystem.DISABLED_TEXT_COLOR);
                g2.fillRect(x, y, ICON_SIZE, ICON_SIZE);
                break;
        }
    }

    private void drawStatus(Graphics2D g2, Recorder recorder) {
        Rectangle r = recorder.getRecordButtonBounds();
        int iconY = r.y + r.height + STATUS_OFFSET_Y - ICON_SIZE;
        int textX = r.x + ICON_SIZE + STATUS_TEXT_GAP;
        int baseline = r.y + r.height + STATUS_OFFSET_Y;

        if (recorder.isRecording()) {
            double seconds = recorder.getDurationSeconds();
            // Blinken an der Aufnahmezeit festmachen, dann braucht es keine eigene Uhr
            if ((int) (seconds * BLINKS_PER_SECOND) % 2 == 0) {
                drawIcon(g2, Icon.RECORD, r.x, iconY, true);
            }
            g2.setColor(UiRenderSystem.TEXT_COLOR);
            g2.drawString(REC_PREFIX + time(seconds), textX, baseline);
        } else if (recorder.isPlaying()) {
            drawIcon(g2, Icon.PLAY, r.x, iconY, true);
            g2.setColor(UiRenderSystem.TEXT_COLOR);
            g2.drawString(time(recorder.getPlaySeconds()) + " / " + time(recorder.getDurationSeconds()),
                    textX, baseline);
        } else if (recorder.hasRecording()) {
            g2.setColor(STATUS_DIM_COLOR);
            g2.drawString(TAKE_PREFIX + time(recorder.getDurationSeconds()), r.x, baseline);
        }
    }

    /** Sekunden als mm:ss. */
    private static String time(double seconds) {
        int total = (int) seconds;
        return String.format("%02d:%02d", total / 60, total % 60);
    }
}
