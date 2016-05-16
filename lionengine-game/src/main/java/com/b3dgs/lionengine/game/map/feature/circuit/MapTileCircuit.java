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
package com.b3dgs.lionengine.game.map.feature.circuit;

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.handler.Feature;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Represents the circuit handling between two different groups of tiles.
 */
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
    void loadCircuits(Map<Circuit, Collection<TileRef>> circuits);

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
    Collection<TileRef> getTiles(Circuit circuit);
}
