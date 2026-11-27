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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link Animator}: the valid calls of each state, the frame order of each loop mode, per-frame durations, the
 * time carried into the next frame, long steps, the end of a {@code once} animation, invalid steps and allocation
 * (REQ-ANIM-002, REQ-ANIM-003, REQ-SPR-002, ADR-0058).
 */
final class AnimatorTest
{
    /** Step of the tests in seconds, exact in binary. */
    private static final double STEP = 0.25;
    /** Frames of the tests: three cells, not the frame indexes, to tell them apart. */
    private static final int[] CELLS =
    {
        10, 11, 12
    };
    /** Updates per allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Returns the frame indexes shown after each step.
     *
     * @param animation The animation, played from its first frame.
     * @param steps The number of steps.
     * @return The frame index after each step, the first frame excluded.
     */
    private static int[] indexes(Animation animation, int steps)
    {
        final Animator animator = new Animator();
        animator.play(animation);
        final int[] result = new int[steps];
        for (int i = 0; i < steps; i++)
        {
            animator.update(STEP);
            result[i] = animator.getFrameIndex();
        }
        return result;
    }

    /**
     * Creates an animator in a state, playing three frames that loop.
     *
     * @param state The state.
     * @return The animator, on the second frame unless stopped.
     */
    private static Animator animator(AnimatorState state)
    {
        final Animator animator = new Animator();
        final AnimationLoop loop;
        if (state == AnimatorState.FINISHED)
        {
            loop = AnimationLoop.ONCE;
        }
        else
        {
            loop = AnimationLoop.LOOP;
        }
        animator.play(new Animation(CELLS, STEP, loop));
        animator.update(STEP);
        if (state == AnimatorState.PAUSED)
        {
            animator.pause();
        }
        else if (state == AnimatorState.STOPPED)
        {
            animator.stop();
        }
        else if (state == AnimatorState.FINISHED)
        {
            animator.update(2 * STEP);
        }
        assertEquals(state, animator.getState());
        return animator;
    }

    /**
     * A new animator is stopped, without animation, and has no frame to draw.
     */
    @Test
    void testNew()
    {
        final Animator animator = new Animator();

        assertEquals(AnimatorState.STOPPED, animator.getState());
        assertNull(animator.getAnimation());
        assertEquals(0, animator.getFrameIndex());
        assertEquals(0.0, animator.getFrameTime());
        assertThrows(IllegalStateException.class, animator::getFrame);
        animator.update(STEP);
        animator.stop();
        assertEquals(AnimatorState.STOPPED, animator.getState());
        assertThrows(IllegalArgumentException.class, () -> animator.play(null));
    }

    /**
     * {@code play} is valid in every state and starts the animation from its first frame, also when it is playing.
     *
     * @param state The state before the call.
     */
    @ParameterizedTest
    @EnumSource(AnimatorState.class)
    void testPlay(AnimatorState state)
    {
        final Animator animator = animator(state);
        final Animation walk = new Animation(CELLS, STEP, AnimationLoop.PING_PONG);

        animator.play(walk);

        assertEquals(AnimatorState.PLAYING, animator.getState());
        assertSame(walk, animator.getAnimation());
        assertEquals(0, animator.getFrameIndex());
        assertEquals(10, animator.getFrame());
        assertEquals(0.0, animator.getFrameTime());
    }

    /**
     * {@code stop} is valid in every state and returns to the first frame.
     *
     * @param state The state before the call.
     */
    @ParameterizedTest
    @EnumSource(AnimatorState.class)
    void testStop(AnimatorState state)
    {
        final Animator animator = animator(state);

        animator.stop();

        assertEquals(AnimatorState.STOPPED, animator.getState());
        assertEquals(0, animator.getFrameIndex());
        assertEquals(0.0, animator.getFrameTime());
        animator.update(STEP);
        assertEquals(0, animator.getFrameIndex());
    }

    /**
     * {@code pause} is valid only while playing, {@code resume} only while paused; any other call throws and changes
     * nothing.
     *
     * @param state The state before the call.
     */
    @ParameterizedTest
    @EnumSource(AnimatorState.class)
    void testPauseResume(AnimatorState state)
    {
        final Animator animator = animator(state);
        final int index = animator.getFrameIndex();

        if (state == AnimatorState.PLAYING)
        {
            animator.pause();
            assertEquals(AnimatorState.PAUSED, animator.getState());
        }
        else
        {
            assertThrows(IllegalStateException.class, animator::pause);
        }
        if (state == AnimatorState.PAUSED)
        {
            animator.resume();
            assertEquals(AnimatorState.PLAYING, animator.getState());
        }
        else if (state != AnimatorState.PLAYING)
        {
            final IllegalStateException error = assertThrows(IllegalStateException.class, animator::resume);
            assertTrue(error.getMessage().contains(state.name()), error.getMessage());
            assertEquals(state, animator.getState());
        }
        assertEquals(index, animator.getFrameIndex());
    }

    /**
     * {@code resume} while playing is refused, as the feature page states.
     */
    @Test
    void testResumeWhilePlaying()
    {
        final Animator animator = animator(AnimatorState.PLAYING);

        assertThrows(IllegalStateException.class, animator::resume);
        assertEquals(AnimatorState.PLAYING, animator.getState());
    }

    /**
     * A pause keeps the frame and the time spent in it; updates while paused change nothing; the resumed animation
     * continues from them.
     */
    @Test
    void testPauseKeepsTime()
    {
        final Animator animator = new Animator();
        animator.play(new Animation(CELLS, 2 * STEP, AnimationLoop.LOOP));
        animator.update(STEP);
        animator.pause();

        animator.update(10 * STEP);

        assertEquals(0, animator.getFrameIndex());
        assertEquals(STEP, animator.getFrameTime());
        animator.resume();
        animator.update(STEP);
        assertEquals(1, animator.getFrameIndex());
        assertEquals(0.0, animator.getFrameTime());
    }

    /**
     * {@code loop} restarts at the first frame after the last.
     */
    @Test
    void testLoop()
    {
        assertArrayEquals(new int[]
        {
            1, 2, 0, 1, 2, 0, 1
        }, indexes(new Animation(CELLS, STEP, AnimationLoop.LOOP), 7));
    }

    /**
     * {@code ping-pong} turns back at both ends, each end frame shown once per turn; with two frames it alternates,
     * and a single frame stays.
     */
    @Test
    void testPingPong()
    {
        assertArrayEquals(new int[]
        {
            1, 2, 1, 0, 1, 2, 1, 0, 1
        }, indexes(new Animation(CELLS, STEP, AnimationLoop.PING_PONG), 9));
        assertArrayEquals(new int[]
        {
            1, 0, 1, 0
        }, indexes(new Animation(new int[]
        {
            5, 6
        }, STEP, AnimationLoop.PING_PONG), 4));
        assertArrayEquals(new int[]
        {
            0, 0, 0
        }, indexes(new Animation(new int[]
        {
            5
        }, STEP, AnimationLoop.PING_PONG), 3));
    }

    /**
     * {@code once} shows its last frame for its full duration, then finishes on it and drops the time left over;
     * updates then change nothing.
     */
    @Test
    void testOnce()
    {
        final Animator animator = new Animator();
        animator.play(new Animation(CELLS, STEP, AnimationLoop.ONCE));
        animator.update(2 * STEP);

        assertEquals(2, animator.getFrameIndex());
        assertEquals(AnimatorState.PLAYING, animator.getState());
        animator.update(1.5 * STEP);
        assertEquals(AnimatorState.FINISHED, animator.getState());
        assertEquals(12, animator.getFrame());
        assertEquals(0.0, animator.getFrameTime());
        animator.update(STEP);
        assertEquals(2, animator.getFrameIndex());
    }

    /**
     * Each frame lasts its own duration.
     */
    @Test
    void testFrameDurations()
    {
        assertArrayEquals(new int[]
        {
            1, 1, 2, 0, 1, 1, 2
        }, indexes(new Animation(CELLS, new double[]
        {
            STEP, 2 * STEP, STEP
        }, AnimationLoop.LOOP), 7));
    }

    /**
     * The time left over by a frame carries into the next one, and a long step crosses several frames: the speed does
     * not depend on the step length.
     */
    @Test
    void testCarryAndLongStep()
    {
        final Animation walk = new Animation(CELLS, STEP, AnimationLoop.LOOP);
        final Animator animator = new Animator();
        animator.play(walk);

        animator.update(1.5 * STEP);
        assertEquals(1, animator.getFrameIndex());
        assertEquals(0.5 * STEP, animator.getFrameTime());
        animator.update(1.5 * STEP);
        assertEquals(0, animator.getFrameIndex());
        assertEquals(0.0, animator.getFrameTime());

        animator.play(walk);
        animator.update(4 * STEP);
        assertEquals(1, animator.getFrameIndex());
        assertEquals(0.0, animator.getFrameTime());
    }

    /**
     * The same animation and steps give the same frames whatever the step length: 1,000 short steps and 250 steps four
     * times longer end on the same frame, as computed from the elapsed time (REQ-SPR-002).
     */
    @Test
    void testDeterministic()
    {
        final Animation walk = new Animation(CELLS, STEP, AnimationLoop.LOOP);
        final Animator shortSteps = new Animator();
        final Animator longSteps = new Animator();
        shortSteps.play(walk);
        longSteps.play(walk);

        for (int i = 0; i < 1_000; i++)
        {
            shortSteps.update(STEP / 4.0);
        }
        for (int i = 0; i < 250; i++)
        {
            longSteps.update(STEP);
        }

        assertEquals(250 % CELLS.length, shortSteps.getFrameIndex());
        assertEquals(shortSteps.getFrameIndex(), longSteps.getFrameIndex());
        assertEquals(shortSteps.getFrameTime(), longSteps.getFrameTime());
    }

    /**
     * Reading the animator changes nothing.
     */
    @Test
    void testReadingChangesNothing()
    {
        final Animator animator = new Animator();
        animator.play(new Animation(CELLS, 2 * STEP, AnimationLoop.PING_PONG));
        animator.update(STEP);

        for (int i = 0; i < 10; i++)
        {
            assertEquals(10, animator.getFrame());
            assertEquals(STEP, animator.getFrameTime());
            assertEquals(AnimatorState.PLAYING, animator.getState());
        }
        animator.update(STEP);
        assertEquals(1, animator.getFrameIndex());
    }

    /**
     * A step that is negative, infinite or not a number is refused; a zero step changes nothing.
     *
     * @param step The invalid step.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        -0.1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    })
    void testInvalidStep(double step)
    {
        final Animator animator = new Animator();
        animator.play(new Animation(CELLS, STEP, AnimationLoop.LOOP));

        assertThrows(IllegalArgumentException.class, () -> animator.update(step));
        animator.update(0.0);
        assertEquals(0, animator.getFrameIndex());
        assertEquals(0.0, animator.getFrameTime());
    }

    /**
     * Runs update steps and reads the frame after each one.
     *
     * @param animator The animator.
     * @return The sum of the frames read, so that the reads are used.
     */
    private static int steps(Animator animator)
    {
        int sum = 0;
        for (int i = 0; i < ITERATIONS; i++)
        {
            animator.update(STEP);
            sum += animator.getFrame();
        }
        return sum;
    }

    /**
     * Updating and reading allocate nothing in the steady state, measured after a first run that loads and
     * initializes what the first calls need.
     */
    @Test
    void testUpdateAllocatesNothing()
    {
        final Animator animator = new Animator();
        animator.play(new Animation(CELLS, new double[]
        {
            STEP, 2 * STEP, STEP
        }, AnimationLoop.PING_PONG));
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        int sum = steps(animator);

        final long before = bean.getCurrentThreadAllocatedBytes();
        sum += steps(animator);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
        assertTrue(sum > 0);
    }
}
