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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test the circuit extractor class.
 */
public class MapCircuitExtractorTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
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
    public void checkCircuitMiddle()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_ROAD, TILE_ROAD, 3);

        Assert.assertEquals(new Circuit(CircuitType.MIDDLE, ROAD, ROAD), get(map, 3, 3));
    }

    /**
     * Check the angles circuits.
     */
    @Test
    public void checkAngles()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_ROAD, TILE_ROAD, 3);
        map.setTile(map.createTile(SHEET, TILE_GROUND, 3, 3));

        Assert.assertEquals(new Circuit(CircuitType.ANGLE_TOP_LEFT, ROAD, GROUND), get(map, 2, 4));
        Assert.assertEquals(new Circuit(CircuitType.ANGLE_TOP_RIGHT, ROAD, GROUND), get(map, 4, 4));
        Assert.assertEquals(new Circuit(CircuitType.ANGLE_BOTTOM_LEFT, ROAD, GROUND), get(map, 2, 2));
        Assert.assertEquals(new Circuit(CircuitType.ANGLE_BOTTOM_RIGHT, ROAD, GROUND), get(map, 4, 2));
    }

    /**
     * Check the horizontal circuits.
     */
    @Test
    public void checkHorizontals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 2, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 4, 3));

        Assert.assertEquals(new Circuit(CircuitType.LEFT, ROAD, GROUND), get(map, 2, 3));
        Assert.assertEquals(new Circuit(CircuitType.HORIZONTAL, ROAD, GROUND), get(map, 3, 3));
        Assert.assertEquals(new Circuit(CircuitType.RIGHT, ROAD, GROUND), get(map, 4, 3));
    }

    /**
     * Check the vertical circuits.
     */
    @Test
    public void checkVerticals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 2));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 4));

        Assert.assertEquals(new Circuit(CircuitType.TOP, ROAD, GROUND), get(map, 3, 4));
        Assert.assertEquals(new Circuit(CircuitType.VERTICAL, ROAD, GROUND), get(map, 3, 3));
        Assert.assertEquals(new Circuit(CircuitType.BOTTOM, ROAD, GROUND), get(map, 3, 2));
    }

    /**
     * Check the three way junction top circuits.
     */
    @Test
    public void checkT3JTop()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 2, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 4, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 2));

        Assert.assertEquals(new Circuit(CircuitType.T3J_TOP, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the three way junction left circuits.
     */
    @Test
    public void checkT3JLeft()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 2));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 4));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 4, 3));

        Assert.assertEquals(new Circuit(CircuitType.T3J_LEFT, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the three way junction right circuits.
     */
    @Test
    public void checkT3JRight()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 2));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 4));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 2, 3));

        Assert.assertEquals(new Circuit(CircuitType.T3J_RIGHT, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the three way junction bottom circuits.
     */
    @Test
    public void checkT3JBottom()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 2, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 4, 3));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 4));

        Assert.assertEquals(new Circuit(CircuitType.T3J_BOTTOM, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the block circuits.
     */
    @Test
    public void checkBlock()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));

        Assert.assertEquals(new Circuit(CircuitType.BLOCK, ROAD, GROUND), get(map, 3, 3));
    }

    /**
     * Check the circuit with <code>null</code> neighbors.
     */
    @Test
    public void checkNullNeighbor()
    {
        final MapTile map = UtilMap.createMap(7);
        map.setTile(map.createTile(SHEET, TILE_ROAD, 4, 2));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 3, 3));

        Assert.assertNull(get(map, 3, 3));
    }

    /**
     * Check the circuit with different sheets.
     */
    @Test
    public void checkDifferentSheet()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        map.setTile(map.createTile(Integer.valueOf(1), TILE_ROAD, 3, 3));

        Assert.assertEquals(new Circuit(CircuitType.BLOCK, MapTileGroupModel.NO_GROUP_NAME, GROUND), get(map, 3, 3));
    }
}
