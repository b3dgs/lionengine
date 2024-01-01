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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.WATER;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link MapTransitionExtractor}.
 */
final class MapTransitionExtractorTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Get the transition for the specified tile.
     * 
     * @param map The map reference.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The tile found.
     */
    private static Transition get(MapTile map, int tx, int ty)
    {
        return new MapTransitionExtractor(map).getTransition(map.getTile(tx, ty));
    }

    /**
     * Check the center transition.
     */
    @Test
    void testTransitionCenter()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        final Transition transition = new Transition(TransitionType.CENTER, WATER, WATER);

        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            assertEquals(transition, get(map, tx, 0));
            assertEquals(transition, get(map, tx, map.getInTileHeight() - 1));
        }

        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            assertEquals(transition, get(map, 0, ty));
            assertEquals(transition, get(map, map.getInTileWidth() - 1, ty));
        }

        assertNull(get(map, map.getInTileWidth() / 2, map.getInTileHeight() / 2));
    }

    /**
     * Check the corners transitions.
     */
    @Test
    void testCorners()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        assertEquals(new Transition(TransitionType.UP_LEFT, WATER, GROUND), get(map, 2, 4));
        assertEquals(new Transition(TransitionType.UP_RIGHT, WATER, GROUND), get(map, 4, 4));
        assertEquals(new Transition(TransitionType.DOWN_LEFT, WATER, GROUND), get(map, 2, 2));
        assertEquals(new Transition(TransitionType.DOWN_RIGHT, WATER, GROUND), get(map, 4, 2));
    }

    /**
     * Check the corners inverted transitions.
     */
    @Test
    void testCornersInverted()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_WATER, TILE_TRANSITION, 3);

        assertEquals(new Transition(TransitionType.CORNER_UP_LEFT, WATER, GROUND), get(map, 2, 4));
        assertEquals(new Transition(TransitionType.CORNER_UP_RIGHT, WATER, GROUND), get(map, 4, 4));
        assertEquals(new Transition(TransitionType.CORNER_DOWN_LEFT, WATER, GROUND), get(map, 2, 2));
        assertEquals(new Transition(TransitionType.CORNER_DOWN_RIGHT, WATER, GROUND), get(map, 4, 2));
    }

    /**
     * Check the horizontal transitions.
     */
    @Test
    void testHorizontals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        assertEquals(new Transition(TransitionType.RIGHT, WATER, GROUND), get(map, 2, 3));
        assertEquals(new Transition(TransitionType.LEFT, WATER, GROUND), get(map, 4, 3));
    }

    /**
     * Check the vertical transitions.
     */
    @Test
    void testVerticals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        assertEquals(new Transition(TransitionType.UP, WATER, GROUND), get(map, 3, 4));
        assertEquals(new Transition(TransitionType.DOWN, WATER, GROUND), get(map, 3, 2));
    }

    /**
     * Check the diagonal transitions.
     */
    @Test
    void testDiagonal()
    {
        final MapTile map = UtilMap.createMap(9);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 5);

        assertEquals(new Transition(TransitionType.UP_RIGHT_DOWN_LEFT, WATER, GROUND), get(map, 4, 4));
    }

    /**
     * Check the diagonal inverted transitions.
     */
    @Test
    void testDiagonalInverted()
    {
        final MapTile map = UtilMap.createMap(9);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 5, 3);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3, 5);

        assertEquals(new Transition(TransitionType.UP_LEFT_DOWN_RIGHT, WATER, GROUND), get(map, 4, 4));
    }

    /**
     * Check the single group transition.
     * <p>
     * Problem here consists in having two groups without transition group to find tile index at right place.
     * </p>
     */
    @Test
    void testSingleGroup()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        map.setTile(3, 3, TILE_TRANSITION);

        assertNull(get(map, 3, 3));
    }

    /**
     * Check the three groups transition.
     * <p>
     * Problem here consists in having more than two groups to solve transition, which requires two different groups.
     * </p>
     */
    @Test
    void testThreeGroups()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        map.setTile(1, 1, TILE_GROUND);
        map.setTile(3, 3, TILE_TREE);

        assertNull(get(map, 2, 2));
    }

    /**
     * Check the transition with <code>null</code> neighbors.
     */
    @Test
    void testNullNeighbor()
    {
        final MapTile map = UtilMap.createMap(7);
        map.setTile(4, 2, TILE_WATER);
        map.setTile(3, 3, TILE_WATER);

        assertEquals(new Transition(TransitionType.CENTER, WATER, WATER), get(map, 3, 3));
    }
}
