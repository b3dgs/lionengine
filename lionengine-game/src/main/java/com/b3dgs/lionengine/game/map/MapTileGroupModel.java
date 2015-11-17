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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGroup;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Map tile group model implementation.
 */
public class MapTileGroupModel implements MapTileGroup
{
    /** No group name. */
    public static final String NO_GROUP_NAME = "none";

    /**
     * Find group for tile.
     * 
     * @param tile The tile reference.
     * @param groups The groups list.
     * @return The tile group, <code>null</code> if none.
     */
    private static String getGroup(Tile tile, Collection<TileGroup> groups)
    {
        for (final TileGroup group : groups)
        {
            if (group.contains(tile))
            {
                return group.getName();
            }
        }
        return null;
    }

    /** Map reference. */
    private final MapTile map;
    /** Tile groups list. */
    private final Map<String, TileGroup> groups = new HashMap<String, TileGroup>();
    /** Groups configuration file. */
    private Media groupsConfig;

    /**
     * Create a map tile group.
     * 
     * @param map The map reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileGroupModel(MapTile map)
    {
        this.map = map;
    }

    /**
     * Create a map tile group.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileGroupModel(Services services)
    {
        map = services.get(MapTile.class);
    }

    /*
     * MapTileGroup
     */

    @Override
    public void loadGroups(Media groupsConfig)
    {
        this.groupsConfig = groupsConfig;

        groups.clear();
        final Collection<TileGroup> groups = TileGroupsConfig.imports(groupsConfig);
        final Collection<TileRef> tilesNoGroup = new HashSet<TileRef>();
        final TileGroup none = new TileGroup(NO_GROUP_NAME, tilesNoGroup);
        groups.add(none);
        for (final TileGroup group : groups)
        {
            this.groups.put(group.getName(), group);
        }

        final int widthInTile = map.getInTileWidth();
        final int heightInTile = map.getInTileHeight();
        for (int ty = 0; ty < heightInTile; ty++)
        {
            for (int tx = 0; tx < widthInTile; tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    final String group = getGroup(tile, groups);
                    if (group == null)
                    {
                        tilesNoGroup.add(new TileRef(tile));
                    }
                    tile.setGroup(group);
                }
            }
        }
        groups.clear();
    }

    @Override
    public Media getGroupsConfig()
    {
        return groupsConfig;
    }

    @Override
    public TileGroup getGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return groups.get(name);
        }
        return groups.get(NO_GROUP_NAME);
    }

    @Override
    public TileGroup getGroup(Integer sheet, int number)
    {
        for (final TileGroup group : groups.values())
        {
            if (group.contains(sheet, number))
            {
                return group;
            }
        }
        return groups.get(NO_GROUP_NAME);
    }

    @Override
    public Collection<TileGroup> getGroups()
    {
        return groups.values();
    }
}
