package com.example.system;

import com.example.component.Renderable;
import com.example.component.Transform;
import com.example.component.Velocity;
import com.example.ecs.System;
import com.example.ecs.World;

/** Transform += Velocity * dt für alle beweglichen Entities, begrenzt auf die Fenstergrenzen. */
public class MovementSystem implements System {
    private final int worldWidth;
    private final int worldHeight;

    public MovementSystem(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public void update(World world, double dt) {
        // Statische Entities (ohne Velocity) fallen durch die Query automatisch raus
        for (int id : world.query(Transform.class, Velocity.class)) {
            Transform t = world.get(id, Transform.class);
            Velocity v = world.get(id, Velocity.class);

            t.x += v.vx * dt;
            t.y += v.vy * dt;

            // Falls vorhanden, die Größe berücksichtigen, damit die ganze Box im Fenster bleibt
            Renderable r = world.get(id, Renderable.class);
            double w = r == null ? 0 : r.width;
            double h = r == null ? 0 : r.height;
            t.x = Math.max(0, Math.min(t.x, worldWidth - w));
            t.y = Math.max(0, Math.min(t.y, worldHeight - h));
        }
    }
}
