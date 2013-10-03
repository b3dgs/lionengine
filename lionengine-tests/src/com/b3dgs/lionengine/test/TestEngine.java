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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Ratio;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Theme;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.ColorRgba;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.TextStyle;
import com.b3dgs.lionengine.core.Transparency;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.utility.UtilityImage;

/**
 * Test the engine package.
 */
public class TestEngine
{
    /** Default test width. */
    private final int width = 320;
    /** Default test height. */
    private final int height = 240;
    /** Default test rate. */
    private final int rate = 60;
    /** Default test windowed flag. */
    private final boolean windowed = true;
    /** Graphic test output. */
    private Graphic g;

    /**
     * Test the display creation function.
     * 
     * @param width The width.
     * @param height The height.
     * @param rate The rate.
     */
    private static void testResolutionCreation(int width, int height, int rate)
    {
        try
        {
            final Resolution resolution = new Resolution(width, height, rate);
            Assert.assertNotNull(resolution);
            Assert.fail();
        }
        catch (final IllegalArgumentException exception)
        {
            // Success
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the resolution configuration.
     * 
     * @param resolution The resolution to test.
     * @param factor The factor used.
     */
    private void testResolution(Resolution resolution, int factor)
    {
        Assert.assertEquals(resolution.getWidth(), width * factor);
        Assert.assertEquals(resolution.getHeight(), height * factor);
        Assert.assertEquals(resolution.getRate(), rate);
    }

    /**
     * Test the config container.
     * 
     * @param config The config to test.
     * @param output The output resolution.
     * @param filter The filter.
     */
    private static void testConfig(Config config, Resolution output, Filter filter)
    {
        Assert.assertEquals(output, config.getOutput());
        Assert.assertEquals(filter, config.getFilter());
    }

    /**
     * Test the loader creation function.
     * 
     * @param config The config used.
     * @return The loader instance.
     */
    private static Loader testLoaderCreation(Config config)
    {
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
        Assert.assertNotNull(loader);
        return loader;
    }

    /**
     * Test the loader creation function.
     * 
     * @param config The config used.
     * @return The loader instance.
     */
    private static Loader testLoaderCreationWithNullScene(Config config)
    {
        final Loader loader = new Loader(config);
        loader.start(null);
        Assert.assertNotNull(loader);
        return loader;
    }

    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        Engine.start("UnitTest", Version.create(1, 0, 0), Media.getPath("resources"));
        g = UtilityImage.createImageBuffer(100, 100, Transparency.OPAQUE).createGraphic();
    }

    /**
     * Test check class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testCheck() throws Exception
    {
        final Constructor<Check> constructor = Check.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Check check = constructor.newInstance();
            Assert.assertNotNull(check);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test engine class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEngine() throws Exception
    {
        final Constructor<Engine> constructor = Engine.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Engine engine = constructor.newInstance();
            Assert.assertNotNull(engine);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test the display class.
     */
    @Test
    public void testDisplay()
    {
        TestEngine.testResolutionCreation(0, 0, -1);
        TestEngine.testResolutionCreation(0, 1, -1);
        TestEngine.testResolutionCreation(0, 0, -1);
        TestEngine.testResolutionCreation(0, 0, 1);
        TestEngine.testResolutionCreation(0, 1, 1);
        TestEngine.testResolutionCreation(0, 0, 1);
        TestEngine.testResolutionCreation(1, 0, -1);
        TestEngine.testResolutionCreation(1, 1, -1);
        TestEngine.testResolutionCreation(1, 1, -1);
    }

    /**
     * Test the loader.
     */
    @Test
    public void testLoader()
    {
        Engine.terminate();
        Engine.start("Test", Version.create(1, 0, 0), Media.getPath("resources"));

        final Resolution internal = new Resolution(width, height, rate);
        testResolution(internal, 1);

        final Resolution output2 = new Resolution(width * 2, height * 2, rate);
        testResolution(output2, 2);

        final Filter filter = Filter.HQ3X;
        final Config config = new Config(output2, 16, windowed, filter);
        TestEngine.testConfig(config, output2, filter);

        try
        {
            TestEngine.testLoaderCreationWithNullScene(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success (need initializer and configuration)
        }

        final Filter filter0 = Filter.HQ2X;
        final Config config0 = new Config(output2, 16, windowed, filter0);
        TestEngine.testConfig(config0, output2, filter0);
        TestEngine.testLoaderCreation(config);
        try
        {
            final Loader loader = TestEngine.testLoaderCreation(config0);
            loader.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Config config1 = new Config(output2, 16, true, Filter.HQ2X);
        config1.setApplet(null);

        TestEngine.testLoaderCreationWithNullScene(config1);

        final Config config2 = new Config(output2, 32, false, Filter.HQ3X);
        TestEngine.testLoaderCreationWithNullScene(config2);

        final Resolution external2 = new Resolution(1280, 720, 0);
        final Config config3 = new Config(external2, 16, false, Filter.BILINEAR);
        try
        {
            TestEngine.testLoaderCreationWithNullScene(config3);
        }
        catch (final LionEngineException exception)
        {
            Assume.assumeTrue(exception.getMessage(), exception.getMessage().contains("Unsupported fullscreen mode"));
            Verbose.info("Fullscreen mode not supported on test machine - Test skipped");
        }

        final Resolution external3 = new Resolution(width, height, 0);
        final Config config4 = new Config(external3, 16, true, Filter.BILINEAR);
        final Loader loader = new Loader(config4);
        loader.start(new Scene(loader));

        final Resolution external4 = new Resolution(width, height * 2, 0);
        final Config config5 = new Config(external4, 16, true, Filter.NONE);
        final Loader loader1 = new Loader(config5);
        final Scene scene = new Scene(loader1);
        scene.setExtrapolated(true);
        loader1.start(scene);

        final Resolution external5 = new Resolution(width * 2, height, 0);
        final Config config6 = new Config(external5, 32, true, Filter.NONE);
        final Loader loader2 = new Loader(config6);
        loader2.start(new Scene(loader2));

        try
        {
            final Config config7 = new Config(external3, 32, false);
            final Loader loader3 = new Loader(config7);
            loader3.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the ratio.
     */
    @Test
    public void testRatio()
    {
        Assert.assertTrue(Ratio.equals(Ratio.R16_10, Ratio.R16_10));
    }

    /**
     * Test the media.
     */
    @Test
    public void testMedia()
    {
        try
        {
            final Media media = new Media(null);
            media.getPath();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success (need initializer and configuration)
        }

        try
        {
            final Media media = Media.get((String[]) null);
            media.getPath();
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success (need initializer and configuration)
        }
    }

    /**
     * Test the theme.
     */
    @Test
    public void testTheme()
    {
        Theme.set(Theme.METAL);
        Theme.set(Theme.MOTIF);
        Theme.set(Theme.SYSTEM);
        try
        {
            Theme.set(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertNotNull(exception.getMessage());
            Verbose.info("----- Print stacktrace -----");
            exception.printStackTrace();
            Verbose.info("----------------------------");
        }
    }

    /**
     * Test the timing class.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testTiming() throws InterruptedException
    {
        final Timing timer = new Timing();
        Assert.assertFalse(timer.isStarted());
        Assert.assertTrue(timer.elapsed() == 0);
        Assert.assertFalse(timer.elapsed(1000L));
        timer.start();
        Assert.assertTrue(timer.isStarted());
        Thread.sleep(100);
        Assert.assertTrue(timer.isStarted());
        Assert.assertTrue(timer.elapsed(50));
        Assert.assertTrue(timer.elapsed() >= 50);
        timer.pause();
        Thread.sleep(50);
        timer.unpause();
        Assert.assertFalse(timer.elapsed(2000));
        timer.stop();
        Assert.assertFalse(timer.isStarted());
        Assert.assertTrue(timer.get() >= 0);
    }

    /**
     * Test the operating system related class.
     */
    @Test
    public void testOperatingSystem()
    {
        Assert.assertNotNull(OperatingSystem.getArchitecture());
        Assert.assertNotNull(OperatingSystem.getOperatingSystem());
        Assert.assertNotNull(OperatingSystem.MAC);
        Assert.assertNotNull(OperatingSystem.WINDOWS);
        Assert.assertNotNull(OperatingSystem.UNIX);
        Assert.assertNotNull(OperatingSystem.UNKNOWN);
        Assert.assertNotNull(OperatingSystem.SOLARIS);

        Assert.assertNotNull(Architecture.X64);
        Assert.assertNotNull(Architecture.X86);
        Assert.assertNotNull(Architecture.UNKNOWN);
    }

    /**
     * Test the text class.
     */
    @Test
    public void testText()
    {
        final Text text1 = UtilityImage.createText(Text.DIALOG, 12, TextStyle.NORMAL);
        final Text text2 = UtilityImage.createText(Text.DIALOG, 12, TextStyle.BOLD);
        final Text text3 = UtilityImage.createText(Text.DIALOG, 12, TextStyle.ITALIC);
        final String text = "test";

        text1.draw(g, 0, 0, text);
        text2.draw(g, 0, 0, text);
        text3.draw(g, 0, 0, text);
        text1.draw(g, 0, 0, Align.CENTER, text);
        text1.draw(g, 0, 0, Align.LEFT, text);
        text1.draw(g, 0, 0, Align.RIGHT, text);
        text1.setAlign(Align.CENTER);
        text1.setColor(ColorRgba.BLACK);
        text1.setLocation(1, 5);
        text1.setText(text);
        Assert.assertEquals(12, text1.getSize());
        Assert.assertEquals(1, text1.getLocationX());
        Assert.assertEquals(5, text1.getLocationY());
        Assert.assertTrue(text1.getWidth() == 0);
        Assert.assertTrue(text1.getHeight() == 0);
        text1.render(g);
        text1.render(g);
        Assert.assertTrue(text1.getWidth() > 0);
        Assert.assertTrue(text1.getHeight() > 0);
    }
}
