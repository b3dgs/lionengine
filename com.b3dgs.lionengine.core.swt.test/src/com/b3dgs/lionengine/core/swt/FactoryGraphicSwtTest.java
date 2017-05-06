/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.swt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.FactoryGraphicTest;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.ImageBufferMock;

/**
 * Test the factory graphic provider class.
 */
public class FactoryGraphicSwtTest extends FactoryGraphicTest
{
    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeClass
    public static void setUp() throws IOException
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        prepare();
        Graphics.setFactoryGraphic(new FactoryGraphicSwt());
        loadResources();
    }

    /**
     * Clean test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test rotate.
     */
    @Test
    @Override
    public void testRotate()
    {
        final ImageBuffer rotate = Graphics.rotate(image, 90);

        Assert.assertNotEquals(image, rotate);
        Assert.assertEquals(image.getWidth(), rotate.getHeight());
        Assert.assertEquals(image.getHeight(), rotate.getWidth());

        rotate.dispose();
    }

    /**
     * Test the get image buffer exception case.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageBufferException()
    {
        Graphics.getImageBuffer(new Media()
        {
            @Override
            public String getPath()
            {
                return null;
            }

            @Override
            public String getParentPath()
            {
                return null;
            }

            @Override
            public OutputStream getOutputStream()
            {
                return null;
            }

            @Override
            public InputStream getInputStream()
            {
                return new InputStream()
                {
                    @Override
                    public int read() throws IOException
                    {
                        return 0;
                    }
                };
            }

            @Override
            public File getFile()
            {
                return null;
            }

            @Override
            public Collection<Media> getMedias()
            {
                return null;
            }

            @Override
            public boolean exists()
            {
                return false;
            }
        });
    }

    /**
     * Test the save image exception case.
     */
    @Test(expected = LionEngineException.class)
    public void testSaveImageException()
    {
        Graphics.saveImage(new ImageBufferMock(16, 32)
        {
            @SuppressWarnings("unchecked")
            @Override
            public <T> T getSurface()
            {
                return (T) ToolsSwt.createImage(100, 100, SWT.TRANSPARENCY_NONE);
            }
        }, new Media()
        {
            @Override
            public String getPath()
            {
                return null;
            }

            @Override
            public String getParentPath()
            {
                return null;
            }

            @Override
            public OutputStream getOutputStream()
            {
                return new OutputStream()
                {
                    @Override
                    public void write(int b) throws IOException
                    {
                        throw new IOException();
                    }
                };
            }

            @Override
            public InputStream getInputStream()
            {
                return null;
            }

            @Override
            public File getFile()
            {
                return null;
            }

            @Override
            public Collection<Media> getMedias()
            {
                return null;
            }

            @Override
            public boolean exists()
            {
                return false;
            }
        });
    }

    /*
     * FactoryGraphicTest
     */

    @Override
    public void testCreateScreen()
    {
        Assume.assumeFalse("Unable to perform this test", false);
    }
}
