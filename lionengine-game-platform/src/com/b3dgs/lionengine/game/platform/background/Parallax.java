package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Ratio;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteParallaxed;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Parallax is used for background depth effect (2.5D).
 */
public class Parallax
        implements BackgroundComponent
{
    /** Magic wrap speed. */
    private static final double SPEED_WRAP = 2.56 / 0.0084;

    /** Parallax element. */
    private final BackgroundElement data;
    /** Parallax surface. */
    private final SpriteParallaxed parallax;
    /** Parallax number. */
    private final int parallaxsNumber;
    /** Parallax wide. */
    private final int w;
    /** Parallax location x. */
    private final double[] x;
    /** Parallax location x2. */
    private final double[] x2;
    /** Parallax location y. */
    private final double[] y;
    /** Screen width. */
    private final int screenWidth;
    /** Screen height. */
    private final int screenHeight;
    /** Wide flag. */
    private final boolean wide;

    /**
     * Create a new parallax.
     * 
     * @param internal The internal display.
     * @param media The parallax image media.
     * @param parallaxsNumber The number parallax lines.
     * @param wide The screen wide.
     * @param decY The vertical offset.
     */
    public Parallax(Display internal, Media media, int parallaxsNumber, boolean wide, int decY)
    {
        this.wide = wide;
        int wi = internal.getWidth();
        if (this.wide)
        {
            wi = (int) Math.ceil(wi * Ratio.K4_3);
        }
        screenWidth = wi;
        screenHeight = internal.getHeight();

        // Load surface
        this.parallaxsNumber = parallaxsNumber;
        parallax = Drawable.loadSpriteParallaxed(media, this.parallaxsNumber, 60, 100);
        final Filter filter = Filter.NONE;
        parallax.prepare(filter);
        data = new BackgroundElement(0, decY + 64, parallax);

        wi = (int) Math.ceil(screenWidth / (parallax.getWidthOriginal() * 0.6)) + 1;
        if (this.wide)
        {
            wi = (int) Math.ceil(wi * Ratio.K4_3);
        }
        w = wi;

        // Create data arrays
        x = new double[this.parallaxsNumber];
        x2 = new double[this.parallaxsNumber];
        y = new double[this.parallaxsNumber];

        // Initialize arrays to 0 (starting position)
        for (int i = 0; i < this.parallaxsNumber; i++)
        {
            x[i] = 0.0;
            x2[i] = 0.0;
            y[i] = 0.0;
        }
    }

    /*
     * BackgroundComponent
     */

    @Override
    public void update(double extrp, int x, int y, double speed)
    {
        data.setOffsetY(y);
        // This will avoid bug on huge speed (lines out of screen)
        final double wrapedSpeed = UtilityMath.wrapDouble(speed, -Parallax.SPEED_WRAP, Parallax.SPEED_WRAP);

        // Move each line, depending of its id and size
        for (int i = 0; i < parallaxsNumber; i++)
        {
            this.x[i] += 0.2 * i * wrapedSpeed * 0.042;
            x2[i] += wrapedSpeed * 0.25;

            // When line has arrived to its border
            if (this.x[1] >= 2.56 || this.x[1] <= -2.56)
            {
                for (int j = 0; j < parallaxsNumber; j++)
                {
                    this.x[j] = 0.0;
                    x2[j] = 0.0;
                }
            }
            this.y[i] = i + data.getOffsetY() + data.getMainY();
        }
    }

    @Override
    public void render(Graphic g)
    {
        // Render each lines
        int i, j, lx, ly, lineWidth;

        // w number of renders used to fill screen
        for (i = 0; i < parallaxsNumber; i++)
        {
            ly = (int) y[i];
            lineWidth = parallax.getLine(i).getWidth();

            if (ly >= 0 && ly < screenHeight)
            {
                for (j = 0; j < w; j++)
                {
                    lx = (int) (-76 + 76 * j - x[i] - x2[i] + i * (-7.68 + 2.56 * j));

                    if (lx + lineWidth >= 0 && lx - 0 <= screenWidth)
                    {
                        parallax.render(g, i, lx, ly);
                    }
                }
            }
        }
    }
}
