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
 * Keeps the active scene and switches it at a frame boundary (ADR-0021, ADR-0031, REQ-SCENE-001, REQ-SCENE-002).
 *
 * <p>
 * The game creates the scene instances and requests a switch with {@link #switchTo(Scene)}; there is no registry and
 * no lookup by id. The manager receives the loop's calls: give it to the {@link GameLoop} as both callbacks, or call
 * {@link #update(double)} and {@link #render(double, double)} from the game's own callbacks, after advancing the input
 * states. {@link #render(double, double)} must be called once per frame, even when the game draws nothing, because the
 * frame ends there.
 * </p>
 *
 * <p>
 * A requested switch applies at the next frame boundary: after the render of the frame in which it was requested,
 * before the first update or render of the next frame. Never inside a step, so the steps that follow the request in the
 * same frame still update the current scene. At the boundary, the current scene is exited and disposed, then the next
 * one is entered; it is updated and rendered in that same frame. One switch can be pending: the first request of a
 * frame wins and later ones return {@code false}, so a request repeated at every step, or in every step of a catch-up
 * frame, gives the same result.
 * </p>
 *
 * <p>
 * An exception thrown by a scene propagates unchanged, so the game loop fails (ADR-0017). A scene whose
 * {@link Scene#enter()} or {@link Scene#exit()} threw is still disposed, once, and receives no other call
 * (ADR-0052); an exception thrown by that disposal is added to the first one as a suppressed exception. When the exit
 * of the current scene throws, the next scene is not entered. At the end of the game, once the loop has stopped or
 * failed, {@link #clear()} exits and disposes the active scene.
 * </p>
 *
 * <p>
 * Thread: runtime thread only; not thread-safe. Allocates nothing in steady state (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
public final class SceneManager implements UpdateCallback, RenderCallback
{
    /** Scene updated and rendered, {@code null} if none. */
    private Scene active;
    /** Scene entered at the next frame boundary, {@code null} if none. */
    private Scene pending;
    /** {@code true} between the first call of a frame and its render: the boundary of the frame is passed. */
    private boolean frameStarted;
    /** {@code true} while a scene method called by this manager runs. */
    private boolean calling;
    /** Guard disposing a scene, reused by every switch (no allocation). */
    private final Disposal disposal = new Disposal();

    /**
     * Creates a manager without scene: until the first switch applies, updates and renders do nothing.
     */
    public SceneManager()
    {
        super();
    }

    /**
     * Requests the switch to a scene at the next frame boundary.
     *
     * <p>
     * May be called at any time on the runtime thread: from a scene method (the usual case), from a callback, or
     * before the loop starts to set the first scene. A scene method called while the switch applies, such as
     * {@link Scene#enter()}, may request the next switch, which then applies at the following frame boundary.
     * </p>
     *
     * @param next The scene to enter, a new instance that was never given to a manager.
     * @return {@code true} if the switch is accepted, {@code false} if a switch is already pending: {@code next} is
     *         then ignored and receives no call.
     * @throws IllegalArgumentException If {@code next} is {@code null} or is the active scene.
     */
    public boolean switchTo(Scene next)
    {
        if (next == null)
        {
            throw new IllegalArgumentException("Scene must not be null");
        }
        if (next == active)
        {
            throw new IllegalArgumentException("Scene already active: " + next);
        }
        final boolean accepted = pending == null;
        if (accepted)
        {
            pending = next;
        }
        return accepted;
    }

    /**
     * Checks if a switch waits for the next frame boundary.
     *
     * @return {@code true} if a switch is pending.
     */
    public boolean isSwitchPending()
    {
        return pending != null;
    }

    /**
     * Checks if a scene is active.
     *
     * @return {@code true} if a scene is active.
     */
    public boolean hasActive()
    {
        return active != null;
    }

    /**
     * Returns the active scene.
     *
     * @return The scene updated and rendered, {@code null} if none (see {@link #hasActive()}).
     */
    public Scene getActive()
    {
        return active;
    }

    /**
     * Exits and disposes the active scene now and cancels a pending switch; the pending scene, never entered, receives
     * no call. Meant for the end of the game, once the loop has stopped or failed, outside any update and render.
     * Idempotent; the manager stays usable.
     *
     * @throws IllegalStateException If called from a scene method called by this manager.
     */
    public void clear()
    {
        checkNotCalling();
        pending = null;
        final Scene previous = active;
        if (previous != null)
        {
            active = null;
            calling = true;
            try
            {
                leave(previous);
            }
            finally
            {
                calling = false;
            }
        }
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
     * Enters a scene. If its enter throws, it is disposed (not exited) before the exception propagates.
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
    }

    /**
     * Applies a pending switch at the first call of a frame.
     */
    private void startFrame()
    {
        if (!frameStarted)
        {
            frameStarted = true;
            if (pending != null)
            {
                applySwitch();
            }
        }
    }

    /**
     * Exits and disposes the active scene, then enters the pending one.
     */
    private void applySwitch()
    {
        final Scene next = pending;
        pending = null;
        final Scene previous = active;
        active = null;
        calling = true;
        try
        {
            if (previous != null)
            {
                leave(previous);
            }
            enter(next);
            active = next;
        }
        finally
        {
            calling = false;
        }
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
     * Updates the active scene, after applying a pending switch if this call starts a frame.
     *
     * @param stepSeconds The constant step duration in seconds.
     * @throws IllegalStateException If called from a scene method called by this manager.
     */
    @Override
    public void update(double stepSeconds)
    {
        checkNotCalling();
        startFrame();
        if (active != null)
        {
            calling = true;
            try
            {
                active.update(stepSeconds);
            }
            finally
            {
                calling = false;
            }
        }
    }

    /**
     * Renders the active scene and ends the frame, after applying a pending switch if this call starts the frame.
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
        if (active != null)
        {
            calling = true;
            try
            {
                active.render(alpha, unscaledDeltaSeconds);
            }
            finally
            {
                calling = false;
            }
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
