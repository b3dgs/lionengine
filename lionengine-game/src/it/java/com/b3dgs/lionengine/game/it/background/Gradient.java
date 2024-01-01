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
package com.b3dgs.lionengine.game.it.background;

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
     */
    Gradient(int maxHeight, int totalHeight, SourceResolutionProvider source)
    {
        super(null, 0, maxHeight);

        final int width = source.getWidth();
        backdrop = new Backdrop(width);
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
        private final BackgroundElement backcolor;
        private int screenWidth;

        /**
         * Constructor.
         * 
         * @param screenWidth The screen width.
         */
        public Backdrop(int screenWidth)
        {
            super();

            backcolor = createElement("backcolor.png", 0, 0);
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
            backcolor.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            final Sprite sprite = (Sprite) backcolor.getRenderable();
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                final int x = backcolor.getMainX() + i * sprite.getWidth();
                final double y = backcolor.getOffsetY() + backcolor.getMainY();
                sprite.setLocation(x, y);
                sprite.render(g);
            }
        }
    }
}
