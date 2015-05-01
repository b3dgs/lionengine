/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.Media;

/**
 * Test the media AWT class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class MediaAwtTest
{
    /**
     * Create a media by reflection.
     * 
     * @param path The media path.
     * @return The created media.
     */
    private static Media create(String path)
    {
        final URL url = MediaAwtTest.class.getResource(path);
        if (url == null)
        {
            return new MediaAwt(path);
        }
        return new MediaAwt(url.getFile());
    }

    /**
     * Test the path getter.
     */
    @Test
    public void testPath()
    {
        final String path = "path";
        Assert.assertEquals(path, create(path).getPath());
    }

    /**
     * Test the path getter.
     */
    @Test
    public void testFile()
    {
        final String path = "path";
        Assert.assertEquals(path, create(path).getFile().getPath());
    }

    /**
     * Test the input stream.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testInputStream() throws IOException
    {
        final Media media = create("image.png");
        try (InputStream input = media.getInputStream())
        {
            Assert.assertNotNull(input);
        }
    }

    /**
     * Test the output stream.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testOutputStream() throws IOException
    {
        final Media media = create("out");
        try (OutputStream output = media.getOutputStream())
        {
            Assert.assertNotNull(output);
        }
        Assert.assertTrue(media.getFile().exists());
        Assert.assertTrue(media.getFile().delete());
        Assert.assertFalse(media.getFile().exists());
    }

    /**
     * Test the equals media.
     */
    @Test
    public void testHashcode()
    {
        final Media media = create("media");
        final Media media1 = create("media");
        final Media media2 = create("media2");
        Assert.assertEquals(media.hashCode(), media.hashCode());
        Assert.assertEquals(media.hashCode(), media1.hashCode());
        Assert.assertNotEquals(media.hashCode(), media2.hashCode());
        Assert.assertNotEquals(media.hashCode(), new Object().hashCode());
    }

    /**
     * Test the equals media.
     */
    @Test
    public void testEquals()
    {
        final Media media = create("media");
        final Media media1 = create("media");
        final Media media2 = create("media2");
        Assert.assertEquals(media, media);
        Assert.assertEquals(media, media1);
        Assert.assertNotEquals(media, media2);
        Assert.assertNotEquals(media, new Object());
    }
}
