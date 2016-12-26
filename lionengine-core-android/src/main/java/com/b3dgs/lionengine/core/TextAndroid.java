/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Text implementation.
 */
final class TextAndroid implements Text
{
    /**
     * Get the text style equivalence.
     * 
     * @param style The text style.
     * @return The text style.
     * @throws LionEngineException If unknown type.
     */
    private static int getStyle(TextStyle style)
    {
        final int value;
        switch (style)
        {
            case NORMAL:
                value = Typeface.NORMAL;
                break;
            case BOLD:
                value = Typeface.BOLD;
                break;
            case ITALIC:
                value = Typeface.ITALIC;
                break;
            default:
                throw new LionEngineException("Unknown type: " + style);
        }
        return value;
    }

    /** Paint. */
    final Paint paint;
    /** Text size. */
    private final int size;
    /** Text location x. */
    private int x;
    /** Text location y. */
    private int y;
    /** Text width. */
    private int w;
    /** Text height. */
    private int h;
    /** String to render. */
    private String txt;
    /** Text alignment. */
    private Align align;
    /** Text color. */
    private ColorRgba color;
    /** Changed flag. */
    private boolean txtChanged;

    /**
     * Internal constructor.
     * 
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     * @throws LionEngineException If unsupported style type.
     */
    TextAndroid(String fontName, int size, TextStyle style)
    {
        this.size = size;
        paint = new Paint();
        align = Align.LEFT;
        color = ColorRgba.WHITE;
        paint.setTextSize(size);
        paint.setColor(color.getRgba());
        paint.setTypeface(Typeface.create(fontName, TextAndroid.getStyle(style)));
    }

    /*
     * Text
     */

    @Override
    public void draw(Graphic g, int x, int y, String text)
    {
        draw(g, x, y, Align.LEFT, text);
    }

    @Override
    public void draw(Graphic g, int x, int y, Align alignment, String text)
    {
        final double ratio = 0.8;
        ((GraphicAndroid) g).drawString(x, y + (int) (size * ratio), alignment, text, paint);
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(color);
        draw(g, x, y, align, txt);
        if (txtChanged)
        {
            w = getStringWidth(g, txt);
            h = getStringHeight(g, txt);
            txtChanged = false;
        }
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
        txt = text;
        txtChanged = true;
    }

    @Override
    public void setAlign(Align align)
    {
        this.align = align;
    }

    @Override
    public void setColor(ColorRgba color)
    {
        this.color = color;
        paint.setColor(color.getRgba());
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
        return w;
    }

    @Override
    public int getHeight()
    {
        return h;
    }

    @Override
    public int getStringWidth(Graphic g, String str)
    {
        final Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.right - bounds.left;
    }

    @Override
    public int getStringHeight(Graphic g, String str)
    {
        final Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);
        return bounds.bottom - bounds.top;
    }
}
