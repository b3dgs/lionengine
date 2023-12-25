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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Font sprite implementation.
 */
final class SpriteFontImpl implements SpriteFont
{
    /** Error already loaded. */
    static final String ERROR_ALREADY_LOADED = "Surface has already been loaded: ";
    /** New line separator character. */
    private static final String NL_STR = Constant.EMPTY_STRING + Constant.PERCENT;
    /** Split with new line. */
    private static final Pattern NL = Pattern.compile(NL_STR);

    /**
     * Get character width depending of alignment.
     * 
     * @param text The text reference.
     * @param align The align.
     * @return The char width.
     */
    private int getCharWidth(String text, Align align)
    {
        final int width;
        if (align == Align.RIGHT)
        {
            width = getTextWidth(text);
        }
        else if (align == Align.CENTER)
        {
            width = getTextWidth(text) / 2;
        }
        else
        {
            width = 0;
        }
        return width;
    }

    /** Font data. */
    private final Map<Character, FontCharData> fontData = new TreeMap<>();
    /** Media reference. */
    private final Media media;
    /** Font surface. */
    private SpriteTiled surface;
    /** Text. */
    private String text = Constant.EMPTY_STRING;
    /** Alignment. */
    private Align align = Align.LEFT;
    /** Char width. */
    private final int tw;
    /** Line height value. */
    private int lineHeight;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Internal constructor.
     * 
     * @param media The font image media (must not be <code>null</code>).
     * @param mediaData The font data media (must not be <code>null</code>).
     * @param tw The horizontal character number (must be strictly positive).
     * @param th The vertical character number (must be strictly positive).
     * @throws LionEngineException If invalid arguments or an error occurred when creating the font.
     */
    SpriteFontImpl(Media media, Media mediaData, int tw, int th)
    {
        super();

        Check.notNull(media);
        Check.notNull(mediaData);

        this.media = media;
        this.tw = tw;
        lineHeight = th;

        loadData(mediaData);
    }

    /**
     * Internal constructor.
     * 
     * @param surface The surface reference (must not be <code>null</code>).
     * @param mediaData The font data media (must not be <code>null</code>).
     * @param tw The horizontal character number (must be strictly positive).
     * @param th The vertical character number (must be strictly positive).
     * @throws LionEngineException If invalid arguments or an error occurred when creating the font.
     */
    SpriteFontImpl(ImageBuffer surface, Media mediaData, int tw, int th)
    {
        super();

        Check.notNull(surface);
        Check.notNull(mediaData);

        this.surface = Drawable.loadSpriteTiled(surface, tw, th);
        media = null;
        this.tw = tw;
        lineHeight = th;

        loadData(mediaData);
    }

    /**
     * Load characters data.
     * 
     * @param mediaData The media data.
     */
    private void loadData(Media mediaData)
    {
        final XmlReader letters = new XmlReader(mediaData);
        final Collection<XmlReader> children = letters.getChildren();
        int id = 0;

        for (final XmlReader node : children)
        {
            final double width = node.getDouble("width");
            final double height = node.getDouble("height");
            final FontCharData data = new FontCharData(id, width, height);
            final String c = node.getString("char");
            fontData.put(Character.valueOf(c.charAt(0)), data);
            id++;
        }

        children.clear();
    }

    @Override
    public void load()
    {
        if (surface != null)
        {
            if (media != null)
            {
                throw new LionEngineException(media, ERROR_ALREADY_LOADED);
            }
            throw new LionEngineException(ERROR_ALREADY_LOADED);
        }
        surface = Drawable.loadSpriteTiled(Graphics.getImageBuffer(media), tw, lineHeight);
    }

    @Override
    public void prepare()
    {
        surface.prepare();
    }

    @Override
    public void dispose()
    {
        surface.dispose();
    }

    @Override
    public void stretch(double percentWidth, double percentHeight)
    {
        surface.stretch(percentWidth, percentHeight);
    }

    @Override
    public void rotate(int angle)
    {
        surface.rotate(angle);
    }

    @Override
    public void filter(Filter filter)
    {
        surface.filter(filter);
    }

    @Override
    public void render(Graphic g)
    {
        draw(g, (int) x, (int) y, align, text);
    }

    @Override
    public void draw(Graphic g, int x, int y, Align align, String text)
    {
        double lx = 0.0;
        double ly = 0.0;

        final String[] split = NL.split(text);
        for (int i = 0; i < split.length; i++)
        {
            final String word = split[i];
            final int width = getCharWidth(word, align);
            final int length = word.length();

            for (int j = 0; j < length; j++)
            {
                final FontCharData d = fontData.get(Character.valueOf(word.charAt(j)));
                if (d != null)
                {
                    surface.setLocation(x + lx - width, y + ly + d.getHeight());
                    surface.setTile(d.getId());
                    surface.render(g);
                    lx += d.getWidth() + 1;
                }
            }

            lx = 0;
            ly += lineHeight;
        }
    }

    @Override
    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public void setOrigin(Origin origin)
    {
        surface.setOrigin(origin);
    }

    @Override
    public void setFrameOffsets(int offsetX, int offsetY)
    {
        surface.setFrameOffsets(offsetX, offsetY);
    }

    @Override
    public void setLocation(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setLocation(Viewer viewer, Localizable localizable)
    {
        surface.setLocation(viewer, localizable);
    }

    @Override
    public void setTransparency(ColorRgba mask)
    {
        surface.setTransparency(mask);
    }

    @Override
    public void setAlpha(int alpha)
    {
        surface.setAlpha(alpha);
    }

    @Override
    public void setAngleAnchor(int angleX, int angleY)
    {
        surface.setAngleAnchor(angleX, angleY);
    }

    @Override
    public void setMirror(Mirror mirror)
    {
        surface.setMirror(mirror);
    }

    @Override
    public void setAlign(Align align)
    {
        this.align = align;
    }

    @Override
    public void setLineHeight(int height)
    {
        lineHeight = height;
    }

    @Override
    public int getTextWidth(String text)
    {
        final int length = text.length();
        int lx = 0;

        for (int i = 0; i < length; i++)
        {
            final FontCharData d = fontData.get(Character.valueOf(text.charAt(i)));
            if (d != null)
            {
                lx += d.getWidth() + 1;
            }
        }

        return lx;
    }

    @Override
    public int getTextHeight(String text)
    {
        final int length = text.length();
        int line = 1;

        // Search next line
        for (int i = 0; i < length; i++)
        {
            if (Constant.PERCENT.equals(String.valueOf(text.charAt(i))))
            {
                line++;
            }
        }

        return lineHeight * line;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public Mirror getMirror()
    {
        return surface.getMirror();
    }

    @Override
    public int getWidth()
    {
        return surface.getWidth();
    }

    @Override
    public int getHeight()
    {
        return surface.getHeight();
    }

    @Override
    public ImageBuffer getSurface()
    {
        if (surface == null)
        {
            return null;
        }
        return surface.getSurface();
    }

    @Override
    public boolean isLoaded()
    {
        if (surface == null)
        {
            return false;
        }
        return surface.isLoaded();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (getSurface() != null)
        {
            result = prime * result + getSurface().hashCode();
        }
        else
        {
            result = prime * result + media.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        return object == this;
    }
}
