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
package com.b3dgs.lionengine.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Checksum;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Strings;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.utility.UtilityConversion;
import com.b3dgs.lionengine.utility.UtilityFile;
import com.b3dgs.lionengine.utility.UtilityImage;
import com.b3dgs.lionengine.utility.UtilityMath;
import com.b3dgs.lionengine.utility.UtilityMessageBox;
import com.b3dgs.lionengine.utility.UtilityProjectStats;
import com.b3dgs.lionengine.utility.UtilityRandom;
import com.b3dgs.lionengine.utility.UtilitySwing;

/**
 * Test checksum package.
 */
public class TestUtility
{
    /**
     * Test creation of a wrong buffered image.
     * 
     * @param width The image width.
     * @param height The image height.
     */
    private static void testCreateBufferedImageFail(int width, int height)
    {
        try
        {
            final BufferedImage image = UtilityImage.createBufferedImage(width, height, Transparency.OPAQUE);
            image.flush();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test creation of a wrong volatile image.
     * 
     * @param width The image width.
     * @param height The image height.
     */
    private static void testCreateVolatileImageFail(int width, int height)
    {
        try
        {
            final VolatileImage image = UtilityImage.createVolatileImage(width, height, Transparency.OPAQUE);
            image.flush();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
    }

    /**
     * Test utility class by introspection.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testIntrospection() throws Exception
    {
        final Constructor<UtilityImage> utilityImage = UtilityImage.class.getDeclaredConstructor();
        utilityImage.setAccessible(true);
        try
        {
            final UtilityImage clazz = utilityImage.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<Strings> strings = Strings.class.getDeclaredConstructor();
        strings.setAccessible(true);
        try
        {
            final Strings clazz = strings.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilityConversion> utilityConversion = UtilityConversion.class.getDeclaredConstructor();
        utilityConversion.setAccessible(true);
        try
        {
            final UtilityConversion clazz = utilityConversion.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilityMath> utilityMath = UtilityMath.class.getDeclaredConstructor();
        utilityMath.setAccessible(true);
        try
        {
            final UtilityMath clazz = utilityMath.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilityRandom> utilityRandom = UtilityRandom.class.getDeclaredConstructor();
        utilityRandom.setAccessible(true);
        try
        {
            final UtilityRandom clazz = utilityRandom.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilityProjectStats> utilityProjectStats = UtilityProjectStats.class.getDeclaredConstructor();
        utilityProjectStats.setAccessible(true);
        try
        {
            final UtilityProjectStats clazz = utilityProjectStats.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilityMessageBox> utilityMessageBox = UtilityMessageBox.class.getDeclaredConstructor();
        utilityMessageBox.setAccessible(true);
        try
        {
            final UtilityMessageBox clazz = utilityMessageBox.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilityFile> utilityFile = UtilityFile.class.getDeclaredConstructor();
        utilityFile.setAccessible(true);
        try
        {
            final UtilityFile clazz = utilityFile.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        final Constructor<UtilitySwing> utilitySwing = UtilitySwing.class.getDeclaredConstructor();
        utilitySwing.setAccessible(true);
        try
        {
            final UtilitySwing clazz = utilitySwing.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test testUtilityImage functions.
     */
    @Test
    public void testUtilityImage()
    {
        TestUtility.testCreateBufferedImageFail(0, 1);
        TestUtility.testCreateBufferedImageFail(1, 0);

        TestUtility.testCreateVolatileImageFail(0, 1);
        TestUtility.testCreateVolatileImageFail(1, 0);

        final BufferedImage bufferedImage = UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE);
        final VolatileImage volatileImage = UtilityImage.createVolatileImage(16, 16, Transparency.OPAQUE);

        Assert.assertEquals(bufferedImage.getWidth(), volatileImage.getWidth());
        Assert.assertEquals(bufferedImage.getHeight(), volatileImage.getHeight());

        final BufferedImage image0 = UtilityImage.getBufferedImage(Media.get("dot.png"), true);
        final BufferedImage image1 = UtilityImage.getBufferedImage(Media.get("dot.png"), false);
        final VolatileImage image2 = UtilityImage.getVolatileImage(Media.get("dot.png"), Transparency.OPAQUE);
        try
        {
            UtilityImage.getBufferedImage(Media.get("null"), false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        try
        {
            UtilityImage.getVolatileImage(Media.get("null"), Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Assert.assertEquals(image1.getWidth(), image0.getWidth());
        Assert.assertEquals(image1.getHeight(), image0.getHeight());

        Assert.assertEquals(image1.getWidth(), image2.getWidth());
        Assert.assertEquals(image1.getHeight(), image2.getHeight());

        final VolatileImage image3 = UtilityImage.getVolatileImage(image1);
        Assert.assertEquals(image1.getWidth(), image3.getWidth());
        Assert.assertEquals(image1.getHeight(), image3.getHeight());

        final BufferedImage image4 = UtilityImage.applyMask(image1, Color.BLACK);
        Assert.assertEquals(image1.getWidth(), image4.getWidth());
        Assert.assertEquals(image1.getHeight(), image4.getHeight());

        UtilityImage.rotate(image1, 90);
        final BufferedImage resized = UtilityImage.resize(image1, 1, 2);
        Assert.assertEquals(1, resized.getWidth());
        Assert.assertEquals(2, resized.getHeight());

        final BufferedImage flipH = UtilityImage.flipHorizontal(image1);
        Assert.assertEquals(image1.getWidth(), flipH.getWidth());
        Assert.assertEquals(image1.getHeight(), flipH.getHeight());

        final BufferedImage flipV = UtilityImage.flipVertical(image1);
        Assert.assertEquals(image1.getWidth(), flipV.getWidth());
        Assert.assertEquals(image1.getHeight(), flipV.getHeight());

        final BufferedImage[] splitRef = UtilityImage.referenceSplit(image1, 2, 2);
        for (final BufferedImage img1 : splitRef)
        {
            for (final BufferedImage img2 : splitRef)
            {
                Assert.assertEquals(img1.getWidth(), img2.getWidth());
                Assert.assertEquals(img1.getHeight(), img2.getHeight());
            }
        }
        Assert.assertEquals(image1.getWidth() / 2, splitRef[0].getWidth());
        Assert.assertEquals(image1.getHeight() / 2, splitRef[0].getHeight());

        final BufferedImage[] split = UtilityImage.splitImage(image1, 2, 2);
        for (final BufferedImage img1 : split)
        {
            for (final BufferedImage img2 : split)
            {
                Assert.assertEquals(img1.getWidth(), img2.getWidth());
                Assert.assertEquals(img1.getHeight(), img2.getHeight());
            }
        }
        Assert.assertEquals(image1.getWidth() / 2, split[0].getWidth());
        Assert.assertEquals(image1.getHeight() / 2, split[0].getHeight());

        final int filterRgb1 = UtilityImage.filterRGB(0, -1, -1, -1);
        Assert.assertTrue(filterRgb1 >= 0);
        final int filterRgb2 = UtilityImage.filterRGB(65535, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        Assert.assertTrue(filterRgb2 >= 0);

        UtilityImage.optimizeGraphicsQuality((Graphics2D) image1.getGraphics());
        UtilityImage.optimizeGraphicsSpeed((Graphics2D) image1.getGraphics());

        try
        {
            UtilityImage.applyFilter(image1, null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        final BufferedImage bilinear = UtilityImage.applyFilter(image1, Filter.BILINEAR);
        bilinear.flush();
        try
        {
            final BufferedImage fail = UtilityImage.applyFilter(image1, Filter.HQ3X);
            fail.flush();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        UtilityImage.saveImage(image1, Media.get("testImage.png"));
        final BufferedImage loaded = UtilityImage.getBufferedImage(Media.get("testImage.png"), false);
        Assert.assertEquals(image1.getWidth(), loaded.getWidth());
        Assert.assertEquals(image1.getHeight(), loaded.getHeight());
        final File file = new File(Media.get("testImage.png").getPath());
        Assert.assertTrue(file.delete());

        final BufferedImage raster = UtilityImage.getRasterBuffer(image1, 0, 0, 0, 255, 255, 255, 5);
        raster.flush();

        Assert.assertNotNull(UtilityImage.loadRaster(Media.get("raster.xml")));
        try
        {
            UtilityImage.loadRaster(Media.get("rasterError.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test checksum functions.
     */
    @Test
    public void testChecksum()
    {
        final Checksum checksum = Checksum.create();
        final int integer = 489464795;
        final String value = "keyToBeEncoded";
        final String other = "anotherKey";
        final String signature = checksum.getSha256(value);
        final String test = checksum.getSha256(integer);

        Assert.assertTrue(checksum.check(value, signature));
        Assert.assertFalse(checksum.check(other, signature));
        Assert.assertTrue(checksum.check(integer, test));
    }

    /**
     * Test image info functions.
     */
    @Test
    public void testImageInfo()
    {
        try
        {
            ImageInfo.get(null);
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            ImageInfo.get(Media.get(""));
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final ImageInfo infoPng = ImageInfo.get(Media.get("dot.png"));
        Assert.assertEquals(64, infoPng.getWidth());
        Assert.assertEquals(32, infoPng.getHeight());
        Assert.assertEquals("png", infoPng.getFormat());

        final ImageInfo infoGif = ImageInfo.get(Media.get("dot.gif"));
        Assert.assertEquals(64, infoGif.getWidth());
        Assert.assertEquals(32, infoGif.getHeight());
        Assert.assertEquals("gif", infoGif.getFormat());

        final ImageInfo infoBmp = ImageInfo.get(Media.get("dot.bmp"));
        Assert.assertEquals(64, infoBmp.getWidth());
        Assert.assertEquals(32, infoBmp.getHeight());
        Assert.assertEquals("bmp", infoBmp.getFormat());

        final ImageInfo infoJpg = ImageInfo.get(Media.get("dot.jpg"));
        Assert.assertEquals(64, infoJpg.getWidth());
        Assert.assertEquals(32, infoJpg.getHeight());
        Assert.assertEquals("jpeg", infoJpg.getFormat());

        final ImageInfo infoTiff = ImageInfo.get(Media.get("dot.tif"));
        Assert.assertEquals(64, infoTiff.getWidth());
        Assert.assertEquals(32, infoTiff.getHeight());
        Assert.assertEquals("tiff", infoTiff.getFormat());

        try
        {
            final ImageInfo infoTga = ImageInfo.get(Media.get("dot.tga"));
            Assert.assertEquals("tga", infoTga.getFormat());
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the strings functions.
     */
    @Test
    public void testStrings()
    {
        final String str = "test";
        final String str1 = Strings.getStringRef(str);
        final String str2 = Strings.getStringRef(str);
        Assert.assertTrue(!Strings.getStringsRefs().isEmpty());
        Assert.assertTrue(str1 == str2);
        Assert.assertEquals(str1, str2);
        for (final String string : Strings.getStringsRefs())
        {
            Assert.assertTrue(string == str);
        }
        Strings.removeStringRef(str);
        Strings.clearStringsRef();
        Assert.assertTrue(Strings.getStringsRefs().isEmpty());
    }

    /**
     * Test the utility conversion.
     */
    @Test
    public void testUtilityConversion()
    {
        final short s = 12345;
        Assert.assertEquals(s, UtilityConversion.byteArrayToShort(UtilityConversion.shortToByteArray(s)));

        final int i = 123456789;
        Assert.assertEquals(i, UtilityConversion.byteArrayToInt(UtilityConversion.intToByteArray(i)));

        Assert.assertEquals(s, UtilityConversion.fromUnsignedShort(UtilityConversion.toUnsignedShort(s)));

        final byte b = 123;
        Assert.assertEquals(b, UtilityConversion.fromUnsignedByte(UtilityConversion.toUnsignedByte(b)));
    }

    /**
     * Test the utility file.
     */
    @Test
    public void testUtilityFile()
    {
        final String file = "dot.png";
        final Media media = Media.get(file);
        final String path = media.getPath();
        final File descriptor = new File(path);

        Assert.assertTrue(UtilityFile.exists(path));
        Assert.assertFalse(UtilityFile.exists(null));
        Assert.assertEquals("png", UtilityFile.getExtension(path));
        Assert.assertEquals("png", UtilityFile.getExtension(descriptor));
        Assert.assertEquals("", UtilityFile.getExtension("noextension"));
        Assert.assertEquals("", UtilityFile.getExtension("noextension."));
        Assert.assertEquals(file, Media.getFilenameFromPath(path));
        Assert.assertTrue(UtilityFile.isFile(path));
        Assert.assertFalse(UtilityFile.isFile(null));
        Assert.assertFalse(UtilityFile.isDir(path));
        Assert.assertFalse(UtilityFile.isDir(null));

        final String[] dirs = UtilityFile.getDirsList(Media.get("").getPath());
        Assert.assertEquals(1, dirs.length);
        Assert.assertEquals("directory", dirs[0]);
        Assert.assertEquals(0, UtilityFile.getDirsList(Media.get("null").getPath()).length);

        final String[] files = UtilityFile.getFilesList(Media.get("").getPath());
        Assert.assertEquals(14, files.length);
        Assert.assertEquals(0, UtilityFile.getFilesList(Media.get("null").getPath()).length);
        Assert.assertEquals(0, UtilityFile.getFilesList(Media.get("null").getPath(), "txt").length);
        Assert.assertEquals(1, UtilityFile.getFilesList(Media.get("").getPath(), "wav").length);

        final Media dir = Media.get("temp");
        final Media test = Media.get("temp", "test");
        final File tempDir = new File(dir.getPath());
        final File testFile = new File(test.getPath());
        Assert.assertTrue(tempDir.mkdirs());
        try
        {
            Assert.assertTrue(testFile.createNewFile());
        }
        catch (final IOException exception)
        {
            Assert.fail();
        }
        UtilityFile.deleteDirectory(tempDir);

        Assert.assertEquals("path" + File.separator + "test", Media.getPath("path", "test"));
    }

    /**
     * Test utility media.
     */
    @Test
    public void testUtilityMedia()
    {
        File file = Media.getTempFile(Media.get("dot.png"), true, false);
        Assert.assertTrue(file.exists());
        try
        {
            Assert.assertNotNull(Media.getTempFile(Media.get("none.png"), true, true));
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        file = Media.getTempFile(Media.get("dot.png"), false, false);
        Assert.assertTrue(file.exists());
    }

    /**
     * Test utility math.
     */
    @Test
    public void testMath()
    {
        final double precision = 0.000001;
        Assert.assertEquals(0, UtilityMath.fixBetween(-10, 0, 10));
        Assert.assertEquals(10, UtilityMath.fixBetween(50, 0, 10));
        Assert.assertEquals(0.0, UtilityMath.fixBetween(-10.0, 0.0, 10.0), precision);
        Assert.assertEquals(10.0, UtilityMath.fixBetween(50.0, 0.0, 10.0), precision);

        Assert.assertTrue(UtilityMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        Assert.assertTrue(UtilityMath.curveValue(0.0, -1.0, 0.5) < 0.0);

        final Line2D line1 = new Line2D.Double(1.0, -1.0, 1.0, 1.0);
        final Line2D line2 = new Line2D.Double(0.0, 0.0, 2.0, 0.0);
        final Point2D point = new Point2D.Double(1.0, 0.0);
        Assert.assertEquals(point, UtilityMath.intersection(line1, line2));

        try
        {
            UtilityMath.intersection(line1, line1);
        }
        catch (final IllegalStateException exception)
        {
            // Success
        }

        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 6, 6));
        Assert.assertEquals(2.0, UtilityMath.getDistance(4.0, 6.0, 6.0, 6.0), precision);
        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 2, 2, 6, 6, 2, 2));

        Assert.assertEquals(0.0, UtilityMath.wrapDouble(360.0, 0.0, 360.0), precision);
        Assert.assertEquals(359.0, UtilityMath.wrapDouble(-1.0, 0.0, 360.0), precision);
        Assert.assertEquals(180.0, UtilityMath.wrapDouble(180.0, 0.0, 360.0), precision);

        Assert.assertEquals(-1.0, UtilityMath.cos(180), precision);
        Assert.assertEquals(0.0, UtilityMath.sin(180), precision);

        Assert.assertTrue(UtilityMath.time() > 0);
        Assert.assertTrue(UtilityMath.nano() > 0);

    }

    /**
     * Test utility random.
     */
    @Test
    public void testRandom()
    {
        UtilityRandom.setSeed(4894516L);
        UtilityRandom.getRandomBoolean();
        UtilityRandom.getRandomInteger();
        UtilityRandom.getRandomDouble();
        UtilityRandom.getRandomInteger(100);
        UtilityRandom.getRandomInteger(-100, 100);
    }

    /**
     * Test project stats.
     */
    @Test
    public void testProjectStats()
    {
        UtilityProjectStats.start("src");
    }

    /**
     * Test swing utility.
     */
    @Test
    public void testSwing()
    {
        Assert.assertNotNull(UtilitySwing.createBorderedPanel("test", 1));
        Assert.assertNotNull(UtilitySwing.createDialog(null, "test", 1, 1));
    }
}
