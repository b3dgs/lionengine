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
package com.b3dgs.lionengine.game.map.transition;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGame;

/**
 * Utility related to map manipulation.
 */
public class UtilMap
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
        final MapTileGame map = services.add(new MapTileGame()
        {
            @Override
            public Tile createTile(Integer sheet, int number, double x, double y)
            {
                return new TileGame(sheet, number, x, y, 1, 1);
            }
        });
        final MapTileGroup mapGroup = new MapTileGroupModel();
        map.addFeature(mapGroup);
        map.addFeature(new MapTileTransitionModel(services));
        map.create(size, size);

        setGroups(map);

        return map;
    }

    /**
     * Create map with transitive.
     * 
     * @return The created configuration.
     */
    public static Media createTransitions()
    {
        final MapTile map1 = createMap(16);
        fill(map1, TILE_GROUND);
        fillTransition(map1, TILE_GROUND, TILE_TRANSITION, 6);
        fillTransition(map1, TILE_WATER, TILE_TRANSITION, 3);
        fillTransition(map1, TILE_TREE, TILE_TRANSITION2, 9);
        fillTransition(map1, TILE_GROUND, TILE_TRANSITION, 13);
        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 13, 13));

        final MapTile map2 = createMap(10);
        fill(map2, TILE_WATER);
        fillTransition(map2, TILE_GROUND, TILE_TRANSITION, 5);

        final MapTile map3 = createMap(10);
        fill(map3, TILE_TREE);
        fillTransition(map3, TILE_GROUND, TILE_TRANSITION2, 5);

        final MapTile map4 = createMap(5);
        fill(map4, TILE_ROAD);
        map4.setTile(map4.createTile(SHEET, TILE_GROUND, 2, 2));

        final Media config = Medias.create("transitives.xml");
        TransitionsConfig.exports(config, new TransitionsExtractorImpl().getTransitions(map1, map2, map3, map4));

        return config;
    }

    /**
     * Set the map tile groups.
     * 
     * @param map The map reference.
     */
    private static void setGroups(MapTile map)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        mapGroup.changeGroup(map.createTile(SHEET, TILE_WATER, 0, 0), WATER);
        mapGroup.changeGroup(map.createTile(SHEET, TILE_GROUND, 0, 0), GROUND);
        mapGroup.changeGroup(map.createTile(SHEET, TILE_TREE, 0, 0), TREE);
        mapGroup.changeGroup(map.createTile(SHEET, TILE_ROAD, 0, 0), ROAD);
        mapGroup.changeGroup(map.createTile(SHEET, TILE_TRANSITION, 0, 0), TRANSITION);
        mapGroup.changeGroup(map.createTile(SHEET, TILE_TRANSITION2, 0, 0), TRANSITION2);
    }

    /**
     * Fill map with transition tiles.
     * 
     * @param map The map reference.
     * @param n1 The center tile number.
     * @param n2 The starting tile transition number.
     * @param pos The starting position.
     */
    public static void fillTransition(MapTile map, int n1, int n2, int pos)
    {
        fillTransition(map, n1, n2, pos, pos);
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
    public static void fillTransition(MapTile map, int n1, int n2, int posX, int posY)
    {
        final Tile center = map.createTile(SHEET, n1, posX, posY);
        map.setTile(center);

        for (final Tile neighbor : map.getNeighbors(center))
        {
            if (neighbor.getNumber() != n1)
            {
                final Tile tile = map.createTile(SHEET, n2, neighbor.getX(), neighbor.getY());
                map.setTile(tile);
            }
        }
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
                final Tile tile = map.createTile(SHEET, number, tx, ty);
                map.setTile(tile);
            }
        }
    }
}
