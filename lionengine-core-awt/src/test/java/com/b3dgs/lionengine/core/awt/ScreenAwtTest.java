/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.util.UtilReflection;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link ScreenAwtAbstract}, {@link ScreenWindowedAwt} and {@link ScreenFullAwt}.
 */
public final class ScreenAwtTest
{
    /** Test timeout in milliseconds. */
    private static final long TIMEOUT = 10000L;
    /** Image media. */
    private static final String IMAGE = "image.png";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        EngineAwt.start(ScreenAwtTest.class.getName(), Version.DEFAULT);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test windowed screen.
     * 
     * @throws Exception If error.
     */
    @Test(timeout = TIMEOUT)
    public void testWindowed() throws Exception
    {
        final Config config = new Config(com.b3dgs.lionengine.util.UtilTests.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Medias.create(IMAGE));
        config.setSource(com.b3dgs.lionengine.util.UtilTests.RESOLUTION_320_240);
        testScreen(config);
        testHeadless(config);
    }

    /**
     * Test applet screen.
     * 
     * @throws Exception If error.
     */
    @Test(timeout = TIMEOUT)
    public void testApplet() throws Exception
    {
        final Config config = new Config(com.b3dgs.lionengine.util.UtilTests.RESOLUTION_320_240,
                                         32,
                                         false,
                                         Medias.create(IMAGE));
        config.setApplet(new AppletAwt());
        config.setSource(com.b3dgs.lionengine.util.UtilTests.RESOLUTION_320_240);

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        testScreen(config);
        Verbose.info("****************************************************************************************");
        testHeadless(config);
    }

    /**
     * Test full screen.
     * 
     * @throws Exception If error.
     */
    @Test(timeout = TIMEOUT)
    public void testFullscreen() throws Exception
    {
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported())
        {
            final int width = gd.getDisplayMode().getWidth();
            final int height = gd.getDisplayMode().getHeight();

            final Resolution resolution = new Resolution(width, height, 60);
            final Config config = new Config(resolution, 32, false, Medias.create(IMAGE));
            config.setSource(resolution);

            testScreen(config);
            testHeadless(config);
        }
    }

    /**
     * Test full screen fail.
     * 
     * @throws Exception If error.
     */
    @Test(timeout = TIMEOUT, expected = LionEngineException.class)
    public void testFullscreenFail() throws Exception
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);
        config.setSource(resolution);
        testScreen(config);
        testHeadless(config);
    }

    /**
     * Test screen.
     * 
     * @param config The config to test with.
     */
    private void testScreen(Config config)
    {
        final Screen screen = Graphics.createScreen(config);
        final InputDeviceKeyListener listener = new InputDeviceKeyListener()
        {
            @Override
            public void keyReleased(int keyCode, char keyChar)
            {
                // Mock
            }

            @Override
            public void keyPressed(int keyCode, char keyChar)
            {
                // Mock
            }
        };
        final AtomicBoolean lost = new AtomicBoolean();
        final AtomicBoolean gained = new AtomicBoolean();
        final AtomicBoolean closed = new AtomicBoolean();
        final ScreenListener screenListener = new ScreenListener()
        {
            @Override
            public void notifyFocusLost()
            {
                lost.set(true);
            }

            @Override
            public void notifyFocusGained()
            {
                gained.set(true);
            }

            @Override
            public void notifyClosed()
            {
                closed.set(true);
            }
        };

        Assert.assertFalse(screen.isReady());

        screen.addListener(screenListener);
        screen.start();
        screen.awaitReady();
        screen.addKeyListener(listener);
        screen.preUpdate();
        screen.update();
        screen.showCursor();
        screen.hideCursor();
        screen.requestFocus();
        screen.onSourceChanged(UtilTests.RESOLUTION_320_240);

        Assert.assertNotNull(screen.getConfig());
        Assert.assertNotNull(screen.getGraphic());
        Assert.assertTrue(screen.getReadyTimeOut() > -1L);
        Assert.assertTrue(screen.getX() > -1);
        Assert.assertTrue(screen.getY() > -1);
        Assert.assertTrue(screen.isReady());

        while (config.isWindowed() && !gained.get())
        {
            continue;
        }
        screen.setIcon("void");
        screen.setIcon("image.png");
        if (!config.hasApplet())
        {
            final javax.swing.JFrame frame = (javax.swing.JFrame) UtilReflection.getField(screen, "frame");
            frame.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING));
            while (config.isWindowed() && !gained.get())
            {
                continue;
            }
        }
        screen.dispose();

        Assert.assertEquals(0, screen.getX());
        Assert.assertEquals(0, screen.getY());

        screen.removeListener(screenListener);
    }

    /**
     * Test headless screen.
     * 
     * @param config The config reference.
     * @throws Exception If error.
     */
    private void testHeadless(Config config) throws Exception
    {
        final Object old = UtilReflection.getField(GraphicsEnvironment.class, "headless");
        final Field field = GraphicsEnvironment.class.getDeclaredField("headless");
        UtilReflection.setAccessible(field, true);
        field.set(GraphicsEnvironment.class, Boolean.TRUE);
        try
        {
            Assert.assertNull(Graphics.createScreen(config));
        }
        catch (final LionEngineException exception)
        {
            Assert.assertTrue(exception.getMessage().equals("No available display !"));
        }
        finally
        {
            field.set(GraphicsEnvironment.class, old);
        }
    }
}
