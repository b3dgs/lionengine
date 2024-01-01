/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * @param width The resolution width in pixel.
 * @param height The resolution height in pixel.
 * @param rate The refresh rate.
 */
public record Resolution(int width, int height, int rate)
{

    /** Double factor. */
    private static final int FACTOR_2X = 2;
    /** Triple factor. */
    private static final int FACTOR_3X = 3;
    /** Quad factor. */
    private static final int FACTOR_4X = 4;

    /**
     * Create a resolution.
     * 
     * @param width The resolution width in pixel (strictly superior to 0).
     * @param height The resolution height in pixel (strictly superior to 0).
     * @param rate The refresh rate, usually 50 or 60 (superior or equal to -1).
     * @throws LionEngineException If invalid arguments.
     */
    public Resolution
    {
        Check.superiorStrict(width, 0);
        Check.superiorStrict(height, 0);
        Check.superiorOrEqual(rate, -1);
    }

    /**
     * Get scaled by 2.
     * 
     * @return The scaled by 2 resolution.
     */
    public Resolution get2x()
    {
        return getScaled(FACTOR_2X, FACTOR_2X);
    }

    /**
     * Get scaled by 3.
     * 
     * @return The scaled by 3 resolution.
     */
    public Resolution get3x()
    {
        return getScaled(FACTOR_3X, FACTOR_3X);
    }

    /**
     * Get scaled by 4.
     * 
     * @return The scaled by 4 resolution.
     */
    public Resolution get4x()
    {
        return getScaled(FACTOR_4X, FACTOR_4X);
    }

    /**
     * Get scaled resolution.
     * 
     * @param factor The scale factor (strictly superior to 0).
     * @return The scaled resolution.
     * @throws LionEngineException If invalid arguments.
     */
    public Resolution getScaled(double factor)
    {
        Check.superiorStrict(factor, 0);

        final double ratio = width / (double) height;
        final double h = Math.ceil(height * factor);
        final double w = Math.ceil(h * ratio);

        return new Resolution((int) w, (int) h, rate);
    }

    /**
     * Get scaled resolution.
     * 
     * @param factorX The horizontal scale factor (strictly superior to 0).
     * @param factorY The vertical scale factor (strictly superior to 0).
     * @return The scaled resolution.
     * @throws LionEngineException If invalid arguments.
     */
    public Resolution getScaled(double factorX, double factorY)
    {
        Check.superiorStrict(factorX, 0);
        Check.superiorStrict(factorY, 0);

        return new Resolution((int) (width * factorX), (int) (height * factorY), rate);
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
     * Get the display rate.
     * 
     * @return The display rate.
     */
    public int getRate()
    {
        return rate;
    }
}
