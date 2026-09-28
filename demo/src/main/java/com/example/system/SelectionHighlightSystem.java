package com.example.system;

import com.example.Game.RenderTarget;
import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.component.Transform;
import com.example.ecs.System;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.resource.Selection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

/** Hebt die selektierte Figur hervor: leichte Aufhellung plus kräftige Kontur, die Form bleibt erkennbar. */
public class SelectionHighlightSystem implements System {
    private static final Color LIGHTEN = new Color(255, 255, 255, 70); // halbtransparentes Weiß
    private static final Color OUTLINE = new Color(255, 200, 60);
    private static final float OUTLINE_WIDTH = 3f;

    @Override
    public void update(World world, double alpha) {
        Selection selection = world.getResource(Selection.class);
        int id = selection.getSelectedId();
        if (!selection.hasSelection() || !world.has(id, Style.class)) {
            return;
        }

        Graphics2D g2 = (Graphics2D) world.getResource(RenderTarget.class).getGraphics();
        Shape shape = FormFactory.toShape(world.get(id, ShapeHolder.class).figur,
                world.get(id, Transform.class), world.get(id, Style.class).size);

        g2.setColor(LIGHTEN);
        g2.fill(shape);
        g2.setStroke(new BasicStroke(OUTLINE_WIDTH));
        g2.setColor(OUTLINE);
        g2.draw(shape);
    }
}
