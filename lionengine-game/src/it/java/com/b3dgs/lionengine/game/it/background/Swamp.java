/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.background;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.game.background.BackgroundElementRastered;
import com.b3dgs.lionengine.game.background.Parallax;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Swamp background implementation.
 */
class Swamp extends BackgroundAbstract
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
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     */
    Swamp(SourceResolutionProvider source, double scaleH, double scaleV)
    {
        super(null, 0, 512);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        totalHeight = 112;

        final int width = source.getWidth();
        final int halfScreen = (int) (source.getWidth() / 3.5);
        setOffsetY(source.getHeight() - 180);

        backdrop = new Backdrop(width);
        clouds = new Clouds(Medias.create("cloud.png"), width, 4);
        parallax = new Parallax(source, Medias.create("parallax.png"), parallaxsNumber, halfScreen, 124, 50, 100);
        add(backdrop);
        add(clouds);
        add(parallax);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        /** Backdrop color A. */
        private final BackgroundElement backcolor;
        /** Mountain element. */
        private final BackgroundElement mountain;
        /** Moon element. */
        private final BackgroundElementRastered moon;
        /** Mountain sprite. */
        private final Sprite mountainSprite;
        /** Original offset. */
        private final int moonOffset;
        /** Screen wide value. */
        private final int w;
        /** Screen width. */
        int screenWidth;

        /**
         * Constructor.
         * 
         * @param screenWidth The screen width.
         */
        Backdrop(int screenWidth)
        {
            backcolor = createElement("backcolor.png", 0, 0);
            mountain = createElement("mountain.png", 0, 124);
            final int x = (int) (208 * scaleH);
            moonOffset = 50;
            moon = new BackgroundElementRastered(x,
                                                 moonOffset,
                                                 Medias.create("moon.png"),
                                                 Medias.create("raster3.xml"),
                                                 MOON_RASTERS);
            mountainSprite = (Sprite) mountain.getRenderable();
            this.screenWidth = screenWidth;
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolor.setOffsetY(y);
            moon.setOffsetY(moonOffset - totalHeight + getOffsetY());
            final double mx = mountain.getOffsetX() + speed * 0.24;
            mountain.setOffsetX(UtilMath.wrapDouble(mx, 0.0, mountainSprite.getWidth()));
            mountain.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            // Render back background first
            final Sprite sprite = (Sprite) backcolor.getRenderable();
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                final int x = backcolor.getMainX() + i * sprite.getWidth();
                final double y = backcolor.getOffsetY() + backcolor.getMainY();
                sprite.setLocation(x, y);
                sprite.render(g);
            }

            // Render moon
            final int id = (int) (mountain.getOffsetY() + (totalHeight - getOffsetY())) / 6;
            final Sprite spriteMoon = moon.getRaster(id);
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
