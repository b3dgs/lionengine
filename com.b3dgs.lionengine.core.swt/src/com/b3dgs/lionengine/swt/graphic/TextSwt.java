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
package com.b3dgs.lionengine.swt.graphic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;

/**
 * Text implementation.
 */
final class TextSwt implements Text
{
    /**
     * Get the style equivalence.
     * 
     * @param style The font style.
     * @return The equivalence.
     */
    private static int getStyle(TextStyle style)
    {
        final int value;
        if (TextStyle.NORMAL == style)
        {
            value = SWT.NORMAL;
        }
        else if (TextStyle.BOLD == style)
        {
            value = SWT.BOLD;
        }
        else if (TextStyle.ITALIC == style)
        {
            value = SWT.ITALIC;
        }
        else
        {
            throw new LionEngineException(style);
        }
        return value;
    }

    /** Device used. */
    private final Device device;
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
    private Align align;
    /** Text color. */
    private ColorRgba color;
    /** Changed flag. */
    private boolean txtChanged;

    /**
     * Internal constructor.
     * 
     * @param device The device reference.
     * @param fontName The font name.
     * @param size The font size (in pixel).
     * @param style The font style.
     */
    TextSwt(Device device, String fontName, int size, TextStyle style)
    {
        this.device = device;
        this.size = size;
        final double scale = 1.5;
        font = new Font(device, fontName, (int) Math.round(size / scale), TextSwt.getStyle(style));
        align = Align.LEFT;
        color = ColorRgba.WHITE;
    }

    @Override
    public void draw(Graphic g, int x, int y, String text)
    {
        draw(g, x, y, Align.LEFT, text);
    }

    @Override
    public void draw(Graphic g, int x, int y, Align alignment, String text)
    {
        final GC gc = (GC) g.getGraphic();
        gc.setTextAntialias(SWT.OFF);
        gc.setFont(font);
        final Point textSize = gc.stringExtent(text);
        final int tx;

        if (Align.LEFT == alignment)
        {
            tx = x;
        }
        else if (Align.CENTER == alignment)
        {
            tx = x - textSize.x / 2;
        }
        else if (Align.RIGHT == alignment)
        {
            tx = x - textSize.x;
        }
        else
        {
            throw new LionEngineException(alignment);
        }

        final Color c = new Color(device, color.getRed(), color.getGreen(), color.getBlue());
        gc.setForeground(c);
        gc.drawString(text, tx, y, true);
        c.dispose();
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
        return ((GC) g.getGraphic()).stringExtent(str).x;
    }

    @Override
    public int getStringHeight(Graphic g, String str)
    {
        return ((GC) g.getGraphic()).stringExtent(str).y;
    }
}
