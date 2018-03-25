/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.TileArea;

/**
 * Test the map tile path model.
 */
public class MapTilePathModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(MapTilePathModelTest.class);
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /** The services reference. */
    private final Services services = new Services();
    /** Map. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Map collision. */
    private MapTilePath mapPath;

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new Camera());
        map.addFeature(new MapTileGroupModel());
        map.create(1, 1, 3, 3);
        UtilMap.setGroups(map);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        map.addFeature(new MapTileGroupModel());
        mapPath = map.addFeatureAndGet(new MapTilePathModel(services));
        mapPath.prepare(map);

        mapPath.loadPathfinding(Medias.create("pathfinding.xml"));
    }

    /**
     * Test the get free tile around.
     */
    @Test
    public void testGetFreeTileAround()
    {
        final Pathfindable pathfindable = createObject();
        final TileArea tile = new TileArea(1, 2, 1, 1);
        CoordTile coord = mapPath.getFreeTileAround(pathfindable, tile);

        Assert.assertEquals(1, coord.getX());
        Assert.assertEquals(2, coord.getY());

        coord = mapPath.getFreeTileAround(pathfindable, tile, 4);

        Assert.assertEquals(1, coord.getX());
        Assert.assertEquals(2, coord.getY());
    }

    /**
     * Test the closest tile available.
     */
    @Test
    public void testGetClosestTileAvailable()
    {
        final Pathfindable pathfindable = createObject();
        final TileArea tile = new TileArea(1, 2, 1, 1);
        CoordTile coord = mapPath.getClosestAvailableTile(pathfindable, tile, 6);

        Assert.assertEquals(0, coord.getX());
        Assert.assertEquals(1, coord.getY());

        coord = mapPath.getClosestAvailableTile(pathfindable, 1, 2, 2, 3, 6);

        Assert.assertEquals(1, coord.getX());
        Assert.assertEquals(2, coord.getY());
    }

    /**
     * Create object test.
     * 
     * @return The object test.
     */
    private Pathfindable createObject()
    {
        final Setup setup = new Setup(config);
        final FeaturableModel object = new FeaturableModel();

        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(setup));
        transformable.setSize(1, 1);

        final Pathfindable pathfindable = object.addFeatureAndGet(new PathfindableModel(services, setup));

        return pathfindable;
    }
}
