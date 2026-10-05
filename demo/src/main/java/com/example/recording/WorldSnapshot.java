package com.example.recording;

import com.example.component.Hidden;
import com.example.component.Layer;
import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.component.Transform;
import com.example.ecs.World;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

/**
 * Zustand aller Figuren zu einem Zeitpunkt: ein Frame der Aufnahme bzw. der Live-Zustand, der
 * nach der Wiedergabe wiederhergestellt wird. Speichert Werte, keine Referenzen auf Components,
 * damit spätere Änderungen an der Welt den Snapshot nicht verfälschen.
 */
public final class WorldSnapshot {
    /** Werte einer Figur; Color ist unveränderlich und darf geteilt werden. */
    private static final class ShapeState {
        final double x;
        final double y;
        final Color fill;
        final Color stroke;
        final float strokeWidth;
        final float size;
        final int zIndex;

        ShapeState(Transform t, Style style, Layer layer) {
            x = t.x;
            y = t.y;
            fill = style.fill;
            stroke = style.stroke;
            strokeWidth = style.strokeWidth;
            size = style.size;
            zIndex = layer.zIndex;
        }
    }

    private final Map<Integer, ShapeState> shapes = new TreeMap<>();

    private WorldSnapshot() {
    }

    /** Nimmt alle sichtbaren Figuren auf. */
    public static WorldSnapshot capture(World world) {
        WorldSnapshot snapshot = new WorldSnapshot();
        Class<?>[] all = { Transform.class, ShapeHolder.class, Style.class, Layer.class };
        Class<?>[] exclude = { Hidden.class };
        for (int id : world.query(all, exclude)) {
            snapshot.shapes.put(id, new ShapeState(world.get(id, Transform.class),
                    world.get(id, Style.class), world.get(id, Layer.class)));
        }
        return snapshot;
    }

    /**
     * Schreibt die Werte zurück in die Welt. Figuren, die es zu diesem Zeitpunkt noch nicht gab,
     * werden versteckt; alle anderen sind sichtbar.
     */
    public void apply(World world) {
        for (int id : world.query(Transform.class, ShapeHolder.class, Style.class, Layer.class)) {
            ShapeState state = shapes.get(id);
            if (state == null) {
                if (!world.has(id, Hidden.class)) {
                    world.add(id, new Hidden());
                }
                continue;
            }
            world.remove(id, Hidden.class);

            Transform t = world.get(id, Transform.class);
            t.x = state.x;
            t.y = state.y;
            Style style = world.get(id, Style.class);
            style.fill = state.fill;
            style.stroke = state.stroke;
            style.strokeWidth = state.strokeWidth;
            style.size = state.size;
            world.get(id, Layer.class).zIndex = state.zIndex;
        }
    }
}
