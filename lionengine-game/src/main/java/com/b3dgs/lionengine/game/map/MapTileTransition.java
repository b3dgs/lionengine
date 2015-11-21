/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileTransition;

/**
 * Represents the transition handling between two different groups of tiles.
 */
public interface MapTileTransition extends MapTileFeature
{
    /**
     * Load the transitions by using the default file.
     */
    void loadTransitions();

    /**
     * Load the transitions from a specific configuration.
     * 
     * @param configTransitions The configuration media.
     */
    void loadTransitions(Media configTransitions);

    /**
     * Resolve transitions by updating tiles if necessary.
     * 
     * @param tile The tile to update.
     */
    void resolve(Tile tile);

    /**
     * Get the tile transition.
     * 
     * @param tile The tile reference.
     * @param group The transition with this group.
     * @return The tile transition with the group.
     */
    TileTransition getTransition(Tile tile, String group);
}
