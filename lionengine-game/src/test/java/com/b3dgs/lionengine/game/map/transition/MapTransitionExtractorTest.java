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

import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGame;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Test the transition extractor class.
 */
public class MapTransitionExtractorTest
{
    /** Ground group name. */
    private static final String GROUND = "ground";
    /** Ground group name. */
    private static final String WATER = "water";
    /** Transition group name. */
    private static final String TRANSITION = "transition";
    /** Sheet ID. */
    private static final Integer SHEET = Integer.valueOf(0);

    /**
     * Create the raw test map without transition.
     * 
     * @return The created map.
     */
    private static MapTile createMap()
    {
        final MapTileGame map = new MapTileGame();
        final MapTileGroup mapGroup = new MapTileGroupModel();

        map.addFeature(mapGroup);
        map.create(7, 7);

        fillMap(map, mapGroup);

        return map;
    }

    /**
     * Fill map with center tiles.
     * 
     * @param map The map reference.
     * @param mapGroup The map group reference.
     */
    private static void fillMap(MapTile map, MapTileGroup mapGroup)
    {
        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                final Tile tile = new TileGame(SHEET, 0, tx, ty, 1, 1);
                map.setTile(tile);
                mapGroup.changeGroup(tile, WATER);
            }
        }
    }

    /**
     * Fill map with transition tiles.
     * 
     * @param map The map reference.
     */
    private static void fillMapTransition(MapTile map)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        int n = 1;

        final Tile center = new TileGame(SHEET, n, map.getInTileWidth() / 2, map.getInTileHeight() / 2, 1, 1);
        mapGroup.changeGroup(center, GROUND);
        map.setTile(center);

        for (final Tile neighbor : map.getNeighbors(map.getTile(map.getInTileWidth() / 2, map.getInTileHeight() / 2)))
        {
            n++;
            final Tile tile = new TileGame(SHEET, n, neighbor.getX(), neighbor.getY(), 1, 1);
            map.setTile(tile);
            mapGroup.changeGroup(tile, TRANSITION);
        }
    }

    /**
     * Invert map groups.
     * 
     * @param map The map reference.
     */
    private static void invertGroups(MapTile map)
    {
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        final Collection<TileRef> changed = new HashSet<TileRef>();
        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                final Tile tile = map.getTile(tx, ty);
                final TileRef ref = new TileRef(tile);
                if (!changed.contains(ref) && WATER.equals(mapGroup.getGroup(tile)))
                {
                    mapGroup.changeGroup(tile, GROUND);
                    changed.add(ref);
                }
                else if (!changed.contains(ref) && GROUND.equals(mapGroup.getGroup(tile)))
                {
                    mapGroup.changeGroup(tile, WATER);
                    changed.add(ref);
                }
            }
        }
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
        final MapTile map = createMap();
        fillMapTransition(map);

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
        final MapTile map = createMap();
        fillMapTransition(map);

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
        final MapTile map = createMap();
        fillMapTransition(map);
        invertGroups(map);

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
        final MapTile map = createMap();
        fillMapTransition(map);

        Assert.assertEquals(new Transition(TransitionType.RIGHT, WATER, GROUND), get(map, 2, 3));
        Assert.assertEquals(new Transition(TransitionType.LEFT, WATER, GROUND), get(map, 4, 3));
    }

    /**
     * Check the vertical transitions.
     */
    @Test
    public void checkVerticals()
    {
        final MapTile map = createMap();
        fillMapTransition(map);

        Assert.assertEquals(new Transition(TransitionType.UP, WATER, GROUND), get(map, 3, 4));
        Assert.assertEquals(new Transition(TransitionType.DOWN, WATER, GROUND), get(map, 3, 2));
    }

    /**
     * Check the diagonal transitions.
     */
    @Test
    public void checkDiagonal()
    {
        final MapTile map = createMap();
        fillMapTransition(map);

        map.setTile(new TileGame(SHEET, 0, 2, 2, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 2, 3, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 2, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 3, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 4, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 4, 3, 1, 1));
        map.setTile(new TileGame(SHEET, 0, 4, 4, 1, 1));

        Assert.assertEquals(new Transition(TransitionType.UP_LEFT_DOWN_RIGHT, WATER, GROUND), get(map, 3, 3));
    }

    /**
     * Check the diagonal transitions.
     */
    @Test
    public void checkDiagonalInverted()
    {
        final MapTile map = createMap();
        fillMapTransition(map);

        map.setTile(new TileGame(SHEET, 0, 4, 2, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 2, 3, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 2, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 3, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 4, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 4, 3, 1, 1));
        map.setTile(new TileGame(SHEET, 0, 2, 4, 1, 1));

        Assert.assertEquals(new Transition(TransitionType.UP_RIGHT_DOWN_LEFT, WATER, GROUND), get(map, 3, 3));
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
        final MapTile map = createMap();
        map.setTile(new TileGame(SHEET, 2, 3, 3, 1, 1));

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
        final MapTile map = createMap();
        map.setTile(new TileGame(SHEET, 1, 1, 1, 1, 1));
        map.setTile(new TileGame(SHEET, 2, 3, 3, 1, 1));
        map.getFeature(MapTileGroup.class).changeGroup(map.getTile(3, 3), GROUND);

        Assert.assertNull(get(map, 2, 2));
    }

    /**
     * Check the transition with <code>null</code> neighbors.
     */
    @Test
    public void checkNullNeighbor()
    {
        final MapTile map = createMap();
        map.create(7, 7);
        map.setTile(new TileGame(SHEET, 1, 2, 2, 1, 1));
        map.setTile(new TileGame(SHEET, 0, 3, 3, 1, 1));

        Assert.assertNull(get(map, 3, 3));
    }

    /**
     * Check the transition with different sheets.
     */
    @Test
    public void checkDifferentSheet()
    {
        final MapTile map = createMap();
        map.setTile(new TileGame(Integer.valueOf(1), 0, 3, 3, 1, 1));

        Assert.assertNull(get(map, 3, 3));
    }
}
