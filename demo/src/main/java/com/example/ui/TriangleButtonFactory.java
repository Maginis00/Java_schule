package com.example.ui;

import com.example.component.UiButton;
import com.example.ecs.World;

import java.awt.Color;
import java.awt.Polygon;

/** Erzeugt die Button-Entity: ein Dreieck aus drei Punkten. */
public final class TriangleButtonFactory {
    private static final int[] XPOINTS = {20, 80, 50};
    private static final int[] YPOINTS = {20, 20, 70};
    private static final String NAME = "Dreieck";

    private static final Color NORMAL_COLOR = new Color(70, 130, 200);
    private static final Color HOVER_COLOR = new Color(100, 160, 230);
    private static final Color ACTIVE_COLOR = new Color(255, 200, 60);

    private TriangleButtonFactory() {
    }

    public static int create(World world) {
        Polygon polygon = new Polygon(XPOINTS, YPOINTS, XPOINTS.length);
        int id = world.createEntity();
        world.add(id, new UiButton(NAME, polygon, NORMAL_COLOR, HOVER_COLOR, ACTIVE_COLOR));
        return id;
    }
}
