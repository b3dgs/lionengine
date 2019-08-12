/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuitModel;

/**
 * Utility related to map manipulation.
 */
public final class UtilMap
{
    /** Water group name. */
    public static final int TILE_WATER = 0;
    /** Ground group name. */
    public static final int TILE_GROUND = 1;
    /** Tree group name. */
    public static final int TILE_TREE = 2;
    /** Road group name. */
    public static final int TILE_ROAD = 3;
    /** Transition group name. */
    public static final int TILE_TRANSITION = 4;
    /** Tree transition group name. */
    public static final int TILE_TRANSITION2 = 5;
    /** Water group name. */
    public static final String WATER = "water";
    /** Ground group name. */
    public static final String GROUND = "ground";
    /** Tree group name. */
    public static final String TREE = "tree";
    /** Road group name. */
    public static final String ROAD = "road";
    /** Transition group name. */
    public static final String TRANSITION = "transition";
    /** Tree transition group name. */
    public static final String TRANSITION2 = "transition2";
    /** Sheet ID. */
    public static final Integer SHEET = Integer.valueOf(0);

    /**
     * Create the raw test map without transition.
     * 
     * @param size The map size in tile.
     * @return The created map.
     */
    public static MapTile createMap(int size)
    {
        final Services services = new Services();
        final MapTileGame map = services.add(new MapTileGame());
        final MapTileGroup mapGroup = new MapTileGroupModel();
        map.addFeature(mapGroup);
        map.addFeature(new MapTileTransitionModel(services));
        map.addFeature(new MapTileCircuitModel(services));
        map.create(1, 1, size, size);

        setGroups(map);

        return map;
    }

    /**
     * Set the map tile groups.
     * 
     * @param map The map reference.
     */
    public static void setGroups(MapTile map)
    {
        final Collection<TileGroup> groups = new ArrayList<>();
        groups.add(new TileGroup(WATER, TileGroupType.PLAIN, Arrays.asList(new TileRef(SHEET, TILE_WATER))));
        groups.add(new TileGroup(GROUND, TileGroupType.PLAIN, Arrays.asList(new TileRef(SHEET, TILE_GROUND))));
        groups.add(new TileGroup(TREE, TileGroupType.PLAIN, Arrays.asList(new TileRef(SHEET, TILE_TREE))));
        groups.add(new TileGroup(ROAD, TileGroupType.CIRCUIT, Arrays.asList(new TileRef(SHEET, TILE_ROAD))));
        groups.add(new TileGroup(TRANSITION,
                                 TileGroupType.TRANSITION,
                                 Arrays.asList(new TileRef(SHEET, TILE_TRANSITION))));
        groups.add(new TileGroup(TRANSITION2,
                                 TileGroupType.TRANSITION,
                                 Arrays.asList(new TileRef(SHEET, TILE_TRANSITION2))));

        final Media config = Medias.create("groups.xml");
        TileGroupsConfig.exports(config, groups);

        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        mapGroup.loadGroups(config);

        assertTrue(config.getFile().delete());
    }

    /**
     * Fill map with center tiles from group.
     * 
     * @param map The map reference.
     * @param number The tile number.
     */
    public static void fill(MapTile map, int number)
    {
        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                map.setTile(tx, ty, SHEET, number);
            }
        }
    }

    /**
     * Fill map with transition tiles.
     * 
     * @param map The map reference.
     * @param n1 The center tile number.
     * @param n2 The starting tile transition number.
     * @param pos The starting position.
     */
    public static void fill(MapTile map, int n1, int n2, int pos)
    {
        fill(map, n1, n2, pos, pos);
    }

    /**
     * Fill map with transition tiles.
     * 
     * @param map The map reference.
     * @param n1 The center tile number.
     * @param n2 The starting tile transition number.
     * @param posX The starting horizontal position.
     * @param posY The starting vertical position.
     */
    public static void fill(MapTile map, int n1, int n2, int posX, int posY)
    {
        map.setTile(posX, posY, SHEET, n1);

        for (final Tile neighbor : map.getNeighbors(map.getTile(posX, posY)))
        {
            if (neighbor.getNumber() != n1)
            {
                map.setTile(neighbor.getInTileX(), neighbor.getInTileY(), SHEET, n2);
            }
        }
    }
}
