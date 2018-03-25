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
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION2;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.WATER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test the group transitive class.
 */
public class TransitiveGroupTest
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
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the transitive groups.
     */
    @Test
    public void testTransitives()
    {
        final MapTile map = UtilMap.createMap(30);
        UtilMap.fill(map, TILE_WATER);

        final Media config = UtilMapTransition.createTransitions();
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        map.getFeature(MapTileTransition.class).loadTransitions(config);

        final TransitiveGroup transitive = new TransitiveGroup(map);
        transitive.load();

        Assert.assertEquals(Arrays.asList(new GroupTransition(WATER, GROUND), new GroupTransition(GROUND, TREE)),
                            transitive.getTransitives(WATER, TREE));

        Assert.assertEquals(WATER, mapGroup.getGroup(map.getTile(15, 15)));

        final Tile tile = map.createTile(SHEET, TILE_TREE, 15, 15);
        map.setTile(tile);
        transitive.checkTransitives(tile);

        Assert.assertEquals(TREE, mapGroup.getGroup(map.getTile(15, 15)));
        Assert.assertEquals(WATER, mapGroup.getGroup(map.getTile(14, 14)));
        Assert.assertEquals(GROUND, mapGroup.getGroup(map.getTile(13, 13)));

        Assert.assertTrue(transitive.getTransitives("a", "b").isEmpty());
        Assert.assertTrue(transitive.getDirectTransitiveTiles(new Transition(TransitionType.CENTER, "a", "a"))
                                    .isEmpty());

        Assert.assertArrayEquals(Arrays.asList(new TileRef(SHEET, TILE_TRANSITION2)).toArray(),
                                 transitive.getDirectTransitiveTiles(new Transition(TransitionType.UP_LEFT,
                                                                                    WATER,
                                                                                    TREE))
                                           .toArray());

        Assert.assertTrue(config.getFile().delete());
    }

    /**
     * Test the transitive reduce function.
     */
    @Test
    public void testReduce()
    {
        final Collection<GroupTransition> transitive = new ArrayList<>();
        transitive.add(new GroupTransition("a", "b"));
        transitive.add(new GroupTransition("b", "c"));
        transitive.add(new GroupTransition("c", "b"));
        transitive.add(new GroupTransition("b", "d"));

        final Collection<GroupTransition> expected = new ArrayList<>();
        expected.add(new GroupTransition("a", "b"));
        expected.add(new GroupTransition("b", "d"));

        TransitiveGroup.reduceTransitive(transitive);

        Assert.assertEquals(expected, transitive);
    }
}
