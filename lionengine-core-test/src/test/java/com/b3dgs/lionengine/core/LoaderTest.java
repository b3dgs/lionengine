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
package com.b3dgs.lionengine.core;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.test.FactoryGraphicMock;
import com.b3dgs.lionengine.test.MediaMock;
import com.b3dgs.lionengine.test.SequenceArgumentsMock;
import com.b3dgs.lionengine.test.SequenceFailMock;
import com.b3dgs.lionengine.test.SequenceNextFailMock;
import com.b3dgs.lionengine.test.SequenceSingleMock;

/**
 * Test the loader class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LoaderTest
{
    /** Output. */
    private static final Resolution OUTPUT = new Resolution(640, 480, 60);
    /** Config. */
    private static final Config CONFIG = new Config(OUTPUT, 16, true);
    /** Icon. */
    private static final Media ICON = new MediaMock("image.png");

    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Wait for loader to end.
     * 
     * @param loader The loader reference.
     */
    private static void waitEnd(Loader loader)
    {
        try
        {
            loader.getRenderer().join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }
    }

    /**
     * Test the loader with no config.
     */
    @Test(expected = LionEngineException.class)
    public void testNullConfig()
    {
        Assert.assertNotNull(new Loader(null));
    }

    /**
     * Test the loader with no sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testNullSequence()
    {
        final Loader loader = new Loader(CONFIG);
        loader.start(null);
    }

    /**
     * Test the loader with fail sequence.
     */
    @Test(expected = LionEngineException.class)
    public void testFailSequence()
    {
        final Loader loader = new Loader(CONFIG);
        loader.start(SequenceFailMock.class);
    }

    /**
     * Test the loader with fail next sequence.
     */
    @Test
    public void testFailNextSequence()
    {
        final Loader loader = new Loader(CONFIG);
        final AtomicBoolean uncaught = new AtomicBoolean(false);
        final Thread.UncaughtExceptionHandler handler = (thread, exception) -> uncaught.set(true);
        loader.getRenderer().setUncaughtExceptionHandler(handler);
        loader.start(SequenceNextFailMock.class);
        waitEnd(loader);
        Assert.assertTrue(uncaught.get());
    }

    /**
     * Test the loader already started.
     */
    @Test(expected = LionEngineException.class)
    public void testStarted()
    {
        final Loader loader = new Loader(CONFIG);
        loader.start(SequenceSingleMock.class);
        loader.start(SequenceSingleMock.class);
    }

    /**
     * Test the loader with an icon in windowed mode.
     */
    @Test
    public void testIconWindowed()
    {
        final Config config = new Config(OUTPUT, 16, true);
        config.setIcon(ICON);

        final Loader loader = new Loader(config);
        loader.start(SequenceSingleMock.class);
    }

    /**
     * Test the loader with an icon in full screen mode.
     */
    @Test
    public void testIconFullScreen()
    {
        final Config config = new Config(OUTPUT, 16, false);
        config.setIcon(ICON);

        final Loader loader = new Loader(config);
        loader.start(SequenceSingleMock.class);
    }

    /**
     * Test the loader with a single sequence.
     */
    @Test
    public void testSequenceSingle()
    {
        final Loader loader = new Loader(CONFIG);
        loader.start(SequenceSingleMock.class);
        waitEnd(loader);
    }

    /**
     * Test the loader with a sequence that have arguments.
     */
    @Test
    public void testSequenceArgument()
    {
        final Loader loader = new Loader(CONFIG);
        loader.start(SequenceArgumentsMock.class, new Object());
        waitEnd(loader);
    }

    /**
     * Test the loader with a bilinear filter.
     */
    @Test
    public void testFilterBilinear()
    {
        final Resolution output = new Resolution(320, 240, 0);
        final Config config = new Config(output, 16, true, Filter.BILINEAR);
        final Loader loader = new Loader(config);
        loader.start(SequenceSingleMock.class);
        waitEnd(loader);
    }

    /**
     * Test the loader with a bilinear filter and screen scaled.
     */
    @Test
    public void testFilterBilinearScaled()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, true, Filter.BILINEAR);
        final Loader loader = new Loader(config);
        loader.start(SequenceSingleMock.class);
        waitEnd(loader);
    }

    /**
     * Test the loader with a hq2x filter.
     */
    @Test
    public void testFilterHq2x()
    {
        final Resolution output = new Resolution(640, 480, 0);
        final Config config = new Config(output, 16, false, Filter.HQ2X);
        final Loader loader = new Loader(config);
        loader.start(SequenceSingleMock.class);
        waitEnd(loader);
    }

    /**
     * Test the loader with a hq3x filter.
     */
    @Test
    public void testFilterHq3x()
    {
        final Resolution output = new Resolution(960, 720, 60);
        final Config config = new Config(output, 16, false, Filter.HQ3X);
        final Loader loader = new Loader(config);
        loader.start(SequenceSingleMock.class);
        waitEnd(loader);
    }
}
