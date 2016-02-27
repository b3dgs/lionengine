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
package com.b3dgs.lionengine.game.object.trait.transformable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.object.ObjectConfig;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.SizeConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the transformable trait.
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

    /**
     * Create the object media.
     * 
     * @param clazz The class type.
     * @return The object media.
     */
    private static Media createMedia(Class<?> clazz)
    {
        final Media media = Medias.create("object.xml");
        final XmlNode root = Xml.create("test");
        root.add(ObjectConfig.exportClass(clazz.getName()));
        root.add(ObjectConfig.exportSetup("com.b3dgs.lionengine.game.object.Setup"));
        root.add(SizeConfig.exports(new SizeConfig(16, 32)));
        Xml.save(root, media);
        return media;
    }

    /**
     * Test the transformable teleport.
     */
    @Test
    public void testTeleport()
    {
        final Media media = createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Transformable transformable = new TransformableModel();
        transformable.prepare(object, services);

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

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the transformable location setting.
     */
    @Test
    public void testSetLocation()
    {
        final Media media = createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Transformable transformable = new TransformableModel();
        transformable.prepare(object, services);

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

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the transformable size.
     */
    @Test
    public void testSetSize()
    {
        final Media media = createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Transformable transformable = new TransformableModel();
        transformable.prepare(object, services);

        Assert.assertEquals(16, transformable.getOldWidth(), UtilTests.PRECISION);
        Assert.assertEquals(32, transformable.getOldHeight(), UtilTests.PRECISION);
        Assert.assertEquals(16, transformable.getWidth(), UtilTests.PRECISION);
        Assert.assertEquals(32, transformable.getHeight(), UtilTests.PRECISION);

        transformable.setSize(64, 48);

        Assert.assertEquals(16, transformable.getOldWidth(), UtilTests.PRECISION);
        Assert.assertEquals(32, transformable.getOldHeight(), UtilTests.PRECISION);
        Assert.assertEquals(64, transformable.getWidth(), UtilTests.PRECISION);
        Assert.assertEquals(48, transformable.getHeight(), UtilTests.PRECISION);

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the transformable moving.
     */
    @Test
    public void testMoveLocation()
    {
        final Media media = createMedia(ObjectGame.class);
        final Services services = new Services();
        services.add(new MapTileGame());
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final Transformable transformable = new TransformableModel();
        transformable.prepare(object, services);

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

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }
}
