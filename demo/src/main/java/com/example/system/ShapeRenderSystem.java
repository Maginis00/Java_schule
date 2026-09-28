package com.example.system;

import com.Figuren.Form;
import com.example.Game.RenderTarget;
import com.example.component.ShapeHolder;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
            g2.fill(toAwtShape(holder.figur, t));
        }
    }

    /**
     * Adapter Figur -> Bildschirm: Die Figur kennt ihre eigene Position (Standardwert aus com.Figuren),
     * hat aber keinen öffentlichen Setter. Deshalb wird die Shape aus erzeugeShape() so verschoben,
     * dass ihr Mittelpunkt auf dem Transform liegt. Die Figur selbst bleibt unverändert.
     */
    private Shape toAwtShape(Form figur, Transform t) {
        Shape local = figur.erzeugeShape();
        Rectangle2D bounds = local.getBounds2D();
        AffineTransform move = AffineTransform.getTranslateInstance(
                t.x - bounds.getCenterX(), t.y - bounds.getCenterY());
        return move.createTransformedShape(local);
    }
}
