/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator.TileArea;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Test {@link MapTilePathModel}.
 */
final class MapTilePathModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(MapTilePathModelTest.class.getSimpleName(), Version.DEFAULT));

        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(MapTilePathModelTest.class);
        Medias.setLoadFromJar(MapTilePathModelTest.class);
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);

        Engine.terminate();
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);
    private final MapTileGame map = services.add(new MapTileGame());
    private MapTilePath mapPath;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        services.add(new Camera());
        map.addFeature(new MapTileGroupModel());
        map.create(1, 1, 7, 7);
        UtilMap.setGroups(map);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        mapPath = map.addFeatureAndGet(new MapTilePathModel());
        mapPath.prepare(map);

        mapPath.loadPathfinding(Medias.create("pathfinding.xml"));
    }

    /**
     * Test the get free tile around.
     */
    @Test
    void testGetFreeTileAround()
    {
        final Pathfindable pathfindable = createObject();
        final TileArea tile = new TileArea(3, 3, 1, 1);
        CoordTile coord = mapPath.getFreeTileAround(pathfindable, tile, map.getInTileRadius());

        assertEquals(2, coord.getX());
        assertEquals(4, coord.getY());

        mapPath.addObjectId(2, 4, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(2, coord.getX());
        assertEquals(3, coord.getY());

        mapPath.addObjectId(2, 3, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(2, coord.getX());
        assertEquals(2, coord.getY());

        mapPath.addObjectId(2, 2, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(3, coord.getX());
        assertEquals(2, coord.getY());

        mapPath.addObjectId(3, 2, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(4, coord.getX());
        assertEquals(2, coord.getY());

        mapPath.addObjectId(4, 2, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(4, coord.getX());
        assertEquals(3, coord.getY());

        mapPath.addObjectId(4, 3, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(4, coord.getX());
        assertEquals(4, coord.getY());

        mapPath.addObjectId(4, 4, Integer.valueOf(10));
        coord = mapPath.getFreeTileAround(pathfindable, tile);

        assertEquals(1, coord.getX());
        assertEquals(5, coord.getY());
    }

    /**
     * Test the get free tile around with none found.
     */
    @Test
    void testGetFreeTileAroundNone()
    {
        final Pathfindable pathfindable = createObject();
        final TileArea tile = new TileArea(0, 0, 10, 10);
        for (int x = 0; x < 10; x++)
        {
            for (int y = 0; y < 10; y++)
            {
                mapPath.addObjectId(x, y, Integer.valueOf(10));
            }
        }

        assertNull(mapPath.getFreeTileAround(pathfindable, tile, map.getInTileRadius()));
    }

    /**
     * Test the closest tile available.
     */
    @Test
    void testGetClosestTileAvailable()
    {
        final Pathfindable pathfindable = createObject();
        final TileArea tile = new TileArea(1, 2, 1, 1);
        CoordTile coord = mapPath.getClosestAvailableTile(pathfindable, tile, 6);

        assertEquals(1, coord.getX());
        assertEquals(1, coord.getY());

        coord = mapPath.getClosestAvailableTile(pathfindable, 1, 2, 2, 3, 6);

        assertEquals(2, coord.getX());
        assertEquals(3, coord.getY());
    }

    /**
     * Test the is area available.
     */
    @Test
    void testIsAreaAvailable()
    {
        mapPath.addObjectId(-1, 0, Integer.valueOf(10));
        mapPath.addObjectId(10, 0, Integer.valueOf(10));
        mapPath.addObjectId(0, -1, Integer.valueOf(10));
        mapPath.addObjectId(0, 10, Integer.valueOf(10));
        mapPath.addObjectId(-1, -1, Integer.valueOf(10));
        mapPath.addObjectId(10, 10, Integer.valueOf(10));

        assertTrue(mapPath.getObjectsId(-1, 0).isEmpty());
        assertTrue(mapPath.getObjectsId(10, 0).isEmpty());
        assertTrue(mapPath.getObjectsId(0, -1).isEmpty());
        assertTrue(mapPath.getObjectsId(0, 10).isEmpty());
        assertTrue(mapPath.getObjectsId(-1, -1).isEmpty());
        assertTrue(mapPath.getObjectsId(10, 10).isEmpty());

        for (int x = 1; x < 4; x++)
        {
            for (int y = 1; y < 4; y++)
            {
                mapPath.addObjectId(x, y, Integer.valueOf(10));
            }
        }
        final Pathfindable mover = createObject();

        assertTrue(mapPath.isAreaAvailable(Geom.createArea(0, 0, 1, 1), mover));
        assertFalse(mapPath.isAreaAvailable(Geom.createArea(2, 2, 2, 2), mover));
        assertTrue(mapPath.isAreaAvailable(Geom.createArea(4, 4, 2, 2), mover));

        for (int x = 1; x < 4; x++)
        {
            for (int y = 1; y < 4; y++)
            {
                mapPath.removeObjectId(x, y, Integer.valueOf(10));
            }
        }

        assertTrue(mapPath.isAreaAvailable(Geom.createArea(2, 2, 2, 2), mover));

        mapPath.removeObjectId(-1, 0, Integer.valueOf(10));
        mapPath.removeObjectId(10, 0, Integer.valueOf(10));
        mapPath.removeObjectId(0, -1, Integer.valueOf(10));
        mapPath.removeObjectId(0, 10, Integer.valueOf(10));
        mapPath.removeObjectId(-1, -1, Integer.valueOf(10));
        mapPath.removeObjectId(10, 10, Integer.valueOf(10));
    }

    /**
     * Create object test.
     * 
     * @return The object test.
     */
    private Pathfindable createObject()
    {
        final FeaturableModel object = new FeaturableModel(services, setup);
        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(services, setup));
        transformable.setSize(1, 1);

        return object.addFeatureAndGet(new PathfindableModel(services, setup));
    }
}
