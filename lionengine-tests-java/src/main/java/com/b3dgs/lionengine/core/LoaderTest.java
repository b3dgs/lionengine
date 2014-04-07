/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.awt.Dimension;
import java.awt.Toolkit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

/**
 * Test the loader class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LoaderTest
{
    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Wait after each test.
     */
    @After
    public void waitAfterTest()
    {
        try
        {
            Thread.sleep(100);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
        }
        Engine.terminate();
    }

    /**
     * Test the loader.
     */
    @Test
    public void testLoaderFail()
    {
        Engine.start("LoaderTest", Version.create(1, 0, 0), "resources");
        final Config config = new Config(new Resolution(320, 240, 60), 32, true);
        final Loader loader = new Loader(config);
        try
        {
            loader.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        Engine.terminate();
        try
        {
            loader.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the loader.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLoader() throws InterruptedException
    {
        Engine.start("LoaderTest", Version.create(1, 0, 0), "resources");
        final Config config = new Config(new Resolution(320, 240, 60), 32, true);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        loader.renderer.join();
    }

    /**
     * Test the loader with bilinear.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLoaderBilinear() throws InterruptedException
    {
        Engine.start("LoaderFilterTest", Version.create(1, 0, 0), "resources");
        final Config config = new Config(new Resolution(640, 480, 0), 16, true, Filter.BILINEAR);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        loader.renderer.join();
    }

    /**
     * Test the loader with hq2x.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLoaderHq2x() throws InterruptedException
    {
        Engine.start("LoaderFilterTest", Version.create(1, 0, 0), "resources");
        final Config config = new Config(new Resolution(613, 273, 0), 16, true, Filter.HQ2X);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        loader.renderer.join();
    }

    /**
     * Test the loader with hq3x.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLoaderHq3x() throws InterruptedException
    {
        Engine.start("LoaderFilterTest", Version.create(1, 0, 0), "resources");
        final Config config = new Config(new Resolution(533, 189, 0), 16, true, Filter.HQ3X);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        loader.renderer.join();
    }

    /**
     * Test the loader with fullscreen.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLoaderFullScreen() throws InterruptedException
    {
        Engine.start("LoaderFilterTest", Version.create(1, 0, 0), "resources");

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension size = toolkit.getScreenSize();

        final Config config = new Config(new Resolution((int) size.getWidth(), (int) size.getHeight(), 60), 16, false);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock3.class);
        loader.renderer.join();
    }

    /**
     * Test the loader with hq3x.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testLoaderInterrupt() throws InterruptedException
    {
        Engine.start("LoaderFilterTest", Version.create(1, 0, 0), "resources");
        final Config config = new Config(new Resolution(533, 189, 0), 16, true, Filter.HQ3X);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        loader.renderer.join();
    }
}
