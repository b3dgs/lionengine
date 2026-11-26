/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.runtime.Clock;
import com.b3dgs.lionengine.runtime.GameLoop;
import com.b3dgs.lionengine.runtime.LoopConfig;
import com.b3dgs.lionengine.runtime.LoopState;
import com.b3dgs.lionengine.runtime.RenderCallback;
import com.b3dgs.lionengine.runtime.Scene;
import com.b3dgs.lionengine.runtime.SceneManager;
import com.b3dgs.lionengine.runtime.UpdateCallback;

/**
 * Runs {@code runtime} on Android API 21 (ADR-0044): fixed-step game loop with a clock moved by the test, the inbox
 * fed by another thread, the system clock, and the scene manager with its overlay stack. Expected values are the ones
 * of the JVM tests of {@code lionengine-runtime} (GameLoopTest, GameLoopInboxTest, SceneManagerTest,
 * SceneOverlayTest).
 */
@RunWith(AndroidJUnit4.class)
public final class RuntimeTest
{
    /** Test configuration: 100 Hz, lag cap 0.25 s, time scale 1, tolerance 0.25 ms, 1,024 events. */
    private static final LoopConfig CONFIG = new LoopConfig(100, 0.25, 1.0, 0.000_25, 1024);

    /**
     * Clock moved by the test.
     */
    private static final class ManualClock implements Clock
    {
        /** Current time, in nanoseconds. */
        private long nanos;

        /**
         * Creates a clock at time 0.
         */
        ManualClock()
        {
            super();
        }

        @Override
        public long nanoTime()
        {
            return nanos;
        }

        /**
         * Moves the clock forward.
         *
         * @param durationNanos The duration, in nanoseconds.
         */
        void advance(long durationNanos)
        {
            nanos += durationNanos;
        }
    }

    /**
     * Records the calls of the loop and of a scene.
     */
    private static final class Recorder implements UpdateCallback, RenderCallback, Scene
    {
        /** Calls in order: {@code U} update, {@code R} render, {@code E} enter, {@code X} exit, {@code D} dispose. */
        private final StringBuilder calls = new StringBuilder();
        /** Number of updates. */
        private long updates;
        /** Factor of the last render. */
        private double alpha;
        /** Unscaled delta of the last render, in seconds. */
        private double delta;

        /**
         * Creates a recorder.
         */
        Recorder()
        {
            super();
        }

        @Override
        public void update(double stepSeconds)
        {
            calls.append('U');
            updates++;
        }

        @Override
        public void render(double factor, double unscaledDeltaSeconds)
        {
            calls.append('R');
            alpha = factor;
            delta = unscaledDeltaSeconds;
        }

        @Override
        public void enter()
        {
            calls.append('E');
        }

        @Override
        public void exit()
        {
            calls.append('X');
        }

        @Override
        public void dispose()
        {
            calls.append('D');
        }
    }

    /**
     * Creates a started loop: one iteration has run.
     *
     * @param clock The clock.
     * @param recorder The recorder of the calls.
     * @return The loop.
     */
    private static GameLoop start(ManualClock clock, Recorder recorder)
    {
        final GameLoop loop = new GameLoop(CONFIG, clock, new Log(), recorder, recorder);
        loop.iterate();
        return loop;
    }

    /**
     * Advances the clock, then iterates.
     *
     * @param loop The loop.
     * @param clock The clock.
     * @param frameNanos The frame duration, in nanoseconds.
     */
    private static void frame(GameLoop loop, ManualClock clock, long frameNanos)
    {
        clock.advance(frameNanos);
        loop.iterate();
    }

    /**
     * Creates the test.
     */
    public RuntimeTest()
    {
        super();
    }

    /**
     * The elapsed time gives the exact number of updates, then one render with the remaining factor.
     */
    @Test
    public void testCatchUp()
    {
        final ManualClock clock = new ManualClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(clock, recorder);

        frame(loop, clock, 35_000_000L);

        assertEquals("RUUUR", recorder.calls.toString());
        assertEquals(0.5, recorder.alpha, 1e-9);
        assertEquals(0.035, recorder.delta, 1e-12);
    }

    /**
     * The same simulated time gives the same number of updates whatever the render cadence.
     */
    @Test
    public void testIndependentOfCadence()
    {
        final long[] frames = {10_000_000L, 100_000_000L, 3_000_000L, 7_000_001L};
        for (final long frameNanos : frames)
        {
            final ManualClock clock = new ManualClock();
            final Recorder recorder = new Recorder();
            final GameLoop loop = start(clock, recorder);
            final long total = 2_000_000_000L;
            for (long elapsed = 0L; elapsed + frameNanos <= total; elapsed += frameNanos)
            {
                frame(loop, clock, frameNanos);
            }
            frame(loop, clock, total - clock.nanoTime());

            assertEquals("Frame " + frameNanos, 200L, recorder.updates);
        }
    }

    /**
     * Events posted by another thread are delivered in arrival order at the start of the next iteration, before the
     * updates.
     *
     * @throws InterruptedException If the posting thread is interrupted.
     */
    @Test
    public void testInboxFromAnotherThread() throws InterruptedException
    {
        final ManualClock clock = new ManualClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(clock, recorder);
        final Inbox inbox = loop.getInbox();
        final Thread poster = new Thread(() ->
        {
            inbox.post(() -> recorder.calls.append('a'));
            inbox.post(() -> recorder.calls.append('b'));
        });
        poster.start();
        poster.join();

        frame(loop, clock, 20_000_000L);

        assertEquals("RabUUR", recorder.calls.toString());
    }

    /**
     * A pause stops the updates at the next frame boundary; a stop ends the loop after its frame.
     */
    @Test
    public void testPauseAndStop()
    {
        final ManualClock clock = new ManualClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(clock, recorder);
        frame(loop, clock, 15_000_000L);
        loop.pause();
        frame(loop, clock, 100_000_000L);
        assertSame(LoopState.PAUSED, loop.getState());
        assertEquals(1L, recorder.updates);

        loop.stop();
        frame(loop, clock, 20_000_000L);
        assertSame(LoopState.STOPPED, loop.getState());
        assertFalse(loop.isRunning());
        assertThrows(IllegalStateException.class, loop::iterate);
    }

    /**
     * The loop runs on the system clock of the device ({@code System.nanoTime}): the platform check of the loop clock
     * (docs/android-compatibility.md). Only a lower bound is asserted, so a slow emulator cannot make it fail.
     */
    @Test
    public void testSystemClock()
    {
        final Recorder recorder = new Recorder();
        final GameLoop loop = new GameLoop(LoopConfig.DEFAULT, new Log(), recorder, recorder);
        final long start = System.nanoTime();
        loop.iterate();
        while (System.nanoTime() - start < 50_000_000L)
        {
            Thread.yield();
        }
        loop.iterate();

        assertTrue(loop.isRunning());
        assertTrue("Updates: " + recorder.updates, recorder.updates >= 2L);
    }

    /**
     * The first scene is entered at the first update, then updated and rendered in the same frame.
     */
    @Test
    public void testScene()
    {
        final ManualClock clock = new ManualClock();
        final Recorder scene = new Recorder();
        final SceneManager scenes = new SceneManager();
        final GameLoop loop = new GameLoop(CONFIG, clock, new Log(), scenes, scenes);
        assertTrue(scenes.switchTo(scene));
        clock.advance(10_000_000L);

        loop.iterate();
        assertEquals("ER", scene.calls.toString());
        frame(loop, clock, 10_000_000L);

        assertEquals("ERUR", scene.calls.toString());
        assertSame(scene, scenes.getActive());
    }

    /**
     * An overlay pushed at a frame boundary is alone updated while both scenes render, bottom first; popped, it is
     * exited and disposed and the base continues without being entered again (REQ-SCENE-003, ADR-0059).
     */
    @Test
    public void testOverlayStack()
    {
        final Recorder base = new Recorder();
        final Recorder overlay = new Recorder();
        final SceneManager scenes = new SceneManager();
        scenes.switchTo(base);
        scenes.update(0.01);
        scenes.render(0.0, 0.01);

        assertTrue(scenes.push(overlay));
        assertFalse(scenes.pop());
        scenes.update(0.01);
        scenes.render(0.0, 0.01);
        assertEquals(2, scenes.getSceneCount());
        assertSame(overlay, scenes.getActive());
        assertSame(base, scenes.getScene(0));
        assertTrue(scenes.pop());
        scenes.update(0.01);
        scenes.render(0.0, 0.01);
        assertThrows(IllegalStateException.class, scenes::pop);

        assertEquals("EURRUR", base.calls.toString());
        assertEquals("EURXD", overlay.calls.toString());
        scenes.clear();
        assertEquals("EURRURXD", base.calls.toString());
    }
}
