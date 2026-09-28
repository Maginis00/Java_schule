package com.example;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Arrays;

/** Fenster, Canvas, Game Loop, Input, Update und Render an einem Ort. */
class Game {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "AWT Game Loop";

    private static final int TARGET_UPS = 60;
    private static final double DT = 1.0 / TARGET_UPS;      // feste Schrittweite in Sekunden
    private static final double MAX_FRAME_TIME = 0.25;      // Schutz vor "Spiral of Death"
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;
    private static final int BUFFER_COUNT = 2;              // Double Buffering
    private static final int BUFFER_STRATEGY_RETRIES = 10;
    private static final long RETRY_DELAY_MS = 10;

    private static final Color BACKGROUND = new Color(30, 40, 60);
    private static final Color PLAYER_COLOR = new Color(80, 200, 120);
    private static final Color OBSTACLE_COLOR = new Color(200, 80, 80);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Rectangle OBSTACLE = new Rectangle(500, 200, 120, 120);
    private static final int TEXT_X = 10;
    private static final int TEXT_Y = 20;

    private final boolean[] keys = new boolean[256];
    private volatile boolean running;

    private Frame frame;
    private Canvas canvas;
    private BufferStrategy bufferStrategy;
    private Player player;

    // Debug-Anzeige, wird einmal pro Sekunde aktualisiert
    private int fps;
    private int ups;

    void start() {
        init();
        running = true;
        loop();
        // Nach dem Loop aufräumen; ohne offenes Fenster beendet sich die JVM von selbst
        frame.dispose();
    }

    private void init() {
        player = new Player((WIDTH - Player.SIZE) / 2.0, (HEIGHT - Player.SIZE) / 2.0);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setIgnoreRepaint(true); // Wir zeichnen aktiv selbst, AWT soll nicht dazwischenfunken
        canvas.setFocusable(true);
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setKey(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                setKey(e.getKeyCode(), false);
            }
        });
        // Bei Fokusverlust alle Tasten loslassen, sonst "klemmt" die Bewegung
        canvas.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Arrays.fill(keys, false);
            }
        });

        frame = new Frame(TITLE);
        frame.setResizable(false);
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        // Schließen beendet nur den Loop; das Dispose passiert danach in start()
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
            }
        });
        frame.setVisible(true);
        canvas.requestFocus();
    }

    private void setKey(int code, boolean down) {
        if (code >= 0 && code < keys.length) {
            keys[code] = down;
        }
    }

    /** Fixed-Timestep-Loop mit Akkumulator: Update in festen Schritten, Render so oft es geht. */
    private void loop() {
        long previous = System.nanoTime();
        long counterStart = previous;
        double accumulator = 0;
        int frameCount = 0;
        int updateCount = 0;

        while (running) {
            long now = System.nanoTime();
            double frameTime = (now - previous) / NANOS_PER_SECOND;
            previous = now;
            // Nach einem langen Hänger nicht endlos nachholen
            accumulator += Math.min(frameTime, MAX_FRAME_TIME);

            handleInput();

            // Verstrichene Zeit in feste Logik-Schritte umsetzen
            while (accumulator >= DT) {
                update(DT);
                accumulator -= DT;
                updateCount++;
            }

            renderFrame();
            frameCount++;

            if (now - counterStart >= NANOS_PER_SECOND) {
                fps = frameCount;
                ups = updateCount;
                frameCount = 0;
                updateCount = 0;
                counterStart = now;
            }

            // Sleep dient nur der CPU-Entlastung; das Timing korrigiert der Akkumulator
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void handleInput() {
        int dx = 0;
        int dy = 0;
        if (keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]) dx--;
        if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]) dx++;
        if (keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W]) dy--;
        if (keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S]) dy++;
        player.setDirection(dx, dy);
    }

    /** Reine Logik, unabhängig vom Zeichnen; dt ist immer die feste Schrittweite. */
    private void update(double dt) {
        player.update(dt, WIDTH, HEIGHT, OBSTACLE);
    }

    private void renderFrame() {
        if (bufferStrategy == null) {
            bufferStrategy = obtainBufferStrategy();
            if (bufferStrategy == null) {
                return; // Fenster noch nicht bereit, im nächsten Durchlauf erneut versuchen
            }
        }
        do {
            // contentsRestored: Buffer wurde wiederhergestellt -> neu zeichnen
            do {
                Graphics g = bufferStrategy.getDrawGraphics();
                try {
                    render(g);
                } finally {
                    g.dispose();
                }
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }

    /** Erzeugt die BufferStrategy; schlägt fehl, solange das Fenster nicht angezeigt wird -> kurz retryn. */
    private BufferStrategy obtainBufferStrategy() {
        for (int i = 0; i < BUFFER_STRATEGY_RETRIES; i++) {
            try {
                canvas.createBufferStrategy(BUFFER_COUNT);
                BufferStrategy strategy = canvas.getBufferStrategy();
                if (strategy != null) {
                    return strategy;
                }
            } catch (IllegalStateException e) {
                // Canvas noch nicht displayable, gleich nochmal
            }
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        return null;
    }

    private void render(Graphics g) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(OBSTACLE_COLOR);
        g.fillRect(OBSTACLE.x, OBSTACLE.y, OBSTACLE.width, OBSTACLE.height);

        Rectangle p = player.getBounds();
        g.setColor(PLAYER_COLOR);
        g.fillRect(p.x, p.y, p.width, p.height);

        g.setColor(TEXT_COLOR);
        g.drawString("FPS: " + fps + "  UPS: " + ups, TEXT_X, TEXT_Y);
    }
}
