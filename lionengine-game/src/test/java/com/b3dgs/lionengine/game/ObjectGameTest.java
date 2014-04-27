/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the object game class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ObjectGameTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryMediaProvider.setFactoryMedia(null);
    }

    /**
     * Test the object game.
     */
    @Test
    public void testObjectGame()
    {
        final Media media = Core.MEDIA.create("src", "test", "resources", "type.xml");
        final ObjectGame object = new TestObject(new SetupGame(media));
        Assert.assertTrue(object.getDataBoolean("flag"));
        Assert.assertTrue(object.getDataInteger("index") == 1);
        Assert.assertTrue(object.getDataDouble("index") == 1.0);
        Assert.assertNotNull(object.getDataString("flag"));
        Assert.assertNotNull(object.getDataCollision("default"));
        Assert.assertNotNull(object.getDataAnimation("idle"));

        object.loadData(media);
        Assert.assertNotNull(object.getDataRoot());
    }
}
