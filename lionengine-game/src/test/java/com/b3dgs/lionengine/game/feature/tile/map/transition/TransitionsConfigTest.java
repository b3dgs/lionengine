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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link TransitionsConfig}.
 */
final class TransitionsConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        assertPrivateConstructor(TransitionsConfig.class);
    }

    /**
     * Test exports and imports.
     * 
     * @throws IOException If error.
     */
    @Test
    void testExportsImports() throws IOException
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, UtilMap.TILE_WATER);
        UtilMap.fill(map, UtilMap.TILE_WATER, UtilMap.TILE_TRANSITION, 3);

        final MapTile map2 = UtilMap.createMap(7);
        UtilMap.fill(map, UtilMap.TILE_WATER);
        UtilMap.fill(map, UtilMap.TILE_GROUND, UtilMap.TILE_TRANSITION, 3);

        final MapTileGame map3 = new MapTileGame();
        map3.addFeature(new MapTileGroupModel());
        map3.create(1, 1, 3, 3);

        final TransitionsExtractor extractor = new TransitionsExtractorImpl();
        final Map<Transition, Collection<Integer>> transitions = extractor.getTransitions(Arrays.asList(map,
                                                                                                        map2,
                                                                                                        map3));

        final Media media = Medias.create("transition.xml");
        TransitionsConfig.exports(media, transitions);

        assertEquals(transitions, TransitionsConfig.imports(media));

        assertTrue(media.getFile().delete());
    }
}
