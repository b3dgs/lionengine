/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Water foreground implementation.
 */
final class Foreground extends BackgroundAbstract
{
    private static final double WATER_EFFECT_SPEED = 0.08;
    private static final double WATER_EFFECT_FREQUENCY = 0.2;
    private static final double WATER_EFFECT_AMPLITUDE = 0.2;
    private static final double WATER_EFFECT_OFFSET = 2.5;
    private static final int WATER_HEIGHT_OFFSET = 35;

    /** Water depth. */
    private final double depth = 8.0;
    /** Water depth offset. */
    private final double depthOffset = -3.0;
    /** Water speed. */
    private final double speed = 0.02;
    /** Primary. */
    private final Primary primary;
    /** Secondary. */
    private final Secondary secondary;
    /** Screen width. */
    private int screenWidth;
    /** Screen height. */
    private int screenHeight;
    /** Water top. */
    private double top;
    /** Water height. */
    private double height;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     */
    Foreground(Resolution source)
    {
        super(null, 0, 0);

        final String path = "";
        primary = new Primary(path, this);
        secondary = new Secondary(path, this);

        setScreenSize(source.getWidth(), source.getHeight());
        add(primary);
        add(secondary);
    }

    /**
     * Render the front part of the water.
     * 
     * @param g The graphic output.
     */
    public void renderBack(Graphic g)
    {
        renderComponent(0, g);
    }

    /**
     * Render the back part of the water.
     * 
     * @param g The graphic output.
     */
    public void renderFront(Graphic g)
    {
        renderComponent(1, g);
    }

    /**
     * Set the height.
     * 
     * @param height The height to set.
     */
    public void setHeight(double height)
    {
        this.height = height;
    }

    /**
     * Get the top of the water.
     * 
     * @return The top of the water.
     */
    public double getTop()
    {
        return height + top;
    }

    /**
     * Get the height.
     * 
     * @return The height.
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * Get the depth.
     * 
     * @return The depth.
     */
    public double getDepth()
    {
        return depth;
    }

    /**
     * Get the depth offset.
     * 
     * @return The depth offset.
     */
    public double getDepthOffset()
    {
        return depthOffset;
    }

    /**
     * Get the water speed.
     * 
     * @return The water speed.
     */
    public double getSpeed()
    {
        return speed;
    }

    @Override
    public void setScreenSize(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;
    }

    /**
     * First front component, including water effect.
     */
    private final class Primary implements BackgroundComponent
    {
        /** Water element. */
        private final BackgroundElement data;
        /** Water reference. */
        private final Foreground water;

        /**
         * Constructor.
         * 
         * @param path The primary surface path.
         * @param water The water reference.
         */
        Primary(String path, Foreground water)
        {
            super();

            this.water = water;

            final Sprite sprite = Drawable.loadSprite(Medias.create(path, "calc.png"));
            sprite.load();
            sprite.prepare();
            data = new BackgroundElement(0, 0, sprite);
            top = data.getRenderable().getHeight();
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            data.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            final Sprite sprite = (Sprite) data.getRenderable();
            final int w = (int) Math.ceil(screenWidth / (double) sprite.getWidth());
            final int y = (int) (screenHeight - WATER_HEIGHT_OFFSET + data.getOffsetY() - water.getHeight() + 4);
            if (y >= 0 && y < screenHeight)
            {
                for (int j = 0; j < w; j++)
                {
                    sprite.setLocation(sprite.getWidth() * j, y);
                    sprite.render(g);
                }
            }
        }
    }

    /**
     * Second front component, including water effect.
     */
    private final class Secondary implements BackgroundComponent
    {
        /** Animation data. */
        private final Animation animation = new Animation(Animation.DEFAULT_NAME, 1, 7, 0.25, false, true);
        /** Sprite. */
        private final SpriteAnimated anim;
        /** Water reference. */
        private final Foreground water;
        /** Main X. */
        private int mainX;
        /** Offset X. */
        private double offsetX;
        /** Offset Y. */
        private double offsetY;
        /** Height value. */
        private double height;
        /** Water x. */
        private double wx;
        /** Position y. */
        private int py;
        /** Anim flicker. */
        private int animFlick;

        /**
         * Constructor.
         * 
         * @param path The secondary surface path.
         * @param water The water reference.
         */
        Secondary(String path, Foreground water)
        {
            super();

            this.water = water;

            anim = Drawable.loadSpriteAnimated(Medias.create(path, "anim.png"), animation.getLast(), 1);
            anim.load();
            anim.play(animation);
        }

        /**
         * Update water effect.
         * 
         * @param g The graphics output.
         * @param speed The effect speed.
         * @param frequency The effect frequency.
         * @param amplitude The effect amplitude.
         * @param offsetForce The offset force.
         */
        private void waterEffect(Graphic g, double speed, double frequency, double amplitude, double offsetForce)
        {
            final int oy = py - (int) water.getHeight();
            for (int y = screenHeight - WATER_HEIGHT_OFFSET + oy; y < screenHeight; y++)
            {
                final double inside = Math.cos(UtilMath.wrapDouble(y + wx * frequency, 0.0, 360.0)) * amplitude;
                final double outside = Math.cos(wx) * offsetForce;
                g.copyArea(0, y, screenWidth, 1, (int) (inside + outside), 0);
            }
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            anim.update(extrp);

            offsetX += speed;
            offsetX = UtilMath.wrapDouble(offsetX, 0.0, anim.getTileWidth());
            offsetY = y;

            height += water.getSpeed() * extrp;
            height = UtilMath.wrapDouble(height, 0.0, 360.0);
            final double waterHeight = Math.sin(height) * water.getDepth() + water.getDepthOffset();
            water.setHeight(waterHeight);

            py = y;
            wx += WATER_EFFECT_SPEED * extrp;
        }

        @Override
        public void render(Graphic g)
        {
            // w number of renders used to fill screen
            int w = (int) Math.ceil(screenWidth / (double) anim.getWidth());
            int y = (int) (screenHeight - WATER_HEIGHT_OFFSET + offsetY - water.getHeight());

            // animation rendering
            if (animFlick > 0)
            {
                w = (int) Math.ceil(screenWidth / (double) anim.getTileWidth());
                final int x = (int) (-offsetX + mainX);

                y -= anim.getTileHeight() - 8;
                if (y >= 0 && y <= screenHeight)
                {
                    for (int j = 0; j <= w; j++)
                    {
                        anim.setLocation(x + anim.getTileWidth() * j, y);
                        anim.render(g);
                    }
                }
            }
            animFlick++;
            if (animFlick > 3)
            {
                animFlick = 0;
            }

            waterEffect(g, WATER_EFFECT_SPEED, WATER_EFFECT_FREQUENCY, WATER_EFFECT_AMPLITUDE, WATER_EFFECT_OFFSET);
        }
    }
}
