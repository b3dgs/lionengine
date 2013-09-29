package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import java.awt.Color;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.utility.UtilityMath;
import com.b3dgs.lionengine.utility.UtilityRandom;

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
    private final Color[] colors;
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
        colors = new Color[normal + 1];
        for (int i = 0; i < colors.length; i++)
        {
            colors[i] = new Color(0, 0, 0, i);
        }
        effectTime = new Timing();
        alpha = normal;
        alphaDest = alpha;
        alphaReal = alphaDest;
        surface = Drawable.loadSpriteTiled(Media.get("stars.png"), 3, 6);
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
            g.setColor(Color.WHITE);
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
