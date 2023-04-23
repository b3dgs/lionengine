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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TRANSITION;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TRANSITION2;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.WATER;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.UtilTransformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link MapTileTransitionModel}.
 */
final class MapTileTransitionModelTest
{
    /** Object config test. */
    private static Media media;
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        media = UtilTransformable.createMedia(MapTileTransitionModelTest.class);
        config = UtilMapTransition.createTransitions();
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        assertTrue(media.getFile().delete());
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the map resolution.
     * 
     * @param tileNumber The initial tile number.
     * @param groupOrigin The initial group.
     * @param tileNew The new tile number.
     * @param groupNew The new tile group.
     * @param transition The transition between initial tile and new tile.
     */
    private static void testResolution(int tileNumber,
                                       String groupOrigin,
                                       int tileNew,
                                       String groupNew,
                                       String transition)
    {
        final MapTile map = UtilMap.createMap(12);
        UtilMap.fill(map, tileNumber);
        map.getFeature(MapTileTransition.class).loadTransitions(config);

        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileTransition mapTransition = map.getFeature(MapTileTransitionModel.class);

        assertEquals(groupOrigin, mapGroup.getGroup(map.getTile(8, 8)));

        map.setTile(8, 8, tileNew);
        final Tile tile = map.getTile(8, 8);

        assertEquals(groupNew, mapGroup.getGroup(map.getTile(8, 8)));

        for (final Tile neighbor : map.getNeighbors(tile))
        {
            assertEquals(groupOrigin, mapGroup.getGroup(neighbor));
        }

        mapTransition.resolve(tile);

        for (final Tile neighbor : map.getNeighbors(tile))
        {
            assertEquals(transition, mapGroup.getGroup(neighbor));
        }
    }

    /**
     * Test the map transition resolution.
     */
    @Test
    void testResolution()
    {
        testResolution(TILE_WATER, WATER, TILE_GROUND, GROUND, TRANSITION);
        testResolution(TILE_WATER, WATER, TILE_TREE, TREE, TRANSITION2);

        testResolution(TILE_GROUND, GROUND, TILE_WATER, WATER, TRANSITION);
        testResolution(TILE_GROUND, GROUND, TILE_TREE, TREE, TRANSITION2);

        testResolution(TILE_TREE, TREE, TILE_GROUND, GROUND, TRANSITION2);
        testResolution(TILE_TREE, TREE, TILE_WATER, WATER, TRANSITION);
    }
}
