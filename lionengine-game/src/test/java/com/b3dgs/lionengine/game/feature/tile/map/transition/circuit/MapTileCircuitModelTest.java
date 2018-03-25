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

import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_ROAD;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TRANSITION;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.UtilMapTransition;

/**
 * Test the map tile circuit model class.
 */
public class MapTileCircuitModelTest
{
    /** Test configuration transitions. */
    private static Media configTransitions;
    /** Test configuration circuits. */
    private static Media configCircuits;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        configTransitions = UtilMapTransition.createTransitions();
        configCircuits = UtilMapCircuit.createCircuits(configTransitions);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(configTransitions.getFile().delete());
        Assert.assertTrue(configCircuits.getFile().delete());
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
    public void testResolution()
    {
        final MapTile map = createMap(TILE_GROUND);
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileCircuit mapCircuit = map.getFeature(MapTileCircuitModel.class);

        map.setTile(map.createTile(SHEET, TILE_ROAD, 5, 6));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 5, 4));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 5, 5));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 4, 5));
        map.setTile(map.createTile(SHEET, TILE_ROAD, 6, 5));

        mapCircuit.resolve(map.getTile(5, 5));

        Assert.assertEquals(ROAD, mapGroup.getGroup(map.getTile(5, 5)));
    }

    /**
     * Test the map circuit resolution with transitive.
     */
    @Test
    public void testTransitive()
    {
        final MapTile map = createMap(TILE_WATER);
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileCircuit mapCircuit = map.getFeature(MapTileCircuitModel.class);

        final Tile newTile = map.createTile(SHEET, TILE_ROAD, 5, 5);
        map.setTile(newTile);
        mapCircuit.resolve(newTile);

        Assert.assertEquals(ROAD, mapGroup.getGroup(map.getTile(5, 5)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(4, 4)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(5, 4)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(6, 4)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(4, 5)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(6, 5)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(4, 6)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(5, 6)));
        Assert.assertEquals(TRANSITION, mapGroup.getGroup(map.getTile(6, 6)));
    }
}
