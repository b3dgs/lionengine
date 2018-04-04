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
package com.b3dgs.lionengine.graphic.engine;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenMock;

/**
 * Test {@link LoopExtrapolated}.
 */
public final class LoopExtrapolatedTest
{
    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @Before
    public void setUp() throws IOException
    {
        Engine.start(new EngineMock(LoopExtrapolatedTest.class.getSimpleName(), Version.DEFAULT));
    }

    /**
     * Clean up test.
     */
    @After
    public void cleanUp()
    {
        Engine.terminate();
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
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testLoop() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 50));

        final Thread thread = getTask(screen);
        thread.start();
        thread.join();

        Assert.assertEquals(maxTick.get(), tick.get());
        Assert.assertEquals(tick.get(), rendered.get());
        Assert.assertTrue(String.valueOf(extrapolation.get().doubleValue()), extrapolation.get().doubleValue() > 0.0);

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() <= expectedRate);
    }

    /**
     * Test without sync.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testNoSync() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 0), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 50));
        final Thread thread = getTask(screen);

        thread.start();
        thread.join();

        Assert.assertEquals(maxTick.get(), tick.get());
        Assert.assertEquals(tick.get(), rendered.get());
        Assert.assertTrue(String.valueOf(extrapolation.get()), extrapolation.get().doubleValue() < 0.1);

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() > expectedRate);
    }

    /**
     * Test without sync full screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testNoSyncFullscreen() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 0), 16, false));
        screen.getConfig().setSource(new Resolution(320, 240, 50));
        final Thread thread = getTask(screen);

        thread.start();
        thread.join();

        Assert.assertEquals(maxTick.get(), tick.get());
        Assert.assertEquals(tick.get(), rendered.get());
        Assert.assertTrue(String.valueOf(extrapolation.get()), extrapolation.get().doubleValue() > 0);

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() > expectedRate);
    }

    /**
     * Test with not ready screen.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testUnready() throws InterruptedException
    {
        ScreenMock.setScreenWait(true);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 50));

        final Thread thread = getTask(screen);
        thread.start();

        latch.await();

        loop.stop();
        thread.join();

        Assert.assertEquals(0, tick.get());
        Assert.assertEquals(0, rendered.get());
        Assert.assertNull(extrapolation.get());
        Assert.assertEquals(-1, computed.get());
    }

    /**
     * Test with not started engine.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testEngineNotStarted() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 50));
        maxTick.set(-1);

        final Thread thread = getTask(screen);
        thread.start();

        while (tick.get() < 1)
        {
            // Continue
        }

        Engine.terminate();
        thread.join();

        Assert.assertEquals(tick.get(), rendered.get());
        Assert.assertEquals(1.0, extrapolation.get().doubleValue(), 0.1);

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() <= expectedRate);
    }
}
