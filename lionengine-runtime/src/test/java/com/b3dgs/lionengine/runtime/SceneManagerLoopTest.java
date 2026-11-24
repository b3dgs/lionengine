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

import static com.b3dgs.lionengine.runtime.RecordingScene.DISPOSE;
import static com.b3dgs.lionengine.runtime.RecordingScene.ENTER;
import static com.b3dgs.lionengine.runtime.RecordingScene.EXIT;
import static com.b3dgs.lionengine.runtime.RecordingScene.RENDER;
import static com.b3dgs.lionengine.runtime.RecordingScene.UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.sun.management.ThreadMXBean;

/**
 * Test {@link SceneManager} driven by a {@link GameLoop} and a controllable clock (REQ-SCENE-001, REQ-SCENE-002,
 * ADR-0017, ADR-0019).
 */
final class SceneManagerLoopTest
{
    /** Bytes tolerated for the allocation measurement itself: far below one allocation per iteration. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** Frame of three steps, in nanoseconds. */
    private static final long THREE_STEPS = 3L * GameLoopTest.STEP;

    /** Records of every scene of a test. */
    private final List<String> calls = new ArrayList<>();
    /** First scene. */
    private final RecordingScene first = new RecordingScene("first", calls);
    /** Second scene. */
    private final RecordingScene second = new RecordingScene("second", calls);
    /** Tested manager. */
    private final SceneManager manager = new SceneManager();
    /** Clock of the loop. */
    private final ControllableClock clock = new ControllableClock();
    /** Loop calling the manager as both callbacks, at 100 Hz. */
    private final GameLoop loop = new GameLoop(GameLoopTest.CONFIG, clock, new Log(), manager, manager);

    /**
     * Starts the loop on the first scene: its first iteration has no update and enters the scene in its render.
     */
    private void startFirst()
    {
        manager.switchTo(first);
        loop.iterate();
        assertEquals(List.of(first.record(ENTER), first.record(RENDER)), calls);
        calls.clear();
    }

    /**
     * In a catch-up frame of three steps, a switch requested by the first step leaves the other steps and the render
     * to the current scene; the next scene starts with the next frame.
     */
    @Test
    void testSwitchInCatchUpFrame()
    {
        startFirst();
        first.setAction(UPDATE, () -> manager.switchTo(second));

        GameLoopTest.frame(loop, clock, THREE_STEPS);

        assertEquals(3, first.getUpdates());
        assertEquals(List.of(first.record(UPDATE), first.record(UPDATE), first.record(UPDATE), first.record(RENDER)),
                     calls);

        calls.clear();
        GameLoopTest.frame(loop, clock, GameLoopTest.STEP);

        assertEquals(List.of(first.record(EXIT),
                             first.record(DISPOSE),
                             second.record(ENTER),
                             second.record(UPDATE),
                             second.record(RENDER)),
                     calls);
    }

    /**
     * While the loop is paused, frames are only rendered: a switch requested from a render still applies at the next
     * frame boundary, and the next scene is rendered without being updated.
     */
    @Test
    void testSwitchWhilePaused()
    {
        startFirst();
        loop.pause();
        GameLoopTest.frame(loop, clock, GameLoopTest.STEP);
        first.setAction(RENDER, () -> manager.switchTo(second));
        calls.clear();

        GameLoopTest.frame(loop, clock, THREE_STEPS);
        GameLoopTest.frame(loop, clock, THREE_STEPS);

        assertEquals(List.of(first.record(RENDER),
                             first.record(EXIT),
                             first.record(DISPOSE),
                             second.record(ENTER),
                             second.record(RENDER)),
                     calls);
        assertEquals(LoopState.PAUSED, loop.getState());
    }

    /**
     * An exception of a scene fails the loop; clearing the manager afterwards exits and disposes the scene.
     */
    @Test
    void testSceneFailureFailsLoop()
    {
        startFirst();
        first.failOn(UPDATE);

        assertThrows(IllegalStateException.class, () -> GameLoopTest.frame(loop, clock, GameLoopTest.STEP));
        assertEquals(LoopState.FAILED, loop.getState());

        manager.clear();

        assertEquals(List.of(first.record(UPDATE), first.record(EXIT), first.record(DISPOSE)), calls);
    }

    /**
     * Stopping the loop leaves the active scene to the game, which clears the manager once the loop has stopped.
     */
    @Test
    void testClearAfterStop()
    {
        startFirst();
        first.setAction(UPDATE, loop::stop);

        GameLoopTest.frame(loop, clock, GameLoopTest.STEP);
        manager.clear();

        assertEquals(LoopState.STOPPED, loop.getState());
        assertEquals(List.of(first.record(UPDATE), first.record(RENDER), first.record(EXIT), first.record(DISPOSE)),
                     calls);
    }

    /**
     * Iterations that update and render a scene, and switch between two scenes, allocate nothing in steady state
     * (ADR-0019), measured with the per-thread allocation counter of the JVM.
     */
    @Test
    void testIterationAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final CountingScene a = new CountingScene();
        final CountingScene b = new CountingScene();
        final SceneManager scenes = new SceneManager();
        final GameLoop counted = new GameLoop(GameLoopTest.CONFIG, clock, new Log(), scenes, scenes);
        scenes.switchTo(a);
        iterate(counted, scenes, a, b);

        final long before = bean.getCurrentThreadAllocatedBytes();
        iterate(counted, scenes, a, b);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(a.updates > 0L && b.updates > 0L);
        assertTrue(a.others > 0L && b.others > 0L);
    }

    /**
     * Runs many iterations, switching between two scenes every hundred iterations.
     *
     * @param counted The loop.
     * @param scenes The manager of the loop.
     * @param a The first scene, active before and after.
     * @param b The second scene.
     */
    private void iterate(GameLoop counted, SceneManager scenes, CountingScene a, CountingScene b)
    {
        for (int i = 1; i <= 20_000; i++)
        {
            GameLoopTest.frame(counted, clock, GameLoopTest.STEP);
            if (i % 100 == 0 && scenes.getActive() == a)
            {
                scenes.switchTo(b);
            }
            else if (i % 100 == 0)
            {
                scenes.switchTo(a);
            }
        }
    }

    /**
     * Scene counting its calls without allocating. Entered again after a dispose, which the allocation test allows
     * itself to avoid creating scenes while it measures.
     */
    private static final class CountingScene implements Scene
    {
        /** Number of updates. */
        private long updates;
        /** Number of other calls. */
        private long others;

        /**
         * Creates the scene.
         */
        CountingScene()
        {
            super();
        }

        @Override
        public void enter()
        {
            others++;
        }

        @Override
        public void update(double stepSeconds)
        {
            updates++;
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            others++;
        }

        @Override
        public void exit()
        {
            others++;
        }

        @Override
        public void dispose()
        {
            others++;
        }
    }
}
