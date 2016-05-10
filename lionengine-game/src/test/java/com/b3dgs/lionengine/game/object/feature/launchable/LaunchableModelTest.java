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
package com.b3dgs.lionengine.game.object.feature.launchable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the launchable model trait.
 */
public class LaunchableModelTest
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

    /**
     * Create launchable.
     * 
     * @param services The services.
     * @param object The object.
     * @return The launchable.
     */
    private static Launchable createLaunchable(Services services, ObjectGame object)
    {
        object.addFeature(new TransformableModel());

        final Launchable launchable = new LaunchableModel();
        launchable.prepare(object, services);
        launchable.setDelay(10);
        launchable.setLocation(0.0, 0.0);
        launchable.setVector(new Force(0.0, 1.0));

        return launchable;
    }

    /**
     * Test the launch.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunch() throws InterruptedException
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Launchable launchable = createLaunchable(services, object);
        services.add(new MapTileGame());
        final Transformable transformable = object.getFeature(Transformable.class);

        launchable.launch();

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        Thread.sleep(11);
        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getY(), UtilTests.PRECISION);

        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getY(), UtilTests.PRECISION);

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the launch without vector.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunchNoVector() throws InterruptedException
    {
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Launchable launchable = createLaunchable(services, object);
        launchable.setVector(null);
        launchable.launch();
        launchable.update(1.0);
        final Transformable transformable = object.getFeature(Transformable.class);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        Thread.sleep(11);
        launchable.update(1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        object.notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }
}
