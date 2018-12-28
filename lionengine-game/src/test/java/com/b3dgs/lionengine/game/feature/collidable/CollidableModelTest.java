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
package com.b3dgs.lionengine.game.feature.collidable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
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
 * Test {@link CollidableModel}.
 */
public final class CollidableModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
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
        Graphics.setFactoryGraphic(null);
        Medias.setResourcesDirectory(null);
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
    @BeforeEach
    public void prepare()
    {
        transformable1.setSize(3, 3);
        collidable1.setGroup(0);
        collidable2.setGroup(1);

        collidable1.addAccept(collidable2.getGroup().intValue());
        collidable2.addAccept(collidable1.getGroup().intValue());
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        collidable1.getFeature(Identifiable.class).destroy();
        collidable2.getFeature(Identifiable.class).destroy();
    }

    /**
     * Test collidable class.
     */
    @Test
    public void testCollidable()
    {
        assertTrue(collidable1.collide(collidable1).isEmpty());
        assertTrue(collidable2.collide(collidable1).isEmpty());

        collidable1.setOrigin(Origin.MIDDLE);
        collidable1.addCollision(Collision.AUTOMATIC);

        final Collision collision2 = new Collision("test2", 0, 1, 3, 3, false);
        collidable2.addCollision(collision2);

        transformable1.moveLocation(1.0, 0.0, 1.0);
        transformable2.moveLocation(1.0, 0.0, 1.0);

        assertEquals(Arrays.asList(Collision.AUTOMATIC), collidable1.collide(collidable2));
        assertEquals(Arrays.asList(collision2), collidable2.collide(collidable1));

        assertTrue(collidable1.getCollisionBounds().iterator().hasNext());
        assertEquals(Collision.AUTOMATIC, collidable1.getCollisions().iterator().next());

        transformable2.moveLocation(1.0, 1.0, 0.0);

        assertEquals(Arrays.asList(collision2), collidable2.collide(collidable1));

        transformable2.moveLocation(1.0, 5.0, 5.0);

        assertTrue(collidable1.collide(collidable2).isEmpty());
    }

    /**
     * Test collidable class with different sizes.
     */
    @Test
    public void testDifferentSizes()
    {
        final AtomicBoolean auto = new AtomicBoolean();
        collidable1.checkListener((CollidableListener) (collidable, collision) -> auto.set(true));

        final Collision collision1 = new Collision("test1", 1, 1, 1, 1, true);
        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, false);

        collidable1.notifyCollided(collidable2, collision1);
        assertTrue(auto.get());

        collidable1.addCollision(collision1);

        collidable2.addCollision(collision2);

        transformable1.moveLocation(1.0, 1, 1);
        transformable2.moveLocation(1.0, 0, 0);

        assertEquals(Arrays.asList(collision2), collidable2.collide(collidable1));

        transformable1.teleport(0.5, 3.5);

        assertEquals(Arrays.asList(collision2), collidable2.collide(collidable1));
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

        transformable1.teleport(0.0, 0.0);
        transformable2.moveLocation(0.0, 0.0, 0.0);

        assertTrue(collidable1.collide(collidable2).isEmpty());
        assertTrue(collidable2.collide(collidable1).isEmpty());

        transformable1.teleport(1.5, 2.5);

        assertEquals(Arrays.asList(collision1), collidable1.collide(collidable2));
        assertEquals(Arrays.asList(collision2), collidable2.collide(collidable1));
    }

    /**
     * Test collidable disabled.
     */
    @Test
    public void testDisabled()
    {
        collidable1.getAccepted().clear();
        assertTrue(collidable1.getAccepted().isEmpty());

        collidable1.addAccept(0);

        assertEquals(0, collidable1.getAccepted().iterator().next().intValue());

        final Collision collision = new Collision("test", 0, 0, 3, 3, false);
        collidable1.addCollision(collision);
        transformable1.teleport(1.0, 1.0);

        assertFalse(collidable1.collide(collidable1).isEmpty());

        collidable1.setEnabled(false);
        transformable1.teleport(1.0, 1.0);

        assertTrue(collidable1.collide(collidable1).isEmpty());
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

        assertTrue(collidable1.collide(collidable1).isEmpty());

        collidable1.addAccept(collidable1.getGroup().intValue());

        assertFalse(collidable1.collide(collidable1).isEmpty());

        collidable1.removeAccept(collidable1.getGroup().intValue());

        assertTrue(collidable1.collide(collidable1).isEmpty());
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
        collidable1.addCollision(Collision.AUTOMATIC);
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
        assertEquals(0, collidable1.getGroup().intValue());

        collidable1.setGroup(1);

        assertEquals(1, collidable1.getGroup().intValue());
    }

    /**
     * Test listener add and remove.
     */
    @Test
    public void testListener()
    {
        final AtomicBoolean called = new AtomicBoolean();
        final CollidableListener listener = (collidable, collision) -> called.set(true);

        collidable1.notifyCollided(null, null);

        assertFalse(called.get());

        collidable1.addListener(listener);

        collidable1.notifyCollided(null, null);

        assertTrue(called.get());

        collidable1.removeListener(listener);
        called.set(false);

        collidable1.notifyCollided(null, null);

        assertFalse(called.get());
    }
}
