/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.background;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteParallaxed;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Parallax is used for background depth effect (2.5D).
 */
public class Parallax implements BackgroundComponent
{
    /** Parallax surface. */
    private final SpriteParallaxed surface;
    /** Parallax number. */
    private final int parallaxsNumber;
    /** Parallax location x. */
    private final double[] x;
    /** Parallax location x2. */
    private final double[] x2;
    /** Parallax location y. */
    private final double[] y;
    /** Fact x. */
    private final double factH;
    /** Offset x. */
    private final int offsetX;
    /** Main y. */
    private final int mainY;
    /** Horizontal offset. */
    private final int decX;
    /** Screen width. */
    private int screenWidth;
    /** Screen height. */
    private int screenHeight;
    /** Amplitude. */
    private int amplitude;

    /**
     * Create a parallax.
     * 
     * @param source The resolution source used.
     * @param media The parallax image media.
     * @param parallaxsNumber The number parallax lines.
     * @param decX The horizontal offset.
     * @param decY The vertical offset.
     * @param sx The starting width.
     * @param sy The starting height.
     * @throws LionEngineException If arguments are invalid.
     */
    public Parallax(Resolution source, Media media, int parallaxsNumber, int decX, int decY, int sx, int sy)

    {
        this.parallaxsNumber = parallaxsNumber;
        surface = Drawable.loadSpriteParallaxed(media, this.parallaxsNumber, sx, sy);
        surface.load(false);
        mainY = decY + 64;
        this.decX = surface.getWidth() + decX;
        offsetX = surface.getWidth();
        factH = sx / 100.0 / 0.6;

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
        setScreenSize(source.getWidth(), source.getHeight());
    }

    /**
     * Set the screen size. Used to know the parallax amplitude, and the overall surface to render in order to fill the
     * screen.
     * 
     * @param screenWidth The screen width.
     * @param screenHeight The screen height.
     */
    public final void setScreenSize(int screenWidth, int screenHeight)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        final int w = (int) Math.ceil(screenWidth / (surface.getWidth() * 0.6 * factH)) + 1;
        amplitude = (int) Math.ceil(w / 2.0) + 1;
    }

    /**
     * Render parallax line.
     * 
     * @param g The graphic output.
     * @param numLine The current line number.
     * @param lineY The line y position.
     */
    private void renderLine(Graphic g, int numLine, int lineY)
    {
        final int lineWidth = surface.getLineWidth(numLine);
        for (int j = -amplitude; j < amplitude; j++)
        {
            final int lx = (int) (-offsetX + offsetX * j - x[numLine] - x2[numLine] + numLine * (2.56 * factH) * j);
            if (lx + lineWidth + decX >= 0 && lx <= screenWidth)
            {
                surface.render(g, numLine, lx + decX, lineY);
            }
        }
    }

    /*
     * BackgroundComponent
     */

    @Override
    public void update(double extrp, int x, int y, double speed)
    {
        // This will avoid bug on huge speed (lines out of screen)
        final double speedWrap = 2.56 * factH / 0.0084;
        final double wrapedSpeed = UtilMath.wrapDouble(speed, -speedWrap, speedWrap);

        // Move each line, depending of its id and size
        for (int lineNum = 0; lineNum < parallaxsNumber; lineNum++)
        {
            this.x[lineNum] += 0.2 * lineNum * wrapedSpeed * 0.042;
            x2[lineNum] += wrapedSpeed * 0.25;

            // When line has arrived to its border
            final double secondLine = this.x[1];
            if (secondLine >= 2.56 * factH || secondLine <= -2.56 * factH)
            {
                for (int j = 0; j < parallaxsNumber; j++)
                {
                    this.x[j] = 0.0;
                    x2[j] = 0.0;
                }
            }
            this.y[lineNum] = lineNum + y + (double) mainY;
        }
    }

    @Override
    public void render(Graphic g)
    {
        for (int numLine = 0; numLine < parallaxsNumber; numLine++)
        {
            final int lineY = (int) y[numLine];
            if (lineY >= 0 && lineY < screenHeight)
            {
                renderLine(g, numLine, lineY);
            }
        }
    }
}
