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
package com.b3dgs.lionengine.graphic.raster;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilMath;

/**
 * Raster color utility class.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilRaster
{
    /**
     * Get raster color.
     * 
     * @param i The color offset.
     * @param data The raster data (must not be <code>null</code>).
     * @param max The max offset (must not be equal to 0).
     * @return The rastered color.
     * @throws LionEngineException If invalid arguments.
     */
    public static int getRasterColor(int i, RasterData data, int max)
    {
        Check.notNull(data);
        Check.different(max, 0);

        final int start = data.getStart();
        final int step = data.getStep();
        final int force = data.getForce();
        final int amplitude = data.getAmplitude();
        final int offset = data.getOffset();

        if (0 == data.getType())
        {
            return start + step * (int) (force * UtilMath.sin(i * (amplitude / (double) max) - offset));
        }
        return start + step * (int) (force * UtilMath.cos(i * (amplitude / (double) max) - offset));
    }

    /**
     * Private constructor.
     */
    private UtilRaster()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
