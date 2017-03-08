/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.drawable;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Filter;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.SpriteFont;
import com.b3dgs.lionengine.graphic.SpriteTiled;
import com.b3dgs.lionengine.io.Xml;

/**
 * Font sprite implementation.
 */
final class SpriteFontImpl implements SpriteFont
{
    /** New line separator character. */
    private static final String NL_STR = Constant.EMPTY_STRING + SpriteFont.NEW_LINE;

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

    /** Font surface. */
    private final SpriteTiled surface;
    /** Font data. */
    private final Map<Character, Data> fontData;
    /** Text. */
    private String text = Constant.EMPTY_STRING;
    /** Alignment. */
    private Align align = Align.LEFT;
    /** Line height value. */
    private int lineHeight;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Internal constructor.
     * 
     * @param media The font image media.
     * @param mediaData The font data media.
     * @param tw The horizontal character number.
     * @param th The vertical character number.
     * @throws LionEngineException If an error occurred when creating the font.
     */
    SpriteFontImpl(Media media, Media mediaData, int tw, int th)
    {
        surface = new SpriteTiledImpl(media, tw, th);
        fontData = new TreeMap<Character, Data>();
        lineHeight = surface.getTileHeight();

        // Load data for each characters
        final Xml letters = new Xml(mediaData);
        final Collection<Xml> children = letters.getChildren();
        int id = 0;

        for (final Xml node : children)
        {
            final Data data = new Data(id, node.readInteger("width"), node.readInteger("height"));
            fontData.put(Character.valueOf(node.readString("char").charAt(0)), data);
            id++;
        }

        children.clear();
    }

    @Override
    public void load()
    {
        surface.load();
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
        final int length = text.length();
        int lx = 0;
        final int ly = 0;
        final int width = getCharWidth(text, align);
        for (int j = 0; j < length; j++)
        {
            final Data d = fontData.get(Character.valueOf(text.charAt(j)));
            surface.setLocation(x + lx - width, y + ly + d.getHeight());
            surface.setTile(d.getId());
            surface.render(g);
            lx += d.getWidth() + 1;
        }
    }

    @Override
    public void draw(Graphic g, int x, int y, Align align, String... texts)
    {
        int lx = 0;
        int ly = 0;

        // Render each character
        for (final String text : texts)
        {
            // Get char width
            final int width = getCharWidth(text, align);
            final int length = text.length();
            for (int j = 0; j < length; j++)
            {
                final Data d = fontData.get(Character.valueOf(text.charAt(j)));
                surface.setLocation(x + lx - (double) width, y + ly + (double) d.getHeight());
                surface.setTile(d.getId());
                surface.render(g);
                lx += d.getWidth() + 1;
            }

            lx = 0;
            ly += lineHeight;
        }
    }

    @Override
    public void draw(Graphic g, int x, int y, Align align, String text)
    {
        draw(g, x, y, align, text.split(NL_STR));
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
    public void setFade(int alpha, int fade)
    {
        surface.setFade(alpha, fade);
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
            final Data d = fontData.get(Character.valueOf(text.charAt(i)));
            lx += d.getWidth() + 1;
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
            if (text.charAt(i) == SpriteFont.NEW_LINE)
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
        return surface.getSurface();
    }

    @Override
    public boolean isLoaded()
    {
        return surface.isLoaded();
    }

    /*
     * Object
     */

    @Override
    public boolean equals(Object object)
    {
        if (object == this)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final SpriteFont sprite = (SpriteFont) object;

        final boolean sameSurface = sprite.getSurface() == getSurface();
        final boolean sameWidth = sprite.getWidth() == getWidth();
        final boolean sameHeight = sprite.getHeight() == getHeight();

        return sameWidth && sameHeight && sameSurface;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + fontData.hashCode();
        result = prime * result + lineHeight;
        result = prime * result + surface.hashCode();
        return result;
    }

    /**
     * Character data.
     */
    private static final class Data
    {
        /** Character id. */
        private final int id;
        /** Character width. */
        private final int width;
        /** Character height. */
        private final int height;

        /**
         * Internal constructor.
         * 
         * @param id The character id.
         * @param width The character width.
         * @param height The character height.
         */
        Data(int id, int width, int height)
        {
            this.id = id;
            this.width = width;
            this.height = height;
        }

        /**
         * Get the character id.
         * 
         * @return The character id.
         */
        int getId()
        {
            return id;
        }

        /**
         * Get the character width.
         * 
         * @return THe character width.
         */
        int getWidth()
        {
            return width;
        }

        /**
         * Get the character height.
         * 
         * @return THe character height.
         */
        int getHeight()
        {
            return height;
        }
    }
}
