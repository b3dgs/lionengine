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
package com.b3dgs.lionengine;

/**
 * Game loop as seen by the platform that runs it, on the thread it requires (ADR-0018, ADR-0040).
 *
 * <p>
 * The platform calls {@link #iterate()} repeatedly on its runtime thread (a dedicated thread on desktop, the renderer
 * thread on OpenGL and Android) while {@link #isRunning()} is {@code true}. The control requests inherited from
 * {@link LoopControl} stay callable from any thread.
 * </p>
 *
 * @since 11.0.0
 */
public interface LoopDriver extends LoopControl
{
    /**
     * Runs one iteration on the calling thread: read the control requests, deliver the inbox, run zero or more fixed
     * steps, render once. The first call starts the loop with an elapsed time of {@code 0}.
     *
     * <p>
     * Fail-fast: an exception thrown by an update, a render or a listener puts the loop in a failed state and is
     * rethrown to the caller; no render follows a failed update (ADR-0017).
     * </p>
     *
     * @throws IllegalStateException If the loop is stopped or failed.
     */
    void iterate();

    /**
     * Tells whether the loop is running.
     *
     * @return {@code true} from the first iteration until the loop stops or fails (a paused loop is running),
     *         {@code false} before the first iteration and after the stop or the failure.
     */
    boolean isRunning();
}
