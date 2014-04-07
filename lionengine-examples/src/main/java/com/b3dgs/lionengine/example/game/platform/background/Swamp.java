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
package com.b3dgs.lionengine.example.game.platform.background;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.platform.background.BackgroundComponent;
import com.b3dgs.lionengine.game.platform.background.BackgroundElement;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;
import com.b3dgs.lionengine.game.platform.background.Parallax;

/**
 * Swamp background implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Swamp
        extends BackgroundPlatform
{
    /** Moon rasters. */
    private static final int MOON_RASTERS = 20;

    /** Backdrop. */
    private final Backdrop backdrop;
    /** Clouds. */
    private final Clouds clouds;
    /** Parallax. */
    private final Parallax parallax;
    /** Number of parallax lines. */
    private final int parallaxsNumber = 96;
    /** The horizontal factor. */
    double scaleH;
    /** The vertical factor. */
    double scaleV;

    /**
     * Create a rastered element.
     * 
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @param rastersNumber The number of rasters to use.
     * @return The created element.
     */
    static ElementRastered createElementRastered(String name, int x, int y, int rastersNumber)
    {
        final Sprite sprite = Drawable.loadSprite(UtilityMedia.get(name));
        sprite.load(false);
        return new ElementRastered(x, y, sprite, rastersNumber);
    }

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     */
    Swamp(Resolution source, double scaleH, double scaleV)
    {
        super(null, 0, 512);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        final int width = source.getWidth();
        final int halfScreen = (int) (source.getWidth() / 3.5);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        setOffsetY(source.getHeight() - Scene.NATIVE.getHeight() + 72);
        backdrop = new Backdrop(width);
        clouds = new Clouds(UtilityMedia.get("cloud.png"), width, 4);
        parallax = new Parallax(source, UtilityMedia.get("parallax.png"), parallaxsNumber, halfScreen, 124, 50, 100);
        add(backdrop);
        add(clouds);
        add(parallax);
        totalHeight = 120;
    }

    /**
     * Backdrop represents the back background plus top background elements.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private final class Backdrop
            implements BackgroundComponent
    {
        /** Backdrop color. */
        private final BackgroundElement backcolor;
        /** Mountain element. */
        private final BackgroundElement mountain;
        /** Moon element. */
        private final ElementRastered moon;
        /** Mountain sprite. */
        private final Sprite mountainSprite;
        /** Original offset. */
        private final int moonOffset;
        /** Screen width. */
        int screenWidth;
        /** Screen wide value. */
        private final int w;

        /**
         * Constructor.
         * 
         * @param screenWidth The screen width.
         */
        Backdrop(int screenWidth)
        {
            backcolor = createElement("backcolor.png", 0, 0, false);
            mountain = createElement("mountain.png", 0, 124, false);
            final int x = (int) (208 * scaleH);
            moonOffset = 40;
            moon = Swamp.createElementRastered("moon.png", x, moonOffset, Swamp.MOON_RASTERS);
            mountainSprite = (Sprite) mountain.getSprite();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getSprite()).getWidthOriginal()) + 1;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolor.setOffsetY(y);
            moon.setOffsetY(-20 - moonOffset + getOffsetY());
            mountain.setOffsetX(UtilityMath.wrapDouble(mountain.getOffsetX() + speed * 0.24, 0.0,
                    mountainSprite.getWidth()));
            mountain.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            // Render back background first
            final Sprite sprite = (Sprite) backcolor.getSprite();
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                sprite.render(g, backcolor.getMainX() + i * sprite.getWidth(),
                        (int) (backcolor.getOffsetY() + backcolor.getMainY()));
            }
            // Render moon
            moon.getRaster((int) ((mountain.getOffsetY() + (moonOffset - getOffsetY())) / 4 + Swamp.MOON_RASTERS))
                    .render(g, moon.getMainX(), (int) (moon.getOffsetY() + moon.getMainY()));

            // Render mountains
            final int oy = (int) (mountain.getOffsetY() + mountain.getMainY());
            final int ox = (int) (-mountain.getOffsetX() + mountain.getMainX());
            final int sx = mountainSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountainSprite.render(g, ox + sx * j, oy);
            }
        }
    }
}
