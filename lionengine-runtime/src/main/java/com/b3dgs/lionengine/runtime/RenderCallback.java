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
 * Presentation of one frame by the game loop (ADR-0017, ADR-0031, ADR-0040, REQ-LOOP-003, REQ-LOOP-006, REQ-LOOP-012).
 *
 * <p>
 * Wired by the application or {@code composition}: render the scene manager into the command buffer, then present it
 * through the render backend. Presentation never changes simulation state.
 * </p>
 *
 * <p>
 * Thread: runtime thread. Must allocate nothing in steady state (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface RenderCallback
{
    /**
     * Renders one frame.
     *
     * @param alpha The presentation factor in {@code [0, 1)}: how far the frame is between the last two simulation
     *            steps; frozen while paused.
     * @param unscaledDeltaSeconds The real time elapsed since the previous render, in seconds, capped by the lag cap
     *            and independent of pause and time scale; for presentation-only state such as menus.
     */
    void render(double alpha, double unscaledDeltaSeconds);
}
