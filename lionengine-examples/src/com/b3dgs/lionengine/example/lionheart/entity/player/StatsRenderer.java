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
package com.b3dgs.lionengine.example.lionheart.entity.player;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.lionheart.AppLionheart;
import com.b3dgs.lionengine.example.lionheart.Scene;

/**
 * Render the stats.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class StatsRenderer
{
    /** Hud sprite. */
    private final SpriteTiled hud;
    /** Heart sprite. */
    private final SpriteTiled heart;
    /** Number font. */
    private final SpriteTiled number;
    /** Horizontal scale factor. */
    private double scaleH;

    /**
     * Constructor.
     * 
     * @param screenWidth The screen width.
     */
    public StatsRenderer(int screenWidth)
    {
        hud = Drawable.loadSpriteTiled(Media.get(AppLionheart.SPRITES_DIR, "hud.png"), 16, 16);
        heart = Drawable.loadSpriteTiled(Media.get(AppLionheart.SPRITES_DIR, "health.png"), 8, 8);
        number = Drawable.loadSpriteTiled(Media.get(AppLionheart.SPRITES_DIR, "numbers.png"), 8, 16);
        setScreenWidth(screenWidth);
    }

    /**
     * Load the sprites.
     */
    public void load()
    {
        hud.load(false);
        heart.load(false);
        number.load(false);
    }

    /**
     * Render the stats.
     * 
     * @param g The graphic output.
     * @param stats The stats to render.
     */
    public void render(Graphic g, Stats stats)
    {
        renderHeart(g, stats);
        renderTalisment(g, stats);
        renderLife(g, stats);

        // Sword level
        int x = getScaledX(160, 0);
        hud.render(g, stats.getSwordLevel() + 1, x, 2);

        // Amulet
        x = getScaledX(230, 0);
        if (stats.getAmulet())
        {
            hud.render(g, 1, x, 2);
        }
    }

    /**
     * Set the screen width.
     * 
     * @param width The screen width.
     */
    public final void setScreenWidth(int width)
    {
        final double scaleH = width / (double) Scene.SCENE_DISPLAY.getWidth();
        this.scaleH = scaleH;
    }

    /**
     * Render heart.
     * 
     * @param g The graphic output.
     * @param stats The stats to render.
     */
    private void renderHeart(Graphic g, Stats stats)
    {
        for (int i = 0; i < 8; i++)
        {
            final int x = i % 4 * 8 + 1;
            final int y = (int) Math.floor(i / 4.0) * 8 + 1;
            if (i < stats.getHeart())
            {
                heart.render(g, 0, x, y);
            }
            else if (i < stats.getHeartMax())
            {
                heart.render(g, 1, x, y);
            }
            else
            {
                heart.render(g, 2, x, y);
            }
        }
    }

    /**
     * Render talisment.
     * 
     * @param g The graphic output.
     * @param stats The stats to render.
     */
    private void renderTalisment(Graphic g, Stats stats)
    {
        final int x = getScaledX(70, 10);
        hud.render(g, 0, x, 2);
        final int talisments = stats.getTalisment();
        if (talisments < 10)
        {
            number.render(g, 1, x + 16, 2);
            number.render(g, talisments + 1, x + 24, 2);
        }
        else if (talisments < 100)
        {
            number.render(g, talisments / 10 + 1, x + 16, 2);
            number.render(g, talisments % 10 + 1, x + 24, 2);
        }
    }

    /**
     * Render life.
     * 
     * @param g The graphic output.
     * @param stats The stats to render.
     */
    private void renderLife(Graphic g, Stats stats)
    {
        final int x = getScaledX((int) (240 + 40 * scaleH), 0);
        hud.render(g, 6, x, 1);
        final int lifes = stats.getLife();
        if (lifes < 10)
        {
            number.render(g, 1, x + 18, 2);
            number.render(g, lifes + 1, x + 26, 2);
        }
        else if (lifes < 100)
        {
            number.render(g, lifes / 10 + 1, x + 18, 2);
            number.render(g, lifes % 10 + 1, x + 26, 2);
        }
    }

    /**
     * Get the scaled horizontal value.
     * 
     * @param x The default x.
     * @param max The max offset.
     * @return The scaled value.
     */
    private int getScaledX(int x, int max)
    {
        return (int) ((x - max * scaleH) * scaleH * (x + max * scaleH) / x);
    }
}
