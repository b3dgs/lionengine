/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilityRandom;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Background implementation.
 */
final class Background
{
    /** Alpha start. */
    private final int normal = 200;
    /** Surface reference. */
    private final SpriteTiled surface;
    /** Stars array. */
    private final Star[] stars;
    /** Colors array. */
    private final ColorRgba[] colors;
    /** Lightning effect timer. */
    private final Timing effectTime;
    /** The real alpha value. */
    private double alphaReal;
    /** The current alpha value. */
    private int alpha;
    /** The alpha destination value. */
    private int alphaDest;
    /** Lightning effect flag. */
    private boolean effect;

    /**
     * Constructor.
     */
    Background()
    {
        colors = new ColorRgba[normal + 1];
        for (int i = 0; i < colors.length; i++)
        {
            colors[i] = new ColorRgba(0, 0, 0, i);
        }
        effectTime = new Timing();
        alpha = normal;
        alphaDest = alpha;
        alphaReal = alphaDest;
        surface = Drawable.loadSpriteTiled(Media.get("backgrounds", "stars.png"), 3, 6);
        surface.load(false);
        stars = new Star[256];
        for (int i = 0; i < stars.length; i++)
        {
            final int id = UtilityRandom.getRandomInteger(0, 5);
            stars[i] = new Star(UtilityRandom.getRandomInteger(-20, 340), UtilityRandom.getRandomInteger(-10, 200),
                    0.0, UtilityRandom.getRandomInteger(20, 50) / 15.0 * 1.0, id);
        }
        effectTime.start();
    }

    /**
     * The update routine.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        alphaReal = UtilityMath.curveValue(alphaReal, alphaDest, 120.0);
        alpha = (int) Math.floor(alphaReal);
        for (final Star star : stars)
        {
            star.update(extrp);
        }

        if (UtilityRandom.getRandomInteger(0, 1000) == 0 && effectTime.elapsed(5000))
        {
            startEffect();
            effectTime.start();
        }
    }

    /**
     * The rendering routine.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        g.setColor(colors[alpha]);
        g.drawRect(0, 0, 320, 200, true);
        for (final Star star : stars)
        {
            surface.render(g, star.getId(), camera.getViewpointX((int) star.getX()), (int) star.getY());
        }
        if (effect)
        {
            g.setColor(ColorRgba.WHITE);
            g.drawRect(0, 0, 320, 200, true);
            effect = false;
        }
    }

    /**
     * Set the alpha value.
     * 
     * @param c alpha value.
     */
    public void setAlpha(int c)
    {
        alpha = c;
    }

    /**
     * Start the lightning effect.
     */
    private void startEffect()
    {
        alphaReal = 25;
        alphaDest = normal;
        effect = true;
    }
}
