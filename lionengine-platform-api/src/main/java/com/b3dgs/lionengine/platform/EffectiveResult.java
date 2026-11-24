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
package com.b3dgs.lionengine.platform;

/**
 * What the platform applied for a display request, field by field (ADR-0006, ADR-0018, ADR-0046, REQ-DISPLAY-002,
 * REQ-DISPLAY-008, REQ-FILTER-002).
 *
 * <p>
 * Applying a request returns the accepted request without blocking. The effective result is delivered through the
 * inbox at the frame boundary where the change is applied, and stays readable as the current state with
 * {@link Platform#getEffectiveResult()}. A missing capability is reported here, never thrown. Immutable and
 * thread-safe.
 * </p>
 *
 * @param requested The request as given.
 * @param effective The settings actually applied, with every field known.
 * @param degraded {@code true} if at least one field differs from the request.
 * @param reason The explanation of the differences, empty when not degraded.
 * @since 11.0.0
 */
public record EffectiveResult(DisplayRequest requested, DisplayRequest effective, boolean degraded, String reason)
{
    /**
     * Creates a result.
     *
     * @param requested The request as given (never {@code null}).
     * @param effective The settings actually applied (never {@code null}).
     * @param degraded {@code true} if at least one field differs from the request.
     * @param reason The explanation of the differences, empty when not degraded (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    public EffectiveResult
    {
        if (requested == null || effective == null || reason == null)
        {
            throw new IllegalArgumentException("Invalid effective result");
        }
    }
}
