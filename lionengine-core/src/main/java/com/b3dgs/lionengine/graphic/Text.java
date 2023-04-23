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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Align;

/**
 * The text allows to render any {@link String} on screen, at a specified location, using a specified font.
 * <p>
 * A font is described by a:
 * </p>
 * <ul>
 * <li>name, as {@link String}</li>
 * <li>size, as <code>int</code>, in pixel</li>
 * <li>style, as <code>int</code> ({@link com.b3dgs.lionengine.graphic.TextStyle#NORMAL},
 * {@link com.b3dgs.lionengine.graphic.TextStyle#ITALIC}, {@link com.b3dgs.lionengine.graphic.TextStyle#BOLD})</li>
 * </ul>
 * Two ways are possible for text rendering:
 * <ul>
 * <li>Direct rendering, by using:
 * <ul>
 * <li>{@link Text#draw(Graphic, int, int, String)}</li>
 * <li>{@link Text#draw(Graphic, int, int, Align, String)}</li>
 * </ul>
 * </li>
 * <li>Defined rendering, by using:
 * <ul>
 * <li>Setting part (one time is enough)
 * <ul>
 * <li>{@link Text#setLocation(double, double)}</li>
 * <li>{@link Text#setAlign(Align)}</li>
 * <li>{@link Text#setColor(ColorRgba)}</li>
 * <li>{@link Text#setText(String)}</li>
 * </ul>
 * </li>
 * <li>Rendering part
 * <ul>
 * <li>{@link Text#render(Graphic)}</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 */
public interface Text extends Renderable
{
    /**
     * Renders text on graphic output, to the specified location.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param text The text string.
     */
    void draw(Graphic g, int x, int y, String text);

    /**
     * Renders text on graphic output, to the specified location and alignment.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param alignment The alignment value.
     * @param text The text string.
     */
    void draw(Graphic g, int x, int y, Align alignment, String text);

    /**
     * Set text location.
     * 
     * @param x The text x.
     * @param y The text y.
     */
    void setLocation(double x, double y);

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
     * Set the new text color value.
     * 
     * @param color The color value.
     */
    void setColor(ColorRgba color);

    /**
     * Get the text size in pixel (8px, 12px...).
     * 
     * @return The text size as integer.
     */
    int getSize();

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    double getLocationX();

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    double getLocationY();

    /**
     * Get the text width.
     * 
     * @return The text width.
     */
    int getWidth();

    /**
     * Get the text height.
     * 
     * @return The text height.
     */
    int getHeight();

    /**
     * Get string width size.
     * 
     * @param g The graphic context.
     * @param str The input string.
     * @return The string width size.
     */
    int getStringWidth(Graphic g, String str);

    /**
     * Get string height size.
     * 
     * @param g The graphic context.
     * @param str The input string.
     * @return The string height size.
     */
    int getStringHeight(Graphic g, String str);
}
