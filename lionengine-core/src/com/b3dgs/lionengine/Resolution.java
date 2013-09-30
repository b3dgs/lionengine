/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

/**
 * Describes a display resolution. It allows to define different parameters:
 * <ul>
 * <li><code>width & height</code> : represent the screen size</li>
 * <li><code>ratio</code>, which is computed by using the <code>width & height</code>, allows to know the screen ratio.</li>
 * <li><code>rate</code> : represents the screen refresh rate (in frames per seconds)</li>
 * </ul>
 * This class is mainly used to describe the display resolution chosen.
 */
public final class Resolution
{
    /** Resolution width. */
    private int width;
    /** Resolution height. */
    private int height;
    /** Resolution ratio. */
    private double ratio;
    /** Display rate. */
    private int rate;

    /**
     * Constructor.
     * 
     * @param width The resolution width (in pixel).
     * @param height The resolution height (in pixel).
     * @param rate The refresh rate (usually 50 or 60).
     */
    public Resolution(int width, int height, int rate)
    {
        set(width, height, rate);
    }

    /**
     * Set the resolution.
     * 
     * @param width The resolution width (in pixel).
     * @param height The resolution height (in pixel).
     */
    public void set(int width, int height)
    {
        set(width, height, rate);
    }

    /**
     * Set the resolution.
     * 
     * @param width The resolution width (in pixel).
     * @param height The resolution height (in pixel).
     * @param rate The refresh rate in hertz (usually 50 or 60).
     */
    public void set(int width, int height, int rate)
    {
        Check.argument(width > 0 && height > 0 && rate >= 0);
        this.width = width;
        this.height = height;
        ratio = width / (double) height;
        this.rate = rate;
    }

    /**
     * Set the ratio and adapt the resolution to the new ratio (based on the height value).
     * 
     * @param ratio The new ratio.
     */
    public void setRatio(double ratio)
    {
        if (!Ratio.equals(this.ratio, ratio))
        {
            width = (int) Math.ceil(height * ratio);
            width = (int) Math.floor(width / 2.0) * 2;
            this.ratio = ratio;
        }
    }

    /**
     * Set the refresh rate value in hertz.
     * 
     * @param rate The refresh rate value.
     */
    public void setRate(int rate)
    {
        this.rate = rate;
    }

    /**
     * Get the resolution width.
     * 
     * @return The resolution width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get the resolution height.
     * 
     * @return The resolution height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get the resolution ratio.
     * 
     * @return The resolution ratio.
     */
    public double getRatio()
    {
        return ratio;
    }

    /**
     * Get the display rate.
     * 
     * @return The display rate.
     */
    public int getRate()
    {
        return rate;
    }
}
