/*
 * Copyright 2018 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.meinders.common.swing;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * A combo box model containing a range of standard colors and a choosable
 * custom color, that can be selected by the user.
 *
 * @version 0.9 (2006.02.22)
 * @author Gerrit Meinders
 */
public class ColorComboBoxModel extends AbstractListModel
        implements ComboBoxModel {
    /**
     * Returns a color combo box model containing a variety of colors.
     *
     * @param selectCustomColors
     *            indicates whether a custom color should be included
     */
    public static ColorComboBoxModel createRainbowModel(
            boolean selectCustomColors) {
        ArrayList<Color> colors = new ArrayList<Color>();
        float hueSteps = 12.0f;
        for (int i=0; i<hueSteps; i++) {
            float brightnessSteps = 3.0f;
            for (int j=0; j<brightnessSteps; j++) {
                float hue = i / hueSteps;
                float saturation = 0.65f;
                float brightness = (j + 1) / brightnessSteps;
                Color color = Color.getHSBColor(hue, saturation, brightness);
                colors.add(color);
            }
            float hue = i / hueSteps;
            float saturation = 0.35f;
            float brightness = 1.0f;
            Color color = Color.getHSBColor(hue, saturation, brightness);
            colors.add(color);
        }

        for (int i=0; i<=4; i++) {
            int gray = (255 * i) / 4;
            colors.add(new Color(gray, gray, gray));
        }

        return new ColorComboBoxModel(colors.toArray(new Color[0]),
                selectCustomColors);
    }

    /** The colors in the model. */
    private Color[] colors;

    /** The currently selected color. */
    private Color selectedColor;

    /** The custom color. */
    private Color customColor;

    /** Indicates whether a custom color can be selected. */
    private boolean selectCustomColors;

    /**
     * Constructs a new color combo box model with the given colors.
     *
     * @param colors the colors
     * @param selectCustomColors
     *            indicates whether a custom color should be included
     */
    public ColorComboBoxModel(Color[] colors, boolean selectCustomColors) {
        assert colors != null : "colors != null";
        this.colors = colors;
        this.customColor = null;
        this.selectCustomColors = selectCustomColors;
    }

    /**
     * Returns the color at the specified index.
     *
     * @param index the index
     * @return the color
     */
    public Color getElementAt(int index) {
        if (index < colors.length) {
            return colors[index];
        } else if (index == colors.length) {
            assert selectCustomColors : "Invalid index: " + index;
            return customColor;
        } else {
            assert selectCustomColors : "Invalid index: " + index;
            return null;
        }
    }

    /**
     * Returns the length of the list.
     *
     * @return the length of the list
     */
    public int getSize() {
        if (selectCustomColors) {
            return colors.length + (customColor == null ? 1 : 2);
        } else {
            return colors.length;
        }
    }

    /**
     * Returns the selected color.
     *
     * @return the color
     */
    public Color getSelectedItem() {
        return selectedColor;
    }

    /**
     * Sets the selected item, which must be a {@link Color}.
     *
     * @param selectedItem the color
     */
    public void setSelectedItem(Object selectedItem) {
        if (selectedItem == null || selectedItem instanceof Color) {
            /*
            Select the given color from the list, or as a custom color if it's
            not on the list.
            */
            Color selectedColor = (Color) selectedItem;
            this.selectedColor = selectedColor;
            for (int i=0; i<colors.length; i++) {
                Color color = colors[i];
                if (color.equals(selectedColor)) {
                    return;
                }
            }

            Color oldCustomColor = customColor;
            customColor = selectedColor;
            if (oldCustomColor == null) {
                fireIntervalAdded(this, colors.length, colors.length);
            } else {
                fireContentsChanged(this, colors.length, colors.length);
            }
        } else {
            assert false : "ColorComboBoxModel can't handle object of class " +
                    selectedItem.getClass();
        }
    }

    /**
     * Returns whether the model's custom color is selected.
     *
     * @return {@code true} if the custom color is selected;
     *         {@code false} otherwise
     */
    public boolean isCustomColorSelected() {
        return selectedColor == null;
    }

    /**
     * Returns the custom color.
     *
     * @return the custom color
     */
    public Color getCustomColor() {
        return customColor;
    }
}

