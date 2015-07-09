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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the utility image.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class UtilityImageTest
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
        final Constructor<UtilityImage> constructor = UtilityImage.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final UtilityImage utility = constructor.newInstance();
        Assert.assertNotNull(utility);
        Assert.fail();
    }

    /**
     * Test the utility.
     */
    @Test
    public void testUtility()
    {
        final ImageBuffer image = UtilityImage.createImage(100, 100, Transparency.OPAQUE);
        Assert.assertNotNull(image);
        Assert.assertNotNull(UtilityImage.getBuffer(image));
        Assert.assertEquals(java.awt.Transparency.OPAQUE, UtilityImage.getTransparency(image.getTransparency()));
        Assert.assertNotNull(UtilityImage.getRasterBuffer(image, 1, 1, 1, 1, 1, 1, 1));
        Assert.assertNotNull(UtilityImage.flipHorizontal(image));
        Assert.assertNotNull(UtilityImage.flipVertical(image));
        Assert.assertNotNull(UtilityImage.resize(image, 10, 10));
        Assert.assertNotNull(UtilityImage.rotate(image, 90));
        Assert.assertNotNull(UtilityImage.splitImage(image, 1, 1));
        Assert.assertNotNull(UtilityImage.applyBilinearFilter(image));
        Assert.assertNotNull(UtilityImage.applyMask(image, ColorRgba.BLACK.getRgba()));
        Assert.assertNotNull(UtilityImage.applyMask(image, ColorRgba.WHITE.getRgba()));
    }

    /**
     * Test the utility.
     */
    @Test(expected = LionEngineException.class)
    public void testWrongImpl()
    {
        final ImageBuffer wrong = new ImageBuffer()
        {
            @Override
            public void prepare() throws LionEngineException
            {
                // Nothing to do
            }

            @Override
            public void setRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
            {
                // Nothing to do
            }

            @Override
            public void setRgb(int x, int y, int rgb)
            {
                // Nothing to do
            }

            @Override
            public int getWidth()
            {
                return 0;
            }

            @Override
            public Transparency getTransparency()
            {
                return null;
            }

            @Override
            public int[] getRgb(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize)
            {
                return new int[0];
            }

            @Override
            public int getRgb(int x, int y)
            {
                return 0;
            }

            @Override
            public int getHeight()
            {
                return 0;
            }

            @Override
            public void dispose()
            {
                // Nothing to do
            }

            @Override
            public Graphic createGraphic()
            {
                return null;
            }
        };
        Assert.assertNotNull(UtilityImage.getBuffer(wrong));
    }

    /**
     * Test the copy.
     */
    @Test
    public void testCopy()
    {
        final ImageBuffer image = UtilityImage.createImage(100, 100, Transparency.TRANSLUCENT);
        final ImageBuffer copy = UtilityImage.copyImage(image, Transparency.OPAQUE);
        Assert.assertEquals(UtilityImage.getBuffer(image).getWidth(), UtilityImage.getBuffer(copy).getWidth());
    }

    /**
     * Test the save.
     */
    @Test
    public void testSave()
    {
        final Media media = new MediaAwt(MediaAwt.class.getResource("image.png").getFile());
        final ImageBuffer image = UtilityImage.getImage(media);
        Assert.assertNotNull(image);

        final MediaAwt save = new MediaAwt("test");
        UtilityImage.saveImage(image, save);
        Assert.assertTrue(save.getFile().exists());
        Assert.assertTrue(save.getFile().delete());
        Assert.assertFalse(save.getFile().exists());
    }

    /**
     * Test the get fail.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFail()
    {
        final ImageBuffer image = UtilityImage.getImage(new MediaAwt("image.png"));
        Assert.assertNotNull(image);
    }

    /**
     * Test the get fail IO.
     */
    @Test(expected = LionEngineException.class)
    public void testGetIoFail()
    {
        final Media media = new MediaAwt(MediaAwt.class.getResource("raster.xml").getFile());
        final ImageBuffer image = UtilityImage.getImage(media);
        Assert.assertNotNull(image);
    }
}
