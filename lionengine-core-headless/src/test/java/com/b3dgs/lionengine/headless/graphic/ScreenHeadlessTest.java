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
package com.b3dgs.lionengine.headless.graphic;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;

/**
 * Test {@link ScreenHeadless}.
 */
public final class ScreenHeadlessTest
{
    /** Test timeout in milliseconds. */
    private static final long TIMEOUT = 1_000L;
    /** Image media. */
    private static final String IMAGE = "image.png";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        EngineHeadless.start(ScreenHeadlessTest.class.getName(), Version.DEFAULT);
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
        final Config config = new Config(com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Medias.create(IMAGE));
        config.setSource(com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240);
        testScreen(config);
    }

    /**
     * Test applet screen.
     * 
     * @throws Exception If error.
     */
    @Test(timeout = TIMEOUT)
    public void testApplet() throws Exception
    {
        final Config config = new Config(com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240,
                                         32,
                                         false,
                                         Medias.create(IMAGE));
        config.setApplet(new AppletHeadless());
        config.setSource(com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240);

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        testScreen(config);
        Verbose.info("****************************************************************************************");
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
        }
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
        screen.dispose();

        Assert.assertEquals(0, screen.getX());
        Assert.assertEquals(0, screen.getY());

        screen.removeListener(screenListener);
    }
}
