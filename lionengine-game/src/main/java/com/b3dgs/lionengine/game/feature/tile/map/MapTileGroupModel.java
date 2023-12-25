/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;

/**
 * Map tile group model implementation.
 */
public class MapTileGroupModel extends FeatureAbstract implements MapTileGroup
{
    /** No group name. */
    public static final String NO_GROUP_NAME = "none";

    /** Group tiles mapping. */
    private final Map<String, Set<Integer>> groupTiles = new HashMap<>();
    /** Group types mapping. */
    private final Map<String, TileGroupType> groupTypes = new HashMap<>();
    /** Tiles group mapping. */
    private final Map<Integer, String> tilesGroup = new HashMap<>();
    /** Groups configuration file. */
    private Media groupsConfig;

    /**
     * Create feature.
     */
    public MapTileGroupModel()
    {
        super();
    }

    @Override
    public void loadGroups(Collection<TileGroup> groups)
    {
        groupTiles.values().forEach(Collection::clear);
        groupTiles.clear();
        tilesGroup.clear();
        groupTypes.clear();

        groupTiles.put(NO_GROUP_NAME, new HashSet<>());

        for (final TileGroup group : groups)
        {
            final String name = group.getName();
            groupTiles.put(name, group.getTiles());
            groupTypes.put(name, group.getType());

            for (final Integer tile : group.getTiles())
            {
                tilesGroup.put(tile, name);
            }
        }
    }

    @Override
    public void loadGroups(Media groupsConfig)
    {
        this.groupsConfig = groupsConfig;
        loadGroups(TileGroupsConfig.imports(groupsConfig));
    }

    @Override
    public void changeGroup(Tile tile, String group)
    {
        final Integer number = tile.getKey();
        final String oldGroup = getGroup(tile);

        final Set<Integer> old = groupTiles.get(oldGroup);
        if (old != null)
        {
            old.remove(number);
        }

        if (group != null)
        {
            tilesGroup.put(number, group);

            final Set<Integer> set = groupTiles.computeIfAbsent(group, k -> new HashSet<>());
            set.add(number);
        }
        else
        {
            tilesGroup.remove(number);
        }
    }

    @Override
    public Media getGroupsConfig()
    {
        return groupsConfig;
    }

    @Override
    public Set<Integer> getGroup(String name)
    {
        final Set<Integer> group;
        if (groupTiles.containsKey(name))
        {
            group = groupTiles.get(name);
        }
        else if (groupTiles.containsKey(NO_GROUP_NAME))
        {
            group = groupTiles.get(NO_GROUP_NAME);
        }
        else
        {
            group = Collections.emptySet();
        }
        return group;
    }

    @Override
    public String getGroup(Integer tile)
    {
        if (tilesGroup.containsKey(tile))
        {
            return tilesGroup.get(tile);
        }
        return NO_GROUP_NAME;
    }

    @Override
    public String getGroup(Tile tile)
    {
        if (tile != null)
        {
            return getGroup(tile.getKey());
        }
        return NO_GROUP_NAME;
    }

    @Override
    public TileGroupType getType(String name)
    {
        if (groupTypes.containsKey(name))
        {
            return groupTypes.get(name);
        }
        return TileGroupType.NONE;
    }

    @Override
    public TileGroupType getType(Tile tile)
    {
        return getType(getGroup(tile));
    }

    @Override
    public Set<String> getGroups()
    {
        return groupTiles.keySet();
    }
}
