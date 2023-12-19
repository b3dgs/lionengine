/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.awt.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsPrefix;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;

/**
 * Test {@link ScreenAwtAbstract}, {@link ScreenWindowedAwt} and {@link ScreenFullAwt}.
 */
final class ScreenAwtTest
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenAwtTest.class);

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicAwt());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
        Engine.terminate();
    }

    /**
     * Test with engine
     */
    @Test
    void testEngineWindowed()
    {
        Medias.setLoadFromJar(ScreenAwtTest.class);

        final Config config = new Config(UtilTests.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Arrays.asList(Medias.create("image.png")));
        EngineAwt.start(ScreenAwtTest.class.getSimpleName(), new Version(1, 0, 0), ScreenAwtTest.class);
        testScreen(config);
    }

    /**
     * Test windowed screen.
     */
    @Test
    void testWindowed()
    {
        Medias.setLoadFromJar(ScreenAwtTest.class);

        final Config config = new Config(UtilTests.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Arrays.asList(Medias.create("image.png")));
        testScreen(config);
    }

    /**
     * Test full screen.
     */
    @Test
    void testFullscreen()
    {
        Medias.setLoadFromJar(ScreenAwtTest.class);

        final DisplayMode res = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                   .getDefaultScreenDevice()
                                                   .getDisplayModes()[0];
        final Config config = new Config(new Resolution(res.getWidth(), res.getHeight(), res.getRefreshRate()),
                                         res.getBitDepth(),
                                         false,
                                         Arrays.asList(Medias.create("image.png")));
        try
        {
            testScreen(config);
        }
        catch (final LionEngineException exception)
        {
            // Skip test
            if (!ScreenFullAwt.ERROR_SWITCH.equals(exception.getMessage()))
            {
                throw exception;
            }
        }
    }

    /**
     * Test full screen fail.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    void testFullscreenFail() throws ReflectiveOperationException
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);

        assertThrowsPrefix(() -> testScreen(config), ScreenFullAwt.ERROR_UNSUPPORTED_FULLSCREEN);
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

        assertTimeout(10_000L, () ->
        {
            while (config.isWindowed() && !gained.get())
            {
                // continue
            }
        });
        assertThrows(() -> screen.setIcons(Arrays.asList(Medias.create("void"))), "[void] Cannot open the media !");
        screen.setIcons(Arrays.asList(Medias.create("image.png")));

        final javax.swing.JFrame frame = (javax.swing.JFrame) UtilTests.getField(screen, "frame");
        frame.dispatchEvent(new java.awt.event.WindowEvent(frame, java.awt.event.WindowEvent.WINDOW_CLOSING));

        assertTimeout(10_000L, () ->
        {
            while (config.isWindowed() && !gained.get())
            {
                // continue
            }
        });

        screen.dispose();

        LOGGER.info("*********************************** EXPECTED VERBOSE ***********************************");
        assertEquals(0, screen.getX());
        assertEquals(0, screen.getY());
        LOGGER.info("****************************************************************************************");

        screen.removeListener(screenListener);
    }
}
