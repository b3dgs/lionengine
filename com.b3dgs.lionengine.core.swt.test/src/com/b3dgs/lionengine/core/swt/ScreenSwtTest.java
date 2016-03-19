/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.swt;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWTError;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.InputDeviceKeyListener;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.ScreenListener;
import com.b3dgs.lionengine.core.Version;

/**
 * Test the screen class.
 */
public class ScreenSwtTest
{
    /** Test timeout in milliseconds. */
    private static final long TIMEOUT = 5000L;
    /** Image media. */
    private static final String IMAGE = "image.png";
    /** Error multiple display. */
    private static final String ERROR_MULTIPLE_DISPLAY = "Not implemented [multiple displays]";

    /**
     * Check multiple display capability.
     */
    public static void checkMultipleDisplaySupport()
    {
        try
        {
            Assert.assertNotNull(ToolsSwt.getDisplay());
        }
        catch (final SWTError error)
        {
            Assume.assumeFalse(ERROR_MULTIPLE_DISPLAY, ERROR_MULTIPLE_DISPLAY.contains(error.getMessage()));
        }
    }

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        EngineSwt.start(ScreenSwtTest.class.getName(), Version.DEFAULT, ScreenSwtTest.class);
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
     * Test the windowed screen.
     */
    @Test(timeout = TIMEOUT)
    public void testWindowed()
    {
        checkMultipleDisplaySupport();

        final Config config = new Config(com.b3dgs.lionengine.test.Constant.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Medias.create(IMAGE));
        testScreen(config);
    }

    /**
     * Test the full screen.
     */
    @Test(timeout = TIMEOUT)
    public void testFullscreen()
    {
        checkMultipleDisplaySupport();

        final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported())
        {
            final int width = gd.getDisplayMode().getWidth();
            final int height = gd.getDisplayMode().getHeight();

            final Resolution resolution = new Resolution(width, height, 60);
            final Config config = new Config(resolution, 32, false, Medias.create(IMAGE));

            testScreen(config);
        }
    }

    /**
     * Test the windowed with wrong resolution.
     */
    @Test(timeout = TIMEOUT, expected = LionEngineException.class)
    public void testWindowedFail()
    {
        checkMultipleDisplaySupport();

        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, true);
        testScreen(config);
    }

    /**
     * Test the full screen with wrong resolution.
     */
    @Test(timeout = TIMEOUT, expected = LionEngineException.class)
    public void testFullscreenFail()
    {
        checkMultipleDisplaySupport();

        final Resolution resolution = new Resolution(Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        final Config config = new Config(resolution, 32, false);
        testScreen(config);
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
        Assert.assertFalse(screen.isReady());
        screen.start();
        screen.awaitReady();
        screen.preUpdate();
        screen.update();
        screen.showCursor();
        screen.hideCursor();
        screen.requestFocus();
        Assert.assertNotNull(screen.getConfig());
        Assert.assertNotNull(screen.getGraphic());
        Assert.assertTrue(screen.getReadyTimeOut() > -1L);
        Assert.assertTrue(screen.getX() > -1);
        Assert.assertTrue(screen.getY() > -1);
        Assert.assertTrue(screen.isReady());
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

        ((ScreenSwt) screen).focusGained(null);
        Assert.assertTrue(focus.get());

        ((ScreenSwt) screen).focusLost(null);
        Assert.assertFalse(focus.get());

        screen.dispose();
        Assert.assertTrue(disposed.get());
    }
}
