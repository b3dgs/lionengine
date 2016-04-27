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
package com.b3dgs.lionengine.game.map.circuit;

import static com.b3dgs.lionengine.game.map.UtilMap.TILE_GROUND;
import static com.b3dgs.lionengine.game.map.UtilMap.TILE_ROAD;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.UtilMap;
import com.b3dgs.lionengine.game.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.map.transition.UtilMapTransition;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the circuits configuration class.
 */
public class CircuitConfigTest
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
        config = UtilMapTransition.createTransitions();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
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
        final MapTile map = UtilMap.createMap(tileNumber);
        map.getFeature(MapTileTransition.class).loadTransitions(config);

        return map;
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(CircuitsConfig.class);
    }

    /**
     * Test the circuits configuration.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testExtraction() throws IOException
    {
        final MapTile map = createMap(7);
        UtilMap.fill(map, TILE_GROUND);
        UtilMap.fill(map, TILE_ROAD, TILE_ROAD, 3);

        final MapTile map3 = new MapTileGame();
        map3.addFeature(new MapTileGroupModel());
        map3.create(3, 3);

        final CircuitsExtractor extractor = new CircuitsExtractorImpl();
        final Map<Circuit, Collection<TileRef>> circuits = extractor.getCircuits(map, map3);

        final Media media = Medias.create("circuit_tmp.xml");
        try
        {
            CircuitsConfig.exports(media, circuits);

            final Map<Circuit, Collection<TileRef>> imported = CircuitsConfig.imports(media);

            Assert.assertEquals(circuits, imported);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }
}
