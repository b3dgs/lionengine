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
package com.b3dgs.lionengine.core.awt;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.junit.AfterClass;
import org.junit.Assert;
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

/**
 * Test the screen class.
 */
public class ScreenAwtTest
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
     * Test the windowed screen.
     */
    @Test(timeout = TIMEOUT)
    public void testWindowed()
    {
        final Config config = new Config(com.b3dgs.lionengine.test.Constant.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Medias.create(IMAGE));
        testScreen(config);
    }

    /**
     * Test the applet screen.
     */
    @Test(timeout = TIMEOUT)
    public void testApplet()
    {
        final Config config = new Config(com.b3dgs.lionengine.test.Constant.RESOLUTION_320_240,
                                         32,
                                         false,
                                         Medias.create(IMAGE));
        config.setApplet(new AppletAwt());

        testScreen(config);
    }

    /**
     * Test the full screen.
     */
    @Test(timeout = TIMEOUT)
    public void testFullscreen()
    {
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
     * Test the full screen.
     */
    @Test(timeout = TIMEOUT, expected = LionEngineException.class)
    public void testFullscreenFail()
    {
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
        Assert.assertFalse(screen.isReady());
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
