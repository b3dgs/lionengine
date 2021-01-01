/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * ParallaxedSprites are used for parallax effect (2.5D perspective). It cuts a sprite surface into an array of lines.
 * They are scaled using a trapeze representation, for the perspective effect:
 * 
 * <pre>
 *   &#47;======\
 *  &#47;========\
 * &#47;==========\
 * </pre>
 * 
 * <p>
 * Usage should be as following:
 * </p>
 * <ul>
 * <li>Create the instance.</li>
 * <li>Scale if necessary with {@link #stretch(int, int)}.</li>
 * <li>Call {@link #load(boolean)}.</li>
 * <li>Then other functions can be used.</li>
 * </ul>
 */
public interface SpriteParallaxed
{
    /**
     * Load surface and prepare it to be displayed. This function must be called if the surface is loaded from a file,
     * else the surface will never be prepared.
     * 
     * @param alpha Set <code>true</code> to enable alpha, <code>false</code> else.
     * @throws LionEngineException If an error occurred when reading the image.
     */
    void load(boolean alpha);

    /**
     * Works as scale, but using different width and height factor. Using different values, the ratio won't be kept, and
     * the sprite will be different !
     * 
     * @param percentWidth The percent value for scaling width (strictly positive).
     * @param percentHeight The percent value for scaling height (strictly positive).
     */
    void stretch(int percentWidth, int percentHeight);

    /**
     * Render a line of parallax to the specified coordinates.
     * 
     * @param g The graphic output.
     * @param line The line to render (positive).
     * @param x The abscissa.
     * @param y The ordinate.
     */
    void render(Graphic g, int line, int x, int y);

    /**
     * Get a parallax line width.
     * 
     * @param line The desired line (positive).
     * @return The line width.
     */
    int getLineWidth(int line);

    /**
     * Get the element width.
     * 
     * @return The element width.
     */
    int getWidth();

    /**
     * Get the element height.
     * 
     * @return The element height.
     */
    int getHeight();
}
