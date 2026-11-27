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
 * Observes the display changes of a {@link Platform}: called on the runtime thread, at the inbox delivery that follows
 * the frame boundary where a display request was applied, or where a surface resize changed the effective result
 * (ADR-0006, ADR-0060, REQ-DISPLAY-005, REQ-DISPLAY-008).
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface DisplayListener
{
    /**
     * Called once a display change is applied.
     *
     * @param result The effective result of the change: requested and effective values, the {@code degraded} flag and
     *            the reason (never {@code null}).
     */
    void onDisplayChanged(EffectiveResult result);
}
