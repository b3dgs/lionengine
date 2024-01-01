/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;

import java.util.Arrays;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;

/**
 * Utility related to circuit manipulation.
 */
final class UtilMapCircuit
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

        map1.setTile(1, 2, TILE_ROAD);
        map1.setTile(2, 3, TILE_ROAD);
        map1.setTile(2, 1, TILE_ROAD);
        map1.setTile(2, 2, TILE_ROAD);
        map1.setTile(3, 2, TILE_ROAD);

        map1.setTile(5, 5, TILE_ROAD);

        final MapTile map2 = UtilMap.createMap(5);
        map2.getFeature(MapTileTransition.class).loadTransitions(configTransition);
        UtilMap.fill(map2, TILE_GROUND);

        map2.setTile(1, 1, TILE_ROAD);
        map2.setTile(1, 2, TILE_ROAD);
        map2.setTile(1, 3, TILE_ROAD);
        map2.setTile(2, 1, TILE_ROAD);
        map2.setTile(2, 2, TILE_ROAD);
        map2.setTile(2, 3, TILE_ROAD);
        map2.setTile(3, 1, TILE_ROAD);
        map2.setTile(3, 2, TILE_ROAD);
        map2.setTile(3, 3, TILE_ROAD);

        final MapTile map3 = UtilMap.createMap(10);
        map3.getFeature(MapTileTransition.class).loadTransitions(configTransition);
        UtilMap.fill(map3, TILE_GROUND);

        map3.setTile(1, 2, TILE_ROAD);
        map3.setTile(2, 2, TILE_ROAD);
        map3.setTile(3, 2, TILE_ROAD);
        map3.setTile(2, 1, TILE_ROAD);

        map3.setTile(1, 5, TILE_ROAD);
        map3.setTile(2, 5, TILE_ROAD);
        map3.setTile(3, 5, TILE_ROAD);
        map3.setTile(2, 6, TILE_ROAD);

        map3.setTile(5, 6, TILE_ROAD);
        map3.setTile(5, 7, TILE_ROAD);
        map3.setTile(5, 8, TILE_ROAD);
        map3.setTile(6, 5, TILE_ROAD);

        map3.setTile(5, 6, TILE_ROAD);
        map3.setTile(5, 7, TILE_ROAD);
        map3.setTile(5, 8, TILE_ROAD);
        map3.setTile(6, 9, TILE_ROAD);

        final Media config = Medias.create("circuits.xml");
        CircuitsConfig.exports(config, new CircuitsExtractorImpl().getCircuits(Arrays.asList(map1, map2, map3)));

        return config;
    }
}
