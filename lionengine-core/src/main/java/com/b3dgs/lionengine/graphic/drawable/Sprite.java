/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;

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
 * <li>Call {@link #load()} (this function will load the surface)</li>
 * </ul>
 * <p>
 * A non loaded sprite can be displayed (nothing will be displayed), but the sprite information are available (size).
 * However, sprite manipulation will throw an exception as the surface is not available.
 * </p>
 */
public interface Sprite extends Image
{
    /**
     * Stretch and resize the image to a different percent. Using different value, the ratio won't be kept,
     * and the sprite will be different.
     * 
     * @param percentWidth The percent value for scaling width (strictly positive).
     * @param percentHeight The percent value for scaling height (strictly positive).
     * @throws LionEngineException If arguments are invalid.
     */
    void stretch(double percentWidth, double percentHeight);

    /**
     * Rotate the sprite with the specified angle in degree.
     * 
     * @param angle The rotation angle in degree <code>[0 - 359]</code>.
     */
    void rotate(int angle);

    /**
     * Apply a filter to the sprite.
     * 
     * @param filter The filter to use (must not be <code>null</code>).
     * @throws LionEngineException If the filter is not supported.
     */
    void filter(Filter filter);

    /**
     * Set frame offsets (offsets on rendering).
     * 
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     */
    void setFrameOffsets(int offsetX, int offsetY);

    /**
     * Set transparency color mask.
     * 
     * @param mask The color mask (must not be <code>null</code>).
     */
    void setTransparency(ColorRgba mask);

    /**
     * Set alpha value. The lower is the value, the higher will be the ghost effect.
     * 
     * @param alpha The alpha value <code>[0 - 255]</code>.
     * @throws LionEngineException If argument is invalid.
     */
    void setAlpha(int alpha);

    /**
     * Set angle anchor for rotation referential.
     * 
     * @param angleX The horizontal anchor.
     * @param angleY The vertical anchor.
     */
    void setAngleAnchor(int angleX, int angleY);

    /**
     * Set the mirror state. The surface will not be modified, as flipping is directly done during rendering process.
     * 
     * @param mirror Set the mirror type to use (must not be <code>null</code>).
     * @throws LionEngineException If invalid mirror type.
     */
    void setMirror(Mirror mirror);

    /**
     * Return the current mirror state used.
     * 
     * @return The current mirror state.
     */
    Mirror getMirror();
}
