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
package com.b3dgs.lionengine.game.collision.object;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.feature.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;

/**
 * Test the collidable model class.
 */
public class CollidableModelTest
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

    /**
     * Create an object test.
     * 
     * @param config The configuration reference.
     * @param services The services reference.
     * @return The object test.
     */
    public static ObjectGame createObject(Media config, Services services)
    {
        final Setup setup = new Setup(config);
        final ObjectGame object = new ObjectGame(setup);

        final Transformable transformable = object.addFeatureAndGet(new TransformableModel(setup));
        transformable.setLocation(1.0, 2.0);
        transformable.setSize(2, 2);

        object.addFeature(new CollidableModel(setup));
        object.prepareFeatures(object, services);

        return object;
    }

    /**
     * Test collidable class.
     */
    @Test
    public void testCollidable()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object1 = createObject(config, services);
        final ObjectGame object2 = createObject(config, services);

        final Collidable collidable1 = object1.getFeature(Collidable.class);
        final Collidable collidable2 = object2.getFeature(Collidable.class);

        Assert.assertNull(collidable1.collide(collidable1));
        Assert.assertNull(collidable2.collide(collidable1));

        final Collision collision1 = new Collision("test1", 0, 0, 3, 3, true);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, false);
        collidable2.addCollision(collision2);

        collidable1.update(1.0);
        collidable2.update(1.0);

        Assert.assertEquals(collision1, collidable1.collide(collidable1));
        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        Assert.assertTrue(collidable1.getCollisionBounds().iterator().hasNext());
        Assert.assertEquals(collision1, collidable1.getCollisions().iterator().next());

        object2.getFeature(Transformable.class).moveLocation(1.0, 5.0, 5.0);
        collidable2.update(1.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        object2.getFeature(Transformable.class).moveLocation(1.0, 5.0, 5.0);
        collidable2.update(1.0);

        Assert.assertNull(collidable1.collide(collidable2));
    }

    /**
     * Test collidable class with different sizes.
     */
    @Test
    public void testCollidableDifferentSizes()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object1 = createObject(config, services);
        final ObjectGame object2 = createObject(config, services);

        final Collidable collidable1 = object1.getFeature(Collidable.class);
        final Collidable collidable2 = object2.getFeature(Collidable.class);

        final AtomicBoolean auto = new AtomicBoolean();
        collidable1.checkListener(new CollidableListener()
        {
            @Override
            public void notifyCollided(Collidable collidable)
            {
                auto.set(true);
            }
        });

        collidable1.notifyCollided(collidable2);
        Assert.assertTrue(auto.get());

        final Collision collision1 = new Collision("test1", 1, 1, 1, 1, true);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, false);
        collidable2.addCollision(collision2);

        object1.getFeature(Transformable.class).teleport(1.0, 1.0);
        collidable1.update(1.0);
        collidable2.update(1.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        object1.getFeature(Transformable.class).teleport(2.0, 2.0);
        collidable1.update(1.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));
    }

    /**
     * Test collidable class with mirror.
     */
    @Test
    public void testCollidableMirror()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object1 = createObject(config, services);
        final Mirrorable mirror1 = object1.addFeatureAndGet(new MirrorableModel());
        mirror1.mirror(Mirror.HORIZONTAL);
        mirror1.update(1.0);

        final ObjectGame object2 = createObject(config, services);
        final Mirrorable mirror2 = object2.addFeatureAndGet(new MirrorableModel());
        mirror2.mirror(Mirror.VERTICAL);
        mirror2.update(1.0);

        final Collidable collidable1 = object1.getFeature(Collidable.class);
        final Collidable collidable2 = object2.getFeature(Collidable.class);

        final Collision collision1 = new Collision("test1", 1, 1, 1, 1, true);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, true);
        collidable2.addCollision(collision2);

        object1.getFeature(Transformable.class).teleport(1.0, 1.0);
        collidable1.update(1.0);
        collidable2.update(1.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        object1.getFeature(Transformable.class).teleport(2.0, 2.0);
        collidable1.update(1.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));
    }

    /**
     * Test collidable disabled.
     */
    @Test
    public void testCollidableDisabled()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object = createObject(config, services);
        final Collidable collidable = object.getFeature(Collidable.class);

        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable.addCollision(collision);
        collidable.update(1.0);

        Assert.assertNotNull(collidable.collide(collidable));

        collidable.setEnabled(false);
        collidable.update(1.0);

        Assert.assertNull(collidable.collide(collidable));
    }

    /**
     * Test collidable ignored collision.
     */
    @Test
    public void testCollidableIgnored()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object = createObject(config, services);
        final Collidable collidable = object.getFeature(Collidable.class);

        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable.addCollision(collision);
        collidable.update(1.0);

        Assert.assertNotNull(collidable.collide(collidable));

        collidable.addIgnore(collidable);

        Assert.assertNull(collidable.collide(collidable));
    }

    /**
     * Test collidable rendering.
     */
    @Test
    public void testRender()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object = createObject(config, services);
        object.getFeature(Transformable.class).moveLocation(1.0, 1.0, 1.0);

        final Graphic g = Graphics.createGraphic();
        final Collidable collidable = object.getFeature(Collidable.class);
        collidable.setOrigin(Origin.MIDDLE);

        collidable.render(g);

        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable.addCollision(collision);
        collidable.setCollisionVisibility(true);
        collidable.update(1.0);

        collidable.render(g);
    }
}
