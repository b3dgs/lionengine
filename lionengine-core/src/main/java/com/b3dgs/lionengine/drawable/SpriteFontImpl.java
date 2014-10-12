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
package com.b3dgs.lionengine.drawable;

import java.util.List;
import java.util.TreeMap;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
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
final class SpriteFontImpl
        implements SpriteFont
{
    /** New line separator character. */
    private static final String NL_STR = "" + SpriteFont.NEW_LINE;

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
         * Constructor.
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

    /** Font surface. */
    private final SpriteTiled surface;
    /** Font data. */
    private final TreeMap<Character, Data> fontData;
    /** Line height value. */
    private int lineHeight;

    /**
     * Constructor.
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
        fontData = new TreeMap<>();
        lineHeight = surface.getTileHeight();

        // Load data for each characters
        final XmlNode letters = Stream.loadXml(mediaData);
        final List<XmlNode> children = letters.getChildren();
        int id = 0;

        for (final XmlNode node : children)
        {
            final Data data = new Data(id, node.readInteger("width"), node.readInteger("height"));
            fontData.put(Character.valueOf(node.readString("char").charAt(0)), data);
            id++;
        }

        children.clear();
    }

    /*
     * SpriteFont
     */

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
                surface.render(g, d.getId(), x + lx - width, y + ly + d.getHeight());
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
    public void setLineHeight(int height)
    {
        lineHeight = height;
    }

    /*
     * Sprite
     */

    @Override
    public void load(boolean alpha) throws LionEngineException
    {
        surface.load(alpha);
    }

    @Override
    public void scale(int percent) throws LionEngineException
    {
        surface.scale(percent);
    }

    @Override
    public void stretch(int percentWidth, int percentHeight) throws LionEngineException
    {
        surface.stretch(percentWidth, percentHeight);
    }

    @Override
    public void rotate(int angle)
    {
        surface.rotate(angle);
    }

    @Override
    public void flipHorizontal()
    {
        surface.flipHorizontal();
    }

    @Override
    public void flipVertical()
    {
        surface.flipVertical();
    }

    @Override
    public void filter(Filter filter) throws LionEngineException
    {
        surface.filter(filter);
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
    public int getWidthOriginal()
    {
        return surface.getWidthOriginal();
    }

    @Override
    public int getHeightOriginal()
    {
        return surface.getHeightOriginal();
    }

    @Override
    public ImageBuffer getSurface()
    {
        return surface.getSurface();
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g, int x, int y)
    {
        surface.render(g, x, y);
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
}
