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
package com.b3dgs.lionengine.core;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;

/**
 * Text implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class TextAwt
        implements Text
{
    /**
     * Get the style equivalence.
     * 
     * @param style The font style.
     * @return The equivalence.
     */
    private static int getStyle(TextStyle style)
    {
        switch (style)
        {
            case NORMAL:
                return Font.TRUETYPE_FONT;
            case BOLD:
                return Font.BOLD;
            case ITALIC:
                return Font.ITALIC;
            default:
                return Font.TYPE1_FONT;
        }
    }

    /** Text java font. */
    private final Font font;
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
     * Constructor.
     * 
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     */
    TextAwt(String fontName, int size, TextStyle style)
    {
        this.size = size;
        font = new Font(fontName, TextAwt.getStyle(style), size);
        align = Align.LEFT;
        color = ColorRgba.WHITE;
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
        final Graphics2D g2d = g.getGraphic();
        final FontRenderContext context = g2d.getFontRenderContext();
        final GlyphVector glyphVector = font.createGlyphVector(context, text);
        final Rectangle2D textSize = font.getStringBounds(text, context);
        final int tx;
        final int ty;

        switch (alignment)
        {
            case LEFT:
                tx = x;
                ty = (int) textSize.getHeight() + y;
                break;
            case CENTER:
                tx = x - (int) textSize.getWidth() / 2;
                ty = (int) textSize.getHeight() + y;
                break;
            case RIGHT:
                tx = x - (int) textSize.getWidth();
                ty = (int) textSize.getHeight() + y;
                break;
            default:
                throw new RuntimeException();
        }

        final ColorRgba colorOld = g.getColor();
        g.setColor(color);
        g2d.drawGlyphVector(glyphVector, tx, ty - size / 2);
        g.setColor(colorOld);
    }

    @Override
    public void render(Graphic g)
    {
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
        return (int) font.getStringBounds(str, ((Graphics2D) g.getGraphic()).getFontRenderContext()).getWidth();
    }

    @Override
    public int getStringHeight(Graphic g, String str)
    {
        return (int) font.getStringBounds(str, ((Graphics2D) g.getGraphic()).getFontRenderContext()).getHeight();
    }
}
