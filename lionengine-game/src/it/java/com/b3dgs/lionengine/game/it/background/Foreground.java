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
package com.b3dgs.lionengine.game.it.background;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.background.BackgroundAbstract;
import com.b3dgs.lionengine.game.background.BackgroundComponent;
import com.b3dgs.lionengine.game.background.BackgroundElement;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Water foreground implementation.
 */
class Foreground extends BackgroundAbstract
{
    /** Screen width. */
    int screenWidth;
    /** Screen height. */
    int screenHeight;
    /** The horizontal factor. */
    double scaleH;
    /** The horizontal factor. */
    double scaleV;
    /** Water top. */
    double top;
    /** Primary. */
    private final Primary primary;
    /** Secondary. */
    private final Secondary secondary;
    /** Standard height. */
    private final int nominal;
    /** Water height. */
    private double height;
    /** Water depth. */
    private final double depth;
    /** Water speed. */
    private final double speed;

    /**
     * Constructor.
     * 
     * @param source The resolution source reference.
     */
    public Foreground(Resolution source)
    {
        super(null, 0, 0);
        nominal = 210;
        height = 0.0;
        depth = 4.0;
        speed = 0.02;
        primary = new Primary(this);
        secondary = new Secondary(this);
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
     * Called when the resolution changed.
     * 
     * @param width The new width.
     * @param height The new height.
     */
    public final void setScreenSize(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;
        final double scaleH = width / (double) Scene.NATIVE.getWidth();
        final double scaleV = height / (double) Scene.NATIVE.getHeight();
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        primary.updateMainY();
        secondary.updateMainY();
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
     * Get the nominal value.
     * 
     * @return The nominal value.
     */
    public int getNominal()
    {
        return nominal;
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
         * @param water The water reference.
         */
        Primary(Foreground water)
        {
            final Sprite sprite = Drawable.loadSprite(Medias.create("calc.png"));
            sprite.load();
            sprite.prepare();
            data = new BackgroundElement(0, (int) Math.ceil(water.getNominal() * scaleV), sprite);
            top = data.getRenderable().getHeight();
            this.water = water;
        }

        /**
         * Update the main vertical location.
         */
        void updateMainY()
        {
            data.setMainY((int) Math.ceil(water.getNominal() * scaleV));
        }

        /*
         * BackgroundComponent
         */

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            data.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            // Render calc
            final Sprite sprite = (Sprite) data.getRenderable();
            final int w = (int) Math.ceil(screenWidth / (double) sprite.getWidth());
            final int y = (int) (data.getOffsetY() + data.getMainY() + water.getHeight());
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
        /** Water element. */
        private final BackgroundElement data;
        /** Sprite. */
        private final SpriteAnimated anim;
        /** Animation data. */
        private final Animation animation;
        /** Water reference. */
        private final Foreground water;
        /** Height value. */
        private double height;
        /** Water x. */
        private double wx;
        /** Position y. */
        private int py;

        /**
         * Constructor.
         * 
         * @param water The water reference.
         */
        Secondary(Foreground water)
        {
            final Sprite back = Drawable.loadSprite(Medias.create("back.png"));
            back.load();
            back.prepare();
            data = new BackgroundElement(0, (int) Math.floor(water.getNominal() * scaleV), back);

            animation = new Animation(null, 1, 7, 0.25, false, true);
            anim = Drawable.loadSpriteAnimated(Medias.create("anim.png"), 7, 1);
            anim.load();
            anim.play(animation);
            this.water = water;
            height = 0.0;
            wx = 0.0;
        }

        /**
         * Update the main vertical location.
         */
        void updateMainY()
        {
            data.setMainY((int) Math.floor(water.getNominal() * scaleV));
        }

        /**
         * Update water effect.
         * 
         * @param g The graphics output.
         * @param frequency The effect frequency.
         * @param amplitude The effect amplitude.
         * @param offsetForce The offset force.
         */
        private void waterEffect(Graphic g, double frequency, double amplitude, double offsetForce)
        {
            final int oy = py + (int) water.getHeight();
            for (int y = screenHeight - 32 + getNominal() - 210 + oy; y < screenHeight; y++)
            {
                final double inside = Math.cos(UtilMath.wrapDouble(y + wx * frequency, 0.0, 360.0)) * amplitude;
                final double outside = Math.cos(wx) * offsetForce;
                g.copyArea(0, y, screenWidth, 1, (int) (inside + outside), 0);
            }
        }

        /*
         * BackgroundComponent
         */

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            anim.update(extrp);

            data.setOffsetX(data.getOffsetX() + speed);
            data.setOffsetX(UtilMath.wrapDouble(data.getOffsetX(), 0.0, anim.getTileWidth()));
            data.setOffsetY(y);

            height += water.getSpeed() * extrp;
            height = UtilMath.wrapDouble(height, 0.0, 360.0);
            water.setHeight(Math.sin(height) * water.getDepth());
            py = y;
            wx += 0.06 * extrp;
        }

        @Override
        public void render(Graphic g)
        {
            // w number of renders used to fill screen
            final Sprite sprite = (Sprite) data.getRenderable();
            int w = (int) Math.ceil(screenWidth / (double) sprite.getWidth());
            int y = (int) (data.getOffsetY() + data.getMainY() + water.getHeight());

            if (y >= 0 && y <= screenHeight)
            {
                for (int j = 0; j < w; j++)
                {
                    sprite.setLocation(sprite.getWidth() * j, y);
                    sprite.render(g);
                }
            }

            // animation rendering
            w = (int) Math.ceil(screenWidth / (double) anim.getTileWidth());
            final int x = (int) (-data.getOffsetX() + data.getMainX());
            y = (int) (data.getOffsetY() + data.getMainY() - 16.0 + water.getHeight());

            if (y >= 0 && y <= screenHeight)
            {
                for (int j = 0; j <= w; j++)
                {
                    anim.setLocation(x + anim.getTileWidth() * j, y);
                    anim.render(g);
                }
            }

            waterEffect(g, 1.5, 0.8, 3.0);
        }
    }
}
