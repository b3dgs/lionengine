/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.GradientColor;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.UtilityMath;

/**
 * Surface representing a bar designed to display a kind of progress bar (life...).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Bar
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
    private int pWidth;
    /** Current height percent. */
    private int pHeight;
    /** Horizontal border. */
    private int hBorder;
    /** Vertical border. */
    private int vBorder;
    /** Background color. */
    private ColorRgba background;
    /** Foreground color. */
    private ColorRgba foreground;
    /** Gradient color. */
    private GradientColor gradientColor;
    /** Left-Right referential. */
    private boolean leftRight;
    /** Up-Down referential. */
    private boolean upDown;

    /**
     * Constructor.
     * 
     * @param width The maximum width.
     * @param height The maximum height.
     */
    public Bar(int width, int height)
    {
        x = 0;
        y = 0;
        maxWidth = width;
        maxHeight = height;
        pWidth = 100;
        pHeight = 100;
        vBorder = 0;
        hBorder = 0;
        leftRight = true;
        upDown = false;
        background = null;
        foreground = null;
    }

    /**
     * Render the bar.
     * 
     * @param g The graphic output.
     */
    public void render(Graphic g)
    {
        if (background != null)
        {
            g.setColor(background);
            g.drawRect(x, y, maxWidth, maxHeight, true);
        }

        final int rx = maxWidth - hBorder * 2;
        final int ry = maxHeight - vBorder * 2;
        if (!(pWidth == 0 || pHeight == 0))
        {
            final int x1, y1, x2, y2;
            if (leftRight)
            {
                x1 = x + hBorder;
                x2 = (int) (rx * (pWidth / 100.0));
            }
            else
            {
                x1 = x + hBorder + (int) (rx - pWidth / 100.0 * rx);
                x2 = (int) Math.ceil(rx * (pWidth / 100.0));
            }
            if (upDown)
            {
                y1 = y + vBorder;
                y2 = (int) (ry * (pHeight / 100.0));
            }
            else
            {
                y1 = y + vBorder + (int) (ry - pHeight / 100.0 * ry);
                y2 = (int) Math.ceil(ry * (pHeight / 100.0));
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
        gradientColor = new GradientColor(x1, y1, color1, x2, y2, color2);
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
        pWidth = UtilityMath.fixBetween(widthPercent, 0, 100);
    }

    /**
     * Set the height percent.
     * 
     * @param heightPercent The height percent [0-100].
     */
    public void setHeightPercent(int heightPercent)
    {
        pHeight = UtilityMath.fixBetween(heightPercent, 0, 100);
    }

    /**
     * Get the current width.
     * 
     * @return The current width.
     */
    public int getWidth()
    {
        return (int) Math.floor(pWidth / 100.0 * maxWidth);
    }

    /**
     * Get the current height.
     * 
     * @return The current height.
     */
    public int getHeight()
    {
        return (int) Math.floor(pHeight / 100.0 * maxHeight);
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
