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
package com.b3dgs.lionengine.game.object.feature.body;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the body assignable.
 */
public class BodyTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    private final Body body = new BodyModel();
    private final Services services = new Services();
    private final Media media = UtilSetup.createMedia(ObjectGame.class);
    private final ObjectGame object = new ObjectGame(new Setup(media));
    private final Transformable transformable = object.addFeatureAndGet(new TransformableModel());

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the gravity on body.
     */
    @Test
    public void testGravity()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object, services);
        body.setMass(2.0);

        Assert.assertEquals(2.0, body.getMass(), UtilTests.PRECISION);
        Assert.assertEquals(2.0 * Body.GRAVITY_EARTH, body.getWeight(), UtilTests.PRECISION);

        body.setGravity(3.0);

        Assert.assertEquals(2.0, body.getMass(), UtilTests.PRECISION);
        Assert.assertEquals(6.0, body.getWeight(), UtilTests.PRECISION);

        body.setGravityMax(8.0);
        body.setVectors(new Force(0.0, 0.0));

        Assert.assertEquals(6.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(6.0, transformable.getY(), UtilTests.PRECISION);

        body.update(1.0);

        Assert.assertEquals(6.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        body.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(-8.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the gravity reset.
     */
    @Test
    public void testResetGravity()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object, services);
        body.setMass(2.0);
        body.setGravity(3.0);
        body.setGravityMax(8.0);
        body.setVectors(new Force(0.0, 0.0));
        body.update(1.0);

        Assert.assertEquals(6.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        body.resetGravity();
        body.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(-6.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the gravity fps.
     */
    @Test
    public void testFps()
    {
        transformable.teleport(0, 6.0);

        body.prepare(object, services);
        body.setMass(1.0);
        body.setGravity(1.0);
        body.setDesiredFps(50);
        body.setVectors(new Force(0.0, -1.0));
        body.update(1.0);

        Assert.assertEquals(6.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(4.98, transformable.getY(), UtilTests.PRECISION);

        body.update(0.5);

        Assert.assertEquals(4.98, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(4.45, transformable.getY(), UtilTests.PRECISION);
    }
}
