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
package com.b3dgs.lionengine.game.object.feature.transformable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.identifiable.IdentifiableModel;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the transformable.
 */
public class TransformableModelTest
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

    private final Media media = UtilTransformable.createMedia(Featurable.class);
    private final Services services = new Services();
    private final Setup setup = new Setup(media);
    private final Featurable object = new FeaturableModel();
    private final Transformable transformable = new TransformableModel(setup);

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        services.add(new MapTileGame());
        object.addFeature(new IdentifiableModel());
        transformable.prepare(object, services);
    }

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        object.getFeature(Identifiable.class).notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the transformable with default size.
     */
    @Test
    public void testDefaultSize()
    {
        final Transformable transformable = new TransformableModel();

        Assert.assertEquals(0, transformable.getWidth());
        Assert.assertEquals(0, transformable.getHeight());
    }

    /**
     * Test the transformable teleport.
     */
    @Test
    public void testTeleport()
    {
        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        transformable.teleport(1.0, -1.0);

        Assert.assertEquals(1.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(-1.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(-1.0, transformable.getY(), UtilTests.PRECISION);

        transformable.teleportX(2.0);

        Assert.assertEquals(2.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(-1.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(-1.0, transformable.getY(), UtilTests.PRECISION);

        transformable.teleportY(3.0);

        Assert.assertEquals(2.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the transformable location setting.
     */
    @Test
    public void testSetLocation()
    {
        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        transformable.setLocation(1.0, 1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getY(), UtilTests.PRECISION);

        transformable.setLocationX(2.0);

        Assert.assertEquals(1.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getY(), UtilTests.PRECISION);

        transformable.setLocationY(3.0);

        Assert.assertEquals(1.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the transformable size.
     */
    @Test
    public void testSetSize()
    {
        Assert.assertEquals(16, transformable.getOldWidth(), UtilTests.PRECISION);
        Assert.assertEquals(32, transformable.getOldHeight(), UtilTests.PRECISION);
        Assert.assertEquals(16, transformable.getWidth(), UtilTests.PRECISION);
        Assert.assertEquals(32, transformable.getHeight(), UtilTests.PRECISION);

        transformable.setSize(64, 48);

        Assert.assertEquals(16, transformable.getOldWidth(), UtilTests.PRECISION);
        Assert.assertEquals(32, transformable.getOldHeight(), UtilTests.PRECISION);
        Assert.assertEquals(64, transformable.getWidth(), UtilTests.PRECISION);
        Assert.assertEquals(48, transformable.getHeight(), UtilTests.PRECISION);
    }

    /**
     * Test the transformable moving.
     */
    @Test
    public void testMoveLocation()
    {
        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        transformable.moveLocation(1.0, 1.0, 2.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getY(), UtilTests.PRECISION);

        transformable.moveLocation(1.0, new Force(-2.0, -3.0), new Force(-1.0, -2.0));

        Assert.assertEquals(1.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(-2.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(-3.0, transformable.getY(), UtilTests.PRECISION);
    }

    /**
     * Test the transformable moving on single axis at a time.
     */
    @Test
    public void testMoveLocationSingleAxis()
    {
        transformable.moveLocationX(1.0, 1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getY(), UtilTests.PRECISION);

        transformable.moveLocationY(1.0, 1.0);

        Assert.assertEquals(0.0, transformable.getOldX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, transformable.getOldY(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getX(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, transformable.getY(), UtilTests.PRECISION);
    }
}
