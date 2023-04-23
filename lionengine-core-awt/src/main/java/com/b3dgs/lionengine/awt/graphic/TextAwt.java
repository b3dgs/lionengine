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
package com.b3dgs.lionengine.awt.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Text implementation.
 */
final class TextAwt implements Text
{
    /**
     * Get the style equivalence.
     * 
     * @param style The font style (must not be <code>null</code>).
     * @return The equivalence.
     * @throws LionEngineException If invalid argument.
     */
    private static int getStyle(TextStyle style)
    {
        final int value;
        if (TextStyle.NORMAL == style)
        {
            value = Font.TRUETYPE_FONT;
        }
        else if (TextStyle.BOLD == style)
        {
            value = Font.BOLD;
        }
        else if (TextStyle.ITALIC == style)
        {
            value = Font.ITALIC;
        }
        else
        {
            throw new LionEngineException(style);
        }
        return value;
    }

    /** Color cache. */
    private final Map<ColorRgba, Color> colorCache = new HashMap<>();
    /** Glyph vector cache. */
    private final Map<String, GlyphVector> textCache = new HashMap<>();
    /** Text java font. */
    private final Font font;
    /** Text size. */
    private final int size;
    /** Text location x. */
    private double x;
    /** Text location y. */
    private double y;
    /** Text width. */
    private int w;
    /** Text height. */
    private int h;
    /** String to render. */
    private String txt;
    /** Text alignment. */
    private Align align = Align.LEFT;
    /** Text color. */
    private ColorRgba color = ColorRgba.WHITE;
    /** Changed flag. */
    private boolean txtChanged;

    /**
     * Internal constructor.
     * 
     * @param fontName The font name (must not be <code>null</code>).
     * @param size The font size in pixel.
     * @param style The font style.
     * @throws LionEngineException If invalid arguments.
     */
    TextAwt(String fontName, int size, TextStyle style)
    {
        super();

        Check.notNull(fontName);
        Check.notNull(style);

        this.size = size;
        font = new Font(fontName, TextAwt.getStyle(style), size);
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
        final Graphics2D g2d = (Graphics2D) g.getGraphic();
        final FontRenderContext context = g2d.getFontRenderContext();
        final Rectangle2D textSize = font.getStringBounds(text, context);
        final int tx;
        final int ty;

        if (Align.LEFT == alignment)
        {
            tx = x;
            ty = (int) textSize.getHeight() + y;
        }
        else if (Align.CENTER == alignment)
        {
            tx = x - (int) textSize.getWidth() / 2;
            ty = (int) textSize.getHeight() + y;
        }
        else if (Align.RIGHT == alignment)
        {
            tx = x - (int) textSize.getWidth();
            ty = (int) textSize.getHeight() + y;
        }
        else
        {
            throw new LionEngineException(alignment);
        }

        final Color colorOld = g2d.getColor();
        g2d.setColor(colorCache.get(color));

        final GlyphVector glyphVector = textCache.computeIfAbsent(text, t -> font.createGlyphVector(context, t));
        g2d.drawGlyphVector(glyphVector, tx, ty - size / 2.0F);
        g2d.setColor(colorOld);
    }

    @Override
    public void render(Graphic g)
    {
        draw(g, (int) Math.floor(x), (int) Math.floor(y), align, txt);
        if (txtChanged)
        {
            w = getStringWidth(g, txt);
            h = getStringHeight(g, txt);
            txtChanged = false;
        }
    }

    @Override
    public void setLocation(double x, double y)
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
        colorCache.computeIfAbsent(color, c -> new Color(c.getRgba(), true));
        this.color = color;
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public double getLocationX()
    {
        return x;
    }

    @Override
    public double getLocationY()
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
