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
import com.b3dgs.lionengine.utility.UtilityRandom;

/**
 * Test checksum package.
 */
public class TestUtility
{
    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
    }

    /**
     * Test images functions.
     */
    @Test
    public void testImage()
    {
        final BufferedImage bufferedImage = UtilityImage.createBufferedImage(16, 16, Transparency.OPAQUE);
        try
        {
            final BufferedImage image = UtilityImage.createBufferedImage(-1, -1, Transparency.OPAQUE);
            image.flush();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final VolatileImage volatileImage = UtilityImage.createVolatileImage(16, 16, Transparency.OPAQUE);
        try
        {
            final VolatileImage image = UtilityImage.createVolatileImage(-1, -1, Transparency.OPAQUE);
            image.flush();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Assert.assertEquals(bufferedImage.getWidth(), volatileImage.getWidth());
        Assert.assertEquals(bufferedImage.getHeight(), volatileImage.getHeight());

        final BufferedImage image0 = UtilityImage.getBufferedImage(Media.get("dot.png"), true);
        final BufferedImage image1 = UtilityImage.getBufferedImage(Media.get("dot.png"), false);
        final VolatileImage image2 = UtilityImage.getVolatileImage(Media.get("dot.png"), Transparency.OPAQUE);

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
        Assert.assertEquals(11, files.length);
        Assert.assertEquals(0, UtilityFile.getFilesList(Media.get("null").getPath()).length);
        Assert.assertEquals(1, UtilityFile.getFilesList(Media.get("").getPath(), "txt").length);
        Assert.assertEquals(0, UtilityFile.getFilesList(Media.get("null").getPath(), "txt").length);

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
        Assert.assertEquals(0, UtilityMath.fixBetween(-10, 0, 10));
        Assert.assertEquals(10, UtilityMath.fixBetween(50, 0, 10));
        Assert.assertEquals(0.0, UtilityMath.fixBetween(-10.0, 0.0, 10.0), 0.000001);
        Assert.assertEquals(10.0, UtilityMath.fixBetween(50.0, 0.0, 10.0), 0.000001);

        Assert.assertTrue(UtilityMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        Assert.assertTrue(UtilityMath.curveValue(0.0, -1.0, 0.5) < 0.0);

        final Line2D line1 = new Line2D.Double(1.0, -1.0, 1.0, 1.0);
        final Line2D line2 = new Line2D.Double(0.0, 0.0, 2.0, 0.0);
        final Point2D point = new Point2D.Double(1.0, 0.0);
        Assert.assertEquals(point, UtilityMath.intersection(line1, line2));

        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 6, 6));
        Assert.assertEquals(2.0, UtilityMath.getDistance(4.0, 6.0, 6.0, 6.0), 0.000001);
        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 2, 2, 6, 6, 2, 2));

        Assert.assertEquals(0.0, UtilityMath.wrapDouble(360.0, 0.0, 360.0), 0.000001);
        Assert.assertEquals(359.0, UtilityMath.wrapDouble(-1, 0.0, 360.0), 0.000001);

        Assert.assertEquals(-1.0, UtilityMath.cos(180), 0.000001);
        Assert.assertEquals(0.0, UtilityMath.sin(180), 0.000001);

        UtilityMath.time();
        UtilityMath.nano();
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
}
