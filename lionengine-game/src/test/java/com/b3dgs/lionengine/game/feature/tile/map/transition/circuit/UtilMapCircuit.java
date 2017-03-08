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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;

/**
 * Utility related to circuit manipulation.
 */
public class UtilMapCircuit
{
    /**
     * Create map with circuits.
     * 
     * @param configTransition The transition configuration.
     * @return The created configuration.
     */
    public static Media createCircuits(Media configTransition)
    {
        final MapTile map1 = UtilMap.createMap(6);
        map1.getFeature(MapTileTransition.class).loadTransitions(configTransition);
        UtilMap.fill(map1, TILE_GROUND);

        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 1, 2));
        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 2, 3));
        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 2, 1));
        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 2, 2));
        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 3, 2));

        map1.setTile(map1.createTile(SHEET, TILE_ROAD, 5, 5));

        final MapTile map2 = UtilMap.createMap(5);
        map2.getFeature(MapTileTransition.class).loadTransitions(configTransition);
        UtilMap.fill(map2, TILE_GROUND);

        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 1, 1));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 1, 2));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 1, 3));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 2, 1));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 2, 2));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 2, 3));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 3, 1));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 3, 2));
        map2.setTile(map2.createTile(SHEET, TILE_ROAD, 3, 3));

        final MapTile map3 = UtilMap.createMap(10);
        map3.getFeature(MapTileTransition.class).loadTransitions(configTransition);
        UtilMap.fill(map3, TILE_GROUND);

        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 1, 2));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 2, 2));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 3, 2));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 2, 1));

        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 1, 5));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 2, 5));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 3, 5));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 2, 6));

        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 5, 6));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 5, 7));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 5, 8));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 6, 5));

        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 5, 6));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 5, 7));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 5, 8));
        map3.setTile(map3.createTile(SHEET, TILE_ROAD, 6, 9));

        final Media config = Medias.create("circuits.xml");
        CircuitsConfig.exports(config, new CircuitsExtractorImpl().getCircuits(map1, map2, map3));

        return config;
    }
}
