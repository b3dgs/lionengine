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

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test the tile collidable model class.
 */
public class TileCollidableModelTest
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
                                                                   new CollisionFunctionLinear(1.0, 0.0),
                                                                   new CollisionConstraint());
    /** Formula horizontal test. */
    private final CollisionFormula formulaH = new CollisionFormula("x",
                                                                   new CollisionRange(Axis.X, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(1.0, 0.0),
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
    /** Tile collidable. */
    private TileCollidable collidable;
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
        map.create(1, 1, 3, 3);
        UtilMap.setGroups(map);
        UtilMap.fill(map, UtilMap.TILE_GROUND);
        mapCollision = map.addFeatureAndGet(new MapTileCollisionModel(services));
        mapCollision.prepare(map);

        formulasConfig = UtilConfig.createFormulaConfig(formulaV, formulaH);
        groupsConfig = UtilConfig.createGroupsConfig(group);
        mapCollision.loadCollisions(formulasConfig, groupsConfig);
    }

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        Assert.assertTrue(formulasConfig.getFile().delete());
        Assert.assertTrue(groupsConfig.getFile().delete());
    }

    /**
     * Create the tile collision listener.
     * 
     * @param collided The collided flag.
     * @return The listener.
     */
    private TileCollidableListener createListener(final AtomicReference<Tile> collided)
    {
        return new TileCollidableListener()
        {
            @Override
            public void notifyTileCollided(Tile tile, Axis axis)
            {
                collided.set(tile);
            }
        };
    }

    /**
     * Test the collidable from top.
     */
    @Test
    public void testFromTop()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.setLocation(0, 2);
        transformable.moveLocation(1.0, 0.0, -2.0);
        collidable.update(1.0);

        Assert.assertEquals(map.getTile(0, 1), collided.get());
    }

    /**
     * Test the collidable from bottom.
     */
    @Test
    public void testFromBottom()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.setLocation(0, -2);
        transformable.moveLocation(1.0, 0.0, 2.0);
        collidable.update(1.0);

        Assert.assertEquals(map.getTile(0, 0), collided.get());
    }

    /**
     * Test the collidable from left.
     */
    @Test
    public void testFromLeft()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.setLocation(-2, 0);
        transformable.moveLocation(1.0, 2.0, 0.0);
        collidable.update(1.0);

        Assert.assertEquals(map.getTile(0, 0), collided.get());
    }

    /**
     * Test the collidable from right.
     */
    @Test
    public void testFromRight()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.setLocation(2, 0);
        transformable.moveLocation(1.0, -2.0, 0.0);
        collidable.update(1.0);

        Assert.assertEquals(map.getTile(2, 1), collided.get());
    }

    /**
     * Test the collidable disabled.
     */
    @Test
    public void testDisabled()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);
        collidable.setEnabled(false);

        transformable.setLocation(0, 2);
        transformable.moveLocation(1.0, 0.0, -5.0);
        collidable.update(1.0);

        Assert.assertNull(collided.get());
    }

    /**
     * Test the collidable with remove listener.
     */
    @Test
    public void testRemoveListener()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.setLocation(0, 2);
        transformable.moveLocation(1.0, 0.0, -5.0);
        collidable.removeListener(listener);
        collidable.update(1.0);

        Assert.assertNull(collided.get());
    }

    /**
     * Test the collidable self listener.
     */
    @Test
    public void testSelfListener()
    {
        final ObjectSelf self = new ObjectSelf();
        final Transformable transformable = createObject(self);
        final AtomicReference<Tile> collided = new AtomicReference<Tile>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.setLocation(0, 2);
        transformable.moveLocation(1.0, 0.0, -5.0);
        collidable.removeListener(listener);
        collidable.update(1.0);

        Assert.assertNull(collided.get());
        Assert.assertTrue(self.called.get());
    }

    /**
     * Create object test.
     * 
     * @param featurable Featurable instance.
     * @return The object test.
     */
    private Transformable createObject(Featurable featurable)
    {
        final Setup setup = new Setup(config);
        CollisionCategoryConfig.exports(setup.getRoot(), categoryY);
        CollisionCategoryConfig.exports(setup.getRoot(), categoryX);

        final Transformable transformable = featurable.addFeatureAndGet(new TransformableModel(setup));
        transformable.setSize(2, 2);

        collidable = featurable.addFeatureAndGet(new TileCollidableModel(services, setup));
        collidable.setEnabled(true);

        Assert.assertEquals(Arrays.asList(categoryY, categoryX), collidable.getCategories());

        return transformable;
    }
}
