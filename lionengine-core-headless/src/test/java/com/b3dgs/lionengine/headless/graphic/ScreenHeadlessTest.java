/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenListener;

/**
 * Test {@link ScreenHeadless}.
 */
final class ScreenHeadlessTest
{
    /** Image media. */
    private static final String IMAGE = "image.png";

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        EngineHeadless.start(ScreenHeadlessTest.class.getName(), new Version(1, 0, 0));
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
     * 
     * @throws Exception If error.
     */
    @Test
    void testWindowed() throws Exception
    {
        final Config config = new Config(com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240,
                                         32,
                                         true,
                                         Arrays.asList(Medias.create(IMAGE)));

        assertTimeout(1000L, () -> testScreen(config));
    }

    /**
     * Test full screen.
     * 
     * @throws Exception If error.
     */
    @Test
    void testFullscreen() throws Exception
    {
        final Resolution resolution = com.b3dgs.lionengine.UtilTests.RESOLUTION_320_240;
        final Config config = new Config(resolution, 32, false, Arrays.asList(Medias.create(IMAGE)));

        assertTimeout(1000L, () -> testScreen(config));
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
        screen.setIcons(Arrays.asList(Medias.create("void")));
        screen.setIcons(Arrays.asList(Medias.create("image.png")));
        screen.dispose();

        assertEquals(0, screen.getX());
        assertEquals(0, screen.getY());

        screen.removeListener(screenListener);
    }
}
