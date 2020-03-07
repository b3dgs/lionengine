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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.tile.Tile;

/**
 * Represents the circuit handling between two different groups of tiles.
 */
@FeatureInterface
public interface MapTileCircuit extends Feature
{
    /**
     * Load the circuits from a specific configuration.
     * 
     * @param circuitsConfig The configuration media.
     */
    void loadCircuits(Media circuitsConfig);

    /**
     * Load the circuits from map configuration.
     *
     * @param levels The level rips used.
     * @param sheetsConfig The sheets configuration media.
     * @param groupsConfig The groups configuration media.
     */
    void loadCircuits(Collection<Media> levels, Media sheetsConfig, Media groupsConfig);

    /**
     * Load the circuits by using existing data.
     * 
     * @param circuits The circuits raw data.
     */
    void loadCircuits(Map<Circuit, Collection<Integer>> circuits);

    /**
     * Resolve circuit by updating tiles if necessary.
     * 
     * @param tile The tile to update.
     */
    void resolve(Tile tile);

    /**
     * Get the tiles associated to the circuit.
     * 
     * @param circuit The circuit reference.
     * @return The associated tiles.
     */
    Collection<Integer> getTiles(Circuit circuit);
}
