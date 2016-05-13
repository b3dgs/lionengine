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
package com.b3dgs.lionengine.game.collision.tile;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.UtilMap;
import com.b3dgs.lionengine.game.map.feature.group.MapTileGroupModel;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;

/**
 * Test the map tile collision model class.
 */
public class MapTileCollisionModelTest
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
        config = UtilSetup.createConfig();
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
    @Before
    public void prepare()
    {
        services.add(new Camera());
        map.addFeature(new MapTileGroupModel());
        map.prepareFeatures(map, services);
        map.create(1, 1, 3, 3);
        UtilMap.setGroups(map);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        mapCollision = map.createFeature(MapTileCollisionModel.class);

        formulasConfig = UtilConfig.createFormulaConfig(formulaV, formulaH);
        groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        transformable = createObject();
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

        Assert.assertNull(result.getX());
        Assert.assertEquals(Double.valueOf(3.0), result.getY());
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

        Assert.assertNull(result.getX());
        Assert.assertEquals(Double.valueOf(-1.0), result.getY());
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

        Assert.assertNull(result.getY());
        Assert.assertEquals(Double.valueOf(-1.0), result.getX());
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

        Assert.assertNull(result.getY());
        Assert.assertEquals(Double.valueOf(3.0), result.getX());
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

        Assert.assertNull(result);
    }

    /**
     * Test the map tile collision getters.
     */
    @Test
    public void testGetter()
    {
        Assert.assertEquals(formulaV, mapCollision.getCollisionFormula("y"));
        Assert.assertEquals(formulaH, mapCollision.getCollisionFormula("x"));
        Assert.assertEquals(group, mapCollision.getCollisionGroup(UtilMap.GROUND));

        Assert.assertTrue(mapCollision.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));
        Assert.assertTrue(mapCollision.getCollisionGroups().containsAll(Arrays.asList(group)));

        Assert.assertEquals(formulasConfig, mapCollision.getFormulasConfig());
        Assert.assertEquals(groupsConfig, mapCollision.getCollisionsConfig());
    }

    /**
     * Test the map tile collision saving.
     */
    @Test
    public void testSaveCollision()
    {
        mapCollision.saveCollisions();
        mapCollision.loadCollisions(formulasConfig, groupsConfig);

        Assert.assertTrue(mapCollision.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));
        Assert.assertTrue(mapCollision.getCollisionGroups().containsAll(Arrays.asList(group)));
    }

    /**
     * Test the map tile collision loading without configuration.
     */
    @Test
    public void testLoadCollisionWithout()
    {
        final MapTileCollision mapTileCollision = new MapTileCollisionModel(services);
        mapTileCollision.loadCollisions(Medias.create("void"), Medias.create("void2"));

        Assert.assertTrue(mapTileCollision.getCollisionFormulas().isEmpty());
        Assert.assertTrue(mapTileCollision.getCollisionGroups().isEmpty());
    }

    /**
     * Test the map tile collision unknown formula.
     */
    @Test(expected = LionEngineException.class)
    public void testUnknownFormula()
    {
        Assert.assertNull(mapCollision.getCollisionFormula("void"));
    }

    /**
     * Test the map tile collision unknown group.
     */
    @Test(expected = LionEngineException.class)
    public void testUnknownGroup()
    {
        Assert.assertNull(mapCollision.getCollisionGroup("void"));
    }

    /**
     * Create object test.
     * 
     * @return The object test.
     */
    private Transformable createObject()
    {
        final Setup setup = new Setup(config);
        CollisionCategoryConfig.exports(setup.getConfigurer().getRoot(), categoryY);
        CollisionCategoryConfig.exports(setup.getConfigurer().getRoot(), categoryX);
        final ObjectGame object = new ObjectGame(setup, services);

        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(setup));
        transformable.setSize(1, 1);

        final TileCollidable collidable = object.addFeatureAndGet(new TileCollidableModel(setup));
        collidable.setEnabled(true);

        object.prepareFeatures(object, services);

        return transformable;
    }
}
