/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represent the raster for all colors.
 */
public final class Raster
{
    /** Red channel. */
    private static final String CHANNEL_RED = "Red";
    /** Green channel. */
    private static final String CHANNEL_GREEN = "Green";
    /** Blue channel. */
    private static final String CHANNEL_BLUE = "Blue";

    /**
     * Load raster from media.
     * 
     * @param media The raster media.
     * @return The raster channels data.
     * @throws LionEngineException If <code>null</code> argument or unable to read data.
     */
    public static Raster load(Media media)
    {
        Check.notNull(media);

        final Xml root = new Xml(media);
        final RasterData dataRed = RasterData.load(root, CHANNEL_RED);
        final RasterData dataGreen = RasterData.load(root, CHANNEL_GREEN);
        final RasterData dataBlue = RasterData.load(root, CHANNEL_BLUE);

        return new Raster(dataRed, dataGreen, dataBlue);
    }

    /** Red raster. */
    private final RasterData red;
    /** Green raster. */
    private final RasterData green;
    /** Blue raster. */
    private final RasterData blue;

    /**
     * Create the raster.
     * 
     * @param red The red raster.
     * @param green The green raster.
     * @param blue The blue raster.
     */
    Raster(RasterData red, RasterData green, RasterData blue)
    {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Get the red raster.
     * 
     * @return The red raster
     */
    public RasterData getRed()
    {
        return red;
    }

    /**
     * Get the green raster.
     * 
     * @return The green raster
     */
    public RasterData getGreen()
    {
        return green;
    }

    /**
     * Get the blue raster.
     * 
     * @return The blue raster
     */
    public RasterData getBlue()
    {
        return blue;
    }
}
