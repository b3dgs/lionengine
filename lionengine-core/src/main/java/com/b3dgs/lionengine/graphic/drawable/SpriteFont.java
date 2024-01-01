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
package com.b3dgs.lionengine.graphic.drawable;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * <p>
 * SpriteFont is an extended sprite that allows to handle a font based on an image. This way is it possible to create a
 * custom font, were data (contained in an XML file) describes each letter size.
 * </p>
 * <p>
 * It is based on a {@link SpriteTiled}, which allow to control each letter separately. There is then a correspondence
 * between each character and their image. It also supports a full text rendering, where the special character
 * {@link com.b3dgs.lionengine.Constant#PERCENT} can be used to insert a new line during rendering.
 * </p>
 * <p>
 * The usage is simple:
 * </p>
 * <ul>
 * <li>{@link #draw(Graphic, int, int, Align, String)} can be used to render a single text (which can contains
 * {@link com.b3dgs.lionengine.Constant#PERCENT} characters).</li>
 * </ul>
 * <p>
 * As the SpriteFont is a sprite, is supports all sprite operations.
 * </p>
 */
public interface SpriteFont extends Sprite
{
    /**
     * Draw a single text at specified location. Multiple lines can be used with
     * {@link com.b3dgs.lionengine.Constant#PERCENT}.
     * 
     * @param g The graphics output.
     * @param x The location x.
     * @param y The location y.
     * @param align The alignment.
     * @param text The input text.
     */
    void draw(Graphic g, int x, int y, Align align, String text);

    /**
     * Set text to render.
     * 
     * @param text The text to render.
     */
    void setText(String text);

    /**
     * Set text alignment.
     * 
     * @param align The text alignment.
     */
    void setAlign(Align align);

    /**
     * Set line height (default = letter height).
     * 
     * @param height The line height.
     */
    void setLineHeight(int height);

    /**
     * Get global text width.
     * 
     * @param text The input text.
     * @return The text width.
     */
    int getTextWidth(String text);

    /**
     * Get global text height.
     * 
     * @param text The input height.
     * @return The text height.
     */
    int getTextHeight(String text);
}
