package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;

/**
 * Render the stats.
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
    private final double scaleH;

    /**
     * Constructor.
     * 
     * @param scaleH The horizontal scale factor.
     */
    public StatsRenderer(double scaleH)
    {
        hud = Drawable.loadSpriteTiled(Media.get(AppLionheart.SPRITES_DIR, "hud.png"), 16, 16);
        heart = Drawable.loadSpriteTiled(Media.get(AppLionheart.SPRITES_DIR, "health.png"), 8, 8);
        number = Drawable.loadSpriteTiled(Media.get(AppLionheart.SPRITES_DIR, "numbers.png"), 8, 16);
        this.scaleH = scaleH;
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
     * Render heart.
     * 
     * @param g The graphic output.
     * @param stats The stats to render.
     */
    private void renderHeart(Graphic g, Stats stats)
    {
        for (int i = 0; i < 8; i++)
        {
            final int x = i % 4 * 9 + 1;
            final int y = (int) Math.floor(i / 4) * 9 + 1;
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
        final int x = getScaledX(60, 10);
        hud.render(g, 0, x, 2);
        final int talisments = stats.getTalisment();
        if (talisments < 10)
        {
            number.render(g, 0, x + 20, 2);
            number.render(g, talisments + 1, x + 28, 2);
        }
        else if (talisments < 100)
        {
            number.render(g, talisments / 10 + 1, x + 20, 2);
            number.render(g, talisments % 10 + 1, x + 28, 2);
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
        final int x = getScaledX(280, 10);
        hud.render(g, 6, x, 2);
        final int lifes = stats.getLife();
        if (lifes < 10)
        {
            number.render(g, 0, x + 20, 2);
            number.render(g, lifes + 1, x + 28, 2);
        }
        else if (lifes < 100)
        {
            number.render(g, lifes / 10 + 1, x + 20, 2);
            number.render(g, lifes % 10 + 1, x + 28, 2);
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
        return (int) ((x - max) * scaleH * (x + max * scaleH) / x);
    }
}
