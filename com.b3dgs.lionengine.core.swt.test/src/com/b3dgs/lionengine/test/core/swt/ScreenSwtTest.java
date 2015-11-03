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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.core.swt.EngineSwt;
import com.b3dgs.lionengine.core.swt.ToolsSwt;

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
    private static void checkMultipleDisplaySupport()
    {
        try
        {
            Assert.assertNotNull(ToolsSwt.getDisplay());
        }
        catch (final SWTError error)
        {
            Assume.assumeFalse(ERROR_MULTIPLE_DISPLAY, ERROR_MULTIPLE_DISPLAY.equals(error.getMessage()));
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

        final Config config = new Config(com.b3dgs.lionengine.test.util.Constant.RESOLUTION_320_240, 32, true);
        config.setIcon(Medias.create(IMAGE));
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
            final Config config = new Config(resolution, 32, false);
            config.setIcon(Medias.create(IMAGE));

            testScreen(config);
        }
    }

    /**
     * Test the full screen.
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
        Assert.assertTrue(screen.isReady());
        screen.start();
        screen.awaitReady();
        screen.preUpdate();
        screen.update();
        screen.showCursor();
        screen.hideCursor();
        Assert.assertNotNull(screen.getConfig());
        Assert.assertNotNull(screen.getGraphic());
        Assert.assertTrue(screen.getReadyTimeOut() > -1L);
        Assert.assertTrue(screen.getX() > -1);
        Assert.assertTrue(screen.getY() > -1);
        Assert.assertTrue(screen.isReady());
        screen.dispose();
    }
}
