/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link MapTileCollisionModel}.
 */
final class MapTileCollisionModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(MapTileCollisionModelTest.class);
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

    private final CollisionFormula formulaV = new CollisionFormula("y",
                                                                   new CollisionRange(Axis.Y, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    private final CollisionFormula formulaH = new CollisionFormula("x",
                                                                   new CollisionRange(Axis.X, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    private final CollisionGroup group = new CollisionGroup(UtilMap.GROUND, Arrays.asList(formulaV, formulaH));
    private final CollisionCategory categoryY = new CollisionCategory("y", Axis.Y, 0, 0, true, Arrays.asList(group));
    private final CollisionCategory categoryX = new CollisionCategory("x", Axis.X, 0, 0, true, Arrays.asList(group));
    private final Services services = new Services();
    private final MapTileGame map = services.add(new MapTileGame());
    private Transformable transformable;
    private MapTileCollision mapCollision;
    private Media formulasConfig;
    private Media groupsConfig;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        map.addFeature(new MapTileGroupModel());
        map.create(1, 1, 3, 3);
        UtilMap.setGroups(map);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        mapCollision = map.addFeatureAndGet(new MapTileCollisionModel());
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
    void testFromTop()
    {
        transformable.teleport(1.0, 3.0);
        transformable.moveLocation(1.0, 0.0, -2.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryY);

        assertTrue(Double.isNaN(result.getX()));
        assertEquals(2.0, result.getY());
    }

    /**
     * Test the map tile collision from top wit fast speed.
     */
    @Test
    void testFromTopFast()
    {
        transformable.teleport(1.0, 3.0);
        transformable.moveLocation(1.0, 0.0, -20.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryY);

        assertTrue(Double.isNaN(result.getX()));
        assertEquals(2.0, result.getY());
    }

    /**
     * Test the map tile collision from bottom.
     */
    @Test
    void testFromBottom()
    {
        transformable.teleport(1.0, -1.0);
        transformable.moveLocation(1.0, 0.0, 3.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryY);

        assertTrue(Double.isNaN(result.getX()));
        assertEquals(0.0, result.getY());
    }

    /**
     * Test the map tile collision from bottom with fast speed.
     */
    @Test
    void testFromBottomFast()
    {
        transformable.teleport(1.0, -1.0);
        transformable.moveLocation(1.0, 0.0, 20.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryY);

        assertTrue(Double.isNaN(result.getX()));
        assertEquals(0.0, result.getY());
    }

    /**
     * Test the map tile collision from left.
     */
    @Test
    void testFromLeft()
    {
        transformable.teleport(-1.0, 0.0);
        transformable.moveLocation(1.0, 2.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertTrue(Double.isNaN(result.getY()));
        assertEquals(0.0, result.getX());
    }

    /**
     * Test the map tile collision from left with fast speed.
     */
    @Test
    void testFromLeftFast()
    {
        transformable.teleport(-1.0, 0.0);
        transformable.moveLocation(1.0, 20.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertTrue(Double.isNaN(result.getY()));
        assertEquals(0.0, result.getX());
    }

    /**
     * Test the map tile collision from right.
     */
    @Test
    void testFromRight()
    {
        transformable.teleport(2.0, 0.0);
        transformable.moveLocation(1.0, -1.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertTrue(Double.isNaN(result.getY()));
        assertEquals(2.0, result.getX());
    }

    /**
     * Test the map tile collision from right with fast speed.
     */
    @Test
    void testFromRightFast()
    {
        transformable.teleport(2.0, 0.0);
        transformable.moveLocation(1.0, -20.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertTrue(Double.isNaN(result.getY()));
        assertEquals(2.0, result.getX());
    }

    /**
     * Test the map tile no collision.
     */
    @Test
    void testNoCollision()
    {
        transformable.teleport(6.0, 6.0);
        transformable.moveLocation(1.0, 1.0, 1.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertNull(result);
    }

    /**
     * Test the map tile no collision formula defined.
     */
    @Test
    void testNoCollisionFormula()
    {
        mapCollision.loadCollisions(new CollisionFormulaConfig(Collections.emptyMap()),
                                    new CollisionGroupConfig(Collections.emptyMap()));
        transformable.teleport(6.0, 0.0);
        transformable.moveLocation(1.0, -5.0, 0.0);
        final CollisionResult result = mapCollision.computeCollision(transformable, categoryX);

        assertNull(result);
    }

    /**
     * Test the map tile collision getters.
     */
    @Test
    void testGetter()
    {
        assertEquals(formulaV, mapCollision.getCollisionFormula("y"));
        assertEquals(formulaH, mapCollision.getCollisionFormula("x"));
        assertEquals(Optional.of(group), mapCollision.getCollisionGroup(UtilMap.GROUND));

        assertTrue(mapCollision.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));
        assertTrue(mapCollision.getCollisionGroups().containsAll(Arrays.asList(group)));

        assertEquals(formulasConfig, mapCollision.getFormulasConfig());
        assertEquals(groupsConfig, mapCollision.getCollisionsConfig());
    }

    /**
     * Test the map tile collision saving.
     */
    @Test
    void testSaveCollision()
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
    void testLoadCollisionWithout()
    {
        final MapTileCollision mapTileCollision = new MapTileCollisionModel();
        mapTileCollision.prepare(map);
        mapTileCollision.loadCollisions(Medias.create("void"), Medias.create("void2"));

        assertTrue(mapTileCollision.getCollisionFormulas().isEmpty());
        assertTrue(mapTileCollision.getCollisionGroups().isEmpty());
    }

    /**
     * Test the map tile collision unknown formula.
     */
    @Test
    void testUnknownFormula()
    {
        assertThrows(() -> mapCollision.getCollisionFormula("void"), MapTileCollisionLoader.ERROR_FORMULA + "void");
    }

    /**
     * Test the map tile collision unknown group.
     */
    @Test
    void testUnknownGroup()
    {
        assertFalse(mapCollision.getCollisionGroup("void").isPresent());
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
        final FeaturableModel object = new FeaturableModel(services, setup);

        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(services, setup));
        transformable.setSize(1, 1);

        final TileCollidable collidable = object.addFeatureAndGet(new TileCollidableModel(services, setup));
        collidable.setEnabled(true);

        return transformable;
    }
}
