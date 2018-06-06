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
package com.b3dgs.lionengine.awt.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsPrefix;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;

/**
 * Test {@link ScreenAwtAbstract}, {@link ScreenWindowedAwt} and {@link ScreenFullAwt}.
 */
public final class ScreenAwtTest
{
    /** Image media. */
    private static final String IMAGE = "image.png";

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        EngineAwt.start(ScreenAwtTest.class.getName(), Version.DEFAULT);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Engine.terminate();
    }

    /**
     * Test windowed screen.
     */
    @Test
    public void testWindowed()
    {
        final Config config = new Config(com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Medias.create(IMAGE));

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

        assertTimeout(10_000L, () -> testScreen(config));

        Verbose.info("****************************************************************************************");

        assertTimeout(10_000L, () -> testHeadless(config));
    }

    /**
     * Test full screen.
     */
    @Test
    public void testFullscreen()
    {
        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported())
        {
            final int width = gd.getDisplayMode().getWidth();
            final int height = gd.getDisplayMode().getHeight();

            final Resolution resolution = new Resolution(width, height, 60);
            final Config config = new Config(resolution, 32, false, Medias.create(IMAGE));

            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");

            assertTimeout(10_000L, () -> testScreen(config));

            Verbose.info("****************************************************************************************");

            assertTimeout(10_000L, () -> testHeadless(config));
        }
    }

    /**
     * Test full screen fail.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testFullscreenFail() throws ReflectiveOperationException
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);

        assertThrowsPrefix(() -> testScreen(config), ScreenFullAwt.ERROR_UNSUPPORTED_FULLSCREEN);
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

        assertFalse(screen.isReady());

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

        assertNotNull(screen.getConfig());
        assertNotNull(screen.getGraphic());
        assertTrue(screen.getReadyTimeOut() > -1L);
        assertTrue(screen.getX() > -1);
        assertTrue(screen.getY() > -1);
        assertTrue(screen.isReady());

        while (config.isWindowed() && !gained.get())
        {
            continue;
        }
        screen.setIcon("void");
        screen.setIcon("image.png");

        final javax.swing.JFrame frame = (javax.swing.JFrame) UtilReflection.getField(screen, "frame");
        frame.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING));
        while (config.isWindowed() && !gained.get())
        {
            continue;
        }

        screen.dispose();

        assertEquals(0, screen.getX());
        assertEquals(0, screen.getY());

        screen.removeListener(screenListener);
    }

    /**
     * Test headless screen.
     * 
     * @param config The config reference.
     * @throws ReflectiveOperationException If error.
     */
    private void testHeadless(Config config) throws ReflectiveOperationException
    {
        final Object old = UtilReflection.getField(GraphicsEnvironment.class, "headless");
        final Field field = GraphicsEnvironment.class.getDeclaredField("headless");
        UtilReflection.setAccessible(field, true);
        field.set(GraphicsEnvironment.class, Boolean.TRUE);
        try
        {
            assertThrows(() -> Graphics.createScreen(config), "No available display !");
        }
        finally
        {
            field.set(GraphicsEnvironment.class, old);
        }
    }
}
