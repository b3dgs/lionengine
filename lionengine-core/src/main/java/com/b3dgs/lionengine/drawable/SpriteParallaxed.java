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
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;

/**
 * ParallaxedSprites are used for parallax effect (2.5D perspective). It cuts a sprite surface into an array of lines.
 * They are scaled using a trapeze representation, for the perspective effect:
 * 
 * <pre>
 *   &#47;======\
 *  &#47;========\
 * &#47;==========\
 * </pre>
 * <p>
 * Usage should be as following:
 * <ul>
 * <li>Create the instance with {@link Drawable#loadSpriteParallaxed(Media, int, int, int)}</li>
 * <li>Scale if necessary with {@link #scale(int)} or {@link #stretch(int, int)}</li>
 * <li>Call {@link #prepare(Filter)}</li>
 * <li>Then other functions can be used.</li>
 * </ul>
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface SpriteParallaxed
        extends Renderable
{
    /**
     * Method used for sprite scaling, in order to modify its size. Normal factor is equal to 100, so 200 will scale it
     * twice bigger, whereas 50 will scale half its size.
     * 
     * @param percent value for scaling (> 0).
     */
    void scale(int percent);

    /**
     * Works as scale, but using different width and height factor. Using different values, the ratio won't be kept, and
     * the sprite will be different !
     * 
     * @param percentWidth The percent value for scaling width (> 0).
     * @param percentHeight The percent value for scaling height (> 0).
     */
    void stretch(int percentWidth, int percentHeight);

    /**
     * Update all changes. Need to be called when changes are done.
     * 
     * @param filter The filter to use.
     */
    void prepare(Filter filter);

    /**
     * Render a line of parallax to the specified coordinates.
     * 
     * @param g The graphic output.
     * @param line The line to render (>= 0).
     * @param x The abscissa.
     * @param y The ordinate.
     */
    void render(Graphic g, int line, int x, int y);

    /**
     * Get the current sprite width (its current size, after scaling operation).
     * 
     * @return The sprite width size as integer.
     */
    int getWidthOriginal();

    /**
     * Get the current sprite height (its current size, after scaling operation).
     * 
     * @return The sprite height size as integer.
     */
    int getHeightOriginal();

    /**
     * Get a parallax line (store it on a new buffered image, no reference, can be slow).
     * 
     * @param line The desired line (>= 0).
     * @return The line's surface.
     */
    ImageBuffer getLine(int line);
}
