/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION2;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;

import java.util.Arrays;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Utility related to map manipulation.
 */
public final class UtilMapTransition
{
    /**
     * Create map with transitive.
     * 
     * @return The created configuration.
     */
    public static Media createTransitions()
    {
        final MapTile map1 = UtilMap.createMap(16);
        UtilMap.fill(map1, TILE_GROUND);
        UtilMap.fill(map1, TILE_GROUND, TILE_TRANSITION, 6);
        UtilMap.fill(map1, TILE_WATER, TILE_TRANSITION, 3);
        UtilMap.fill(map1, TILE_TREE, TILE_TRANSITION2, 9);
        UtilMap.fill(map1, TILE_GROUND, TILE_TRANSITION, 13);
        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 13, 13));

        final MapTile map2 = UtilMap.createMap(10);
        UtilMap.fill(map2, TILE_WATER);
        UtilMap.fill(map2, TILE_GROUND, TILE_TRANSITION, 5);

        final MapTile map3 = UtilMap.createMap(10);
        UtilMap.fill(map3, TILE_TREE);
        UtilMap.fill(map3, TILE_GROUND, TILE_TRANSITION2, 5);

        final MapTile map4 = UtilMap.createMap(5);
        UtilMap.fill(map4, TILE_ROAD);
        map4.setTile(map4.createTile(SHEET, TILE_GROUND, 2, 2));

        final Media config = Medias.create("transitives.xml");
        TransitionsConfig.exports(config,
                                  new TransitionsExtractorImpl().getTransitions(Arrays.asList(map1, map2, map3, map4)));

        return config;
    }
}
