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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Test the overlay stack of {@link SceneManager}: push and pop at a frame boundary, update of the top scene only,
 * render of every scene from the bottom, one change per frame, a switch replacing the whole stack, invalid requests,
 * failures and allocation (REQ-SCENE-003, REQ-SCENE-002, ADR-0031, ADR-0052, ADR-0059).
 */
final class SceneOverlayTest
{
    /** Step duration, in seconds. */
    private static final double STEP = 0.01;
    /** Presentation factor. */
    private static final double ALPHA = 0.5;
    /** Push and pop cycles of the allocation measurement. */
    private static final int CYCLES = 1_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per cycle. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /** Records of every scene of a test. */
    private final List<String> calls = new ArrayList<>();
    /** Base scene: the level. */
    private final RecordingScene level = new RecordingScene("level", calls);
    /** Overlay: the pause menu. */
    private final RecordingScene pause = new RecordingScene("pause", calls);
    /** Overlay above the pause: the options. */
    private final RecordingScene options = new RecordingScene("options", calls);
    /** Scene that replaces the stack: the menu. */
    private final RecordingScene menu = new RecordingScene("menu", calls);
    /** Tested manager. */
    private final SceneManager manager = new SceneManager();

    /**
     * Runs a frame: updates then a render.
     *
     * @param updates The number of updates.
     */
    private void frame(int updates)
    {
        for (int i = 0; i < updates; i++)
        {
            manager.update(STEP);
        }
        manager.render(ALPHA, STEP);
    }

    /**
     * Starts the level with the pause menu above it, and clears the records.
     */
    private void startPaused()
    {
        manager.switchTo(level);
        frame(1);
        assertTrue(manager.push(pause));
        frame(1);
        calls.clear();
    }

    /**
     * A push requested by the level during an update applies at the next frame boundary: the other steps of the frame
     * still update the level; then the pushed scene is entered and alone updated, every scene is rendered from the
     * bottom, and the covered level is not exited and keeps its state.
     */
    @Test
    void testPush()
    {
        manager.switchTo(level);
        frame(1);
        final int levelUpdates = level.getUpdates();
        calls.clear();
        level.setAction(UPDATE, () -> manager.push(pause));

        manager.update(STEP);
        assertTrue(manager.isChangePending());
        assertFalse(manager.isSwitchPending());
        manager.update(STEP);
        assertSame(level, manager.getActive());
        manager.render(ALPHA, STEP);
        level.setAction(UPDATE, null);
        frame(2);

        assertEquals(List.of(level.entry(UPDATE),
                             level.entry(UPDATE),
                             level.entry(RENDER),
                             pause.entry(ENTER),
                             pause.entry(UPDATE),
                             pause.entry(UPDATE),
                             level.entry(RENDER),
                             pause.entry(RENDER)),
                     calls);
        assertEquals(levelUpdates + 2, level.getUpdates());
        assertSame(pause, manager.getActive());
        assertEquals(2, manager.getSceneCount());
        assertSame(level, manager.getScene(0));
        assertSame(pause, manager.getScene(1));
        assertFalse(manager.isChangePending());
    }

    /**
     * A pop requested by the overlay applies at the next frame boundary: the top scene is exited and disposed, and the
     * scene below is the top again without any call, updated from that frame.
     */
    @Test
    void testPop()
    {
        startPaused();
        pause.setAction(UPDATE, () -> assertTrue(manager.pop()));

        manager.update(STEP);
        manager.render(ALPHA, STEP);
        pause.setAction(UPDATE, null);
        frame(1);

        assertEquals(List.of(pause.entry(UPDATE),
                             level.entry(RENDER),
                             pause.entry(RENDER),
                             pause.entry(EXIT),
                             pause.entry(DISPOSE),
                             level.entry(UPDATE),
                             level.entry(RENDER)),
                     calls);
        assertSame(level, manager.getActive());
        assertEquals(1, manager.getSceneCount());
    }

    /**
     * A pop requested by the overlay during its update applies at the next boundary: the other steps of the frame
     * still update the overlay.
     */
    @Test
    void testPopFromOverlay()
    {
        startPaused();
        final int pauseUpdates = pause.getUpdates();
        pause.setAction(UPDATE, manager::pop);

        frame(3);
        pause.setAction(UPDATE, null);
        frame(1);

        assertEquals(pauseUpdates + 3, pause.getUpdates());
        assertSame(level, manager.getActive());
    }

    /**
     * Overlays stack above each other, deeper than the initial capacity, and render from the bottom.
     */
    @Test
    void testDeepStack()
    {
        manager.switchTo(level);
        frame(1);
        final List<RecordingScene> overlays = new ArrayList<>();
        for (int i = 0; i < 6; i++)
        {
            final RecordingScene overlay = new RecordingScene("overlay" + i, calls);
            overlays.add(overlay);
            manager.push(overlay);
            frame(1);
        }
        calls.clear();

        frame(1);

        assertEquals(7, manager.getSceneCount());
        assertEquals(overlays.get(5).entry(UPDATE), calls.get(0));
        assertEquals(level.entry(RENDER), calls.get(1));
        assertEquals(overlays.get(5).entry(RENDER), calls.get(7));
        assertEquals(8, calls.size());
    }

    /**
     * One change is pending at a time, whatever its kind: the first request of a frame wins, the others return
     * {@code false} and their scenes receive no call.
     */
    @Test
    void testOneChangePerFrame()
    {
        startPaused();

        assertTrue(manager.push(options));
        assertFalse(manager.pop());
        assertFalse(manager.switchTo(menu));
        assertFalse(manager.push(new RecordingScene("other", calls)));
        frame(1);
        assertTrue(manager.pop());
        assertFalse(manager.push(menu));
        frame(1);

        assertEquals(List.of(options.entry(ENTER),
                             options.entry(UPDATE),
                             level.entry(RENDER),
                             pause.entry(RENDER),
                             options.entry(RENDER),
                             options.entry(EXIT),
                             options.entry(DISPOSE),
                             pause.entry(UPDATE),
                             level.entry(RENDER),
                             pause.entry(RENDER)),
                     calls);
    }

    /**
     * A switch replaces the whole stack: every scene is left from the top, then the new one is entered alone.
     */
    @Test
    void testSwitchReplacesStack()
    {
        startPaused();

        assertTrue(manager.switchTo(menu));
        assertTrue(manager.isSwitchPending());
        frame(1);

        assertEquals(List.of(pause.entry(EXIT),
                             pause.entry(DISPOSE),
                             level.entry(EXIT),
                             level.entry(DISPOSE),
                             menu.entry(ENTER),
                             menu.entry(UPDATE),
                             menu.entry(RENDER)),
                     calls);
        assertEquals(1, manager.getSceneCount());
        assertSame(menu, manager.getActive());
    }

    /**
     * A push onto an empty stack enters the scene as the only one.
     */
    @Test
    void testPushOnEmpty()
    {
        assertTrue(manager.push(level));
        frame(1);

        assertSame(level, manager.getActive());
        assertEquals(List.of(level.entry(ENTER), level.entry(UPDATE), level.entry(RENDER)), calls);
    }

    /**
     * A pop needs an overlay; a scene in the stack cannot be pushed or switched to again; nothing changes then.
     */
    @Test
    void testInvalidRequests()
    {
        assertThrows(IllegalStateException.class, manager::pop);
        manager.switchTo(level);
        assertFalse(manager.pop());
        frame(1);
        assertThrows(IllegalStateException.class, manager::pop);
        manager.push(pause);
        frame(1);

        assertThrows(IllegalArgumentException.class, () -> manager.push(level));
        assertThrows(IllegalArgumentException.class, () -> manager.push(pause));
        assertThrows(IllegalArgumentException.class, () -> manager.switchTo(level));
        assertThrows(IllegalArgumentException.class, () -> manager.push(null));
        assertFalse(manager.isChangePending());
        assertThrows(IndexOutOfBoundsException.class, () -> manager.getScene(2));
        assertThrows(IndexOutOfBoundsException.class, () -> manager.getScene(-1));
    }

    /**
     * Clear leaves every scene from the top and cancels a pending change.
     */
    @Test
    void testClear()
    {
        startPaused();
        manager.push(options);

        manager.clear();

        assertEquals(List.of(pause.entry(EXIT), pause.entry(DISPOSE), level.entry(EXIT), level.entry(DISPOSE)), calls);
        assertEquals(0, manager.getSceneCount());
        assertFalse(manager.isChangePending());
    }

    /**
     * A pushed scene whose enter throws is disposed and not added: the stack below is unchanged.
     */
    @Test
    void testPushEnterFailure()
    {
        startPaused();
        options.failOn(ENTER);
        manager.push(options);

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));

        assertEquals(List.of(options.entry(ENTER), options.entry(DISPOSE)), calls);
        assertSame(pause, manager.getActive());
        assertEquals(2, manager.getSceneCount());
    }

    /**
     * A popped scene whose exit throws is still disposed and removed.
     */
    @Test
    void testPopExitFailure()
    {
        startPaused();
        pause.failOn(EXIT);
        manager.pop();

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));

        assertEquals(List.of(pause.entry(EXIT), pause.entry(DISPOSE)), calls);
        assertSame(level, manager.getActive());
    }

    /**
     * When several scenes leave together and their exits throw, every one is still left and disposed; the first
     * exception propagates with the later one suppressed; the switch enters no scene.
     */
    @Test
    void testLeaveAllFailures()
    {
        startPaused();
        pause.setAction(EXIT, () ->
        {
            throw new IllegalStateException("pause");
        });
        level.setAction(EXIT, () ->
        {
            throw new IllegalStateException("level");
        });
        manager.switchTo(menu);

        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                              () -> manager.update(STEP));

        assertEquals("pause", exception.getMessage());
        assertEquals(1, exception.getSuppressed().length);
        assertEquals("level", exception.getSuppressed()[0].getMessage());
        assertEquals(List.of(pause.entry(EXIT), pause.entry(DISPOSE), level.entry(EXIT), level.entry(DISPOSE)), calls);
        assertEquals(0, manager.getSceneCount());
        assertFalse(manager.isChangePending());
    }

    /**
     * Pushing and popping an overlay within the capacity of the stack allocates nothing.
     */
    @Test
    void testPushPopAllocatesNothing()
    {
        final Scene base = new EmptyScene();
        final Scene overlay = new EmptyScene();
        manager.switchTo(base);
        frame(1);
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        cycles(overlay);

        final long before = bean.getCurrentThreadAllocatedBytes();
        cycles(overlay);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertSame(base, manager.getActive());
    }

    /**
     * Pushes and pops an overlay, one change per frame.
     *
     * @param overlay The overlay.
     */
    private void cycles(Scene overlay)
    {
        for (int i = 0; i < CYCLES; i++)
        {
            manager.push(overlay);
            frame(1);
            manager.pop();
            frame(1);
        }
    }

    /**
     * Scene that does nothing, so that only the manager can allocate.
     */
    private static final class EmptyScene implements Scene
    {
        /**
         * Creates the scene.
         */
        EmptyScene()
        {
            super();
        }

        @Override
        public void enter()
        {
            // Nothing to enter.
        }

        @Override
        public void update(double stepSeconds)
        {
            // Nothing to update.
        }

        @Override
        public void render(double alpha, double unscaledDeltaSeconds)
        {
            // Nothing to render.
        }

        @Override
        public void exit()
        {
            // Nothing to exit.
        }

        @Override
        public void dispose()
        {
            // Nothing to dispose.
        }
    }
}
