package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.platform.background.BackgroundElementRastered;

/**
 * Rastered background element implementation.
 */
final class RasteredBackgroundElement
        extends BackgroundElementRastered
{
    /**
     * @see BackgroundElementRastered#BackgroundElementRastered(int, int, Sprite, int)
     */
    RasteredBackgroundElement(int mainX, int mainY, Sprite sprite, int rastersNumber)
    {
        super(mainX, mainY, sprite, rastersNumber);
    }

    @Override
    protected void load(Sprite sprite, int rastersNumber)
    {
        for (int i = 0; i < rastersNumber; i++)
        {
            swamp1(sprite, i);
        }
    }

    /**
     * Raster 1.
     * 
     * @param sprite The sprite reference.
     * @param i The index value.
     */
    protected void swamp1(Sprite sprite, int i)
    {
        final int r = 0x280000 - 0x050000 * (int) (i / 1.0);
        final int g = 0x000600 - 0x000100 * (i / 6);
        final int b = -0x000006 + 0x000002 * (int) (i / 1.5);
        addRaster(sprite, r, g, b);
    }

    /**
     * Raster 2.
     * 
     * @param sprite The sprite reference.
     * @param i The index value.
     */
    protected void swamp2(Sprite sprite, int i)
    {
        final int r = 0x300000 - 0x080000 * (int) (i / 1.5);
        final int g = 0x002000 - 0x000100 * (i / 5);
        final int b = -0x000026 + 0x000006 * (int) (i / 1.5);
        addRaster(sprite, r, g, b);
    }

    /**
     * Raster 3.
     * 
     * @param sprite The sprite reference.
     * @param i The index value.
     */
    protected void swamp3(Sprite sprite, int i)
    {
        final int r = 0x200000 - 0x010000 * (int) (i / 1.5);
        final int g = 0x002000 + 0x000100 * (int) (i / 1.5);
        final int b = -0x000020 + 0x000004 * (int) (i / 1.0);
        addRaster(sprite, r, g, b);
    }

    /**
     * Raster 4.
     * 
     * @param sprite The sprite reference.
     * @param i The index value.
     */
    protected void swamp4(Sprite sprite, int i)
    {
        final int r = 0x280000 - 0x030000 * i;
        final int g = 0x000600;
        final int b = -0x000006 - 0x000002 * i;
        addRaster(sprite, r, g, b);
    }

    /**
     * Raster 5.
     * 
     * @param sprite The sprite reference.
     * @param i The index value.
     */
    protected void swamp5(Sprite sprite, int i)
    {
        final int r = 0x010000 - 0x040000 * (int) (i / 1.1);
        final int g = 0x000600 + 0x000100 * (int) (i / 2.0);
        final int b = -0x000006 + 0x000001 * (int) (i / 2.0);
        addRaster(sprite, r, g, b);
    }
}
