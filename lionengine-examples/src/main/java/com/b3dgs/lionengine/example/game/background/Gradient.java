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
package com.b3dgs.lionengine.example.game.background;

import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Gradient color background implementation.
 */
final class Gradient extends BackgroundAbstract
{
    private static final int OFFSET_Y = 82;

    private final Backdrop backdrop;

    /**
     * Constructor.
     * 
     * @param maxHeight The max height.
     * @param totalHeight The total height.
     * @param source The resolution source reference.
     * @param flickering The flickering flag.
     */
    Gradient(int maxHeight, int totalHeight, SourceResolutionProvider source, boolean flickering)
    {
        super(null, 0, maxHeight);

        final int width = source.getWidth();
        backdrop = new Backdrop(flickering, width);
        add(backdrop);
        this.totalHeight = totalHeight;
        setScreenSize(source.getWidth(), source.getHeight());
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        setOffsetY(height - Scene.NATIVE.getHeight() + OFFSET_Y);
        backdrop.setScreenWidth(width);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    public static final class Backdrop implements BackgroundComponent
    {
        private final BackgroundElement backcolorA;
        private final BackgroundElement backcolorB;
        private final boolean flickering;
        private int screenWidth;
        private boolean flicker;

        /**
         * Constructor.
         * 
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         */
        public Backdrop(boolean flickering, int screenWidth)
        {
            super();

            this.flickering = flickering;

            if (flickering)
            {
                backcolorA = createElement("backcolor1.png", 0, 0);
                backcolorB = createElement("backcolor2.png", 0, 0);
            }
            else
            {
                backcolorA = createElement("backcolor.png", 0, 0);
                backcolorB = null;
            }
            setScreenWidth(screenWidth);
        }

        /**
         * Called when the resolution changed.
         * 
         * @param width The new width.
         */
        public void setScreenWidth(int width)
        {
            screenWidth = width;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            final Sprite sprite;
            if (flicker)
            {
                sprite = (Sprite) backcolorB.getRenderable();
            }
            else
            {
                sprite = (Sprite) backcolorA.getRenderable();
            }
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                final int x = backcolorA.getMainX() + i * sprite.getWidth();
                final double y = backcolorA.getOffsetY() + backcolorA.getMainY();
                sprite.setLocation(x, y);
                sprite.render(g);
            }
            if (flickering)
            {
                flicker = !flicker;
            }
        }
    }
}
