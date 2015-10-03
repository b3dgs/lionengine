/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.drawable;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Font sprite implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class SpriteFontImpl implements SpriteFont
{
    /** New line separator character. */
    private static final String NL_STR = Constant.EMPTY_STRING + SpriteFont.NEW_LINE;

    /** Font surface. */
    private final SpriteTiled surface;
    /** Font data. */
    private final Map<Character, Data> fontData;
    /** Line height value. */
    private int lineHeight;

    /**
     * Internal constructor.
     * 
     * @param media The font image media.
     * @param mediaData The font data media.
     * @param tw The horizontal character number.
     * @param th The vertical character number.
     * @throws LionEngineException If an error occurred when creating the font.
     */
    SpriteFontImpl(Media media, Media mediaData, int tw, int th) throws LionEngineException
    {
        surface = Drawable.loadSpriteTiled(media, tw, th);
        fontData = new TreeMap<Character, Data>();
        lineHeight = surface.getTileHeight();

        // Load data for each characters
        final XmlNode letters = Stream.loadXml(mediaData);
        final Collection<XmlNode> children = letters.getChildren();
        int id = 0;

        for (final XmlNode node : children)
        {
            final Data data = new Data(id, node.readInteger("width"), node.readInteger("height"));
            fontData.put(Character.valueOf(node.readString("char").charAt(0)), data);
            id++;
        }

        children.clear();
    }

    @Override
    public void load() throws LionEngineException
    {
        surface.load();
    }

    @Override
    public void prepare() throws LionEngineException
    {
        surface.prepare();
    }

    @Override
    public void stretch(double percentWidth, double percentHeight) throws LionEngineException
    {
        surface.stretch(percentWidth, percentHeight);
    }

    @Override
    public void rotate(int angle)
    {
        surface.rotate(angle);
    }

    @Override
    public void filter(Filter filter) throws LionEngineException
    {
        surface.filter(filter);
    }

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }

    @Override
    public void draw(Graphic g, int x, int y, Align align, String... texts)
    {
        int lx = 0;
        int ly = 0;
        int width = 0;

        // Render each character
        for (final String text : texts)
        {
            // Get char width
            if (align == Align.RIGHT)
            {
                width = getTextWidth(text);
            }
            if (align == Align.CENTER)
            {
                width = getTextWidth(text) / 2;
            }

            final int length = text.length();
            for (int j = 0; j < length; j++)
            {
                final Data d = fontData.get(Character.valueOf(text.charAt(j)));
                surface.setLocation(x + lx - width, y + ly + d.getHeight());
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
    public void setOrigin(Origin origin)
    {
        surface.setOrigin(origin);
    }

    @Override
    public void setLocation(double x, double y)
    {
        surface.setLocation(x, y);
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
    public void setAlpha(int alpha) throws LionEngineException
    {
        surface.setAlpha(alpha);
    }

    @Override
    public void setFade(int alpha, int fade)
    {
        surface.setFade(alpha, fade);
    }

    @Override
    public void setMirror(Mirror mirror) throws LionEngineException
    {
        surface.setMirror(mirror);
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
        return surface.getX();
    }

    @Override
    public double getY()
    {
        return surface.getY();
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
        if (object instanceof SpriteFont)
        {
            final SpriteFont sprite = (SpriteFont) object;

            final boolean sameSurface = sprite.getSurface() == getSurface();
            final boolean sameWidth = sprite.getWidth() == getWidth();
            final boolean sameHeight = sprite.getHeight() == getHeight();

            return sameWidth && sameHeight && sameSurface;
        }
        return false;
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
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
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
