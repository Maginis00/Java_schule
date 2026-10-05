package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.ui.ZOrder;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;

/** Zeichnet alle Figuren von unten nach oben (zIndex aufsteigend), so liegen höhere Ebenen obenauf. */
public class ShapeRenderSystem implements System {
    @Override
    public void update(World world, double alpha) {
        // Antialiasing ist für den ganzen Frame in Game.render() eingeschaltet
        Graphics2D g2 = (Graphics2D) world.getResource(RenderTarget.class).getGraphics();

        for (int id : ZOrder.bottomToTop(world)) {
            Transform t = world.get(id, Transform.class);
            ShapeHolder holder = world.get(id, ShapeHolder.class);
            Style style = world.get(id, Style.class);

            Shape shape = FormFactory.toShape(holder.figur, t, style.size);
            g2.setColor(style.fill);
            g2.fill(shape);
            if (style.strokeWidth > 0) {
                g2.setStroke(new BasicStroke(style.strokeWidth));
                g2.setColor(style.stroke);
                g2.draw(shape);
            }
        }
    }
}
