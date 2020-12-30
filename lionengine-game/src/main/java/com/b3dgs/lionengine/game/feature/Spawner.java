/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;

/**
 * Allows to create and add a {@link Featurable}.
 */
public interface Spawner
{
    /**
     * Set raster media.
     * 
     * @param raster The raster media.
     */
    default void setRaster(Media raster)
    {
        // Nothing by default
    }

    /**
     * Spawn a {@link Featurable} at specified {@link Localizable}. Must have {@link Transformable} feature.
     * 
     * @param media The featurable media.
     * @param localizable The localizable reference.
     * @return The spawned featurable.
     * @throws LionEngineException If invalid media or missing feature.
     */
    default Featurable spawn(Media media, Localizable localizable)
    {
        return spawn(media, localizable.getX(), localizable.getY());
    }

    /**
     * Spawn a {@link Featurable} at specified location. Must have {@link Transformable} feature.
     * 
     * @param media The featurable media.
     * @param x The horizontal spawn location.
     * @param y The vertical spawn location.
     * @return The spawned featurable.
     * @throws LionEngineException If invalid media or missing feature.
     */
    Featurable spawn(Media media, double x, double y);
}
