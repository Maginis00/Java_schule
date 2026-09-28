package com.example;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import com.example.component.PlayerTag;
import com.example.component.Renderable;
import com.example.component.Transform;
import com.example.component.Velocity;
import com.example.ecs.Schedule;
import com.example.ecs.World;
import com.example.input.KeyState;
import com.example.input.MouseState;
import com.example.resource.SpawnMode;
import com.example.system.ButtonVisualSystem;
import com.example.system.DebugSystem;
import com.example.system.InputSystem;
import com.example.system.MovementSystem;
import com.example.system.RenderSystem;
import com.example.system.ShapeRenderSystem;
import com.example.system.UiInputSystem;
import com.example.system.UiRenderSystem;
import com.example.system.WorldInputSystem;
import com.example.ui.TriangleButtonFactory;

/** Fenster, Canvas, Loop und Keyboard-State; die Spiellogik steckt komplett in den Systems. */
public class Game {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String TITLE = "AWT ECS";

    private static final int TARGET_UPS = 60;
    private static final double DT = 1.0 / TARGET_UPS;      // feste Schrittweite in Sekunden
    private static final double MAX_FRAME_TIME = 0.25;      // Schutz vor "Spiral of Death"
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;
    private static final int BUFFER_COUNT = 2;              // Double Buffering
    private static final int BUFFER_STRATEGY_RETRIES = 10;
    private static final long RETRY_DELAY_MS = 10;

    private static final double PLAYER_SIZE = 40;
    private static final Color PLAYER_COLOR = new Color(80, 200, 120);
    private static final Color OBSTACLE_COLOR = new Color(200, 80, 80);

    /** Resource: aktuelle Zeichenfläche des Frames plus Debug-Werte für das DebugSystem. */
    public static final class RenderTarget {
        private final int width;
        private final int height;
        private Graphics graphics;
        private int fps;
        private int ups;

        RenderTarget(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Graphics getGraphics() {
            return graphics;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getFps() {
            return fps;
        }

        public int getUps() {
            return ups;
        }
    }

    private final KeyState keyState = new KeyState();
    private final MouseState mouseState = new MouseState();
    private final SpawnMode spawnMode = new SpawnMode();
    private final RenderTarget renderTarget = new RenderTarget(WIDTH, HEIGHT);
    private final World world = new World();
    private final Schedule schedule = new Schedule();
    private volatile boolean running;

    private Frame frame;
    private Canvas canvas;
    private BufferStrategy bufferStrategy;

    public void start() {
        init();
        running = true;
        loop();
        // Nach dem Loop aufräumen; ohne offenes Fenster beendet sich die JVM von selbst
        frame.dispose();
    }

    private void init() {
        initWorld();
        initWindow();
    }

    /** Resources, Systems in fester Set-Zuordnung und die Start-Entities anlegen. */
    private void initWorld() {
        world.setResource(KeyState.class, keyState);
        world.setResource(MouseState.class, mouseState);
        world.setResource(SpawnMode.class, spawnMode);
        world.setResource(RenderTarget.class, renderTarget);

        // Sets laufen fest als Input -> Update -> Render, innerhalb eines Sets gilt die Einfügereihenfolge.
        // Im InputSet zuerst die UI (verbraucht Klicks), zuletzt die Welt (löscht die Klick-Flags).
        schedule.inputSet().add(new UiInputSystem()).add(new InputSystem()).add(new WorldInputSystem());
        schedule.updateSet().add(new MovementSystem(WIDTH, HEIGHT)).add(new ButtonVisualSystem());
        schedule.renderSet().add(new RenderSystem()).add(new ShapeRenderSystem())
                .add(new UiRenderSystem()).add(new DebugSystem());

        TriangleButtonFactory.create(world);

        // Hindernis zuerst, damit der Player (höhere ID) darüber gezeichnet wird
        int obstacle = world.createEntity();
        world.add(obstacle, new Transform(500, 200));
        world.add(obstacle, new Renderable(120, 120, OBSTACLE_COLOR));

        int player = world.createEntity();
        world.add(player, new Transform((WIDTH - PLAYER_SIZE) / 2.0, (HEIGHT - PLAYER_SIZE) / 2.0));
        world.add(player, new Velocity(0, 0));
        world.add(player, new Renderable(PLAYER_SIZE, PLAYER_SIZE, PLAYER_COLOR));
        world.add(player, new PlayerTag());
    }

    private void initWindow() {
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setIgnoreRepaint(true); // Wir zeichnen aktiv selbst, AWT soll nicht dazwischenfunken
        canvas.setFocusable(true);
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyState.set(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyState.set(e.getKeyCode(), false);
            }
        });
        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                canvas.requestFocusInWindow();
                mouseState.setPosition(e.getX(), e.getY());
                mouseState.press(e.getButton());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseState.release(e.getButton());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseState.setPosition(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseState.setPosition(e.getX(), e.getY());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseState.setInsideWindow(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseState.setInsideWindow(false);
            }
        };
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        // Bei Fokusverlust alle Tasten/Maustasten loslassen, sonst "klemmt" die Bewegung
        canvas.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                keyState.clear();
                mouseState.clear();
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

    /** Fixed-Timestep-Loop mit Akkumulator: Update in festen Schritten, Render so oft es geht. */
    private void loop() {
        long previous = java.lang.System.nanoTime();
        long counterStart = previous;
        double accumulator = 0;
        int frameCount = 0;
        int updateCount = 0;

        while (running) {
            long now = java.lang.System.nanoTime();
            double frameTime = (now - previous) / NANOS_PER_SECOND;
            previous = now;
            // Nach einem langen Hänger nicht endlos nachholen
            accumulator += Math.min(frameTime, MAX_FRAME_TIME);

            // Verstrichene Zeit in feste Logik-Schritte umsetzen
            while (accumulator >= DT) {
                update(DT);
                accumulator -= DT;
                updateCount++;
            }

            // Restzeit im Akkumulator / DT = Interpolationsfaktor (0..1) für das Rendering
            renderFrame(accumulator / DT);
            frameCount++;

            if (now - counterStart >= NANOS_PER_SECOND) {
                renderTarget.fps = frameCount;
                renderTarget.ups = updateCount;
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

    /** Logik-Pfad: InputSet und UpdateSet, nie im Render-Pfad. */
    private void update(double dt) {
        schedule.update(world, dt);
    }

    private void renderFrame(double alpha) {
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
                    render(g, alpha);
                } finally {
                    g.dispose();
                }
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }

    /** Render-Pfad: nur das RenderSet, zeichnet auf den Graphics des aktuellen Buffers. */
    private void render(Graphics g, double alpha) {
        // Antialiasing für den ganzen Frame (Formen und Text)
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderTarget.graphics = g;
        schedule.render(world, alpha);
        renderTarget.graphics = null;
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
}
