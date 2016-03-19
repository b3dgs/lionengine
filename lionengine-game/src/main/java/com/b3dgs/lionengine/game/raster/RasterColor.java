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
package com.b3dgs.lionengine.game.raster;

import com.b3dgs.lionengine.graphic.RasterData;
import com.b3dgs.lionengine.graphic.UtilColor;

/**
 * Represents the raster color.
 */
public final class RasterColor
{
    /**
     * Load all rasters data.
     * 
     * @param data The raster data.
     * @param m The raster smooth.
     * @param i The raster id.
     * @param smooth <code>true</code> to smooth raster, <code>false</code> else.
     * @return The color transition.
     */
    public static RasterColor load(RasterData data, int m, int i, boolean smooth)
    {
        final int start;
        final int end;
        if (smooth)
        {
            if (m == 0)
            {
                start = UtilColor.getRasterColor(i, data, Rasterable.MAX_RASTERS);
                end = UtilColor.getRasterColor(i + 1, data, Rasterable.MAX_RASTERS);
            }
            else
            {
                start = UtilColor.getRasterColor(Rasterable.MAX_RASTERS - i, data, Rasterable.MAX_RASTERS);
                end = UtilColor.getRasterColor(Rasterable.MAX_RASTERS - i - 1, data, Rasterable.MAX_RASTERS);
            }
        }
        else
        {
            start = UtilColor.getRasterColor(i, data, Rasterable.MAX_RASTERS);
            end = start;
        }
        return new RasterColor(start, end);
    }

    /** The starting color. */
    private final int start;
    /** The ending color. */
    private final int end;

    /**
     * Create the raster color.
     * 
     * @param start The starting color.
     * @param end The ending color.
     */
    private RasterColor(int start, int end)
    {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the starting color.
     * 
     * @return The starting color.
     */
    public int getStart()
    {
        return start;
    }

    /**
     * Get the ending color.
     * 
     * @return The ending color.
     */
    public int getEnd()
    {
        return end;
    }
}
