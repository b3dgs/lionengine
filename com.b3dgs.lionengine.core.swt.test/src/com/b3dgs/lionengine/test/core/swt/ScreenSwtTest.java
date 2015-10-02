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
package com.b3dgs.lionengine.test.core.swt;

import java.lang.Thread.State;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.swt.EngineSwt;
import com.b3dgs.lionengine.core.swt.ToolsSwt;

/**
 * Test the screen class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ScreenSwtTest
{
    /** Test timeout in milliseconds. */
    private static final long TIMEOUT = 5000;
    /** Get renderer function. */
    private static final String GET_RENDERER = "getRenderer";
    /** Sequence field. */
    private static final String SEQUENCE = "sequence";
    /** Screen field. */
    private static final String SCREEN = "screen";
    /** Image media. */
    private static final String IMAGE = "image.png";

    /**
     * Prepare test.
     */
    @Before
    public void setUp()
    {
        EngineSwt.start(ScreenSwtTest.class.getName(), Version.DEFAULT, ScreenSwtTest.class);
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
     * Test the windowed screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = TIMEOUT)
    public void testWindowed() throws InterruptedException
    {
        final Config config = new Config(com.b3dgs.lionengine.test.util.Constant.RESOLUTION_320_240, 32, true);
        config.setIcon(Medias.create(IMAGE));

        final Loader loader = new Loader(config);
        final Renderer renderer = UtilReflection.getMethod(loader, GET_RENDERER);

        loader.start(SequenceMock.class);
        while (renderer.getState() == State.RUNNABLE || UtilReflection.getField(renderer, SEQUENCE) == null)
        {
            Thread.sleep(Constant.DECADE);
        }

        final Screen screen = UtilReflection.getField(renderer, SCREEN);
        screen.requestFocus();
        renderer.join();
    }

    /**
     * Test the full screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = TIMEOUT)
    public void testFullscreen() throws InterruptedException
    {
        final Rectangle bounds = new Rectangle(0, 0, 0, 0);
        final Display display = ToolsSwt.getDisplay();
        display.syncExec(() ->
        {
            final Rectangle size = display.getPrimaryMonitor().getBounds();
            bounds.width = size.width;
            bounds.height = size.height;
        });

        final Resolution resolution = new Resolution(bounds.width, bounds.height, 60);
        final Config config = new Config(resolution, 32, false);
        config.setIcon(Medias.create(IMAGE));

        final Loader loader = new Loader(config);
        final Renderer renderer = UtilReflection.getMethod(loader, GET_RENDERER);

        loader.start(SequenceMock.class);
        while (renderer.getState() == State.RUNNABLE || UtilReflection.getField(renderer, SEQUENCE) == null)
        {
            Thread.sleep(Constant.DECADE);
        }

        final Screen screen = UtilReflection.getField(renderer, SCREEN);
        screen.requestFocus();
        renderer.join();
    }

    /**
     * Test the full screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = TIMEOUT)
    public void testFullscreenFail() throws InterruptedException
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);
        final Loader loader = new Loader(config);
        final Renderer renderer = UtilReflection.getMethod(loader, GET_RENDERER);

        final AtomicBoolean uncaught = new AtomicBoolean(false);
        final Thread.UncaughtExceptionHandler handler = (thread, exception) -> uncaught.set(true);
        renderer.setUncaughtExceptionHandler(handler);

        loader.start(SequenceMock.class);
        while (renderer.getState() == State.RUNNABLE)
        {
            Thread.sleep(Constant.DECADE);
        }

        renderer.join();
        Assert.assertTrue(uncaught.get());
    }
}
