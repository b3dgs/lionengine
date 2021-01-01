/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link MapCircuitExtractor}.
 */
final class MapCircuitExtractorTest
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
     * Get the circuit for the specified tile.
     * 
     * @param map The map reference.
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The tile found.
     */
    private static Circuit get(MapTile map, int tx, int ty)
    {
        return new MapCircuitExtractor(map).getCircuit(map.getTile(tx, ty));
    }

    /**
     * Check the middle circuit.
     */
    @Test
    void testCircuitMiddle()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_ROAD, TILE_ROAD, 3);

        assertEquals(new Circuit(CircuitType.MIDDLE, ROAD, ROAD), get(map, 3, 3));
    }

    /**
     * Check the angles circuits.
     */
    @Test
    void testAngles()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_ROAD, TILE_ROAD, 3);
        map.setTile(3, 3, TILE_GROUND);

        assertEquals(new Circuit(CircuitType.ANGLE_TOP_LEFT, ROAD, GROUND), get(map, 2, 4));
        assertEquals(new Circuit(CircuitType.ANGLE_TOP_RIGHT, ROAD, GROUND), get(map, 4, 4));
        assertEquals(new Circuit(CircuitType.ANGLE_BOTTOM_LEFT, ROAD, GROUND), get(map, 2, 2));
        assertEquals(new Circuit(CircuitType.ANGLE_BOTTOM_RIGHT, ROAD, GROUND), get(map, 4, 2));
    }

    /**
     * Check the horizontal circuits.
     */
    @Test
    void testHorizontals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(2, 3, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);
        map.setTile(4, 3, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.LEFT, ROAD, GROUND), get(map, 2, 3));
        assertEquals(new Circuit(CircuitType.HORIZONTAL, ROAD, GROUND), get(map, 3, 3));
        assertEquals(new Circuit(CircuitType.RIGHT, ROAD, GROUND), get(map, 4, 3));
    }

    /**
     * Check the vertical circuits.
     */
    @Test
    void testVerticals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(3, 2, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);
        map.setTile(3, 4, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.TOP, ROAD, GROUND), get(map, 3, 4));
        assertEquals(new Circuit(CircuitType.VERTICAL, ROAD, GROUND), get(map, 3, 3));
        assertEquals(new Circuit(CircuitType.BOTTOM, ROAD, GROUND), get(map, 3, 2));
    }

    /**
     * Check the three way junction top circuits.
     */
    @Test
    void testT3JTop()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(2, 3, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);
        map.setTile(4, 3, TILE_ROAD);
        map.setTile(3, 2, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.T3J_TOP, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the three way junction left circuits.
     */
    @Test
    void testT3JLeft()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(3, 2, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);
        map.setTile(3, 4, TILE_ROAD);
        map.setTile(4, 3, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.T3J_LEFT, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the three way junction right circuits.
     */
    @Test
    void testT3JRight()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(3, 2, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);
        map.setTile(3, 4, TILE_ROAD);
        map.setTile(2, 3, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.T3J_RIGHT, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the three way junction bottom circuits.
     */
    @Test
    void testT3JBottom()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(2, 3, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);
        map.setTile(4, 3, TILE_ROAD);
        map.setTile(3, 4, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.T3J_BOTTOM, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the block circuits.
     */
    @Test
    void testBlock()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(3, 3, TILE_ROAD);

        assertEquals(new Circuit(CircuitType.BLOCK, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the circuit with <code>null</code> neighbors.
     */
    @Test
    void testNullNeighbor()
    {
        final MapTile map = UtilMap.createMap(7);
        map.setTile(4, 2, TILE_ROAD);
        map.setTile(3, 3, TILE_ROAD);

        assertNull(get(map, 3, 3));
    }
}
