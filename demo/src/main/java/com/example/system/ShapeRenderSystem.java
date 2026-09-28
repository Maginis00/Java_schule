package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.ShapeHolder;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;

import java.awt.Graphics2D;

/** Zeichnet alle Entities mit Transform + ShapeHolder über die Shape der Figur. */
public class ShapeRenderSystem implements System {
    @Override
    public void update(World world, double alpha) {
        // Antialiasing ist für den ganzen Frame in Game.render() eingeschaltet
        Graphics2D g2 = (Graphics2D) world.getResource(RenderTarget.class).getGraphics();

        for (int id : world.query(Transform.class, ShapeHolder.class)) {
            Transform t = world.get(id, Transform.class);
            ShapeHolder holder = world.get(id, ShapeHolder.class);
            g2.setColor(holder.color);
            g2.fill(FormFactory.toShape(holder.figur, t));
        }
    }
}
