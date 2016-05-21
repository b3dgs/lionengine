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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the launchable model.
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

    private final Media media = UtilSetup.createMedia(ObjectGame.class);
    private final Services services = new Services();
    private final ObjectGame object = new ObjectGame(new Setup(media));
    private final Launchable launchable = UtilLaunchable.createLaunchable(services, object);
    private final Transformable transformable = object.getFeature(Transformable.class);

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new MapTileGame());
    }

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
     * Test the launch.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunch() throws InterruptedException
    {
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
    }

    /**
     * Test the launch without vector.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLaunchNoVector() throws InterruptedException
    {
        launchable.setVector(null);
        launchable.launch();
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
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);
    }
}
