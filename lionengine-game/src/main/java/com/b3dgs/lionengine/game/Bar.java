/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.ColorGradient;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Surface representing a bar designed to display a kind of progress bar (life...).
 */
public class Bar implements Renderable
{
    /** Horizontal location. */
    private int x;
    /** Vertical location. */
    private int y;
    /** Maximum width. */
    private int maxWidth;
    /** Maximum height. */
    private int maxHeight;
    /** Current width percent. */
    private int pWidth = 100;
    /** Current height percent. */
    private int pHeight = 100;
    /** Horizontal border. */
    private int hBorder;
    /** Vertical border. */
    private int vBorder;
    /** Left-Right referential. */
    private boolean leftRight = true;
    /** Up-Down referential. */
    private boolean upDown;

    /** Implemented Extract class refactoring method
     *  As Bar class was responsible to set the color
     *  Created new class BackgroundColor and
     *  moved all the methods and logic into that new class
     **/
    private final BackgroundColor backgroundColor = new BackgroundColor();

    /**
     * Create a bar.
     * 
     * @param width The maximum width.
     * @param height The maximum height.
     */
    public Bar(int width, int height)
    {
        super();

        maxWidth = width;
        maxHeight = height;
    }

    /**
     * Performed Decompose conditional refactoring.
     * Refactor the code by removing multiple if else conditions.
     * And directly checking the condition for leftRight and upDown.
     */
    @Override
    public void render(Graphic g)
    {
        ColorRgba background = backgroundColor.getBackground();
        ColorRgba foreground = backgroundColor.getForeground();
        ColorGradient gradientColor = backgroundColor.getGradientColor();
        if (background != null)
        {
            g.setColor(background);
            g.drawRect(x, y, maxWidth, maxHeight, true);
        }

        final int rx = maxWidth - hBorder * 2;
        final int ry = maxHeight - vBorder * 2;
        if (!(pWidth == 0 || pHeight == 0))
        {
            int x1 = x + hBorder;
            final int x2 = calculateCeiling(rx, pWidth);;
            if (!leftRight)
            {
                x1 = x1 + (int) Math.ceil(rx - pWidth / 100.0 * rx);
            }

            int y1 = y + vBorder;
            final int y2 = calculateCeiling(ry, pHeight);
            if (!upDown)
            {
                y1 = y1 + (int) Math.floor(ry - pHeight / 100.0 * ry);
            }

            if (foreground != null)
            {
                g.setColor(foreground);
                g.drawRect(x1, y1, x2, y2, true);
            }

            if (gradientColor != null)
            {
                g.setColorGradient(gradientColor);
                g.drawGradient(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Performed Extract method refactoring.
     * Refactor the code by creating new method to find the ceiling of two values.
     * And used this method in the render method
     *
     * @param xValue refers to the first value for ceiling
     * @param yValue refers to the second value for ceiling
     */
    private int calculateCeiling(int xValue, int yValue) {
        return (int) Math.ceil(xValue * (yValue / 100));
    }


    /**
     * Set the horizontal rendering referential.
     * 
     * @param leftRight <code>true</code> if bar is from left to right, <code>false</code> else.
     */
    public void setHorizontalReferential(boolean leftRight)
    {
        this.leftRight = leftRight;
    }

    /**
     * Set the vertical rendering referential.
     * 
     * @param upDown <code>true</code> will start from top to down, <code>false</code> else.
     */
    public void setVerticalReferential(boolean upDown)
    {
        this.upDown = upDown;
    }

    /**
     * Set the bar border size.
     * 
     * @param hBorder The horizontal border size.
     * @param vBorder The vertical border size.
     */
    public void setBorderSize(int hBorder, int vBorder)
    {
        this.hBorder = hBorder;
        this.vBorder = vBorder;
    }

    /**
     * Set the background color.
     * 
     * @param background The background color.
     * @param foreground The foreground color.
     */
    public void setColor(ColorRgba background, ColorRgba foreground)
    {
        backgroundColor.setColor(background, foreground);
    }

    /**
     * Set the background color.
     * 
     * @param color The background color.
     */
    public void setColorBackground(ColorRgba color)
    {
        backgroundColor.setColorBackground(color);
    }

    /**
     * Set the foreground color.
     * 
     * @param color The foreground color.
     */
    public void setColorForeground(ColorRgba color)
    {
        backgroundColor.setColorForeground(color);
    }

    /**
     * Set a gradient color from point 1 with color 1 to point2 with color 2.
     *
     * @param color1 The first color.
     * @param color2 The last color.
     */
    public void setColorGradient(ColorRgba color1, ColorRgba color2)
    {
        setColorGradient(x, y, color1, x + maxWidth, y + maxHeight, color2);
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
        backgroundColor.setColorGradient(x1, y1, color1, x2, y2, color2);
    }

    /**
     * Set the bar location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the maximum size.
     * 
     * @param width The maximum width.
     * @param height The maximum height.
     */
    public void setMaximumSize(int width, int height)
    {
        maxWidth = width;
        maxHeight = height;
    }

    /**
     * Set the width percent.
     * 
     * @param widthPercent The width percent [0-100].
     */
    public void setWidthPercent(int widthPercent)
    {
        pWidth = UtilMath.clamp(widthPercent, 0, 100);
    }

    /**
     * Set the height percent.
     * 
     * @param heightPercent The height percent [0-100].
     */
    public void setHeightPercent(int heightPercent)
    {
        pHeight = UtilMath.clamp(heightPercent, 0, 100);
    }

    /**
     * Get the current width.
     * 
     * @return The current width.
     */
    public int getWidth()
    {
        return (int) Math.ceil(pWidth / 100.0 * maxWidth);
    }

    /**
     * Get the current height.
     * 
     * @return The current height.
     */
    public int getHeight()
    {
        return (int) Math.ceil(pHeight / 100.0 * maxHeight);
    }

    /**
     * Get the current width percent.
     * 
     * @return The current width percent.
     */
    public int getWidthPercent()
    {
        return pWidth;
    }

    /**
     * Get the current height percent.
     * 
     * @return The current height percent.
     */
    public int getHeightPercent()
    {
        return pHeight;
    }

    /**
     * Get the maximum width.
     * 
     * @return The maximum width.
     */
    public int getWidthMax()
    {
        return maxWidth;
    }

    /**
     * Get the maximum height.
     * 
     * @return The maximum height.
     */
    public int getHeightMax()
    {
        return maxHeight;
    }
}
