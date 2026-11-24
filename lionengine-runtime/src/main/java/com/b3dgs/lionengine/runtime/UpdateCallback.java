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
 * Simulation step of the game loop (ADR-0017, ADR-0040, REQ-LOOP-003, REQ-LOOP-007).
 *
 * <p>
 * Wired by the application or {@code composition}: advance every input state to the step, then update the scene
 * manager. The game loop itself knows neither input nor scenes.
 * </p>
 *
 * <p>
 * Thread: runtime thread. Must allocate nothing in steady state (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface UpdateCallback
{
    /**
     * Runs one fixed simulation step.
     *
     * @param stepSeconds The constant step duration in seconds (positive, {@code 1.0 / 60} by default); identical for
     *            every step, whatever the time scale.
     */
    void update(double stepSeconds);
}
