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

import static com.b3dgs.lionengine.game.map.transition.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.TRANSITION;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.TRANSITION2;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.TREE;
import static com.b3dgs.lionengine.game.map.transition.UtilMap.WATER;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Test the map tile transition model class.
 */
public class MapTileTransitionModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilMap.createTransitions();
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
     * Create the map and configure it.
     * 
     * @param tileNumber The number to fill.
     * @return The configured map.
     */
    private static MapTile createMap(int tileNumber)
    {
        final MapTile map = UtilMap.createMap(12);
        UtilMap.fill(map, tileNumber);

        map.getFeature(MapTileTransition.class).loadTransitions(config);

        return map;
    }

    /**
     * Test the map transition resolution.
     */
    @Test
    public void testResolution()
    {
        testResolution(TILE_WATER, WATER, TILE_GROUND, GROUND, TRANSITION);
        testResolution(TILE_WATER, WATER, TILE_TREE, TREE, TRANSITION2);

        testResolution(TILE_GROUND, GROUND, TILE_WATER, WATER, TRANSITION);
        testResolution(TILE_GROUND, GROUND, TILE_TREE, TREE, TRANSITION2);

        testResolution(TILE_TREE, TREE, TILE_GROUND, GROUND, TRANSITION2);
        testResolution(TILE_TREE, TREE, TILE_WATER, WATER, TRANSITION);
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
    private void testResolution(int tileNumber, String groupOrigin, int tileNew, String groupNew, String transition)
    {
        final MapTile map = createMap(tileNumber);
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final MapTileTransition mapTransition = map.getFeature(MapTileTransitionModel.class);

        Assert.assertEquals(groupOrigin, mapGroup.getGroup(map.getTile(8, 8)));

        final Tile tile = map.createTile(SHEET, tileNew, 8, 8);
        map.setTile(tile);

        Assert.assertEquals(groupNew, mapGroup.getGroup(map.getTile(8, 8)));

        for (final Tile neighbor : map.getNeighbors(tile))
        {
            Assert.assertEquals(groupOrigin, mapGroup.getGroup(neighbor));
        }

        mapTransition.resolve(tile);

        for (final Tile neighbor : map.getNeighbors(tile))
        {
            Assert.assertEquals(transition, mapGroup.getGroup(neighbor));
        }
    }
}
