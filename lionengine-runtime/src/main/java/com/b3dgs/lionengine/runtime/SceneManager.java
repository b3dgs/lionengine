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

/**
 * Keeps the stack of scenes: a base scene and the overlays pushed above it, changed at a frame boundary (ADR-0021,
 * ADR-0031, ADR-0059, REQ-SCENE-001, REQ-SCENE-002, REQ-SCENE-003).
 *
 * <p>
 * The game creates the scene instances and requests changes; there is no registry and no lookup by id:
 * </p>
 *
 * <ul>
 * <li>{@link #switchTo(Scene)} replaces the whole stack: every scene is left from the top to the bottom, then the new
 * scene is entered alone;</li>
 * <li>{@link #push(Scene)} enters a scene on top, such as a pause menu; the covered scenes keep their state;</li>
 * <li>{@link #pop()} exits and disposes the top scene; the scene below becomes the top again without any call. A pop
 * needs at least two scenes in the stack.</li>
 * </ul>
 *
 * <p>
 * Only the top scene ({@link #getActive()}) is updated; every scene of the stack is rendered, from the bottom to the
 * top. The manager receives the loop's calls: give it to the {@link GameLoop} as both callbacks, or call
 * {@link #update(double)} and {@link #render(double, double)} from the game's own callbacks, after advancing the input
 * states. {@link #render(double, double)} must be called once per frame, even when the game draws nothing, because the
 * frame ends there.
 * </p>
 *
 * <p>
 * A requested change applies at the next frame boundary: after the render of the frame in which it was requested,
 * before the first update or render of the next frame. Never inside a step, so the steps that follow the request in the
 * same frame still update the current top scene. A scene entered at the boundary is updated and rendered in that same
 * frame. One change can be pending, whatever its kind: the first request of a frame wins and later ones return
 * {@code false}, so a request repeated at every step, or in every step of a catch-up frame, gives the same result.
 * </p>
 *
 * <p>
 * An exception thrown by a scene propagates unchanged, so the game loop fails (ADR-0017). A scene whose
 * {@link Scene#enter()} or {@link Scene#exit()} threw is still disposed, once, and receives no other call
 * (ADR-0052); an exception thrown by that disposal is added to the first one as a suppressed exception. When several
 * scenes leave together, by a switch or {@link #clear()}, every one is left even if one fails: the first exception
 * propagates once all are left, with the later ones suppressed, and a switch then enters no scene. At the end of the
 * game, once the loop has stopped or failed, {@link #clear()} leaves every scene.
 * </p>
 *
 * <p>
 * Thread: runtime thread only; not thread-safe. Allocates nothing in steady state (ADR-0019): the stack grows only
 * when it is deeper than ever before.
 * </p>
 *
 * @since 11.0.0
 */
public final class SceneManager implements UpdateCallback, RenderCallback
{
    /** Initial capacity of the stack. */
    private static final int CAPACITY = 4;
    /** Error message of a null scene. */
    private static final String ERROR_NULL = "Scene must not be null";
    /** Error message of a scene already in the stack. */
    private static final String ERROR_IN_STACK = "Scene already in the stack: ";

    /**
     * Kind of a pending change.
     */
    private enum Change
    {
        /** Replace the whole stack. */
        SWITCH,
        /** Enter a scene on top. */
        PUSH,
        /** Leave the top scene. */
        POP
    }

    /** Scenes of the stack, from the bottom; {@code null} above {@link #size}. */
    private Scene[] stack = new Scene[CAPACITY];
    /** Number of scenes in the stack. */
    private int size;
    /** Kind of the change applied at the next frame boundary, {@code null} if none. */
    private Change change;
    /** Scene of the pending switch or push, {@code null} otherwise. */
    private Scene pending;
    /** {@code true} between the first call of a frame and its render: the boundary of the frame is passed. */
    private boolean frameStarted;
    /** {@code true} while a scene method called by this manager runs. */
    private boolean calling;
    /** Guard disposing a scene, reused by every change (no allocation). */
    private final Disposal disposal = new Disposal();

    /**
     * Creates a manager without scene: until the first switch or push applies, updates and renders do nothing.
     */
    public SceneManager()
    {
        super();
    }

    /**
     * Requests the replacement of the whole stack by a scene at the next frame boundary: every scene is left, from the
     * top to the bottom, then the new scene is entered alone.
     *
     * <p>
     * May be called at any time on the runtime thread: from a scene method (the usual case), from a callback, or
     * before the loop starts to set the first scene. A scene method called while a change applies, such as
     * {@link Scene#enter()}, may request the next change, which then applies at the following frame boundary.
     * </p>
     *
     * @param next The scene to enter, a new instance that was never given to a manager.
     * @return {@code true} if the switch is accepted, {@code false} if a change is already pending: {@code next} is
     *         then ignored and receives no call.
     * @throws IllegalArgumentException If {@code next} is {@code null} or is in the stack.
     */
    public boolean switchTo(Scene next)
    {
        checkNew(next);
        return request(Change.SWITCH, next);
    }

    /**
     * Requests a scene on top of the stack at the next frame boundary, such as a pause menu: it is entered, then
     * updated and rendered last; the covered scenes are no longer updated, keep their state and are still rendered.
     * On an empty stack, the scene is entered as the only one.
     *
     * @param overlay The scene to enter, a new instance that was never given to a manager.
     * @return {@code true} if the push is accepted, {@code false} if a change is already pending: {@code overlay} is
     *         then ignored and receives no call.
     * @throws IllegalArgumentException If {@code overlay} is {@code null} or is in the stack.
     */
    public boolean push(Scene overlay)
    {
        checkNew(overlay);
        return request(Change.PUSH, overlay);
    }

    /**
     * Requests the removal of the top scene at the next frame boundary: it is exited and disposed, and the scene below
     * becomes the top again, without any call.
     *
     * @return {@code true} if the pop is accepted, {@code false} if a change is already pending.
     * @throws IllegalStateException If no change is pending and the stack holds fewer than two scenes: the base scene
     *             is replaced by {@link #switchTo(Scene)}, and every scene leaves with {@link #clear()}.
     */
    public boolean pop()
    {
        if (change == null && size < 2)
        {
            throw new IllegalStateException("No overlay to pop, scenes in the stack: " + size);
        }
        return request(Change.POP, null);
    }

    /**
     * Checks if a switch waits for the next frame boundary.
     *
     * @return {@code true} if the pending change is a switch.
     */
    public boolean isSwitchPending()
    {
        return change == Change.SWITCH;
    }

    /**
     * Checks if a change waits for the next frame boundary.
     *
     * @return {@code true} if a switch, a push or a pop is pending.
     */
    public boolean isChangePending()
    {
        return change != null;
    }

    /**
     * Checks if a scene is active.
     *
     * @return {@code true} if the stack holds a scene.
     */
    public boolean hasActive()
    {
        return size > 0;
    }

    /**
     * Returns the active scene: the top of the stack.
     *
     * @return The scene updated, {@code null} if none (see {@link #hasActive()}).
     */
    public Scene getActive()
    {
        final Scene top;
        if (size > 0)
        {
            top = stack[size - 1];
        }
        else
        {
            top = null;
        }
        return top;
    }

    /**
     * Returns the number of scenes in the stack.
     *
     * @return The number of scenes, {@code 0} if none.
     */
    public int getSceneCount()
    {
        return size;
    }

    /**
     * Returns a scene of the stack.
     *
     * @param index The index from the bottom of the stack, {@code 0} for the base scene.
     * @return The scene.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, getSceneCount())}.
     */
    public Scene getScene(int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException("Scene index: " + index + " of " + size);
        }
        return stack[index];
    }

    /**
     * Leaves every scene of the stack now, from the top to the bottom, and cancels a pending change; the pending
     * scene, never entered, receives no call. Meant for the end of the game, once the loop has stopped or failed,
     * outside any update and render. Idempotent; the manager stays usable.
     *
     * @throws IllegalStateException If called from a scene method called by this manager.
     */
    public void clear()
    {
        checkNotCalling();
        change = null;
        pending = null;
        calling = true;
        try
        {
            leaveAll();
        }
        finally
        {
            calling = false;
        }
    }

    /**
     * Checks a scene given to a switch or a push.
     *
     * @param scene The scene.
     * @throws IllegalArgumentException If the scene is {@code null} or is in the stack.
     */
    private void checkNew(Scene scene)
    {
        if (scene == null)
        {
            throw new IllegalArgumentException(ERROR_NULL);
        }
        for (int i = 0; i < size; i++)
        {
            if (stack[i] == scene)
            {
                throw new IllegalArgumentException(ERROR_IN_STACK + scene);
            }
        }
    }

    /**
     * Records a change if none is pending.
     *
     * @param kind The kind of change.
     * @param scene The scene of a switch or a push, {@code null} for a pop.
     * @return {@code true} if the change is accepted.
     */
    private boolean request(Change kind, Scene scene)
    {
        final boolean accepted = change == null;
        if (accepted)
        {
            change = kind;
            pending = scene;
        }
        return accepted;
    }

    /**
     * Exits then disposes a scene. If its exit throws, it is still disposed before the exception propagates.
     *
     * @param scene The scene to leave.
     */
    private void leave(Scene scene)
    {
        try (Disposal guard = disposal.guard(scene))
        {
            scene.exit();
            guard.keep(false);
        }
    }

    /**
     * Leaves every scene of the stack, from the top to the bottom, even if one fails; the stack is then empty.
     *
     * @throws RuntimeException The first exception of a scene, the later ones suppressed in it.
     */
    private void leaveAll()
    {
        RuntimeException failure = null;
        while (size > 0)
        {
            size--;
            final Scene scene = stack[size];
            stack[size] = null;
            try
            {
                leave(scene);
            }
            // CHECKSTYLE IGNORE LINE: IllegalCatch
            catch (final RuntimeException exception)
            {
                if (failure == null)
                {
                    failure = exception;
                }
                else
                {
                    failure.addSuppressed(exception);
                }
            }
        }
        if (failure != null)
        {
            throw failure;
        }
    }

    /**
     * Enters a scene and puts it on top of the stack. If its enter throws, it is disposed (not exited) and not added
     * before the exception propagates.
     *
     * @param scene The scene to enter.
     */
    private void enter(Scene scene)
    {
        try (Disposal guard = disposal.guard(scene))
        {
            scene.enter();
            guard.keep(true);
        }
        if (size == stack.length)
        {
            final Scene[] grown = new Scene[size * 2];
            System.arraycopy(stack, 0, grown, 0, size);
            stack = grown;
        }
        stack[size] = scene;
        size++;
    }

    /**
     * Applies a pending change if this call starts a frame, for {@link SceneFlow}, which observes the change before
     * the first update or render of the frame.
     *
     * @throws IllegalStateException If called from a scene method called by this manager.
     */
    void beginFrame()
    {
        checkNotCalling();
        startFrame();
    }

    /**
     * Applies a pending change at the first call of a frame.
     */
    private void startFrame()
    {
        if (!frameStarted)
        {
            frameStarted = true;
            if (change != null)
            {
                applyChange();
            }
        }
    }

    /**
     * Applies the pending change.
     */
    private void applyChange()
    {
        final Change kind = change;
        final Scene next = pending;
        change = null;
        pending = null;
        calling = true;
        try
        {
            switch (kind)
            {
                case SWITCH ->
                {
                    leaveAll();
                    enter(next);
                }
                case PUSH -> enter(next);
                default -> leaveTop();
            }
        }
        finally
        {
            calling = false;
        }
    }

    /**
     * Removes the top scene of the stack, then exits and disposes it.
     */
    private void leaveTop()
    {
        size--;
        final Scene top = stack[size];
        stack[size] = null;
        leave(top);
    }

    /**
     * Checks that no scene method called by this manager is running.
     *
     * @throws IllegalStateException If a scene method called by this manager is running.
     */
    private void checkNotCalling()
    {
        if (calling)
        {
            throw new IllegalStateException("Nested call from a scene of this manager");
        }
    }

    /**
     * Updates the top scene, after applying a pending change if this call starts a frame.
     *
     * @param stepSeconds The constant step duration in seconds.
     * @throws IllegalStateException If called from a scene method called by this manager.
     */
    @Override
    public void update(double stepSeconds)
    {
        checkNotCalling();
        startFrame();
        if (size > 0)
        {
            calling = true;
            try
            {
                stack[size - 1].update(stepSeconds);
            }
            finally
            {
                calling = false;
            }
        }
    }

    /**
     * Renders every scene of the stack from the bottom to the top and ends the frame, after applying a pending change
     * if this call starts the frame.
     *
     * @param alpha The presentation factor in {@code [0, 1)}.
     * @param unscaledDeltaSeconds The real time elapsed since the previous render, in seconds.
     * @throws IllegalStateException If called from a scene method called by this manager.
     */
    @Override
    public void render(double alpha, double unscaledDeltaSeconds)
    {
        checkNotCalling();
        startFrame();
        frameStarted = false;
        calling = true;
        try
        {
            for (int i = 0; i < size; i++)
            {
                stack[i].render(alpha, unscaledDeltaSeconds);
            }
        }
        finally
        {
            calling = false;
        }
    }

    /**
     * Disposes a scene when closed, unless kept. Used in a try-with-resources, so that a failure of the disposal is
     * added to the exception already thrown by the scene instead of replacing it.
     */
    private static final class Disposal implements AutoCloseable
    {
        /** Scene to dispose. */
        private Scene scene;
        /** {@code true} if the scene is kept when closed. */
        private boolean kept;

        /**
         * Creates an empty guard.
         */
        Disposal()
        {
            super();
        }

        /**
         * Guards a scene.
         *
         * @param guarded The scene to dispose when closed.
         * @return This guard.
         */
        Disposal guard(Scene guarded)
        {
            scene = guarded;
            kept = false;
            return this;
        }

        /**
         * Tells whether the guarded scene is kept when closed.
         *
         * @param keep {@code true} to keep it, {@code false} to dispose it.
         */
        void keep(boolean keep)
        {
            kept = keep;
        }

        @Override
        public void close()
        {
            final Scene guarded = scene;
            scene = null;
            if (!kept)
            {
                guarded.dispose();
            }
        }
    }
}
