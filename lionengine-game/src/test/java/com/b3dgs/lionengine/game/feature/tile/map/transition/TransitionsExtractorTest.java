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

import static com.b3dgs.lionengine.UtilAssert.assertTrue;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_TRANSITION;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.TILE_WATER;
import static com.b3dgs.lionengine.game.feature.tile.map.UtilMap.WATER;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.UtilTransformable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link TransitionsExtractor}.
 */
final class TransitionsExtractorTest
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
        media = UtilTransformable.createMedia(TransitionsExtractorTest.class);
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
     * Create the map and configure it.
     * 
     * @param services The services reference.
     * @param setup The setup.
     * @param tileNumber The number to fill.
     * @return The configured map.
     */
    private static MapTile createMap(Services services, Setup setup, int tileNumber)
    {
        final MapTile map = UtilMap.createMap(tileNumber);
        map.getFeature(MapTileTransition.class).loadTransitions(config);

        return map;
    }

    /**
     * Check the transition with tile.
     * 
     * @param transitions The transitions found.
     * @param type The transition type.
     * @param in The group in.
     * @param out The group out.
     * @param number The tile number.
     */
    private static void has(Map<Transition, Collection<Integer>> transitions,
                            TransitionType type,
                            String in,
                            String out,
                            int number)
    {
        final Transition transition = new Transition(type, in, out);

        assertTrue(transitions.get(transition).contains(Integer.valueOf(number)));
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the transitions extraction.
     */
    @Test
    void testExtraction()
    {
        final MapTile map = createMap(services, setup, 7);
        UtilMap.fill(map, TILE_WATER);
        UtilMap.fill(map, TILE_GROUND, TILE_TRANSITION, 3);

        final MapTile map2 = createMap(services, setup, 7);
        UtilMap.fill(map2, TILE_GROUND);
        UtilMap.fill(map2, TILE_WATER, TILE_TRANSITION, 3);

        final MapTile map3 = createMap(services, setup, 3);

        final TransitionsExtractor extractor = new TransitionsExtractorImpl();
        final Map<Transition, Collection<Integer>> transitions = extractor.getTransitions(Arrays.asList(map,
                                                                                                        map2,
                                                                                                        map3));

        has(transitions, TransitionType.UP_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.UP_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.UP, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.UP, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.UP_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.UP_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.DOWN_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.DOWN_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.DOWN, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.DOWN, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.DOWN_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.DOWN_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_UP_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_UP_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_UP_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_UP_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_DOWN_LEFT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_DOWN_LEFT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CORNER_DOWN_RIGHT, WATER, GROUND, TILE_TRANSITION);
        has(transitions, TransitionType.CORNER_DOWN_RIGHT, GROUND, WATER, TILE_TRANSITION);

        has(transitions, TransitionType.CENTER, WATER, WATER, TILE_WATER);
        has(transitions, TransitionType.CENTER, GROUND, GROUND, TILE_GROUND);
    }
}
