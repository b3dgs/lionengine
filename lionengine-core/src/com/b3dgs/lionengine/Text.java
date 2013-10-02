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
package com.b3dgs.lionengine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/**
 * The text allows to render any {@link String} on screen, at a specified location, using a specified {@link Font}.
 * <p>
 * A font is described by a:
 * </p>
 * <ul>
 * <li>name, as {@link String} (or a {@link Font} instance)</li>
 * <li>size, as <code>int</code>, in pixel</li>
 * <li>style, as <code>int</code> ({@link Text#NORMAL}, {@link Text#ITALIC}, {@link Text#BOLD})</li>
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
 * <li>{@link Text#setLocation(int, int)}</li>
 * <li>{@link Text#setAlign(Align)}</li>
 * <li>{@link Text#setColor(Color)}</li>
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
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Create the text
 * Text text = new Text(Font.SANS_SERIF, 12, Text.NORMAL);
 * 
 * // Rendering type 1
 * text.setText(&quot;Hello&quot;);
 * text.setLocation(0, 0);
 * text.setAlign(Align.CENTER);
 * text.render(g);
 * 
 * // Rendering type 2
 * text.draw(g, 0, 0, &quot;World&quot;);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Align
 */
public class Text
{
    /** Normal text. */
    public static final int NORMAL = Font.TRUETYPE_FONT;
    /** Italic text. */
    public static final int ITALIC = Font.ITALIC;
    /** Bold text. */
    public static final int BOLD = Font.BOLD;

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
    private Align align = Align.LEFT;
    /** Text color. */
    private Color color = Color.WHITE;
    /** Changed flag. */
    private boolean txtChanged = false;

    /**
     * Constructor.
     * 
     * @param fontName The font name ({@link Font}).
     * @param size The font size (in pixel).
     * @param style The font style ({@link Text}).
     * @see Font#DIALOG
     * @see Font#MONOSPACED
     * @see Font#SANS_SERIF
     * @see Font#SERIF
     * @see Text#NORMAL
     * @see Text#BOLD
     * @see Text#ITALIC
     */
    public Text(String fontName, int size, int style)
    {
        this.size = size;
        font = new Font(fontName, style, size);
    }

    /**
     * Renders text on graphic output, to the specified location.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param text The text string.
     */
    public void draw(Graphic g, int x, int y, String text)
    {
        draw(g, x, y, Align.LEFT, text);
    }

    /**
     * Renders text on graphic output, to the specified location and alignment.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param alignment The alignment value.
     * @param text The text string.
     */
    public void draw(Graphic g, int x, int y, Align alignment, String text)
    {
        final Graphics2D g2d = g.getGraphics();
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

        final Color colorOld = g.getColor();
        g.setColor(color);
        g2d.drawGlyphVector(glyphVector, tx, ty - size / 2);
        g.setColor(colorOld);
    }

    /**
     * Render configured text.
     * 
     * @param g The graphic output.
     */
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

    /**
     * Set text location.
     * 
     * @param x The text x.
     * @param y The text y.
     */
    public void setLocation(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set text to render.
     * 
     * @param text The text to render.
     */
    public void setText(String text)
    {
        txt = text;
        txtChanged = true;
    }

    /**
     * Set text alignment.
     * 
     * @param align The text alignment.
     */
    public void setAlign(Align align)
    {
        this.align = align;
    }

    /**
     * Set the new text color value.
     * 
     * @param color The color value.
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Get the text size in pixel (8px, 12px...).
     * 
     * @return The text size as integer.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getLocationX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public int getLocationY()
    {
        return y;
    }

    /**
     * Get the text width.
     * 
     * @return The text width.
     */
    public int getWidth()
    {
        return w;
    }

    /**
     * Get the text height.
     * 
     * @return The text height.
     */
    public int getHeight()
    {
        return h;
    }

    /**
     * Get string width size.
     * 
     * @param g The graphic context.
     * @param str The input string.
     * @return The string width size.
     */
    public int getStringWidth(Graphic g, String str)
    {
        return (int) font.getStringBounds(str, g.getGraphics().getFontRenderContext()).getWidth();
    }

    /**
     * Get string height size.
     * 
     * @param g The graphic context.
     * @param str The input string.
     * @return The string height size.
     */
    public int getStringHeight(Graphic g, String str)
    {
        return (int) font.getStringBounds(str, g.getGraphics().getFontRenderContext()).getHeight();
    }
}
