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

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

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
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        ImageInfoTest.PATH = UtilityFile.getPath("src", "test", "resources", "imageinfo");
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
     * Test the image failure
     * 
     * @param media The image media.
     */
    private static void testImageInfoFailure(Media media)
    {
        try
        {
            ImageInfo.get(media);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the image info from its type.
     * 
     * @param type The expected image type.
     */
    private static void testImageInfo(String type)
    {
        final ImageInfo info = ImageInfo.get(Core.MEDIA.create(ImageInfoTest.PATH, "image." + type));
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
        ImageInfoTest.testImageInfoFailure(null);
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(""));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image.tga"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error1.gif"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error2.gif"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error1.jpg"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error2.jpg"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error3.jpg"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error4.jpg"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error5.jpg"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error1.png"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error2.png"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error1.bmp"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error1.tiff"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error2.tiff"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error3.tiff"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error4.tiff"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error5.tiff"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error6.tiff"));
        ImageInfoTest.testImageInfoFailure(Core.MEDIA.create(ImageInfoTest.PATH, "image_error7.tiff"));
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

        final ImageInfo info = ImageInfo.get(Core.MEDIA.create(ImageInfoTest.PATH, "image.tif"));
        Assert.assertEquals(64, info.getWidth());
        Assert.assertEquals(32, info.getHeight());
        Assert.assertEquals("tiff", info.getFormat());

        final ImageInfo info2 = ImageInfo.get(Core.MEDIA.create(ImageInfoTest.PATH, "image2.tiff"));
        Assert.assertNotNull(info2);
    }
}
