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
 * State of an {@link AsyncHandle} (ADR-0019, ADR-0031, REQ-RT-004).
 *
 * <p>
 * A handle starts {@link #PENDING} and reaches exactly one final state; states change only on the runtime thread, at
 * inbox delivery.
 * </p>
 *
 * @since 11.0.0
 */
public enum AsyncState
{
    /** The work is queued or running; no value yet. */
    PENDING,
    /** The value is available. Final state. */
    READY,
    /** The work failed; the handle keeps the exception. Final state. */
    FAILED,
    /** The handle was cancelled; a late result is discarded and released. Final state. */
    CANCELLED
}
