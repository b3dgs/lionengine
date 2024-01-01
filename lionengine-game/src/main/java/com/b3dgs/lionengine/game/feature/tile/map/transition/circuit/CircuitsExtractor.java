/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Provides all map circuits.
 */
public interface CircuitsExtractor
{
    /**
     * Get map tile circuits from map configuration.
     *
     * @param levels The level rips used.
     * @param sheetsConfig The sheets configuration media.
     * @param groupsConfig The groups configuration media.
     * @return The circuits found with their associated tiles.
     */
    Map<Circuit, Collection<Integer>> getCircuits(Collection<Media> levels, Media sheetsConfig, Media groupsConfig);

    /**
     * Get map tile circuits from existing maps.
     *
     * @param maps The maps reference.
     * @return The circuits found with their associated tiles.
     */
    Map<Circuit, Collection<Integer>> getCircuits(Collection<MapTile> maps);
}
