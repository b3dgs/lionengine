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
package com.b3dgs.lionengine.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.LogSink;

/**
 * Test {@link GameLoop} with a controllable clock (REQ-LOOP-003 to REQ-LOOP-012, REQ-TEST-001).
 */
final class GameLoopTest
{
    /** Step of the test configuration: 10 ms at 100 Hz. */
    static final long STEP = 10_000_000L;
    /** Test configuration: 100 Hz, lag cap 0.25 s, time scale 1, tolerance 0.25 ms, 1,024 events. */
    static final LoopConfig CONFIG = new LoopConfig(100, 0.25, 1.0, 0.000_25, 1024);
    /** Tolerance on simulated time, in nanoseconds. */
    private static final double SIMULATED_TOLERANCE = 1.0;

    /**
     * Records the calls of the loop.
     */
    static final class Recorder implements UpdateCallback, RenderCallback, LogSink
    {
        /** Calls in order: {@code U} for an update, {@code R} for a render, other letters added by tests. */
        final StringBuilder calls = new StringBuilder();
        /** Messages written to the log. */
        final List<String> logged = new ArrayList<>();
        /** Number of updates. */
        long updates;
        /** Step of the last update, in seconds. */
        double stepSeconds;
        /** Number of renders. */
        int renders;
        /** Factor of the last render. */
        double alpha;
        /** Unscaled delta of the last render, in seconds. */
        double delta;

        @Override
        public void update(double step)
        {
            calls.append('U');
            updates++;
            stepSeconds = step;
        }

        @Override
        public void render(double factor, double unscaledDeltaSeconds)
        {
            calls.append('R');
            renders++;
            alpha = factor;
            delta = unscaledDeltaSeconds;
        }

        @Override
        public void write(LogLevel level, CharSequence message, Throwable error)
        {
            logged.add(level + " " + message);
        }
    }

    /**
     * Snapping cases at 100 Hz with a tolerance of 0.25 ms: frame, expected updates and factor.
     *
     * @return The test cases.
     */
    static Stream<Arguments> snapping()
    {
        return Stream.of(Arguments.of(10_250_000L, 1L, 0.0),
                         Arguments.of(9_750_000L, 1L, 0.0),
                         Arguments.of(10_260_000L, 1L, 0.026),
                         Arguments.of(20_200_000L, 2L, 0.0),
                         Arguments.of(39_800_000L, 4L, 0.0),
                         Arguments.of(50_100_000L, 5L, 0.01),
                         Arguments.of(4_900_000L, 0L, 0.49));
    }

    /**
     * Creates a started loop: one iteration has run.
     *
     * @param config The configuration.
     * @param clock The clock.
     * @param recorder The recorder of the calls.
     * @return The loop.
     */
    static GameLoop start(LoopConfig config, ControllableClock clock, Recorder recorder)
    {
        final GameLoop loop = new GameLoop(config, clock, new Log(recorder), recorder, recorder);
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
    static void frame(GameLoop loop, ControllableClock clock, long frameNanos)
    {
        clock.advance(frameNanos);
        loop.iterate();
    }

    /**
     * The first iteration uses an elapsed time of 0: no update, a render with a zero factor and delta.
     */
    @Test
    void testFirstIteration()
    {
        final ControllableClock clock = new ControllableClock();
        clock.advance(5_000_000_000L);
        final Recorder recorder = new Recorder();
        final GameLoop loop = new GameLoop(CONFIG, clock, new Log(recorder), recorder, recorder);
        assertSame(LoopState.CREATED, loop.getState());

        loop.iterate();

        assertEquals("R", recorder.calls.toString());
        assertEquals(0.0, recorder.alpha);
        assertEquals(0.0, recorder.delta);
        assertSame(LoopState.RUNNING, loop.getState());
    }

    /**
     * The elapsed time gives the exact number of updates, then one render with the remaining factor.
     */
    @Test
    void testCatchUp()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);

        frame(loop, clock, 35_000_000L);

        assertEquals("RUUUR", recorder.calls.toString());
        assertEquals(3L, recorder.updates);
        assertEquals(0.01, recorder.stepSeconds);
        assertEquals(0.5, recorder.alpha, 1e-9);
        assertEquals(0.035, recorder.delta, 1e-12);
    }

    /**
     * The same simulated time gives the same number of updates whatever the render cadence.
     */
    @Test
    void testIndependentOfCadence()
    {
        final long[] frames = {10_000_000L, 100_000_000L, 3_000_000L, 7_000_001L};
        for (final long frameNanos : frames)
        {
            final ControllableClock clock = new ControllableClock();
            final Recorder recorder = new Recorder();
            final GameLoop loop = start(CONFIG, clock, recorder);
            final long total = 2_000_000_000L;
            for (long elapsed = 0L; elapsed + frameNanos <= total; elapsed += frameNanos)
            {
                frame(loop, clock, frameNanos);
            }
            frame(loop, clock, total - clock.nanoTime());

            assertEquals(200L, recorder.updates, () -> "Frame " + frameNanos);
        }
    }

    /**
     * The factor stays in [0, 1) for irregular frames.
     */
    @Test
    void testAlphaBounds()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        long frameNanos = 1_234_567L;
        for (int i = 0; i < 10_000; i++)
        {
            frameNanos = (frameNanos * 1_103_515_245L + 12_345L) % 60_000_000L;
            frame(loop, clock, frameNanos);
            assertTrue(recorder.alpha >= 0.0 && recorder.alpha < 1.0, "Alpha " + recorder.alpha);
        }
    }

    /**
     * No drift over a million steps: the simulated time equals the elapsed time, within a nanosecond.
     */
    @Test
    void testNoDrift()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        final long frameNanos = 7_000_001L;
        long total = 0L;
        while (recorder.updates < 1_000_000L)
        {
            frame(loop, clock, frameNanos);
            total += frameNanos;
        }
        final double simulated = recorder.updates * (double) STEP + recorder.alpha * (double) STEP;

        assertEquals(total / STEP, recorder.updates);
        assertEquals((double) total, simulated, SIMULATED_TOLERANCE);
    }

    /**
     * The rounding remainder of scaled time is carried: no time is lost over a million frames.
     */
    @Test
    void testRemainderCarried()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(new LoopConfig(100, 0.25, 0.1, 0.0, 1024), clock, recorder);
        final long frameNanos = 1_000_003L;
        for (int i = 0; i < 1_000_000; i++)
        {
            frame(loop, clock, frameNanos);
        }
        final double simulated = recorder.updates * (double) STEP + recorder.alpha * (double) STEP;

        assertEquals(1_000_000L * frameNanos * 0.1, simulated, SIMULATED_TOLERANCE);
    }

    /**
     * Elapsed times near 1 to 4 steps are snapped within the tolerance only.
     *
     * @param frameNanos The frame duration.
     * @param updates The expected updates.
     * @param alpha The expected factor.
     */
    @ParameterizedTest
    @MethodSource("snapping")
    void testSnapping(long frameNanos, long updates, double alpha)
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);

        frame(loop, clock, frameNanos);

        assertEquals(updates, recorder.updates);
        assertEquals(alpha, recorder.alpha, 1e-9);
        assertEquals(frameNanos / 1e9, recorder.delta, 1e-12);
    }

    /**
     * A tolerance of 0 disables snapping.
     */
    @Test
    void testSnappingDisabled()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(new LoopConfig(100, 0.25, 1.0, 0.0, 1024), clock, recorder);

        frame(loop, clock, 10_100_000L);

        assertEquals(1L, recorder.updates);
        assertEquals(0.01, recorder.alpha, 1e-9);
    }

    /**
     * A long frame is capped: the excess is dropped, counted and reported before the updates.
     */
    @Test
    void testLagCap()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        final List<Double> reported = new ArrayList<>();
        loop.setLagListener(seconds ->
        {
            recorder.calls.append('L');
            reported.add(Double.valueOf(seconds));
        });

        frame(loop, clock, 2_000_000_000L);

        assertEquals(25L, recorder.updates);
        assertTrue(recorder.calls.toString().startsWith("RLU"), recorder.calls.toString());
        assertEquals(1, reported.size());
        assertEquals(1.75, reported.get(0).doubleValue(), 1e-12);
        assertEquals(1L, loop.getDroppedLagCount());
        assertEquals(1.75, loop.getDroppedLagSeconds(), 1e-12);
        assertEquals(0.25, recorder.delta, 1e-12);

        loop.setLagListener(null);
        frame(loop, clock, 1_000_000_000L);
        assertEquals(2L, loop.getDroppedLagCount());
        assertEquals(2.5, loop.getDroppedLagSeconds(), 1e-12);
    }

    /**
     * Pause accumulates nothing and resume causes no catch-up; rendering continues with a frozen factor and the real
     * delta.
     */
    @Test
    void testPauseResume()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        frame(loop, clock, 15_000_000L);
        assertEquals(1L, recorder.updates);

        loop.pause();
        frame(loop, clock, 100_000_000L);
        assertSame(LoopState.PAUSED, loop.getState());
        assertTrue(loop.isRunning());
        assertEquals(1L, recorder.updates);
        assertEquals(0.5, recorder.alpha, 1e-9);
        assertEquals(0.1, recorder.delta, 1e-12);

        frame(loop, clock, 2_000_000_000L);
        assertEquals(1L, recorder.updates);
        assertEquals(0.25, recorder.delta, 1e-12);
        assertEquals(0L, loop.getDroppedLagCount());

        loop.resume();
        frame(loop, clock, 500_000_000L);
        assertSame(LoopState.RUNNING, loop.getState());
        assertEquals(1L, recorder.updates);
        assertEquals(0.0, recorder.alpha);

        frame(loop, clock, STEP);
        assertEquals(2L, recorder.updates);
    }

    /**
     * A pause requested and cancelled before the same frame boundary is applied as a pause immediately resumed: the
     * elapsed time is discarded, nothing is caught up and no lag is reported.
     */
    @Test
    void testPauseCancelledBeforeBoundary()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        frame(loop, clock, 15_000_000L);

        loop.pause();
        loop.resume();
        frame(loop, clock, 3_000_000_000L);

        assertSame(LoopState.RUNNING, loop.getState());
        assertEquals(1L, recorder.updates);
        assertEquals(0.0, recorder.alpha);
        assertEquals(0L, loop.getDroppedLagCount());
        assertEquals(0.25, recorder.delta, 1e-12);

        frame(loop, clock, STEP);
        assertEquals(2L, recorder.updates);
    }

    /**
     * A pause requested before the first iteration starts the loop paused.
     */
    @Test
    void testPausedStart()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = new GameLoop(CONFIG, clock, new Log(recorder), recorder, recorder);
        loop.pause();
        loop.iterate();

        frame(loop, clock, 100_000_000L);

        assertSame(LoopState.PAUSED, loop.getState());
        assertEquals(0L, recorder.updates);
    }

    /**
     * A time scale of 0.25 gives a quarter of the updates; render calls and the unscaled delta are unchanged.
     */
    @Test
    void testTimeScale()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        loop.setTimeScale(0.25);
        assertEquals(1.0, loop.getTimeScale());

        for (int i = 0; i < 100; i++)
        {
            frame(loop, clock, STEP);
        }

        assertEquals(0.25, loop.getTimeScale());
        assertEquals(25L, recorder.updates);
        assertEquals(101, recorder.renders);
        assertEquals(0.01, recorder.delta, 1e-12);
        assertEquals(0.01, recorder.stepSeconds);
    }

    /**
     * An exception in an update fails the loop: rethrown, no render after it, no further iteration.
     */
    @Test
    void testUpdateFailure()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final IllegalStateException failure = new IllegalStateException("update");
        final GameLoop loop = new GameLoop(CONFIG, clock, new Log(recorder), step ->
        {
            throw failure;
        }, recorder);
        loop.iterate();
        clock.advance(STEP);

        assertSame(failure, assertThrows(IllegalStateException.class, loop::iterate));
        assertSame(LoopState.FAILED, loop.getState());
        assertFalse(loop.isRunning());
        assertEquals(1, recorder.renders);
        assertThrows(IllegalStateException.class, loop::iterate);
    }

    /**
     * An exception in a render fails the loop.
     */
    @Test
    void testRenderFailure()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = new GameLoop(CONFIG, clock, new Log(recorder), recorder, (alpha, delta) ->
        {
            throw new IllegalArgumentException("render");
        });

        assertThrows(IllegalArgumentException.class, loop::iterate);
        assertSame(LoopState.FAILED, loop.getState());
    }

    /**
     * An exception in the lag listener fails the loop before the updates.
     */
    @Test
    void testListenerFailure()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop loop = start(CONFIG, clock, recorder);
        loop.setLagListener(seconds ->
        {
            throw new IllegalStateException("listener");
        });
        clock.advance(1_000_000_000L);

        assertThrows(IllegalStateException.class, loop::iterate);
        assertSame(LoopState.FAILED, loop.getState());
        assertEquals(0L, recorder.updates);
    }

    /**
     * A stop requested during an update finishes the iteration: its steps and its render.
     */
    @Test
    void testStopDuringIteration()
    {
        final ControllableClock clock = new ControllableClock();
        final Recorder recorder = new Recorder();
        final GameLoop[] holder = new GameLoop[1];
        holder[0] = new GameLoop(CONFIG, clock, new Log(recorder), step ->
        {
            recorder.update(step);
            holder[0].stop();
        }, recorder);
        holder[0].iterate();

        frame(holder[0], clock, 30_000_000L);

        assertEquals("RUUUR", recorder.calls.toString());
        assertSame(LoopState.STOPPED, holder[0].getState());
        holder[0].stop();
        assertThrows(IllegalStateException.class, holder[0]::iterate);
    }

    /**
     * {@code null} arguments are rejected.
     */
    @Test
    void testNullArguments()
    {
        final Recorder recorder = new Recorder();
        final Log log = new Log(recorder);
        final ControllableClock clock = new ControllableClock();

        assertThrows(IllegalArgumentException.class, () -> new GameLoop(null, log, recorder, recorder));
        assertThrows(IllegalArgumentException.class, () -> new GameLoop(CONFIG, null, log, recorder, recorder));
        assertThrows(IllegalArgumentException.class, () -> new GameLoop(CONFIG, clock, null, recorder, recorder));
        assertThrows(IllegalArgumentException.class, () -> new GameLoop(CONFIG, clock, log, null, recorder));
        assertThrows(IllegalArgumentException.class, () -> new GameLoop(CONFIG, clock, log, recorder, null));
    }

    /**
     * The constructor with the system clock measures real time.
     */
    @Test
    void testSystemClock()
    {
        final Recorder recorder = new Recorder();
        final GameLoop loop = new GameLoop(CONFIG, new Log(recorder), recorder, recorder);

        loop.iterate();
        loop.iterate();

        assertTrue(recorder.delta >= 0.0 && recorder.delta <= 0.25, "Delta " + recorder.delta);
        assertEquals(2, recorder.renders);
    }
}
