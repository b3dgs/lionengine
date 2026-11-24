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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test the lifecycle calls of {@link SceneManager} and its switch at a frame boundary (REQ-SCENE-001,
 * REQ-SCENE-002), with the loop calls made by hand.
 */
final class SceneManagerTest
{
    /** Step duration, in seconds. */
    private static final double STEP = 0.01;
    /** Presentation factor. */
    private static final double ALPHA = 0.5;

    /** Records of every scene of a test. */
    private final List<String> calls = new ArrayList<>();
    /** First scene. */
    private final RecordingScene first = new RecordingScene("first", calls);
    /** Second scene. */
    private final RecordingScene second = new RecordingScene("second", calls);
    /** Third scene. */
    private final RecordingScene third = new RecordingScene("third", calls);
    /** Tested manager. */
    private final SceneManager manager = new SceneManager();

    /**
     * Runs one frame: the given number of updates, then one render.
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
     * Starts the manager on the first scene and clears the records.
     */
    private void startFirst()
    {
        assertTrue(manager.switchTo(first));
        frame(1);
        calls.clear();
    }

    /**
     * Without a scene, updates and renders do nothing.
     */
    @Test
    void testNoScene()
    {
        frame(2);

        assertFalse(manager.hasActive());
        assertNull(manager.getActive());
        assertFalse(manager.isSwitchPending());
    }

    /**
     * The first scene, requested before the loop starts, is entered at the first call, then updated and rendered in
     * the same frame.
     */
    @Test
    void testFirstScene()
    {
        assertTrue(manager.switchTo(first));
        assertTrue(manager.isSwitchPending());
        assertFalse(manager.hasActive());

        frame(1);

        assertEquals(List.of(first.record(ENTER), first.record(UPDATE), first.record(RENDER)), calls);
        assertTrue(manager.hasActive());
        assertSame(first, manager.getActive());
        assertFalse(manager.isSwitchPending());
    }

    /**
     * The first scene is entered by a render when the first frame has no update, as with a paused loop or the first
     * iteration of the game loop.
     */
    @Test
    void testFirstSceneEnteredByRender()
    {
        manager.switchTo(first);

        manager.render(0.0, 0.0);

        assertEquals(List.of(first.record(ENTER), first.record(RENDER)), calls);
    }

    /**
     * A switch requested during an update applies after the render of the frame: the other steps of the frame still
     * update the current scene; then the current scene is exited and disposed before the next one is entered.
     */
    @Test
    void testSwitchAtFrameBoundary()
    {
        startFirst();
        first.setAction(UPDATE, () -> manager.switchTo(second));

        frame(3);

        assertEquals(List.of(first.record(UPDATE), first.record(UPDATE), first.record(UPDATE), first.record(RENDER)),
                     calls);
        assertSame(first, manager.getActive());
        assertTrue(manager.isSwitchPending());

        calls.clear();
        frame(1);

        assertEquals(List.of(first.record(EXIT),
                             first.record(DISPOSE),
                             second.record(ENTER),
                             second.record(UPDATE),
                             second.record(RENDER)),
                     calls);
        assertSame(second, manager.getActive());
    }

    /**
     * A switch requested during a render applies at the start of the next frame.
     */
    @Test
    void testSwitchRequestedInRender()
    {
        startFirst();
        first.setAction(RENDER, () -> manager.switchTo(second));

        frame(0);
        frame(0);

        assertEquals(List.of(first.record(RENDER),
                             first.record(EXIT),
                             first.record(DISPOSE),
                             second.record(ENTER),
                             second.record(RENDER)),
                     calls);
    }

    /**
     * Only the first request of a frame is kept: later ones return {@code false} and their scenes receive no call.
     */
    @Test
    void testFirstRequestWins()
    {
        startFirst();

        assertTrue(manager.switchTo(second));
        assertFalse(manager.switchTo(third));
        assertFalse(manager.switchTo(second));
        frame(1);

        assertSame(second, manager.getActive());
        assertEquals(0, third.getUpdates() + third.getRenders());
        assertFalse(calls.contains(third.record(ENTER)));
    }

    /**
     * A switch requested while a switch applies, here from {@link Scene#enter()}, applies at the next frame boundary.
     */
    @Test
    void testSwitchRequestedFromEnter()
    {
        startFirst();
        second.setAction(ENTER, () -> assertTrue(manager.switchTo(third)));
        manager.switchTo(second);

        frame(1);

        assertSame(second, manager.getActive());
        assertTrue(manager.isSwitchPending());

        frame(1);

        assertSame(third, manager.getActive());
        assertEquals(1, second.getUpdates());
    }

    /**
     * A {@code null} scene and the active scene are rejected.
     */
    @Test
    void testInvalidSwitch()
    {
        startFirst();

        assertThrows(IllegalArgumentException.class, () -> manager.switchTo(null));
        assertThrows(IllegalArgumentException.class, () -> manager.switchTo(first));
        assertFalse(manager.isSwitchPending());
    }

    /**
     * Clear exits and disposes the active scene, cancels the pending switch, is idempotent and leaves the manager
     * usable.
     */
    @Test
    void testClear()
    {
        startFirst();
        manager.switchTo(second);

        manager.clear();
        manager.clear();
        frame(1);

        assertEquals(List.of(first.record(EXIT), first.record(DISPOSE)), calls);
        assertFalse(manager.hasActive());
        assertFalse(manager.isSwitchPending());

        manager.switchTo(third);
        frame(1);

        assertSame(third, manager.getActive());
    }

    /**
     * A scene cannot clear the manager or call it again from a method the manager called.
     */
    @Test
    void testNestedCallsRejected()
    {
        startFirst();
        first.setAction(UPDATE, manager::clear);

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));
        assertSame(first, manager.getActive());

        first.setAction(UPDATE, null);
        first.setAction(RENDER, () -> manager.update(STEP));

        assertThrows(IllegalStateException.class, () -> manager.render(ALPHA, STEP));

        first.setAction(RENDER, null);
        second.setAction(ENTER, () -> manager.render(ALPHA, STEP));
        manager.switchTo(second);

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));
    }

    /**
     * A scene whose enter throws receives no further call, and the manager has no active scene.
     */
    @Test
    void testEnterFailure()
    {
        startFirst();
        second.failOn(ENTER);
        manager.switchTo(second);

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));
        manager.render(ALPHA, STEP);
        manager.clear();

        assertEquals(List.of(first.record(EXIT), first.record(DISPOSE), second.record(ENTER)), calls);
        assertFalse(manager.hasActive());
    }

    /**
     * A scene whose exit throws is not disposed, and the next scene is not entered.
     */
    @Test
    void testExitFailure()
    {
        startFirst();
        first.failOn(EXIT);
        manager.switchTo(second);

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));
        manager.clear();

        assertEquals(List.of(first.record(EXIT)), calls);
        assertFalse(manager.hasActive());
        assertFalse(manager.isSwitchPending());
    }

    /**
     * A scene whose update throws stays active: clearing the manager exits and disposes it.
     */
    @Test
    void testUpdateFailure()
    {
        startFirst();
        first.failOn(UPDATE);

        assertThrows(IllegalStateException.class, () -> manager.update(STEP));
        manager.clear();

        assertEquals(List.of(first.record(UPDATE), first.record(EXIT), first.record(DISPOSE)), calls);
    }

    /**
     * A failing exit during clear leaves no active scene and does not dispose it.
     */
    @Test
    void testClearExitFailure()
    {
        startFirst();
        first.failOn(EXIT);

        assertThrows(IllegalStateException.class, manager::clear);
        manager.clear();

        assertEquals(List.of(first.record(EXIT)), calls);
        assertFalse(manager.hasActive());
    }
}
