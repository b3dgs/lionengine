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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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

/**
 * Test {@link MapTileCollisionModel}.
 */
public final class MapTileCollisionModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /** Formula vertical test. */
    private final CollisionFormula formulaV = new CollisionFormula("y",
                                                                   new CollisionRange(Axis.Y, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    /** Formula horizontal test. */
    private final CollisionFormula formulaH = new CollisionFormula("x",
                                                                   new CollisionRange(Axis.X, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup(UtilMap.GROUND, Arrays.asList(formulaV, formulaH));
    /** Category vertical test. */
    private final CollisionCategory categoryY = new CollisionCategory("y", Axis.Y, 0, 0, Arrays.asList(group));
    /** Category horizontal test. */
    private final CollisionCategory categoryX = new CollisionCategory("x", Axis.X, 0, 0, Arrays.asList(group));
    /** The services reference. */
    private final Services services = new Services();
    /** Map. */
    private final MapTile map = services.create(MapTileGame.class);
    /** Object test. */
    private Transformable transformable;
    /** Map collision. */
    private MapTileCollision mapCollision;
    /** Formulas config. */
    private Media formulasConfig;
    /** Groups config. */
    private Media groupsConfig;

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
        mapCollision = map.addFeatureAndGet(new MapTileCollisionModel(services));
        mapCollision.prepare(map);

        formulasConfig = UtilConfig.createFormulaConfig(formulaV, formulaH);
        groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        transformable = createObject();
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        assertTrue(formulasConfig.getFile().delete());
        assertTrue(groupsConfig.getFile().delete());
    }

    /**
     * Test the map tile collision from top.
     */
    @Test
    public void testFromTop()
    {
        transformable.teleport(0.0, 6.0);
        transformable.moveLocation(1.0, 0.0, -5.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryY);

        assertNull(result.getX());
        assertEquals(Double.valueOf(3.0), result.getY());
    }

    /**
     * Test the map tile collision from bottom.
     */
    @Test
    public void testFromBottom()
    {
        transformable.teleport(0.0, -2.0);
        transformable.moveLocation(1.0, 0.0, 5.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryY);

        assertNull(result.getX());
        assertEquals(Double.valueOf(3.0), result.getY());
    }

    /**
     * Test the map tile collision from left.
     */
    @Test
    public void testFromLeft()
    {
        transformable.teleport(-2.0, 0.0);
        transformable.moveLocation(1.0, 5.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertNull(result.getY());
        assertEquals(Double.valueOf(1.0), result.getX());
    }

    /**
     * Test the map tile collision from left.
     */
    @Test
    public void testFromRight()
    {
        transformable.teleport(6.0, 0.0);
        transformable.moveLocation(1.0, -5.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertNull(result.getY());
        assertEquals(Double.valueOf(2.0), result.getX());
    }

    /**
     * Test the map tile collision from left.
     */
    @Test
    public void testNoCollision()
    {
        transformable.teleport(6.0, 6.0);
        transformable.moveLocation(1.0, 1.0, 1.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertNull(result);
    }

    /**
     * Test the map tile collision getters.
     */
    @Test
    public void testGetter()
    {
        assertEquals(formulaV, mapCollision.getCollisionFormula("y"));
        assertEquals(formulaH, mapCollision.getCollisionFormula("x"));
        assertEquals(group, mapCollision.getCollisionGroup(UtilMap.GROUND));

        assertTrue(mapCollision.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));
        assertTrue(mapCollision.getCollisionGroups().containsAll(Arrays.asList(group)));

        assertEquals(formulasConfig, mapCollision.getFormulasConfig());
        assertEquals(groupsConfig, mapCollision.getCollisionsConfig());
    }

    /**
     * Test the map tile collision saving.
     */
    @Test
    public void testSaveCollision()
    {
        mapCollision.saveCollisions();
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        assertTrue(mapCollision.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));
        assertTrue(mapCollision.getCollisionGroups().containsAll(Arrays.asList(group)));
    }

    /**
     * Test the map tile collision loading without configuration.
     */
    @Test
    public void testLoadCollisionWithout()
    {
        final MapTileCollision mapTileCollision = new MapTileCollisionModel(services);
        mapTileCollision.prepare(map);
        mapTileCollision.loadCollisions(Medias.create("void"), Medias.create("void2"));

        assertTrue(mapTileCollision.getCollisionFormulas().isEmpty());
        assertTrue(mapTileCollision.getCollisionGroups().isEmpty());
    }

    /**
     * Test the map tile collision unknown formula.
     */
    @Test
    public void testUnknownFormula()
    {
        assertThrows(() -> mapCollision.getCollisionFormula("void"), MapTileCollisionLoader.ERROR_FORMULA + "void");
    }

    /**
     * Test the map tile collision unknown group.
     */
    @Test
    public void testUnknownGroup()
    {
        assertThrows(() -> mapCollision.getCollisionGroup("void"), MapTileCollisionLoader.ERROR_FORMULA + "void");
    }

    /**
     * Create object test.
     * 
     * @return The object test.
     */
    private Transformable createObject()
    {
        final Setup setup = new Setup(config);
        CollisionCategoryConfig.exports(setup.getRoot(), categoryY);
        CollisionCategoryConfig.exports(setup.getRoot(), categoryX);
        final FeaturableModel object = new FeaturableModel();

        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(setup));
        transformable.setSize(1, 1);

        final TileCollidable collidable = object.addFeatureAndGet(new TileCollidableModel(services, setup));
        collidable.setEnabled(true);

        return transformable;
    }
}
