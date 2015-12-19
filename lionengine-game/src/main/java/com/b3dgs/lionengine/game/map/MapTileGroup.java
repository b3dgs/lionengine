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

import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Represents the group definition for each tile.
 */
public interface MapTileGroup extends MapTileFeature
{
    /**
     * Load tiles group from an external file.
     * 
     * @param groupsConfig The tile collision groups descriptor.
     * @throws LionEngineException If error when reading groups.
     */
    void loadGroups(Media groupsConfig);

    /**
     * Change the group of the tile.
     * 
     * @param tile The tile to change its group.
     * @param group The group destination name.
     */
    void changeGroup(Tile tile, String group);

    /**
     * Get the groups configuration media file.
     * 
     * @return The groups configuration media file.
     */
    Media getGroupsConfig();

    /**
     * Get the tiles from group name.
     * 
     * @param name The group name.
     * @return The associated tiles.
     */
    Collection<TileRef> getGroup(String name);

    /**
     * Get the group name of the tile.
     * 
     * @param tile The tile reference.
     * @return The associated group name.
     */
    String getGroup(TileRef tile);

    /**
     * Get the group name of the tile.
     * 
     * @param tile The tile reference.
     * @return The associated group name.
     */
    String getGroup(Tile tile);

    /**
     * Get the group name from the sheet and number value.
     * 
     * @param sheet The sheet number.
     * @param number The tile number on sheet.
     * @return The associated group name.
     */
    String getGroup(Integer sheet, int number);

    /**
     * Get the groups list.
     * 
     * @return The groups list.
     */
    Collection<String> getGroups();
}
