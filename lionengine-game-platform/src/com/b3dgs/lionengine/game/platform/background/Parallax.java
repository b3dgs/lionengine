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
package com.b3dgs.lionengine.game.platform.background;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteParallaxed;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Parallax is used for background depth effect (2.5D).
 */
public class Parallax
        implements BackgroundComponent
{
    /** Parallax element. */
    private final BackgroundElement data;
    /** Parallax surface. */
    private final SpriteParallaxed parallax;
    /** Parallax number. */
    private final int parallaxsNumber;
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
    /** Horizontal offset. */
    private final int decX;
    /** Fact x. */
    private final double factH;
    /** Offset x. */
    private final int offsetX;
    /** Amplitude. */
    private final int amplitude;

    /**
     * Create a new parallax.
     * 
     * @param source The resolution source used.
     * @param media The parallax image media.
     * @param parallaxsNumber The number parallax lines.
     * @param decX The horizontal offset.
     * @param decY The vertical offset.
     * @param sx The starting width.
     * @param sy The starting height.
     */
    public Parallax(Resolution source, Media media, int parallaxsNumber, int decX, int decY, int sx, int sy)
    {
        screenWidth = source.getWidth();
        screenHeight = source.getHeight();
        factH = sx / 100.0 / 0.6;

        // Load surface
        this.parallaxsNumber = parallaxsNumber;
        parallax = Drawable.loadSpriteParallaxed(media, this.parallaxsNumber, sx, sy);
        parallax.prepare(Filter.NONE);
        data = new BackgroundElement(0, decY + 64, parallax);
        this.decX = parallax.getWidthOriginal() + decX;
        offsetX = parallax.getWidth();
        final int w = (int) Math.ceil(screenWidth / (parallax.getWidthOriginal() * 0.6 * factH));
        amplitude = (int) Math.ceil(w / 2.0) + 1;

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
        final double speedWrap = 2.56 * factH / 0.0084;
        final double wrapedSpeed = UtilityMath.wrapDouble(speed, -speedWrap, speedWrap);

        // Move each line, depending of its id and size
        for (int i = 0; i < parallaxsNumber; i++)
        {
            this.x[i] += 0.2 * i * wrapedSpeed * 0.042;
            x2[i] += wrapedSpeed * 0.25;

            // When line has arrived to its border
            if (this.x[1] >= 2.56 * factH || this.x[1] <= -2.56 * factH)
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
        int i, j, lx, ly, lineWidth;
        for (i = 0; i < parallaxsNumber; i++)
        {
            ly = (int) y[i];
            lineWidth = parallax.getLine(i).getWidth();

            if (ly >= 0 && ly < screenHeight)
            {
                for (j = -amplitude; j < amplitude; j++)
                {
                    lx = (int) (-offsetX + offsetX * j - x[i] - x2[i] + i * (2.56 * factH) * j);

                    if (lx + lineWidth + decX >= 0 && lx <= screenWidth)
                    {
                        parallax.render(g, i, lx + decX, ly);
                    }
                }
            }
        }
    }
}
