/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
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
 * Test {@link MapTilePathModel}.
 */
public final class MapTilePathModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(MapTilePathModelTest.class);
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
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
    @BeforeEach
    public void prepare()
    {
        services.add(new Camera());
        map.addFeature(new MapTileGroupModel());
        map.create(1, 1, 3, 3);
        UtilMap.setGroups(map);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        mapPath = map.addFeatureAndGet(new MapTilePathModel(services));
        mapPath.prepare(map);

        mapPath.loadPathfinding(Medias.create("pathfinding.xml"));
    }

    /**
     * Test constructor with null services.
     */
    @Test
    public void testConstructorNullServices()
    {
        assertThrows(() -> new MapTilePathModel(null), "Unexpected null argument !");
        assertThrows(() -> new PathfindableModel(null, null), "Unexpected null argument !");
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

        assertEquals(1, coord.getX());
        assertEquals(2, coord.getY());

        coord = mapPath.getFreeTileAround(pathfindable, tile, 4);

        assertEquals(1, coord.getX());
        assertEquals(2, coord.getY());
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

        assertEquals(0, coord.getX());
        assertEquals(1, coord.getY());

        coord = mapPath.getClosestAvailableTile(pathfindable, 1, 2, 2, 3, 6);

        assertEquals(1, coord.getX());
        assertEquals(2, coord.getY());
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
