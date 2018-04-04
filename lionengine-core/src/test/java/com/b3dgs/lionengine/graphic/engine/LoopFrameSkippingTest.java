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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.Screen;
import com.b3dgs.lionengine.graphic.ScreenMock;

/**
 * Test {@link LoopFrameSkipping}.
 */
public final class LoopFrameSkippingTest
{
    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @Before
    public void setUp() throws IOException
    {
        Engine.start(new EngineMock(LoopFrameSkippingTest.class.getSimpleName(), Version.DEFAULT));
    }

    /**
     * Clean up test.
     */
    @After
    public void cleanUp()
    {
        Engine.terminate();
    }

    private final AtomicLong rendered = new AtomicLong();
    private final AtomicLong computed = new AtomicLong(-1);
    private final AtomicLong tick = new AtomicLong();
    private final AtomicLong maxTick = new AtomicLong(4);
    private final AtomicLong pause = new AtomicLong();
    private final Loop loop = new LoopFrameSkipping();
    private final CountDownLatch latch = new CountDownLatch(1);

    private Thread getTask(final Screen screen)
    {
        return new Thread(() -> loop.start(screen, new Frame()
        {
            @Override
            public void check()
            {
                latch.countDown();
            }

            @Override
            public void update(double extrp)
            {
                if (tick.incrementAndGet() == maxTick.get())
                {
                    loop.stop();
                }
            }

            @Override
            public void render()
            {
                final long old = System.nanoTime();
                while (UtilTests.getElapsedMilli(old, System.nanoTime()) < pause.get())
                {
                    // Pause
                }
                rendered.incrementAndGet();
            }

            @Override
            public void computeFrameRate(long lastTime, long currentTime)
            {
                final long fps = Constant.ONE_SECOND_IN_NANO / (currentTime - lastTime);
                computed.set(fps);
            }
        }));
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

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() <= expectedRate);
        Assert.assertTrue(String.valueOf(computed.get()), computed.get() > 0);
    }

    /**
     * Test with slow rendering
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testSlowRender() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 50));
        pause.set(screen.getConfig().getSource().getRate());

        final Thread thread = getTask(screen);
        thread.start();
        thread.join();

        Assert.assertTrue(String.valueOf(tick.get() + " " + maxTick.get()), tick.get() >= maxTick.get());

        final boolean min = rendered.get() >= Math.round(Math.floor(tick.get() / 2.0));
        final boolean max = rendered.get() <= Math.round(Math.ceil(tick.get() / 2.0));

        Assert.assertTrue(String.valueOf(tick.get() + " " + rendered.get()), min || max);

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() < expectedRate / 2);
        Assert.assertTrue(String.valueOf(computed.get()), computed.get() > expectedRate / 2 - expectedRate);
    }

    /**
     * Test with max rate.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 1000L)
    public void testMaxRate() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 0));

        final Thread thread = getTask(screen);
        thread.start();
        thread.join();

        Assert.assertTrue(tick.get() + " " + maxTick.get(), tick.get() >= maxTick.get());
        Assert.assertTrue(rendered.get() + " " + tick.get(), rendered.get() <= tick.get());
        Assert.assertTrue(String.valueOf(rendered.get()), rendered.get() > 0);
        Assert.assertTrue(String.valueOf(computed.get()), computed.get() > 0);
    }

    /**
     * Test with spike of death.
     * 
     * @throws InterruptedException If error.
     */
    @Test(timeout = 2000L)
    public void testSpikeOfDeath() throws InterruptedException
    {
        ScreenMock.setScreenWait(false);

        final Screen screen = new ScreenMock(new Config(new Resolution(320, 240, 50), 16, true));
        screen.getConfig().setSource(new Resolution(320, 240, 50));

        final long maxFrameTime = Math.round(LoopFrameSkipping.MAX_FRAME_TIME_NANO / Constant.NANO_TO_MILLI);
        pause.set(maxFrameTime * 2);

        final Thread thread = getTask(screen);
        thread.start();
        thread.join();

        final double frameTimeMilli = 1000.0 / screen.getConfig().getSource().getRate();
        Assert.assertEquals(Math.round(Math.floor(maxFrameTime / frameTimeMilli)), tick.get());
        Assert.assertEquals(2, rendered.get());

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() < expectedRate);
    }

    /**
     * Test without sync
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

        final int expectedRate = screen.getConfig().getOutput().getRate();

        Assert.assertTrue(String.valueOf(computed.get()), computed.get() <= expectedRate);
    }
}
