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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link TileCollidableModel}.
 */
public final class TileCollidableModelTest
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

    /** Formula vertical test. */
    private final CollisionFormula formulaV = new CollisionFormula("y",
                                                                   new CollisionRange(Axis.Y, 0, 1, 0, 0),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    /** Formula horizontal test. */
    private final CollisionFormula formulaH = new CollisionFormula("x",
                                                                   new CollisionRange(Axis.X, 0, 0, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup(UtilMap.GROUND, Arrays.asList(formulaV, formulaH));
    /** Category vertical test. */
    private final CollisionCategory categoryY = new CollisionCategory("y", Axis.Y, 0, 0, true, Arrays.asList(group));
    /** Category horizontal test. */
    private final CollisionCategory categoryX = new CollisionCategory("x", Axis.X, 0, 0, true, Arrays.asList(group));
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
     * Create the tile collision listener.
     * 
     * @param collided The collided flag.
     * @return The listener.
     */
    private TileCollidableListener createListener(final AtomicReference<Tile> collided)
    {
        return (result, category) -> collided.set(result.getTile());
    }

    /**
     * Test the collidable from top.
     */
    @Test
    public void testFromTop()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.teleport(0.0, 3.0);
        transformable.moveLocation(1.0, 0.0, -2.0);
        collidable.update(1.0);

        assertEquals(map.getTile(0, 2), collided.get());
    }

    /**
     * Test the collidable from bottom.
     */
    @Test
    public void testFromBottom()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.teleport(0.0, -1.0);
        transformable.moveLocation(1.0, 0.0, 3.0);
        collidable.update(1.0);

        assertEquals(map.getTile(0, 0), collided.get());
    }

    /**
     * Test the collidable from left.
     */
    @Test
    public void testFromLeft()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.teleport(-1.0, 0.0);
        transformable.moveLocation(1.0, 2.0, 0.0);
        collidable.update(1.0);

        assertEquals(map.getTile(0, 0), collided.get());
    }

    /**
     * Test the collidable from right.
     */
    @Test
    public void testFromRight()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.teleport(3.0, 0.0);
        transformable.moveLocation(1.0, -2.0, 0.0);
        collidable.update(1.0);

        assertEquals(map.getTile(2, 0), collided.get());
    }

    /**
     * Test the collidable disabled.
     */
    @Test
    public void testDisabled()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);
        collidable.setEnabled(false);

        transformable.teleport(0, 2);
        transformable.moveLocation(1.0, 0.0, -1.0);
        collidable.update(1.0);

        assertNull(collided.get());
    }

    /**
     * Test the collidable with remove listener.
     */
    @Test
    public void testRemoveListener()
    {
        final Transformable transformable = createObject(new FeaturableModel());
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.checkListener(transformable);
        collidable.checkListener(listener);

        transformable.teleport(0.0, 2.0);
        transformable.moveLocation(1.0, 0.0, -1.0);
        collidable.update(1.0);

        assertNotNull(collided.get());
        collided.set(null);

        collidable.removeListener(listener);
        collidable.update(1.0);

        assertNull(collided.get());
    }

    /**
     * Test the collidable self listener.
     */
    @Test
    public void testSelfListener()
    {
        final ObjectSelf self = new ObjectSelf();
        final Transformable transformable = createObject(self);
        final AtomicReference<Tile> collided = new AtomicReference<>();
        final TileCollidableListener listener = createListener(collided);
        collidable.addListener(listener);

        transformable.teleport(0.0, 2.0);
        transformable.moveLocation(1.0, 0.0, -1.0);
        collidable.removeListener(listener);
        collidable.update(1.0);

        assertNull(collided.get());
        assertTrue(self.called.get());
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

        assertArrayEquals(Arrays.asList(categoryY, categoryX).toArray(), collidable.getCategories().toArray());

        return transformable;
    }
}
