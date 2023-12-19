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
package com.b3dgs.lionengine.swt.graphic;

import static com.b3dgs.lionengine.swt.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.swt.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.swt.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.swt.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.widgets.Monitor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;
import com.b3dgs.lionengine.swt.UtilAssert;

/**
 * Test {@link ScreenSwtAbstract}, {@link ScreenWindowedSwt} and {@link ScreenFullSwt}.
 */
public final class ScreenSwtTest
{
    /** Image media. */
    private static final String IMAGE = "image.png";

    /**
     * Check multiple display capability.
     */
    public static void checkMultipleDisplaySupport()
    {
        try
        {
            assertNotNull(ToolsSwt.getDisplay());
        }
        catch (final SWTError error)
        {
            Assumptions.assumeTrue(error.getMessage().contains("Not implemented [multiple displays]"),
                                   error.getMessage());
        }
    }

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        EngineSwt.start(ScreenSwtTest.class.getName(), new Version(1, 0, 0), ScreenSwtTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Engine.terminate();
    }

    /**
     * Test the windowed screen.
     */
    @Test
    void testWindowed()
    {
        final Config config = new Config(UtilAssert.RESOLUTION_320_240, 32, true, Arrays.asList(Medias.create(IMAGE)));

        testScreen(config);
    }

    /**
     * Test the full screen.
     */
    @Test
    void testFullscreen()
    {
        final Monitor monitor = ToolsSwt.getDisplay().getPrimaryMonitor();

        final Resolution resolution = new Resolution(monitor.getBounds().width, monitor.getBounds().height, 60);
        final Config config = new Config(resolution, 32, false, Arrays.asList(Medias.create(IMAGE)));

        testScreen(config);
    }

    /**
     * Test the windowed with wrong resolution.
     */
    @Test
    void testWindowedFail()
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, true);
        final Screen screen = Graphics.createScreen(config);
        try
        {
            screen.start();
        }
        catch (final LionEngineException exception)
        {
            assertTrue(ScreenWindowedSwt.ERROR_WINDOWED.equals(exception.getMessage()));
        }
        catch (final Throwable exception)
        {
            assertEquals("Argument not valid", exception.getMessage());
        }
    }

    /**
     * Test the full screen with wrong resolution.
     */
    @Test
    void testFullscreenFail()
    {
        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);
        final Screen screen = Graphics.createScreen(config);
        try
        {
            screen.start();
        }
        catch (final LionEngineException exception)
        {
            assertTrue(ScreenFullSwt.ERROR_FULL_SCREEN.equals(exception.getMessage()));
        }
        catch (final Throwable exception)
        {
            assertEquals("Argument not valid", exception.getMessage());
        }
    }

    /**
     * Test the screen.
     * 
     * @param config The config to test with.
     */
    private void testScreen(Config config)
    {
        final Screen screen = Graphics.createScreen(config);
        screen.addKeyListener(new InputDeviceKeyListener()
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
        });
        assertFalse(screen.isReady());

        try
        {
            screen.start();
        }
        catch (final Throwable exception)
        {
            Assumptions.assumeFalse("Not implemented [multiple displays]".equals(exception.getMessage()));
        }
        screen.awaitReady();
        screen.preUpdate();
        screen.update();
        screen.showCursor();
        screen.hideCursor();
        screen.update();
        screen.requestFocus();
        screen.onSourceChanged(UtilAssert.RESOLUTION_320_240);

        assertNotNull(screen.getConfig());
        assertNotNull(screen.getGraphic());
        assertTrue(screen.getReadyTimeOut() > -1L);
        assertTrue(screen.getX() > -1);
        assertTrue(screen.getY() > -1);
        assertTrue(screen.isReady());

        final AtomicBoolean focus = new AtomicBoolean();
        final AtomicBoolean disposed = new AtomicBoolean();
        screen.addListener(new ScreenListener()
        {
            @Override
            public void notifyFocusGained()
            {
                focus.set(true);
            }

            @Override
            public void notifyFocusLost()
            {
                focus.set(false);
            }

            @Override
            public void notifyClosed()
            {
                disposed.set(true);
            }
        });

        ((ScreenSwtAbstract) screen).focusGained(null);
        assertTrue(focus.get());

        ((ScreenSwtAbstract) screen).focusLost(null);
        assertFalse(focus.get());

        screen.dispose();
        assertTrue(disposed.get());

        screen.setIcons(Collections.emptyList());
        screen.addKeyListener(new InputDeviceKeyListener()
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
        });
        screen.update();
        screen.requestFocus();
        screen.hideCursor();
        screen.showCursor();

        assertEquals(0, screen.getX());
        assertEquals(0, screen.getY());
    }
}
