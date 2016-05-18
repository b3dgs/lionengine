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
package com.b3dgs.lionengine.game.object.feature.extractable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the extractable trait.
 */
public class ExtractableModelTest
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

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the extraction config.
     */
    @Test
    public void testConfig()
    {
        final Extractable extractable = UtilExtractable.createExtractable(media);

        Assert.assertEquals(10, extractable.getResourceQuantity());
        Assert.assertEquals(ResourceType.WOOD, extractable.getResourceType());

        Assert.assertEquals(0, extractable.getInTileX(), UtilTests.PRECISION);
        Assert.assertEquals(0, extractable.getInTileY(), UtilTests.PRECISION);
        Assert.assertEquals(0, extractable.getInTileWidth(), UtilTests.PRECISION);
        Assert.assertEquals(0, extractable.getInTileHeight(), UtilTests.PRECISION);

        extractable.getOwner().notifyDestroyed();
    }

    /**
     * Test the extraction.
     */
    @Test
    public void testExtract()
    {
        final Extractable extractable = new ExtractableModel();
        final Services services = new Services();
        services.add(new MapTileGame());

        final ObjectGame object = new ObjectGame(new Setup(media));
        object.addFeature(new TransformableModel());
        extractable.prepare(object, services);
        extractable.setResourcesQuantity(10);

        Assert.assertEquals(10, extractable.getResourceQuantity());

        extractable.extractResource(5);

        Assert.assertEquals(5, extractable.getResourceQuantity());

        extractable.getOwner().notifyDestroyed();
    }
}
