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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the image info class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImageInfoTest
{
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
        final Media media = new MediaMock("image." + type);
        final ImageInfo info = ImageInfo.get(media);
        Assert.assertEquals(64, info.getWidth());
        Assert.assertEquals(32, info.getHeight());
        Assert.assertEquals(type, info.getFormat());
        Assert.assertTrue(ImageInfo.isImage(media));
    }

    /**
     * Test image failure cases.
     */
    @Test
    public void testImageFailure()
    {
        testImageInfoFailure(null);
        testImageInfoFailure(new MediaMock(""));
        testImageInfoFailure(new MediaMock("image_error"));
        testImageInfoFailure(new MediaMock("image.tga"));
        testImageInfoFailure(new MediaMock("image_error1.gif"));
        testImageInfoFailure(new MediaMock("image_error2.gif"));
        testImageInfoFailure(new MediaMock("image_error1.jpg"));
        testImageInfoFailure(new MediaMock("image_error2.jpg"));
        testImageInfoFailure(new MediaMock("image_error3.jpg", true));
        testImageInfoFailure(new MediaMock("image_error1.png"));
        testImageInfoFailure(new MediaMock("image_error2.png"));
        testImageInfoFailure(new MediaMock("image_error1.bmp"));
        testImageInfoFailure(new MediaMock("image_error1.tiff"));
        testImageInfoFailure(new MediaMock("image_error2.tiff"));
        testImageInfoFailure(new MediaMock("image_error3.tiff"));
        testImageInfoFailure(new MediaMock("image_error4.tiff"));
        testImageInfoFailure(new MediaMock("image_error5.tiff"));
        testImageInfoFailure(new MediaMock("image_error6.tiff"));
        testImageInfoFailure(new MediaMock("image_error7.tiff"));
        Assert.assertFalse(ImageInfo.isImage(new MediaMock("image_error7.tiff")));
    }

    /**
     * Test image info functions.
     */
    @Test
    public void testImageInfo()
    {
        testImageInfo("png");
        testImageInfo("gif");
        testImageInfo("bmp");
        testImageInfo("jpg");
        testImageInfo("tiff");

        final ImageInfo info = ImageInfo.get(new MediaMock("image.tif"));
        Assert.assertEquals(64, info.getWidth());
        Assert.assertEquals(32, info.getHeight());
        Assert.assertEquals("tiff", info.getFormat());

        final ImageInfo info2 = ImageInfo.get(new MediaMock("image2.tiff"));
        Assert.assertNotNull(info2);
    }
}
