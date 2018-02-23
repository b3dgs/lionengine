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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Provides all map transitions.
 */
public interface TransitionsExtractor
{
    /**
     * Get map tile transitions from map configuration.
     *
     * @param levels The level rips used.
     * @param sheetsConfig The sheets configuration media.
     * @param groupsConfig The groups configuration media.
     * @return The transitions found with their associated tiles.
     */
    Map<Transition, Collection<TileRef>> getTransitions(Collection<Media> levels,
                                                        Media sheetsConfig,
                                                        Media groupsConfig);

    /**
     * Get map tile transitions from existing maps.
     *
     * @param maps The maps reference.
     * @return The transitions found with their associated tiles.
     */
    Map<Transition, Collection<TileRef>> getTransitions(Collection<MapTile> maps);
}
