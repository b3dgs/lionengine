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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test the component collision model class.
 */
public class ComponentCollisionTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
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
        Graphics.setFactoryGraphic(null);
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    private final Services services = new Services();
    private final Handler handler = new Handler(services);
    private final Setup setup = new Setup(config);
    private final AtomicReference<Collidable> collide = new AtomicReference<>();

    private final Featurable nonCollidable = new FeaturableModel();

    private ObjectSelf featurable1;
    private Transformable transformable1;
    private Collidable collidable1;

    private Featurable featurable2;
    private Transformable transformable2;
    private Collidable collidable2;

    private Featurable featurable3;
    private Transformable transformable3;
    private Collidable collidable3;

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new Camera());

        featurable1 = new ObjectSelf();
        transformable1 = featurable1.addFeatureAndGet(new TransformableModel(setup));
        collidable1 = featurable1.addFeatureAndGet(new CollidableModel(services, setup));
        collidable1.setGroup(1);
        collidable1.addAccept(0);

        final Collision collision1 = new Collision("test1", 0, 0, 3, 3, false);
        collidable1.addCollision(collision1);

        featurable2 = CollidableModelTest.createFeaturable(config, services);
        transformable2 = featurable2.getFeature(Transformable.class);
        collidable2 = featurable2.getFeature(Collidable.class);
        collidable2.addAccept(1);
        collidable2.setGroup(0);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, true);
        collidable2.addCollision(collision2);

        featurable3 = new ObjectSelf();
        transformable3 = featurable3.addFeatureAndGet(new TransformableModel(setup));
        collidable3 = featurable3.addFeatureAndGet(new CollidableModel(services, setup));
        collidable3.setGroup(0);

        final ComponentCollision component = new ComponentCollision();
        handler.addComponent(component);
        handler.add(featurable1);
        handler.add(featurable2);
        handler.add(featurable3);
        handler.add(nonCollidable);

        final CollidableListener listener = new CollidableListener()
        {
            @Override
            public void notifyCollided(Collidable collidable)
            {
                collide.set(collidable);
            }
        };
        collidable2.addListener(listener);
    }

    /**
     * Test collidable in normal case.
     */
    @Test
    public void testCollidable()
    {
        transformable1.teleport(1.0, 2.0);
        transformable2.teleport(1.0, 1.0);

        handler.update(1.0);

        Assert.assertEquals(collidable1, collide.get());
        Assert.assertEquals(collidable2, featurable1.called.get());

        collide.set(null);
        featurable1.called.set(null);
        transformable1.teleport(ComponentCollision.REDUCE_FACTOR, ComponentCollision.REDUCE_FACTOR);

        handler.update(1.0);

        Assert.assertNull(collide.get());
        Assert.assertNull(featurable1.called.get());
    }

    /**
     * Test collidable in extremity case, where their position correspond to an adjacent map case, but size collide
     * neighbor map.
     */
    @Test
    public void testCollidableExtremity()
    {
        testExtremity(-1, 1);
        testExtremity(-1, 0);
        testExtremity(-1, -1);

        testExtremity(0, 1);
        testExtremity(0, -1);

        testExtremity(1, 1);
        testExtremity(1, 0);
        testExtremity(1, -1);
    }

    /**
     * Test the extremity map collision.
     * 
     * @param ox The horizontal offset.
     * @param oy The vertical offset.
     */
    private void testExtremity(int ox, int oy)
    {
        collide.set(null);
        featurable1.called.set(null);

        transformable1.teleport(ComponentCollision.REDUCE_FACTOR + ox, ComponentCollision.REDUCE_FACTOR + oy);
        transformable2.teleport(ComponentCollision.REDUCE_FACTOR, ComponentCollision.REDUCE_FACTOR);

        handler.update(1.0);

        Assert.assertEquals(collidable1, collide.get());
        Assert.assertEquals(collidable2, featurable1.called.get());
    }

    /**
     * Test collidables changing map position.
     */
    @Test
    public void testCollidablesRemovePoints()
    {
        transformable1.teleport(0.0, 0.0);
        transformable2.teleport(0.0, 0.0);
        transformable3.teleport(0.0, 0.0);

        handler.update(1.0);

        Assert.assertEquals(collidable1, collide.get());
        Assert.assertEquals(collidable2, featurable1.called.get());

        collide.set(null);
        featurable1.called.set(null);

        transformable1.teleport(ComponentCollision.REDUCE_FACTOR, ComponentCollision.REDUCE_FACTOR);

        handler.update(1.0);

        Assert.assertNull(collide.get());
        Assert.assertNull(featurable1.called.get());

        transformable2.teleport(ComponentCollision.REDUCE_FACTOR, ComponentCollision.REDUCE_FACTOR);

        handler.update(1.0);

        Assert.assertEquals(collidable1, collide.get());
        Assert.assertEquals(collidable2, featurable1.called.get());
    }

    /**
     * Test collidable with removed object.
     */
    @Test
    public void testRemoved()
    {
        transformable1.teleport(1.0, 2.0);
        transformable2.teleport(2.0, 3.0);
        handler.update(1.0);

        Assert.assertEquals(collidable1, collide.get());
        Assert.assertEquals(collidable2, featurable1.called.get());

        collide.set(null);
        featurable1.called.set(null);
        handler.remove(featurable1);
        handler.update(1.0);

        Assert.assertNull(collide.get());
        Assert.assertNull(featurable1.called.get());

        handler.remove(featurable2);
        handler.remove(nonCollidable);
        handler.update(1.0);

        Assert.assertNull(collide.get());
        Assert.assertNull(featurable1.called.get());
    }
}
