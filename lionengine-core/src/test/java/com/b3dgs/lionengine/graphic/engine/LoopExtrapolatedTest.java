/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTimeout;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenMock;

/**
 * Test {@link LoopExtrapolated}.
 */
final class LoopExtrapolatedTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
    }

    private final AtomicReference<Double> extrapolation = new AtomicReference<>();
    private final AtomicLong rendered = new AtomicLong();
    private final AtomicLong computed = new AtomicLong(-1);
    private final AtomicLong tick = new AtomicLong();
    private final AtomicLong maxTick = new AtomicLong(5);
    private final Loop loop = new LoopExtrapolated();
    private final CountDownLatch latch = new CountDownLatch(1);

    private Thread getTask(Screen screen)
    {
        return new Thread(() ->
        {
            loop.start(screen, new Frame()
            {
                @Override
                public void check()
                {
                    latch.countDown();
                }

                @Override
                public void update(double extrp)
                {
                    extrapolation.set(Double.valueOf(extrp));
                    if (tick.incrementAndGet() == maxTick.get())
                    {
                        loop.stop();
                    }
                }

                @Override
                public void render()
                {
                    rendered.incrementAndGet();
                }

                @Override
                public void computeFrameRate(long lastTime, long currentTime)
                {
                    final long fps = Constant.ONE_SECOND_IN_NANO / (currentTime - lastTime);
                    computed.set(fps);
                }
            });
        });
    }

    /**
     * Test loop.
     */
    @Test
    void testLoop()
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));

        final Thread thread = getTask(screen);
        thread.start();

        assertTimeout(1000L, thread::join);
        assertEquals(maxTick.get(), tick.get());
        assertEquals(tick.get(), rendered.get());
        assertTrue(extrapolation.get().doubleValue() > 0.0, String.valueOf(extrapolation.get().doubleValue()));

        final int expectedRate = screen.getConfig().getOutput().getRate();

        assertTrue(computed.get() <= expectedRate, String.valueOf(computed.get()));
    }

    /**
     * Test without sync.
     */
    @Test
    void testNoSync()
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 0), 16, true));

        final Thread thread = getTask(screen);
        thread.start();

        assertTimeout(1000L, thread::join);
        assertEquals(maxTick.get(), tick.get());
        assertEquals(tick.get(), rendered.get());
        assertTrue(extrapolation.get().doubleValue() < 0.1, String.valueOf(extrapolation.get()));

        final int expectedRate = screen.getConfig().getOutput().getRate();

        assertTrue(computed.get() > expectedRate, String.valueOf(computed.get()));
    }

    /**
     * Test without sync full screen.
     */
    @Test
    void testNoSyncFullscreen()
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 0), 16, false));
        loop.notifyRateChanged(50);

        final Thread thread = getTask(screen);
        thread.start();

        assertTimeout(1000L, thread::join);
        assertEquals(maxTick.get(), tick.get());
        assertEquals(tick.get(), rendered.get());
        assertTrue(extrapolation.get().doubleValue() > 0, String.valueOf(extrapolation.get()));

        final int expectedRate = screen.getConfig().getOutput().getRate();

        assertTrue(computed.get() > expectedRate, String.valueOf(computed.get()));
    }

    /**
     * Test with not ready screen.
     */
    @Test
    void testUnready()
    {
        ScreenMock.setScreenWait(true);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));

        final Thread thread = getTask(screen);
        thread.start();

        assertTimeout(1000L, latch::await);

        loop.stop();

        assertTimeout(1000L, thread::join);

        assertEquals(0, tick.get());
        assertEquals(0, rendered.get());
        assertNull(extrapolation.get());
        assertEquals(-1, computed.get());
    }
}
