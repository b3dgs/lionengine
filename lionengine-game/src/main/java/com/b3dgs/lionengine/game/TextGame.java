/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Game text implementation. This class enhance the text in order to use it in a game referential. It is perfect to
 * display text over an entity for example, or to give the impression that the text is in the world and not simply on
 * screen.
 * <p>
 * The use is strictly the same as {@link Text}, just including an additional function which is
 * {@link TextGame#update(Viewer)}, needed to update the text location.
 * </p>
 */
public class TextGame implements Text
{
    /** Text reference. */
    private final Text text;
    /** Location x. */
    private int x;
    /** Location y. */
    private int y;
    /** Camera view height. */
    private int height;

    /**
     * Create a text game.
     * 
     * @param fontName The font name.
     * @param size The font size.
     * @param style The font style.
     */
    public TextGame(String fontName, int size, TextStyle style)
    {
        super();

        text = Graphics.createText(fontName, size, style);
    }

    /**
     * Update game text to store current location view.
     * 
     * @param viewer The viewer reference.
     */
    public void update(Viewer viewer)
    {
        x = (int) viewer.getX();
        y = (int) viewer.getY() - viewer.getViewY();
        height = viewer.getHeight();
    }

    /**
     * Renders text on graphic output, to the specified location using the specified localizable referential.
     * 
     * @param g The graphic output.
     * @param localizable The localizable used to draw the text.
     * @param offsetX The horizontal offset from the localizable horizontal location.
     * @param offsetY The vertical offset from the localizable vertical location.
     * @param align The alignment value.
     * @param text The text string.
     */
    public void draw(Graphic g, Localizable localizable, int offsetX, int offsetY, Align align, String text)
    {
        draw(g, (int) localizable.getX() + offsetX, (int) localizable.getY() + offsetY, align, text);
    }

    /**
     * Renders text on graphic output, to the specified location using the specified localizable referential.
     * 
     * @param g The graphic output.
     * @param color The rectangle color.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The rectangle width.
     * @param height The rectangle height.
     */
    public void drawRect(Graphic g, ColorRgba color, int x, int y, int width, int height)
    {
        g.setColor(color);
        g.drawRect(x - this.x, this.y - y - height + this.height, width, height, false);
    }

    /*
     * Text
     */

    @Override
    public void draw(Graphic g, int x, int y, Align alignment, String text)
    {
        this.text.draw(g, x - this.x, this.y - y + height, alignment, text);
    }

    @Override
    public void draw(Graphic g, int x, int y, String text)
    {
        this.text.draw(g, x - this.x, this.y - y + height, text);
    }

    @Override
    public void render(Graphic g)
    {
        text.render(g);
    }

    @Override
    public void setLocation(int x, int y)
    {
        text.setLocation(x, y);
    }

    @Override
    public void setText(String text)
    {
        this.text.setText(text);
    }

    @Override
    public void setAlign(Align align)
    {
        text.setAlign(align);
    }

    @Override
    public void setColor(ColorRgba color)
    {
        text.setColor(color);
    }

    @Override
    public int getSize()
    {
        return text.getSize();
    }

    @Override
    public int getLocationX()
    {
        return text.getLocationX();
    }

    @Override
    public int getLocationY()
    {
        return text.getLocationY();
    }

    @Override
    public int getWidth()
    {
        return text.getWidth();
    }

    @Override
    public int getHeight()
    {
        return text.getHeight();
    }

    @Override
    public int getStringWidth(Graphic g, String str)
    {
        return text.getStringWidth(g, str);
    }

    @Override
    public int getStringHeight(Graphic g, String str)
    {
        return text.getStringHeight(g, str);
    }
}
