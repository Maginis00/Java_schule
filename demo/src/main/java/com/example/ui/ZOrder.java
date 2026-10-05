package com.example.ui;

import com.example.component.Hidden;
import com.example.component.Layer;
import com.example.component.ShapeHolder;
import com.example.component.Style;
import com.example.component.Transform;
import com.example.ecs.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Einzige Quelle der Z-Ordnung: Zeichnen (unten -> oben) und Picking (oben -> unten)
 * benutzen dieselbe Sortierung. zIndex ist immer eindeutig (neue Figuren bekommen max + 1,
 * Verschieben tauscht mit dem Nachbarn), ein Gleichstand würde nach ID entschieden.
 */
public final class ZOrder {
    private ZOrder() {
    }

    /** Alle sichtbaren Figuren (ohne Hidden), unterste zuerst. */
    public static List<Integer> bottomToTop(World world) {
        List<Integer> ids = new ArrayList<>();
        Class<?>[] all = { Transform.class, ShapeHolder.class, Style.class, Layer.class };
        Class<?>[] exclude = { Hidden.class };
        for (int id : world.query(all, exclude)) {
            ids.add(id);
        }
        ids.sort((a, b) -> {
            int byZ = Integer.compare(world.get(a, Layer.class).zIndex, world.get(b, Layer.class).zIndex);
            return byZ != 0 ? byZ : Integer.compare(a, b);
        });
        return ids;
    }

    /** Größter vergebener zIndex, -1 wenn es noch keine Figur gibt. */
    public static int maxZ(World world) {
        int max = -1;
        for (int id : bottomToTop(world)) {
            max = Math.max(max, world.get(id, Layer.class).zIndex);
        }
        return max;
    }

    /** Eine Ebene nach oben: tauscht den zIndex mit der Figur direkt darüber. */
    public static boolean moveForward(World world, int id) {
        return swapWithNeighbor(world, id, +1);
    }

    /** Eine Ebene nach unten. */
    public static boolean moveBackward(World world, int id) {
        return swapWithNeighbor(world, id, -1);
    }

    private static boolean swapWithNeighbor(World world, int id, int direction) {
        List<Integer> order = bottomToTop(world);
        int index = order.indexOf(id);
        int neighborIndex = index + direction;
        if (index < 0 || neighborIndex < 0 || neighborIndex >= order.size()) {
            return false; // schon ganz oben bzw. unten
        }
        Layer a = world.get(id, Layer.class);
        Layer b = world.get(order.get(neighborIndex), Layer.class);
        int tmp = a.zIndex;
        a.zIndex = b.zIndex;
        b.zIndex = tmp;
        return true;
    }
}
