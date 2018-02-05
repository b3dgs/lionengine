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

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;

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

    private final Services services = new Services();
    @SuppressWarnings("unused") private final Camera camera = services.add(new Camera());

    private final Featurable featurable1 = createFeaturable(config, services);
    private final Transformable transformable1 = featurable1.getFeature(Transformable.class);
    private final Collidable collidable1 = featurable1.getFeature(Collidable.class);

    private final Featurable featurable2 = createFeaturable(config, services);
    private final Transformable transformable2 = featurable2.getFeature(Transformable.class);
    private final Collidable collidable2 = featurable2.getFeature(Collidable.class);

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        collidable1.setGroup(0);
        collidable2.setGroup(1);

        collidable1.addAccept(collidable2.getGroup().intValue());
        collidable2.addAccept(collidable1.getGroup().intValue());
    }

    /**
     * Create a featurable test.
     * 
     * @param config The configuration reference.
     * @param services The services reference.
     * @return The featurable test.
     */
    public static FeaturableModel createFeaturable(Media config, Services services)
    {
        final Setup setup = new Setup(config);
        final FeaturableModel featurable = new FeaturableModel();

        final Transformable transformable = featurable.addFeatureAndGet(new TransformableModel(setup));
        transformable.setLocation(1.0, 2.0);
        transformable.setSize(2, 2);

        featurable.addFeature(new CollidableModel(services, setup));

        return featurable;
    }

    /**
     * Test collidable class.
     */
    @Test
    public void testCollidable()
    {
        Assert.assertNull(collidable1.collide(collidable1));
        Assert.assertNull(collidable2.collide(collidable1));

        final Collision collision1 = new Collision("test1", 0, 0, 3, 3, true);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, false);
        collidable2.addCollision(collision2);

        transformable1.moveLocation(1.0, 0, 1);
        transformable2.moveLocation(1.0, 0, 1);

        Assert.assertEquals(collision1, collidable1.collide(collidable2));
        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        Assert.assertTrue(collidable1.getCollisionBounds().iterator().hasNext());
        Assert.assertEquals(collision1, collidable1.getCollisions().iterator().next());

        transformable2.moveLocation(1.0, 2.0, 2.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        transformable2.moveLocation(1.0, 5.0, 5.0);

        Assert.assertNull(collidable1.collide(collidable2));
    }

    /**
     * Test collidable class with different sizes.
     */
    @Test
    public void testDifferentSizes()
    {
        final AtomicBoolean auto = new AtomicBoolean();
        collidable1.checkListener((CollidableListener) collidable -> auto.set(true));

        collidable1.notifyCollided(collidable2);
        Assert.assertTrue(auto.get());

        final Collision collision1 = new Collision("test1", 1, 1, 1, 1, true);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, false);
        collidable2.addCollision(collision2);

        transformable1.moveLocation(1.0, 1, 1);
        transformable2.moveLocation(1.0, 0, 0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));

        transformable1.teleport(2.0, 2.0);

        Assert.assertEquals(collision2, collidable2.collide(collidable1));
    }

    /**
     * Test collidable class with mirror.
     */
    @Test
    public void testMirror()
    {
        final Mirrorable mirror1 = featurable1.addFeatureAndGet(new MirrorableModel());
        mirror1.mirror(Mirror.HORIZONTAL);
        mirror1.update(1.0);

        final Mirrorable mirror2 = featurable2.addFeatureAndGet(new MirrorableModel());
        mirror2.mirror(Mirror.VERTICAL);
        mirror2.update(1.0);

        final Collision collision1 = new Collision("test1", 1, 1, 1, 1, true);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, true);
        collidable2.addCollision(collision2);

        transformable1.teleport(0, 0);
        transformable2.moveLocation(0.0, 0.0, 0.0);

        Assert.assertNull(collidable1.collide(collidable2));
        Assert.assertNull(collidable2.collide(collidable1));

        transformable1.teleport(2.0, 2.0);

        Assert.assertEquals(collision1, collidable1.collide(collidable2));
        Assert.assertEquals(collision2, collidable2.collide(collidable1));
    }

    /**
     * Test collidable disabled.
     */
    @Test
    public void testDisabled()
    {
        collidable1.getAccepted().clear();
        Assert.assertTrue(collidable1.getAccepted().isEmpty());

        collidable1.addAccept(0);

        Assert.assertEquals(0, collidable1.getAccepted().iterator().next().intValue());

        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable1.addCollision(collision);
        transformable1.teleport(1.0, 1.0);

        Assert.assertNotNull(collidable1.collide(collidable1));

        collidable1.setEnabled(false);
        transformable1.teleport(1.0, 1.0);

        Assert.assertNull(collidable1.collide(collidable1));
    }

    /**
     * Test collidable accept collision.
     */
    @Test
    public void testAccept()
    {
        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable1.addCollision(collision);
        transformable1.teleport(1.0, 1.0);

        Assert.assertNull(collidable1.collide(collidable1));

        collidable1.addAccept(collidable1.getGroup().intValue());

        Assert.assertNotNull(collidable1.collide(collidable1));

        collidable1.removeAccept(collidable1.getGroup().intValue());

        Assert.assertNull(collidable1.collide(collidable1));
    }

    /**
     * Test collidable rendering.
     */
    @Test
    public void testRender()
    {
        transformable1.moveLocation(1.0, 1.0, 1.0);

        final Graphic g = Graphics.createGraphic();
        collidable1.setOrigin(Origin.MIDDLE);
        collidable1.render(g);

        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable1.addCollision(collision);
        collidable1.setCollisionVisibility(true);

        transformable1.teleport(1.0, 1.0);
        transformable1.moveLocation(1.0, 1.0, 1.0);

        collidable1.render(g);
    }

    /**
     * Test the group.
     */
    @Test
    public void testGroup()
    {
        Assert.assertEquals(0, collidable1.getGroup().intValue());

        collidable1.setGroup(1);

        Assert.assertEquals(1, collidable1.getGroup().intValue());
    }
}
