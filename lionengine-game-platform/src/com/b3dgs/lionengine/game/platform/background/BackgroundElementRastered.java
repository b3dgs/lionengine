package com.b3dgs.lionengine.game.platform.background;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Specific background element, supporting raster effects.
 */
public abstract class BackgroundElementRastered
        extends BackgroundElement
{
    /** Rasters list. */
    private final List<Sprite> rasters;

    /**
     * Create a new rastered background element.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param sprite The sprite to be rastered.
     * @param rastersNumber The number of rasters.
     */
    public BackgroundElementRastered(int mainX, int mainY, Sprite sprite, int rastersNumber)
    {
        super(mainX, mainY, sprite);
        rasters = new ArrayList<>(rastersNumber);
        initialize(sprite, rastersNumber);
    }

    /**
     * Load rasters from original sprite.
     * 
     * @param sprite The original sprite.
     * @param rastersNumber The number of rasters to use.
     */
    protected abstract void load(Sprite sprite, int rastersNumber);

    /**
     * Get raster surface from its id.
     * 
     * @param id The raster id.
     * @return The raster surface.
     */
    public Sprite getRaster(int id)
    {
        if (id < 0)
        {
            return rasters.get(0);
        }
        return rasters.get(id);
    }

    /**
     * Add a raster with specified colour code.
     * 
     * @param sprite The original sprite.
     * @param fr The red color offset.
     * @param fg The green color offset.
     * @param fb The blue color offset.
     */
    protected void addRaster(Sprite sprite, int fr, int fg, int fb)
    {
        final BufferedImage buf = sprite.getSurface();
        final BufferedImage rasterBuf = UtilityImage.createBufferedImage(buf.getWidth(), buf.getHeight(),
                buf.getTransparency());

        for (int i = 0; i < rasterBuf.getWidth(); i++)
        {
            for (int j = 0; j < rasterBuf.getHeight(); j++)
            {
                rasterBuf.setRGB(i, j, BackgroundElementRastered.filterRGB(buf.getRGB(i, j), fr, fg, fb));
            }
        }

        final Sprite raster = Drawable.loadSpriteTiled(rasterBuf, sprite.getWidth(), sprite.getHeight());
        rasters.add(raster);
    }

    /**
     * Initialize rastered element.
     * 
     * @param sprite The sprite reference.
     * @param rastersNumber The number of rasters
     */
    private void initialize(Sprite sprite, int rastersNumber)
    {
        load(sprite, rastersNumber);
    }

    /**
     * Get filtered rgb from data.
     * 
     * @param rgb The input rgb.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @return The filtered color.
     */
    private static int filterRGB(int rgb, int fr, int fg, int fb)
    {
        if (-16711423 == rgb || 0 == rgb || 16711935 == rgb)
        {
            return rgb;
        }

        int nr = (rgb & 0xFF0000) + fr;
        if (nr < 0x000000)
        {
            nr = 0x000000;
        }
        if (nr > 0xFF0000)
        {
            nr = 0xFF0000;
        }

        int ng = (rgb & 0x00FF00) + fg;
        if (ng < 0x000000)
        {
            ng = 0x000000;
        }
        if (ng > 0x00FF00)
        {
            ng = 0x00FF00;
        }

        int nb = (rgb & 0x0000FF) + fb;
        if (nb < 0x000000)
        {
            nb = 0x000000;
        }
        if (nb > 0x0000FF)
        {
            nb = 0x0000FF;
        }

        final int a = rgb & 0xFF000000;
        final int r = nr;
        final int g = ng;
        final int b = nb;

        return a | r | g | b;
    }
}
