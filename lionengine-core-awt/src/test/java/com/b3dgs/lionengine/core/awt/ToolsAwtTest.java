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

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the tools class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class ToolsAwtTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        Medias.setFactoryMedia(new FactoryMediaAwt());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setFactoryMedia(null);
    }

    /**
     * Test the constructor.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = InvocationTargetException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        final Constructor<ToolsAwt> constructor = ToolsAwt.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final ToolsAwt utility = constructor.newInstance();
        Assert.assertNotNull(utility);
        Assert.fail();
    }

    /**
     * Test the utility.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test
    public void testUtility() throws LionEngineException, IOException
    {
        final BufferedImage image = ToolsAwt.createImage(100, 100, Transparency.OPAQUE);
        Assert.assertNotNull(image);
        Assert.assertNotNull(ToolsAwt.getRasterBuffer(image, 1, 1, 1, 1, 1, 1, 1));
        Assert.assertNotNull(ToolsAwt.flipHorizontal(image));
        Assert.assertNotNull(ToolsAwt.flipVertical(image));
        Assert.assertNotNull(ToolsAwt.resize(image, 10, 10));
        Assert.assertNotNull(ToolsAwt.rotate(image, 90));
        Assert.assertNotNull(ToolsAwt.splitImage(image, 1, 1));
        Assert.assertNotNull(ToolsAwt.applyBilinearFilter(image));
        Assert.assertNotNull(ToolsAwt.applyMask(image, ColorRgba.BLACK.getRgba()));
        Assert.assertNotNull(ToolsAwt.applyMask(image, ColorRgba.WHITE.getRgba()));

        final Media media = new MediaAwt(MediaAwt.class.getResource("image.png").getFile());
        final BufferedImage buffer = ToolsAwt.getImage(media.getInputStream());
        Assert.assertNotNull(buffer);
        Assert.assertNotNull(ToolsAwt.getImageData(image));
        ToolsAwt.optimizeGraphicsQuality(buffer.createGraphics());

        Assert.assertNotNull(ToolsAwt.createHiddenCursor());
    }

    /**
     * Test the copy.
     */
    @Test
    public void testCopy()
    {
        final BufferedImage image = ToolsAwt.createImage(100, 100, Transparency.TRANSLUCENT);
        final BufferedImage copy = ToolsAwt.copyImage(image, Transparency.OPAQUE);
        Assert.assertEquals(image.getWidth(), copy.getWidth());
    }

    /**
     * Test the save.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test
    public void testSave() throws LionEngineException, IOException
    {
        final Media media = new MediaAwt(MediaAwt.class.getResource("image.png").getFile());
        final BufferedImage image = ToolsAwt.getImage(media.getInputStream());
        Assert.assertNotNull(image);

        final MediaAwt save = new MediaAwt("test");
        try (OutputStream output = save.getOutputStream())
        {
            ToolsAwt.saveImage(image, output);
        }
        Assert.assertTrue(save.getFile().exists());
        Assert.assertTrue(save.getFile().delete());
        Assert.assertFalse(save.getFile().exists());
    }

    /**
     * Test the get fail.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFail() throws LionEngineException, IOException
    {
        final BufferedImage image = ToolsAwt.getImage(new MediaAwt("image.png").getInputStream());
        Assert.assertNotNull(image);
    }

    /**
     * Test the get fail IO.
     * 
     * @throws IOException If error.
     * @throws LionEngineException If error.
     */
    @Test(expected = IOException.class)
    public void testGetIoFail() throws LionEngineException, IOException
    {
        final Media media = new MediaAwt(MediaAwt.class.getResource("raster.xml").getFile());
        final BufferedImage image = ToolsAwt.getImage(media.getInputStream());
        Assert.assertNotNull(image);
    }
}
