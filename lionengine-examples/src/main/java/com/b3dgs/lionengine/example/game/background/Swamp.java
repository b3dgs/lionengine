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
package com.b3dgs.lionengine.example.game.background;

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
final class Swamp extends BackgroundAbstract
{
    private static final int HEIGHT_MAX = 338;
    private static final int HEIGHT_TOTAL = 82;

    private static final int CLOUD_Y = 4;

    private static final double MOON_SCALE_X = 0.6;
    private static final int MOON_OFFSET_Y = 50;

    private static final int PARALLAX_W = 50;
    private static final int PARALLAX_H = 100;
    private static final int PARALLAX_Y = 127;
    private static final int PARALLAX_LINES = 96;

    private final Backdrop backdrop;
    private final Clouds clouds;
    private final Parallax parallax;

    private double moonOffsetX;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     * @param flickering The flickering flag.
     */
    Swamp(SourceResolutionProvider source, boolean flickering)
    {
        super(null, 0, HEIGHT_MAX);

        totalHeight = HEIGHT_TOTAL;

        final int width = source.getWidth();
        final int halfScreen = source.getWidth() / 3;
        backdrop = new Backdrop(flickering, width);
        clouds = new Clouds(Medias.create("cloud.png"), width, CLOUD_Y);
        parallax = new Parallax(source,
                                Medias.create("parallax.png"),
                                PARALLAX_LINES,
                                halfScreen,
                                PARALLAX_Y,
                                PARALLAX_W,
                                PARALLAX_H);
        moonOffsetX = width * MOON_SCALE_X;

        add(backdrop);
        add(clouds);
        add(parallax);
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        moonOffsetX = width * MOON_SCALE_X;
        setOffsetY(height - Scene.NATIVE.getHeight());
        backdrop.setScreenWidth(width);
        clouds.setScreenWidth(width);
        parallax.setScreenSize(width, height);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop implements BackgroundComponent
    {
        private final Gradient.Backdrop backdrop;
        private final BackgroundElement mountain;
        private final BackgroundElementRastered moon;
        private final Sprite mountainSprite;
        private final int moonOffset;
        private int w;

        /**
         * Constructor.
         * 
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         */
        Backdrop(boolean flickering, int screenWidth)
        {
            super();

            backdrop = new Gradient.Backdrop(flickering, screenWidth);
            mountain = createElement("mountain.png", 0, PARALLAX_Y);
            moonOffset = MOON_OFFSET_Y;
            moon = new BackgroundElementRastered(0,
                                                 moonOffset,
                                                 Medias.create("moon.png"),
                                                 Medias.create("palette.png"),
                                                 Medias.create("raster.png"));
            mountainSprite = (Sprite) mountain.getRenderable();
            w = (int) Math.ceil(screenWidth / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        private void setScreenWidth(int width)
        {
            backdrop.setScreenWidth(width);
            w = (int) Math.ceil(width / (double) ((Sprite) mountain.getRenderable()).getWidth()) + 1;
        }

        /**
         * Render moon element.
         * 
         * @param g The graphic output.
         */
        private void renderMoon(Graphic g)
        {
            final int id = (int) (mountain.getOffsetY() + (totalHeight - getOffsetY()));
            moon.setRaster(id);
            moon.setLocation(moon.getMainX() + moonOffsetX, moon.getOffsetY() + moon.getMainY());
            moon.render(g);
        }

        /**
         * Render mountains element.
         * 
         * @param g The graphic output.
         */
        private void renderMountains(Graphic g)
        {
            final int oy = (int) (mountain.getOffsetY() + mountain.getMainY());
            final int ox = (int) (-mountain.getOffsetX() + mountain.getMainX());
            final int sx = mountainSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountainSprite.setLocation(ox + sx * j, oy);
                mountainSprite.render(g);
            }
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backdrop.update(extrp, x, y, speed);
            moon.setOffsetY(moonOffset - totalHeight + getOffsetY());
            final double mx = mountain.getOffsetX() + speed * 0.25;
            mountain.setOffsetX(UtilMath.wrapDouble(mx, 0.0, mountainSprite.getWidth()));
            mountain.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            backdrop.render(g);
            renderMoon(g);
            renderMountains(g);
        }
    }
}
