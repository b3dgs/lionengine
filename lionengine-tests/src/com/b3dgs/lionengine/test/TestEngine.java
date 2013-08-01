package com.b3dgs.lionengine.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Ratio;
import com.b3dgs.lionengine.Theme;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.utility.UtilityProjectStats;

/**
 * Test the engine package.
 */
public class TestEngine
{
    /**
     * Test engine class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testDrawable() throws Exception
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
     * Test the loader.
     */
    @Test
    public void testLoader()
    {
        Engine.start("Test", Version.create(1, 0, 0), Media.getPath("resources"));

        final int width = 320;
        final int height = 240;
        final int depth = 16;
        final int rate = 60;
        final boolean windowed = true;

        final Display internal = new Display(width, height, depth, rate);

        Assert.assertEquals(internal.getWidth(), width);
        Assert.assertEquals(internal.getHeight(), height);
        Assert.assertEquals(internal.getDepth(), depth);
        Assert.assertEquals(internal.getRate(), rate);

        final Display external = new Display(width * 2, height * 2, depth, rate);

        Assert.assertEquals(external.getWidth(), width * 2);
        Assert.assertEquals(external.getHeight(), height * 2);
        Assert.assertEquals(external.getDepth(), depth);
        Assert.assertEquals(external.getRate(), rate);

        final Filter filter = Filter.HQ3X;
        final Config config = new Config(internal, external, windowed, filter);

        Assert.assertEquals(internal, config.internal);
        Assert.assertNotSame(external, config.external);
        Assert.assertEquals(filter, config.filter);

        try
        {
            final Loader loader = new Loader(null);
            loader.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success (need initializer and configuration)
        }

        final Filter filter0 = Filter.HQ2X;
        final Config config0 = new Config(internal, external, windowed, filter0);

        Assert.assertEquals(internal, config0.internal);
        Assert.assertNotSame(external, config0.external);
        Assert.assertEquals(filter0, config0.filter);

        final Loader loader0 = new Loader(config0);
        loader0.start(new Scene(loader0));
        Assert.assertNotNull(loader0);

        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
        Assert.assertNotNull(loader);

        try
        {
            loader.start(new Scene(loader));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Config config1 = new Config(internal, external, true, Filter.HQ2X);
        config1.setApplet(null);
        final Loader loader1 = new Loader(config1);
        Assert.assertNotNull(loader1);
        loader1.start(null);

        final Config config2 = new Config(internal, external, false, Filter.HQ3X);
        try
        {
            final Loader loader2 = new Loader(config2);
            Assert.assertNotNull(loader2);
            loader2.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Display internal2 = new Display(640, 480, depth, rate);
        final Display external2 = new Display(1280, 720, depth, rate);
        final Config config3 = new Config(internal2, external2, false);
        try
        {
            final Loader loader3 = new Loader(config3);
            Assert.assertNotNull(loader3);
            loader3.start(null);
        }
        catch (final LionEngineException exception)
        {
            org.junit.Assume.assumeTrue(exception.getMessage(),
                    exception.getMessage().contains("Unsupported fullscreen mode"));
            Verbose.info("Fullscreen mode not supported on test machine - Test skipped");
        }
    }

    /**
     * Test the ratio.
     */
    @Test
    public void testRatio()
    {
        Assert.assertEquals(Ratio.K4_3, Ratio.getRatioFromEnum(Ratio.R4_3), 0.000001);
        Assert.assertEquals(Ratio.K5_4, Ratio.getRatioFromEnum(Ratio.R5_4), 0.000001);
        Assert.assertEquals(Ratio.K16_10, Ratio.getRatioFromEnum(Ratio.R16_10), 0.000001);
        Assert.assertEquals(Ratio.K16_9, Ratio.getRatioFromEnum(Ratio.R16_9), 0.000001);

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
            // Success
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
     * Test project stats.
     */
    @Test
    public void testProjectStats()
    {
        UtilityProjectStats.start("src");
    }
}
