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
 * Standard traits and systems (REQ-STD-001 to REQ-STD-011, ADR-0011, ADR-0034, ADR-0080 to ADR-0084).
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
 * velocity;</li>
 * <li>{@link com.b3dgs.lionengine.traits.Gravity} (vertical acceleration, terminal velocity) and
 * {@link com.b3dgs.lionengine.traits.GravitySystem} (slot 2000), which accelerates the velocity of a
 * {@link com.b3dgs.lionengine.traits.Movable};</li>
 * <li>{@link com.b3dgs.lionengine.traits.Lifetime} (remaining duration) and
 * {@link com.b3dgs.lionengine.traits.LifetimeSystem} (slot 5000), which removes the object when it expires, at the
 * flush;</li>
 * <li>{@link com.b3dgs.lionengine.traits.Collidable} (box, layer, mask, trigger flag) and
 * {@link com.b3dgs.lionengine.traits.CollisionSystem} (slot 4000), which detects the overlapping pairs, reports their
 * enter and exit events and pushes objects out of the solid tiles of a
 * {@link com.b3dgs.lionengine.physics.ShapeQuery}, writing the grounded flag of their
 * {@link com.b3dgs.lionengine.traits.Jumpable};</li>
 * <li>{@link com.b3dgs.lionengine.traits.Jumpable} (impulse, grounded flag, jump held flag, coyote time, jump buffer,
 * cut factor) and {@link com.b3dgs.lionengine.traits.JumpSystem} (slot 1000), which applies the impulse with the
 * coyote time and the jump buffer and cuts an early-released jump.</li>
 * </ul>
 *
 * <p>
 * Runtime thread only.
 * </p>
 */
package com.b3dgs.lionengine.traits;
