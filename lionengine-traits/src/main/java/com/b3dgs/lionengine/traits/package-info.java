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

/**
 * Standard traits and systems (REQ-STD-001 to REQ-STD-011, ADR-0011, ADR-0034, ADR-0080).
 *
 * <p>
 * Traits are data only, in world units ({@code double} meters, meters per second, seconds; Y up); systems implement
 * {@link com.b3dgs.lionengine.ecs.GameSystem} and declare their slot. A game registers each trait type it uses in its
 * world with the {@code TYPE} constant of the trait, creates the systems with the storages the registration returned,
 * and adds each system with its {@code SLOT}:
 * </p>
 * <ul>
 * <li>{@link com.b3dgs.lionengine.traits.Transformable} (center position, full size, previous position) and
 * {@link com.b3dgs.lionengine.traits.TransformCaptureSystem}, which captures the previous position at the start of the
 * step;</li>
 * <li>{@link com.b3dgs.lionengine.traits.Movable} (velocity, maximum speed) and
 * {@link com.b3dgs.lionengine.traits.MovementSystem} (slot 3000), which clamps the speed and integrates the
 * velocity.</li>
 * </ul>
 *
 * <p>
 * Gravity, jump, collision and lifetime come with the next batches of {@code features/standard-traits.md}. Runtime
 * thread only.
 * </p>
 */
package com.b3dgs.lionengine.traits;
