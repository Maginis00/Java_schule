package com.example.resource;

import com.example.ui.FormMenuItem;

import java.awt.Rectangle;
import java.util.List;

/** Resource: Zustand und Layout des Form-Menüs (Anker + ausklappbare Liste). */
public class UiMenuState {
    public static final int NO_ITEM = -1;

    private final Rectangle anchorBounds;
    private final Rectangle dropdownBounds;
    private final Rectangle settingsButtonBounds;
    private final List<FormMenuItem> items;

    private boolean formMenuOpen;
    private boolean anchorHovered;
    private boolean settingsHovered;
    private int hoveredIndex = NO_ITEM;
    private int selectedIndex;

    public UiMenuState(Rectangle anchorBounds, Rectangle dropdownBounds, Rectangle settingsButtonBounds,
            List<FormMenuItem> items) {
        this.anchorBounds = anchorBounds;
        this.dropdownBounds = dropdownBounds;
        this.settingsButtonBounds = settingsButtonBounds;
        this.items = items;
    }

    public Rectangle getAnchorBounds() {
        return anchorBounds;
    }

    public Rectangle getDropdownBounds() {
        return dropdownBounds;
    }

    public Rectangle getSettingsButtonBounds() {
        return settingsButtonBounds;
    }

    public boolean isSettingsHovered() {
        return settingsHovered;
    }

    public void setSettingsHovered(boolean settingsHovered) {
        this.settingsHovered = settingsHovered;
    }

    public List<FormMenuItem> getItems() {
        return items;
    }

    public boolean isFormMenuOpen() {
        return formMenuOpen;
    }

    public void setFormMenuOpen(boolean formMenuOpen) {
        this.formMenuOpen = formMenuOpen;
    }

    public boolean isAnchorHovered() {
        return anchorHovered;
    }

    public void setAnchorHovered(boolean anchorHovered) {
        this.anchorHovered = anchorHovered;
    }

    public int getHoveredIndex() {
        return hoveredIndex;
    }

    public void setHoveredIndex(int hoveredIndex) {
        this.hoveredIndex = hoveredIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}
