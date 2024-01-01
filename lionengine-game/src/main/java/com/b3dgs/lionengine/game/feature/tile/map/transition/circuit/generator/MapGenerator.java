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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator;

import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Allows to generate a random map from defined parameters by using a set of existing level rips.
 */
public interface MapGenerator
{
    /**
     * Generate a random map from basic configuration.
     *
     * @param parameters The parameters involved in map generation.
     * @param levels The level rips used.
     * @param sheetsConfig The sheets configuration media.
     * @param groupsConfig The groups configuration media.
     * @return The transitions found with their associated tiles.
     */
    MapTile generateMap(GeneratorParameter parameters,
                        Collection<Media> levels,
                        Media sheetsConfig,
                        Media groupsConfig);
}
