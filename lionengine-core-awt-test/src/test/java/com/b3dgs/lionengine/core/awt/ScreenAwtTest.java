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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.Thread.State;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.core.Screen;

/**
 * Test the screen class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ScreenAwtTest
{
    /** Exception thread flag. */
    boolean uncaught;

    /**
     * Prepare test.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Before
    public void setUp() throws ReflectiveOperationException
    {
        Engine.start("screen", Version.create(0, 0, 0), ScreenAwtTest.class);
    }

    /**
     * Clean up test.
     */
    @After
    public void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Get the loader renderer by reflection.
     * 
     * @param loader The loader to use.
     * @return The renderer found.
     * @throws LionEngineException If reflection error.
     */
    private static Renderer getRenderer(Loader loader) throws LionEngineException
    {
        try
        {
            final Method method = loader.getClass().getDeclaredMethod("getRenderer");
            method.setAccessible(true);
            return (Renderer) method.invoke(loader);
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Get the screen by reflection.
     * 
     * @param renderer The loader to use.
     * @return The screen found.
     * @throws LionEngineException If reflection error.
     */
    private static Screen getScreen(Renderer renderer) throws LionEngineException
    {
        try
        {
            final Field field = Renderer.class.getDeclaredField("screen");
            field.setAccessible(true);
            return (Screen) field.get(renderer);
        }
        catch (final ReflectiveOperationException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Test the windowed screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testWindowed() throws InterruptedException
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        final Config config = new Config(resolution, 32, true);
        config.setIcon(Medias.create("image.png"));
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        final Renderer renderer = getRenderer(loader);
        while (renderer.getState() == State.RUNNABLE)
        {
            Thread.sleep(10);
        }
        final Screen screen = getScreen(renderer);
        screen.start();
        screen.requestFocus();
        renderer.join();
    }

    /**
     * Test the applet screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testApplet() throws InterruptedException
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        final Config config = new Config(resolution, 32, false);
        config.setIcon(Medias.create("image.png"));
        config.setApplet(new AppletAwt());
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        final Renderer renderer = getRenderer(loader);

        final Thread.UncaughtExceptionHandler handler = (t, exception) -> uncaught = true;
        renderer.setUncaughtExceptionHandler(handler);

        while (renderer.getState() == State.RUNNABLE)
        {
            Thread.sleep(10);
        }
        final Screen screen = getScreen(renderer);
        screen.requestFocus();
        renderer.join();
        Assert.assertTrue(uncaught);
        uncaught = false;
    }

    /**
     * Test the full screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testFullscreen() throws InterruptedException
    {
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final int width = gd.getDisplayMode().getWidth();
        final int height = gd.getDisplayMode().getHeight();

        final Resolution resolution = new Resolution(width, height, 60);
        final Config config = new Config(resolution, 32, false);
        config.setIcon(Medias.create("image.png"));
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        final Renderer renderer = getRenderer(loader);
        while (renderer.getState() == State.RUNNABLE)
        {
            Thread.sleep(10);
        }
        final Screen screen = getScreen(renderer);
        screen.start();
        screen.requestFocus();
        renderer.join();
    }

    /**
     * Test the full screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testFullscreenFail() throws InterruptedException
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        final Renderer renderer = getRenderer(loader);

        final Thread.UncaughtExceptionHandler handler = (t, exception) -> uncaught = true;
        renderer.setUncaughtExceptionHandler(handler);

        while (renderer.getState() == State.RUNNABLE)
        {
            Thread.sleep(10);
        }
        renderer.join();
        Assert.assertTrue(uncaught);
        uncaught = false;
    }
}
