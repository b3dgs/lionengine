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
    /** Width original. */
    private int widthOriginal;
    /** Height original. */
    private int heightOriginal;
    /** Surface of each line. */
    private BufferedImage[] lines;
    /** Line width. */
    private int lineWidth;
    /** Line height. */
    private int lineHeight;
    /** Rendering factor h. */
    private final double factorH;
    /** Rendering factor v. */
    private final double factorV;

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
        factorH = 1.0;
        factorV = 1.0;
        this.sx = sx;
        this.sy = sy;
        lines = null;
    }

    /*
     * SpriteParallaxed
     */

    @Override
    public void scale(int percent)
    {
        // Nothing to do
    }

    @Override
    public void stretch(int widthPercent, int heightPercent)
    {
        // Nothing to do
    }

    @Override
    public void prepare(Filter filter)
    {
        BufferedImage surface = UtilityImage.getBufferedImage(media, false);
        widthOriginal = surface.getWidth();
        heightOriginal = surface.getHeight();

        if (0 != Double.compare(factorH, 1.0) || 0 != Double.compare(factorV, 1.0))
        {
            surface = UtilityImage.resize(surface, (int) (surface.getWidth() * factorH),
                    (int) (surface.getHeight() * factorV));
        }
        if (Filter.BILINEAR == filter)
        {
            surface = UtilityImage.applyFilter(surface, filter);
        }

        lineWidth = (int) Math.floor(surface.getWidth() * sx / 100.0);
        lineHeight = (int) Math.floor(surface.getHeight() / linesNumber * sy / 100.0);
        lines = UtilityImage.splitImage(surface, 1, linesNumber);
        final double factH = sx / 100.0 / 0.6;

        for (int i = 0; i < linesNumber; i++)
        {
            final int width = (int) Math.ceil(lines[i].getWidth() * (sx + i * 2 * factH) / 100);
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
        return widthOriginal;
    }

    @Override
    public int getHeightOriginal()
    {
        return heightOriginal;
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
