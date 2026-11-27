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
 * State of a {@link GameLoop} (ADR-0017, REQ-LOOP-010).
 *
 * <p>
 * {@link #CREATED} until the first iteration, then {@link #RUNNING} and {@link #PAUSED} in turn, and finally
 * {@link #STOPPED} after a stop request, or {@link #FAILED} when an update, a render, an inbox event or a listener
 * throws.
 * </p>
 *
 * @since 11.0.0
 */
public enum LoopState
{
    /** Created, no iteration yet. */
    CREATED,
    /** Running: the simulation advances. */
    RUNNING,
    /** Paused: no time is accumulated; rendering continues with a frozen factor. */
    PAUSED,
    /** Stopped by a request, at the end of an iteration. */
    STOPPED,
    /** Stopped by an exception, which was rethrown to the caller of the iteration. */
    FAILED
}
