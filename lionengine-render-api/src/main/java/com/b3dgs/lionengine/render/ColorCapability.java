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
package com.b3dgs.lionengine.render;

/**
 * Typed capability query of the per-frame color effects of a render backend (ADR-0026, ADR-0056, REQ-SPRITE-004).
 *
 * <p>
 * Every {@link RenderBackend} is a color capability. The {@code graphics} module asks it which effects of a color
 * transform the backend applies per frame, and bakes the others on the CPU; it never needs the whole backend. The
 * answer of a backend never changes.
 * </p>
 *
 * @since 11.0.0
 */
public interface ColorCapability
{
    /**
     * Tells whether the backend applies a color effect of the command entries per frame.
     *
     * @param effect The effect (never {@code null}).
     * @return {@code true} if the backend applies the effect; {@code false} if it draws the entries as if the field
     *         kept its default.
     * @throws IllegalArgumentException If {@code effect} is {@code null}.
     */
    boolean isSupported(ColorEffect effect);
}
