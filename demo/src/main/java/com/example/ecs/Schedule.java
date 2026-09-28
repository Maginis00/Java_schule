package com.example.ecs;

/**
 * Legt die feste Reihenfolge InputSet -> UpdateSet -> RenderSet fest.
 * Die Reihenfolge steckt im Code von update()/render(), nicht in den Systemen selbst.
 */
public class Schedule {
    private final SystemSet setupSet = new SystemSet("SetupSet");
    private final SystemSet inputSet = new SystemSet("InputSet");
    private final SystemSet updateSet = new SystemSet("UpdateSet");
    private final SystemSet renderSet = new SystemSet("RenderSet");

    public SystemSet setupSet() {
        return setupSet;
    }

    public SystemSet inputSet() {
        return inputSet;
    }

    public SystemSet updateSet() {
        return updateSet;
    }

    public SystemSet renderSet() {
        return renderSet;
    }

    public void init(World world, double fixedDt) {
        setupSet.run(world, fixedDt);
    }

    /** Logik-Pfad: nur Input und Update, wird im festen Zeitschritt aufgerufen. */
    public void update(World world, double fixedDt) {
        inputSet.run(world, fixedDt);
        updateSet.run(world, fixedDt);
    }

    /** Render-Pfad: nur das RenderSet; alpha ist der Interpolationsfaktor (0..1) oder 0 ohne Interpolation. */
    public void render(World world, double alpha) {
        renderSet.run(world, alpha);
    }

    /** Alle drei Sets in fester Reihenfolge nacheinander (Update und Render gekoppelt). */
    public void process(World world, double dt) {
        update(world, dt);
        render(world, 0);
    }
}
