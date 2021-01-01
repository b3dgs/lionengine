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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Align;

/**
 * Mock text.
 */
final class TextMock implements Text
{
    /** Text size. */
    private final int size;
    /** Text location x. */
    private int x;
    /** Text location y. */
    private int y;
    /** Width. */
    private int width;
    /** Height. */
    private int height;
    /** Text. */
    private String text;

    /**
     * Constructor.
     * 
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     */
    TextMock(String fontName, int size, TextStyle style)
    {
        this.size = size;
    }

    /*
     * Text
     */

    @Override
    public void draw(Graphic g, int x, int y, String text)
    {
        // Mock
    }

    @Override
    public void draw(Graphic g, int x, int y, Align alignment, String text)
    {
        // Mock
    }

    @Override
    public void render(Graphic g)
    {
        width = text.length() * size;
        height = size;
    }

    @Override
    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public void setAlign(Align align)
    {
        // Mock
    }

    @Override
    public void setColor(ColorRgba color)
    {
        // Mock
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public int getLocationX()
    {
        return x;
    }

    @Override
    public int getLocationY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getStringWidth(Graphic g, String str)
    {
        return 0;
    }

    @Override
    public int getStringHeight(Graphic g, String str)
    {
        return 0;
    }
}
