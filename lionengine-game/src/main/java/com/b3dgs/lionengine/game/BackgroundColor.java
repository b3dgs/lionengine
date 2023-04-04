package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.graphic.ColorGradient;
import com.b3dgs.lionengine.graphic.ColorRgba;

public class BackgroundColor {


    /**
     * Move field refactoring is implemented on the variables
     * As all the mentioned variables were declared in the Bar class
     * But now they are moved into this class and just accessed there using the getter method*/
    private ColorRgba background;
    /** Foreground color. */
    private ColorRgba foreground;
    /** Gradient color. */
    private ColorGradient gradientColor;

    public ColorRgba getBackground() {
        return background;
    }

    public ColorRgba getForeground() {
        return foreground;
    }

    public ColorGradient getGradientColor() {
        return gradientColor;
    }

    /**
     * Set the background color.
     *
     * @param background The background color.
     * @param foreground The foreground color.
     */
    public void setColor(ColorRgba background, ColorRgba foreground)
    {
        setColorBackground(background);
        setColorForeground(foreground);
    }

    /**
     * Set the background color.
     *
     * @param color The background color.
     */
    public void setColorBackground(ColorRgba color)
    {
        background = color;
    }

    /**
     * Set the foreground color.
     *
     * @param color The foreground color.
     */
    public void setColorForeground(ColorRgba color)
    {
        foreground = color;
    }

    /**
     * Set a gradient color from point 1 with color 1 to point2 with color 2.
     *
     * @param x1 The first horizontal location.
     * @param y1 The first vertical location.
     * @param color1 The first color.
     * @param x2 The last horizontal location.
     * @param y2 The last vertical location.
     * @param color2 The last color.
     */
    public void setColorGradient(int x1, int y1, ColorRgba color1, int x2, int y2, ColorRgba color2)
    {
        gradientColor = new ColorGradient(x1, y1, color1, x2, y2, color2);
    }

}
