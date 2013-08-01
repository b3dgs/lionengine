package com.b3dgs.lionengine.drawable;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Parallaxed sprite implementation.
 */
final class SpriteParallaxedImpl
        implements SpriteParallaxed
{
    /** Parallax surface file name. */
    private final Media media;
    /** Number of parallax line. */
    private final int linesNumber;
    /** Parallax width. */
    private final int sx;
    /** Parallax height. */
    private final int sy;
    /** Surface of each line. */
    private BufferedImage[] lines;
    /** Line width. */
    private int lineWidth;
    /** Line height. */
    private int lineHeight;
    /** Rendering factor. */
    private double factor;

    /**
     * Create a new parallaxed sprite.
     * 
     * @param media The sprite media.
     * @param linesNumber The number of line.
     * @param sx The parallax width.
     * @param sy The parallax height.
     */
    SpriteParallaxedImpl(Media media, int linesNumber, int sx, int sy)
    {
        Check.argument(linesNumber > 0, "The number of parallax lines must be strictly positive !");

        this.media = media;
        this.linesNumber = linesNumber;
        this.sx = sx;
        this.sy = sy;
        factor = 1.0;
        lines = null;
    }

    /*
     * SpriteParallaxed
     */

    @Override
    public void scale(int percent)
    {
        stretch(percent, percent);
    }

    @Override
    public void stretch(int widthPercent, int heightPercent)
    {
        factor = Math.max(sx * widthPercent / 100, sy * heightPercent / 100) / 100.0;
    }

    @Override
    public void prepare(Filter filter)
    {
        BufferedImage surface = UtilityImage.getBufferedImage(media, false);

        if (0 != Double.compare(factor, 1.0))
        {
            surface = UtilityImage.resize(surface, surface.getWidth() * (int) factor, surface.getHeight()
                    * (int) factor);
        }
        if (Filter.BILINEAR == filter)
        {
            surface = UtilityImage.applyFilter(surface, filter);
        }

        lineWidth = surface.getWidth();
        lineHeight = surface.getHeight() / linesNumber;
        lines = UtilityImage.splitImage(surface, 1, linesNumber);

        for (int i = 0; i < linesNumber; i++)
        {
            final int width = lines[i].getWidth() * (sx + i * 2) / 100;
            final int height = lines[i].getHeight() * sy / 100;
            lines[i] = UtilityImage.resize(lines[i], width, height);
        }
    }

    @Override
    public void render(Graphic g, int line, int x, int y)
    {
        g.drawImage(lines[line], null, x, y);
    }

    @Override
    public int getWidthOriginal()
    {
        return (int) (lineWidth / factor);
    }

    @Override
    public int getHeightOriginal()
    {
        return (int) (lineHeight / factor);
    }

    @Override
    public BufferedImage getLine(int line)
    {
        Check.notNull(lines, "Lines must be initialized !");
        return lines[line];
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g, int x, int y)
    {
        for (int line = 0; line < linesNumber; line++)
        {
            g.drawImage(lines[line], x, y);
        }
    }

    @Override
    public int getWidth()
    {
        return lineWidth;
    }

    @Override
    public int getHeight()
    {
        return lineHeight;
    }
}
