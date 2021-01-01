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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION2;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TREE;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.WATER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link TransitiveGroup}.
 */
final class TransitiveGroupTest
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
     * Test the transitive groups.
     */
    @Test
    void testTransitives()
    {
        final MapTile map = UtilMap.createMap(30);
        UtilMap.fill(map, TILE_WATER);

        final Media config = UtilMapTransition.createTransitions();
        final MapTileGroup mapGroup = map.getFeature(MapTileGroup.class);
        map.getFeature(MapTileTransition.class).loadTransitions(config);

        final TransitiveGroup transitive = new TransitiveGroup(map);
        transitive.load();

        assertEquals(Arrays.asList(new GroupTransition(WATER, GROUND), new GroupTransition(GROUND, TREE)),
                     transitive.getTransitives(WATER, TREE));

        assertEquals(WATER, mapGroup.getGroup(map.getTile(15, 15)));

        map.setTile(15, 15, TILE_TREE);
        final Tile tile = map.getTile(15, 15);
        transitive.checkTransitives(tile);

        assertEquals(TREE, mapGroup.getGroup(map.getTile(15, 15)));
        assertEquals(WATER, mapGroup.getGroup(map.getTile(14, 14)));
        assertEquals(GROUND, mapGroup.getGroup(map.getTile(13, 13)));

        assertTrue(transitive.getTransitives("a", "b").isEmpty());
        assertTrue(transitive.getDirectTransitiveTiles(new Transition(TransitionType.CENTER, "a", "a")).isEmpty());

        assertArrayEquals(Arrays.asList(Integer.valueOf(TILE_TRANSITION2)).toArray(),
                          transitive.getDirectTransitiveTiles(new Transition(TransitionType.UP_LEFT, WATER, TREE))
                                    .toArray());

        assertTrue(config.getFile().delete());
    }

    /**
     * Test the transitive reduce function.
     */
    @Test
    void testReduce()
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

        assertEquals(expected, transitive);
    }
}
