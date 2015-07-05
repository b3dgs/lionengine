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
package com.b3dgs.lionengine.example.game.background;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.background.BackgroundGame;
import com.b3dgs.lionengine.game.background.Parallax;

/**
 * Swamp background implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Swamp
        extends BackgroundGame
{
    /** Moon rasters. */
    private static final int MOON_RASTERS = 20;

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
        final Sprite sprite = Drawable.loadSprite(Medias.create(name));
        sprite.load();
        sprite.prepare();
        return new ElementRastered(x, y, sprite, rastersNumber);
    }

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
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     */
    public Swamp(Resolution source, double scaleH, double scaleV)
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
        clouds = new Clouds(Medias.create("cloud.png"), width, 4);
        parallax = new Parallax(source, Medias.create("parallax.png"), parallaxsNumber, halfScreen, 124, 50, 100);
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
            mountainSprite = (Sprite) mountain.getRenderable();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolor.setOffsetY(y);
            moon.setOffsetY(-20 - moonOffset + getOffsetY());
            mountain.setOffsetX(
                    UtilMath.wrapDouble(mountain.getOffsetX() + speed * 0.24, 0.0, mountainSprite.getWidth()));
            mountain.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            // Render back background first
            final Sprite sprite = (Sprite) backcolor.getRenderable();
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                sprite.setLocation(backcolor.getMainX() + i * sprite.getWidth(),
                        backcolor.getOffsetY() + backcolor.getMainY());
                sprite.render(g);
            }
            // Render moon
            final Sprite spriteMoon = moon
                    .getRaster((int) ((mountain.getOffsetY() + (moonOffset - getOffsetY())) / 4 + Swamp.MOON_RASTERS));
            spriteMoon.setLocation(moon.getMainX(), moon.getOffsetY() + moon.getMainY());
            spriteMoon.render(g);

            // Render mountains
            final int oy = (int) (mountain.getOffsetY() + mountain.getMainY());
            final int ox = (int) (-mountain.getOffsetX() + mountain.getMainX());
            final int sx = mountainSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountainSprite.setLocation(ox + sx * j, oy);
                mountainSprite.render(g);
            }
        }
    }
}
