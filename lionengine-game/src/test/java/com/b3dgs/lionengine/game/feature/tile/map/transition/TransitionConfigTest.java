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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link TransitionsConfig}.
 */
public final class TransitionConfigTest // TODO rename to TransitionsConfig
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
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(TransitionsConfig.class);
    }

    /**
     * Test exports and imports.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testExportsImports() throws IOException
    {
        final MapTile map = UtilMap.createMap(7);
        UtilMap.fill(map, UtilMap.TILE_WATER);
        UtilMap.fill(map, UtilMap.TILE_WATER, UtilMap.TILE_TRANSITION, 3);

        final MapTile map2 = UtilMap.createMap(7);
        UtilMap.fill(map, UtilMap.TILE_WATER);
        UtilMap.fill(map, UtilMap.TILE_GROUND, UtilMap.TILE_TRANSITION, 3);

        final MapTile map3 = new MapTileGame();
        map3.addFeature(new MapTileGroupModel());
        map3.create(1, 1, 3, 3);

        final TransitionsExtractor extractor = new TransitionsExtractorImpl();
        final Map<Transition, Collection<TileRef>> transitions = extractor.getTransitions(Arrays.asList(map,
                                                                                                        map2,
                                                                                                        map3));

        final Media media = Medias.create("transition.xml");
        TransitionsConfig.exports(media, transitions);

        Assert.assertEquals(transitions, TransitionsConfig.imports(media));

        Assert.assertTrue(media.getFile().delete());
    }
}
