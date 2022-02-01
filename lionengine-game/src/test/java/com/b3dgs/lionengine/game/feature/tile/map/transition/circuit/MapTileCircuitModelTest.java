/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TRANSITION;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.UtilMapTransition;

/**
 * Test {@link MapTileCircuitModel}.
 */
final class MapTileCircuitModelTest
{
    /** Test configuration transitions. */
    private static Media configTransitions;
    /** Test configuration circuits. */
    private static Media configCircuits;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        configTransitions = UtilMapTransition.createTransitions();
        configCircuits = UtilMapCircuit.createCircuits(configTransitions);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(configTransitions.getFile().delete());
        assertTrue(configCircuits.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /**
     * Create the map and configure it.
     * 
     * @param tileNumber The number to fill.
     * @return The configured map.
     */
    private static MapTile createMap(int tileNumber)
    {
        final MapTile map = UtilMap.createMap(8);
        map.getFeature(MapTileTransition.class).loadTransitions(configTransitions);
        map.getFeature(MapTileCircuit.class).loadCircuits(configCircuits);

        UtilMap.fill(map, tileNumber);

        return map;
    }

    /**
     * Test the map circuit resolution.
     */
    @Test
    void testResolution()
    {
        final MapTile map = createMap(TILE_GROUND);
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileCircuit mapCircuit = map.getFeature(MapTileCircuitModel.class);

        map.setTile(5, 6, TILE_ROAD);
        map.setTile(5, 4, TILE_ROAD);
        map.setTile(5, 5, TILE_ROAD);
        map.setTile(4, 5, TILE_ROAD);
        map.setTile(6, 5, TILE_ROAD);

        mapCircuit.resolve(map.getTile(5, 5));

        assertEquals(ROAD, mapGroup.getGroup(map.getTile(5, 5)));
    }

    /**
     * Test the map circuit resolution with transitive.
     */
    @Test
    void testTransitive()
    {
        final MapTile map = createMap(TILE_WATER);
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileCircuit mapCircuit = map.getFeature(MapTileCircuitModel.class);

        map.setTile(5, 5, TILE_ROAD);
        final Tile newTile = map.getTile(5, 5);
        mapCircuit.resolve(newTile);

        assertEquals(ROAD, mapGroup.getGroup(map.getTile(5, 5)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(4, 4)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(5, 4)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(6, 4)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(4, 5)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(6, 5)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(4, 6)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(5, 6)));
        assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(6, 6)));
    }
}
