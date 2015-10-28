/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * <li><code>width</code> and <code>height</code> : represent the screen size</li>
 * <li>
 * <code>ratio</code>, which is computed by using the <code>width</code> and <code>height</code>, allows to know the
 * screen ratio.</li>
 * <li><code>rate</code> : represents the screen refresh rate (in frames per seconds)</li>
 * </ul>
 * This class is mainly used to describe the display resolution chosen.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Resolution
{
    /** Lock. */
    private final Object lock = new Object();
    /** Display rate. */
    private volatile int rate;
    /** Resolution width (locked by {@link #lock}). */
    private int width;
    /** Resolution height (locked by {@link #lock}). */
    private int height;
    /** Resolution ratio (locked by {@link #lock}). */
    private double ratio;

    /**
     * Create a resolution.
     * 
     * @param width The resolution width (in pixel) (strictly positive).
     * @param height The resolution height (in pixel) (strictly positive).
     * @param rate The refresh rate (usually 50 or 60) (positive).
     * @throws LionEngineException If arguments are invalid.
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
     * @throws LionEngineException If arguments are invalid.
     */
    public void setSize(int width, int height)
    {
        synchronized (lock)
        {
            set(width, height, rate);
        }
    }

    /**
     * Set the ratio and adapt the resolution to the new ratio (based on the height value).
     * 
     * @param ratio The new ratio (strictly positive).
     * @throws LionEngineException If ratio is not strictly positive.
     */
    public void setRatio(double ratio)
    {
        Check.superiorStrict(ratio, 0);

        synchronized (lock)
        {
            if (!Ratio.equals(this.ratio, ratio))
            {
                width = (int) Math.ceil(height * ratio);
                width = (int) Math.floor(width / 2.0) * 2;
                this.ratio = ratio;
            }
        }
    }

    /**
     * Set the refresh rate value in hertz.
     * 
     * @param rate The refresh rate value (positive).
     * @throws LionEngineException If ratio is not strictly positive.
     */
    public void setRate(int rate)
    {
        Check.superiorOrEqual(rate, 0);

        this.rate = rate;
    }

    /**
     * Get the resolution width.
     * 
     * @return The resolution width.
     */
    public int getWidth()
    {
        synchronized (lock)
        {
            return width;
        }
    }

    /**
     * Get the resolution height.
     * 
     * @return The resolution height.
     */
    public int getHeight()
    {
        synchronized (lock)
        {
            return height;
        }
    }

    /**
     * Get the resolution ratio.
     * 
     * @return The resolution ratio.
     */
    public double getRatio()
    {
        synchronized (lock)
        {
            return ratio;
        }
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

    /**
     * Set the resolution.
     * 
     * @param width The resolution width (in pixel, strictly positive).
     * @param height The resolution height (in pixel, strictly positive).
     * @param rate The refresh rate in hertz (usually 50 or 60, positive).
     * @throws LionEngineException If arguments are invalid.
     */
    private void set(int width, int height, int rate)
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);
        Check.superiorOrEqual(rate, 0);

        this.width = width;
        this.height = height;
        this.rate = rate;
        ratio = width / (double) height;
    }
}
