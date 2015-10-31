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
package com.b3dgs.lionengine.test.core.swt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.swt.ToolsSwt;
import com.b3dgs.lionengine.test.util.UtilTests;

/**
 * Test the tools class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ToolsSwtTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        Medias.setLoadFromJar(ToolsSwtTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(ToolsSwt.class);
    }

    /**
     * Test the utility.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testUtility() throws IOException
    {
        final Display display = ToolsSwt.getDisplay();
        final Image image = ToolsSwt.createImage(100, 100, SWT.TRANSPARENCY_NONE);
        Assert.assertNotNull(image);
        Assert.assertNotNull(ToolsSwt.getRasterBuffer(image, 1, 1, 1, 1, 1, 1, 1));
        Assert.assertNotNull(ToolsSwt.flipHorizontal(image));
        Assert.assertNotNull(ToolsSwt.flipVertical(image));
        Assert.assertNotNull(ToolsSwt.resize(image, 10, 10));
        Assert.assertNotNull(ToolsSwt.rotate(image, 90));
        Assert.assertNotNull(ToolsSwt.splitImage(image, 1, 1));
        Assert.assertNotNull(ToolsSwt.applyBilinearFilter(image));
        Assert.assertNotNull(ToolsSwt.applyMask(image, ColorRgba.BLACK.getRgba()));
        Assert.assertNotNull(ToolsSwt.applyMask(image, ColorRgba.WHITE.getRgba()));
        image.dispose();

        final Media media = Medias.create("image.png");
        try (InputStream input = media.getInputStream())
        {
            final Image buffer = ToolsSwt.getImage(display, input);
            Assert.assertNotNull(buffer);

            try (InputStream input2 = media.getInputStream())
            {
                Assert.assertNotNull(ToolsSwt.getImageData(input2));
            }
            finally
            {
                buffer.dispose();
            }
        }

        Assert.assertNotNull(ToolsSwt.createHiddenCursor(display));
    }

    /**
     * Test the copy.
     */
    @Test
    public void testCopy()
    {
        final Image image = ToolsSwt.createImage(100, 100, SWT.TRANSPARENCY_NONE);
        final Image copy = ToolsSwt.getImage(image);
        Assert.assertEquals(image.getImageData().width, copy.getImageData().width);
        image.dispose();
        copy.dispose();
    }

    /**
     * Test the save.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testSave() throws IOException
    {
        final Media media = Medias.create("image.png");
        try (InputStream input = media.getInputStream())
        {
            final Image image = ToolsSwt.getImage(ToolsSwt.getDisplay(), input);
            Assert.assertNotNull(image);

            final Media save = Medias.create("test");
            try (OutputStream output = save.getOutputStream())
            {
                ToolsSwt.saveImage(image, output);
            }
            finally
            {
                image.dispose();
            }
            Assert.assertTrue(save.getFile().exists());
            Assert.assertTrue(save.getFile().delete());
            Assert.assertFalse(save.getFile().exists());
        }
    }

    /**
     * Test the get fail.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFail() throws IOException
    {
        final Media media = Medias.create("image.xml");
        try (InputStream input = media.getInputStream())
        {
            final Image image = ToolsSwt.getImage(ToolsSwt.getDisplay(), input);
            Assert.assertNotNull(image);
        }
    }

    /**
     * Test the get fail IO.
     * 
     * @throws IOException If error.
     */
    @Test(expected = SWTException.class)
    public void testGetIoFail() throws IOException
    {
        final Media media = Medias.create("raster.xml");
        try (InputStream input = media.getInputStream())
        {
            final Image image = ToolsSwt.getImage(ToolsSwt.getDisplay(), input);
            Assert.assertNotNull(image);
        }
    }
}
