package com.example.ui;

import com.Figuren.Form;
import com.example.component.Transform;
import com.example.ecs.World;
import com.example.figurenadapter.FormFactory;
import com.example.resource.SpawnMode;
import com.example.resource.UiMenuState;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/** Baut das Form-Menü (Anker + Einträge für alle FormFactory-Figuren) und legt es als Resource ab. */
public final class FormMenuFactory {
    private static final int MENU_X = 10;
    private static final int MENU_Y = 10;
    private static final int ANCHOR_WIDTH = 90;
    private static final int ANCHOR_HEIGHT = 28;
    private static final int ITEM_HEIGHT = 30;
    private static final int DROPDOWN_WIDTH = 170;
    private static final double PREVIEW_SIZE = 18;

    private FormMenuFactory() {
    }

    /** Braucht die SpawnMode-Resource; die erste Form ist vorausgewählt (Modus bleibt aus). */
    public static UiMenuState create(World world) {
        Rectangle anchor = new Rectangle(MENU_X, MENU_Y, ANCHOR_WIDTH, ANCHOR_HEIGHT);

        // Dropdown direkt unter dem Anker, Einträge untereinander
        List<String> ids = FormFactory.formIds();
        List<FormMenuItem> items = new ArrayList<>();
        int itemY = anchor.y + anchor.height;
        for (String id : ids) {
            Rectangle bounds = new Rectangle(anchor.x, itemY, DROPDOWN_WIDTH, ITEM_HEIGHT);
            items.add(new FormMenuItem(id, FormFactory.label(id), previewOf(id), bounds));
            itemY += ITEM_HEIGHT;
        }
        Rectangle dropdown = new Rectangle(anchor.x, anchor.y + anchor.height,
                DROPDOWN_WIDTH, ITEM_HEIGHT * items.size());

        UiMenuState menu = new UiMenuState(anchor, dropdown, items);
        world.setResource(UiMenuState.class, menu);
        world.getResource(SpawnMode.class).setSelectedForm(items.get(menu.getSelectedIndex()).formId);
        return menu;
    }

    /** Preview aus derselben Figur/Shape-Methode wie in der Welt, nur klein skaliert. */
    private static Shape previewOf(String formId) {
        Form figur = FormFactory.create(formId);
        Shape shape = FormFactory.toShape(figur, new Transform(0, 0));
        Rectangle2D bounds = shape.getBounds2D();
        double scale = PREVIEW_SIZE / Math.max(bounds.getWidth(), bounds.getHeight());
        return AffineTransform.getScaleInstance(scale, scale).createTransformedShape(shape);
    }
}
