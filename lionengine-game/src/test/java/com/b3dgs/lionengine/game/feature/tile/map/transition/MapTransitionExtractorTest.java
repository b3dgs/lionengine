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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.SHEET;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.WATER;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test the transition extractor class.
 */
public class MapTransitionExtractorTest
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
    public void checkTransitionCenter()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        final Transition transition = new Transition(TransitionType.CENTER, WATER, WATER);

        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            Assert.assertEquals(transition, get(map, tx, 0));
            Assert.assertEquals(transition, get(map, tx, map.getInTileHeight() - 1));
        }

        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            Assert.assertEquals(transition, get(map, 0, ty));
            Assert.assertEquals(transition, get(map, map.getInTileWidth() - 1, ty));
        }

        Assert.assertNull(get(map, map.getInTileWidth() / 2, map.getInTileHeight() / 2));
    }

    /**
     * Check the corners transitions.
     */
    @Test
    public void checkCorners()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        Assert.assertEquals(new Transition(TransitionType.UP_LEFT, WATER, GROUND), get(map, 2, 4));
        Assert.assertEquals(new Transition(TransitionType.UP_RIGHT, WATER, GROUND), get(map, 4, 4));
        Assert.assertEquals(new Transition(TransitionType.DOWN_LEFT, WATER, GROUND), get(map, 2, 2));
        Assert.assertEquals(new Transition(TransitionType.DOWN_RIGHT, WATER, GROUND), get(map, 4, 2));
    }

    /**
     * Check the corners inverted transitions.
     */
    @Test
    public void checkCornersInverted()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_WATER, TILE_TRANSITION, 3);

        Assert.assertEquals(new Transition(TransitionType.CORNER_UP_LEFT, WATER, GROUND), get(map, 2, 4));
        Assert.assertEquals(new Transition(TransitionType.CORNER_UP_RIGHT, WATER, GROUND), get(map, 4, 4));
        Assert.assertEquals(new Transition(TransitionType.CORNER_DOWN_LEFT, WATER, GROUND), get(map, 2, 2));
        Assert.assertEquals(new Transition(TransitionType.CORNER_DOWN_RIGHT, WATER, GROUND), get(map, 4, 2));
    }

    /**
     * Check the horizontal transitions.
     */
    @Test
    public void checkHorizontals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        Assert.assertEquals(new Transition(TransitionType.RIGHT, WATER, GROUND), get(map, 2, 3));
        Assert.assertEquals(new Transition(TransitionType.LEFT, WATER, GROUND), get(map, 4, 3));
    }

    /**
     * Check the vertical transitions.
     */
    @Test
    public void checkVerticals()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        Assert.assertEquals(new Transition(TransitionType.UP, WATER, GROUND), get(map, 3, 4));
        Assert.assertEquals(new Transition(TransitionType.DOWN, WATER, GROUND), get(map, 3, 2));
    }

    /**
     * Check the diagonal transitions.
     */
    @Test
    public void checkDiagonal()
    {
        final MapTile map = UtilMap.createMap(9);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 5);

        Assert.assertEquals(new Transition(TransitionType.UP_RIGHT_DOWN_LEFT, WATER, GROUND), get(map, 4, 4));
    }

    /**
     * Check the diagonal inverted transitions.
     */
    @Test
    public void checkDiagonalInverted()
    {
        final MapTile map = UtilMap.createMap(9);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 5, 3);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3, 5);

        Assert.assertEquals(new Transition(TransitionType.UP_LEFT_DOWN_RIGHT, WATER, GROUND), get(map, 4, 4));
    }

    /**
     * Check the single group transition.
     * <p>
     * Problem here consists in having two groups without transition group to find tile index at right place.
     * </p>
     */
    @Test
    public void checkSingleGroup()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        map.setTile(map.createTile(SHEET, TILE_TRANSITION, 3, 3));

        Assert.assertNull(get(map, 3, 3));
    }

    /**
     * Check the three groups transition.
     * <p>
     * Problem here consists in having more than two groups to solve transition, which requires two different groups.
     * </p>
     */
    @Test
    public void checkThreeGroups()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        map.setTile(map.createTile(SHEET, TILE_GROUND, 1, 1));
        map.setTile(map.createTile(SHEET, TILE_TREE, 3, 3));

        Assert.assertNull(get(map, 2, 2));
    }

    /**
     * Check the transition with <code>null</code> neighbors.
     */
    @Test
    public void checkNullNeighbor()
    {
        final MapTile map = UtilMap.createMap(7);
        map.setTile(map.createTile(SHEET, TILE_WATER, 4, 2));
        map.setTile(map.createTile(SHEET, TILE_WATER, 3, 3));

        Assert.assertEquals(new Transition(TransitionType.CENTER, WATER, WATER), get(map, 3, 3));
    }

    /**
     * Check the transition with different sheets.
     */
    @Test
    public void checkDifferentSheet()
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, TILE_WATER);
        map.setTile(map.createTile(Integer.valueOf(1), TILE_WATER, 3, 3));

        Assert.assertNull(get(map, 3, 3));
    }
}
