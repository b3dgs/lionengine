package com.b3dgs.lionengine.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.OperatingSystem;
import com.b3dgs.lionengine.Ratio;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.Theme;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
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
    /** Default test depth. */
    private final int depth = 16;
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
     * @param depth The depth.
     * @param rate The rate.
     */
    private static void testDisplayCreation(int width, int height, int depth, int rate)
    {
        try
        {
            final Display display1 = new Display(width, height, depth, rate);
            Assert.assertNotNull(display1);
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
     * Test the display configuration.
     * 
     * @param display The display to test.
     * @param factor The factor used.
     */
    private void testDisplay(Display display, int factor)
    {
        Assert.assertEquals(display.getWidth(), width * factor);
        Assert.assertEquals(display.getHeight(), height * factor);
        Assert.assertEquals(display.getDepth(), depth);
        Assert.assertEquals(display.getRate(), rate);
    }

    /**
     * Test the config container.
     * 
     * @param config The config to test.
     * @param internal The internal display.
     * @param external The external display.
     * @param filter The filter.
     */
    private static void testConfig(Config config, Display internal, Display external, Filter filter)
    {
        Assert.assertEquals(internal, config.internal);
        Assert.assertNotSame(external, config.external);
        Assert.assertEquals(filter, config.filter);
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
        final Graphics2D g2d = UtilityImage.createBufferedImage(100, 100, Transparency.OPAQUE).createGraphics();
        g = new Graphic(g2d);
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
        TestEngine.testDisplayCreation(0, 0, 0, -1);
        TestEngine.testDisplayCreation(0, 1, 0, -1);
        TestEngine.testDisplayCreation(0, 0, 1, -1);
        TestEngine.testDisplayCreation(0, 0, 0, 1);
        TestEngine.testDisplayCreation(0, 1, 1, 1);
        TestEngine.testDisplayCreation(0, 0, 1, 1);
        TestEngine.testDisplayCreation(1, 0, 0, -1);
        TestEngine.testDisplayCreation(1, 1, 0, -1);
        TestEngine.testDisplayCreation(1, 1, 1, -1);
    }

    /**
     * Test the loader.
     */
    @Test
    public void testLoader()
    {
        Engine.start("Test", Version.create(1, 0, 0), Media.getPath("resources"));

        final Display internal = new Display(width, height, depth, rate);
        testDisplay(internal, 1);

        final Display external = new Display(width * 2, height * 2, depth, rate);
        testDisplay(external, 2);

        final Filter filter = Filter.HQ3X;
        final Config config = new Config(internal, external, windowed, filter);
        TestEngine.testConfig(config, internal, external, filter);

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
        final Config config0 = new Config(internal, external, windowed, filter0);
        TestEngine.testConfig(config0, internal, external, filter0);
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

        final Config config1 = new Config(internal, external, true, Filter.HQ2X);
        config1.setApplet(null);

        TestEngine.testLoaderCreationWithNullScene(config1);

        final Config config2 = new Config(internal, external, false, Filter.HQ3X);
        try
        {
            TestEngine.testLoaderCreationWithNullScene(config2);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Display internal2 = new Display(640, 480, depth, rate);
        final Display external2 = new Display(1280, 720, depth, 0);
        final Config config3 = new Config(internal2, external2, false, Filter.BILINEAR);
        try
        {
            TestEngine.testLoaderCreationWithNullScene(config3);
        }
        catch (final LionEngineException exception)
        {
            Assume.assumeTrue(exception.getMessage(), exception.getMessage().contains("Unsupported fullscreen mode"));
            Verbose.info("Fullscreen mode not supported on test machine - Test skipped");
        }

        final Display external3 = new Display(width, height, depth, 0);
        final Config config4 = new Config(internal, external3, true, Filter.BILINEAR);
        final Loader loader = new Loader(config4);
        loader.start(new Scene(loader));

        final Display external4 = new Display(width, height * 2, depth, 0);
        final Config config5 = new Config(internal, external4, true, Filter.NONE);
        final Loader loader1 = new Loader(config5);
        final Scene scene = new Scene(loader1);
        scene.setExtrapolated(true);
        loader1.start(scene);

        final Display external5 = new Display(width * 2, height, depth, 0);
        final Config config6 = new Config(internal, external5, true, Filter.NONE);
        final Loader loader2 = new Loader(config6);
        loader2.start(new Scene(loader2));

        try
        {
            final Config config7 = new Config(internal, external3, false);
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
        final double precision = 0.000001;
        Assert.assertEquals(Ratio.K4_3, Ratio.getRatioFromEnum(Ratio.R4_3), precision);
        Assert.assertEquals(Ratio.K5_4, Ratio.getRatioFromEnum(Ratio.R5_4), precision);
        Assert.assertEquals(Ratio.K16_10, Ratio.getRatioFromEnum(Ratio.R16_10), precision);
        Assert.assertEquals(Ratio.K16_9, Ratio.getRatioFromEnum(Ratio.R16_9), precision);

        Assert.assertEquals(Ratio.R4_3, Ratio.getRatioFromValue(4, 3));
        Assert.assertEquals(Ratio.R5_4, Ratio.getRatioFromValue(5, 4));
        Assert.assertEquals(Ratio.R16_10, Ratio.getRatioFromValue(16, 10));
        Assert.assertEquals(Ratio.R16_9, Ratio.getRatioFromValue(16, 9));
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
            exception.printStackTrace();
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
        final Text text1 = new Text(Font.DIALOG, 12, Text.NORMAL);
        final Text text2 = new Text(Font.DIALOG, 12, Text.BOLD);
        final Text text3 = new Text(Font.DIALOG, 12, Text.ITALIC);
        final String text = "test";

        text1.draw(g, 0, 0, text);
        text2.draw(g, 0, 0, text);
        text3.draw(g, 0, 0, text);
        text1.draw(g, 0, 0, Align.CENTER, text);
        text1.draw(g, 0, 0, Align.LEFT, text);
        text1.draw(g, 0, 0, Align.RIGHT, text);
        text1.setAlign(Align.CENTER);
        text1.setColor(Color.BLACK);
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
