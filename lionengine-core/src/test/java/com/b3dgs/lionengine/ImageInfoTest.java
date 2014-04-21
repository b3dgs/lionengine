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
package com.b3dgs.lionengine;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.FactoryMediaMock;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the image info class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImageInfoTest
{
    /** Resources path. */
    private static String PATH;

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Media.setMediaFactory(new FactoryMediaMock());
        Media.setSeparator(java.io.File.separator);
        ImageInfoTest.PATH = Media.getPath("src", "test", "resources", "imageinfo");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Media.setMediaFactory(null);
    }

    /**
     * Test the image info from its type.
     * 
     * @param type The expected image type.
     */
    private static void testImageInfo(String type)
    {
        final ImageInfo info = ImageInfo.get(Media.create(Media.getPath(ImageInfoTest.PATH, "image." + type)));
        Assert.assertEquals(64, info.getWidth());
        Assert.assertEquals(32, info.getHeight());
        Assert.assertEquals(type, info.getFormat());
    }

    /**
     * Test image failure cases.
     */
    @Test
    public void testImageFailure()
    {
        try
        {
            ImageInfo.get(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            ImageInfo.get(Media.create(""));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            ImageInfoTest.testImageInfo("tga");
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test image info functions.
     */
    @Test
    public void testImageInfo()
    {
        ImageInfoTest.testImageInfo("png");
        ImageInfoTest.testImageInfo("gif");
        ImageInfoTest.testImageInfo("bmp");
        ImageInfoTest.testImageInfo("jpg");
        ImageInfoTest.testImageInfo("tiff");
    }
}
