/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Test the image info class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImageInfoTest
{
    /**
     * Test image info functions.
     */
    @Test
    public void testImageInfo()
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

        final ImageInfo infoPng = ImageInfo.get(Media.create(Media.getPath("src", "test", "resources", "dot.png")));
        Assert.assertEquals(64, infoPng.getWidth());
        Assert.assertEquals(32, infoPng.getHeight());
        Assert.assertEquals("png", infoPng.getFormat());

        final ImageInfo infoGif = ImageInfo.get(Media.create(Media.getPath("src", "test", "resources", "dot.gif")));
        Assert.assertEquals(64, infoGif.getWidth());
        Assert.assertEquals(32, infoGif.getHeight());
        Assert.assertEquals("gif", infoGif.getFormat());

        final ImageInfo infoBmp = ImageInfo.get(Media.create(Media.getPath("src", "test", "resources", "dot.bmp")));
        Assert.assertEquals(64, infoBmp.getWidth());
        Assert.assertEquals(32, infoBmp.getHeight());
        Assert.assertEquals("bmp", infoBmp.getFormat());

        final ImageInfo infoJpg = ImageInfo.get(Media.create(Media.getPath("src", "test", "resources", "dot.jpg")));
        Assert.assertEquals(64, infoJpg.getWidth());
        Assert.assertEquals(32, infoJpg.getHeight());
        Assert.assertEquals("jpeg", infoJpg.getFormat());

        final ImageInfo infoTiff = ImageInfo.get(Media.create(Media.getPath("src", "test", "resources", "dot.tif")));
        Assert.assertEquals(64, infoTiff.getWidth());
        Assert.assertEquals(32, infoTiff.getHeight());
        Assert.assertEquals("tiff", infoTiff.getFormat());

        try
        {
            final ImageInfo infoTga = ImageInfo.get(Media.create(Media.getPath("src", "test", "resources", "dot.tga")));
            Assert.assertEquals("tga", infoTga.getFormat());
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
