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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileRef;

/**
 * Represents the transition handling between two different groups of tiles.
 */
public interface MapTileTransition extends Feature
{
    /**
     * Load the transitions from a specific configuration.
     * 
     * @param transitionsConfig The configuration media.
     */
    void loadTransitions(Media transitionsConfig);

    /**
     * Load the transitions from map configuration.
     *
     * @param levels The level rips used.
     * @param sheetsConfig The sheets configuration media.
     * @param groupsConfig The groups configuration media.
     */
    void loadTransitions(Collection<Media> levels, Media sheetsConfig, Media groupsConfig);

    /**
     * Load the transitions from raw data.
     * 
     * @param transitions The transitions data.
     */
    void loadTransitions(Map<Transition, Collection<TileRef>> transitions);

    /**
     * Resolve transitions by updating tiles if necessary.
     * 
     * @param tile The new tile placed.
     * @return The updated tiles.
     */
    Collection<Tile> resolve(Tile tile);

    /**
     * Get the tile transition.
     * 
     * @param tile The tile reference.
     * @param groupOut The transition with this group.
     * @return The tile transition with the group.
     */
    Transition getTransition(TileRef tile, String groupOut);

    /**
     * Get the tile transition.
     * 
     * @param tile The tile reference.
     * @param group The transition with this group.
     * @return The tile transition with the group.
     */
    Transition getTransition(Tile tile, String group);

    /**
     * Get the transitive groups list to reach a group from another.
     * 
     * @param groupIn The first group.
     * @param groupOut The last group.
     * @return The transitive groups.
     */
    Collection<GroupTransition> getTransitives(String groupIn, String groupOut);

    /**
     * Get all supported transitions.
     * 
     * @return The defined transitions.
     */
    Collection<Transition> getTransitions();

    /**
     * Get the tiles associated to the transition.
     * 
     * @param transition The transition reference.
     * @return The associated tiles.
     */
    Collection<TileRef> getTiles(Transition transition);
}
