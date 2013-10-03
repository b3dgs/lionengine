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
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.core.ColorRgba;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;

/**
 * <p>
 * Sprites work like images, but support modifications, such as scaling and filtering. They are recommended for dynamic
 * uses, such as menus, entities or backgrounds elements (which are not statics).
 * </p>
 * <p>
 * For each modifications (scale, flip, rotate, filter...), the original surface is kept. So <code>rotate(1)</code>
 * followed by <code>rotate(-1)</code> will give the same sprite as before.
 * </p>
 * <p>
 * There are two steps for the initialization:
 * </p>
 * <ul>
 * <li>Create the sprite.</li>
 * <li>Call {@link #load(boolean)} (this function will load the surface)</li>
 * </ul>
 * <p>
 * <p>
 * A non loaded sprite can be displayed (nothing will be displayed), but the sprite information are available (size).
 * However, sprite manipulation will throw an exception as the surface is not available.
 * </p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Load
 * final Sprite sprite = Drawable.loadSprite(Media.get(&quot;sprite.png&quot;));
 * sprite.load(false);
 * 
 * // Render
 * sprite.render(g, 64, 280);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Sprite
        extends Image
{
    /**
     * Load surface and prepare it to be displayed. This function must be called if the surface is loaded from a file,
     * else the surface will never be prepared.
     * 
     * @param alpha Set <code>true</code> to enable alpha, <code>false</code> else.
     */
    void load(boolean alpha);

    /**
     * Method used for sprite scaling, in order to modify its size. Normal factor is equal to <code>100</code>, so
     * <code>200</code> will scale it twice bigger, whereas <code>50</code> will scale half its size.
     * 
     * @param percent The value for scaling in percent (> 0).
     */
    void scale(int percent);

    /**
     * Works as scale, but using different width and height factor. Using different value, the ratio won't be kept, and
     * the sprite will be different.
     * 
     * @param percentWidth The percent value for scaling width (> 0).
     * @param percentHeight The percent value for scaling height (> 0).
     */
    void stretch(int percentWidth, int percentHeight);

    /**
     * Rotate the sprite with the specified angle in degree.
     * 
     * @param angle The rotation angle in degree <code>[0 - 360]</code>.
     */
    void rotate(int angle);

    /**
     * Flip the sprite horizontally (horizontal mirror).
     */
    void flipHorizontal();

    /**
     * Flip the sprite vertically (vertical mirror).
     */
    void flipVertical();

    /**
     * Apply a filter to the sprite.
     * 
     * @param filter The filter to use.
     */
    void filter(Filter filter);

    /**
     * Set transparency color mask.
     * 
     * @param mask The color mask.
     */
    void setTransparency(ColorRgba mask);

    /**
     * Set alpha value. The lower is the value, the higher will be the ghost effect.
     * 
     * @param alpha The alpha value <code>[0 - 255]</code>.
     */
    void setAlpha(int alpha);

    /**
     * Get the current sprite width (its current size, after scaling operation).
     * 
     * @return sprite The sprite width.
     */
    int getWidthOriginal();

    /**
     * Get the current sprite height (its current size, after scaling operation).
     * 
     * @return sprite The sprite height.
     */
    int getHeightOriginal();

    /*
     * Renderable
     */

    /**
     * Render the sprite on graphic output at specified coordinates.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    @Override
    void render(Graphic g, int x, int y);

    /*
     * Image
     */

    /**
     * Get the sprite surface.
     * 
     * @return The buffer reference representing the sprite.
     */
    @Override
    ImageBuffer getSurface();
}
