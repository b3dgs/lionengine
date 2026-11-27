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
 * Thread-safe control requests of the game loop (ADR-0017, ADR-0031, ADR-0040, REQ-LOOP-008, REQ-LOOP-009).
 *
 * <p>
 * Requests are atomic fields read at the next frame boundary, never inbox entries, so they are never dropped. They may
 * be called from any thread (a platform maps going to the background to {@link #pause()} and a close request to
 * {@link #stop()}).
 * </p>
 *
 * @since 11.0.0
 */
public interface LoopControl
{
    /**
     * Requests a pause: from the next frame boundary no elapsed time is accumulated, so no update runs and there is no
     * catch-up on resume; rendering may continue. No effect if already paused or stopped.
     */
    void pause();

    /**
     * Requests the end of a pause: the clock reference is reset and the accumulator cleared, so time does not jump. No
     * effect if not paused.
     */
    void resume();

    /**
     * Requests a stop, honored at the end of the current iteration; never interrupts a step. Idempotent.
     */
    void stop();

    /**
     * Requests a time scale, applied at the next frame boundary. It multiplies the elapsed time; the step never
     * changes.
     *
     * @param scale The time scale in {@code [0.1, 4.0]} ({@code 1.0} is real time). Pause is a separate state, never a
     *            scale of {@code 0}.
     * @throws IllegalArgumentException If {@code scale} is outside {@code [0.1, 4.0]} or not a number.
     */
    void setTimeScale(double scale);
}
