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
package com.b3dgs.lionengine.ecs;

/**
 * Logic over the objects of a world, run once per step in a fixed order (REQ-ECS-008, REQ-ECS-013, ADR-0011, ADR-0017,
 * ADR-0030, ADR-0034, ADR-0037, ADR-0077).
 *
 * <p>
 * A system holds no object data: it reads and changes traits, usually by iterating the {@link Query}s it created at
 * initialization, and records structural changes (creating and removing objects, adding and removing traits), which
 * the world applies at the flush that ends the step. The game implements this interface and registers the system with
 * {@link World#addSystem(GameSystem, int)} and an {@code int} slot: {@link World#update(double)} runs the enabled
 * systems from the lowest slot to the highest, systems of equal slots in registration order, then flushes once.
 * </p>
 *
 * <p>
 * Slots of the standard systems (ADR-0034): {@code Jump} 1000, {@code Gravity} 2000, {@code Movement} 3000,
 * {@code Collision} 4000, {@code Lifetime} 5000. Recommended game ranges: input and intent below 1000; decisions that
 * set velocities between 1000 and 2000; reactions to collisions between 4000 and 5000; late bookkeeping after 5000.
 * </p>
 *
 * <p>
 * Called on the runtime thread only. During {@link #update(double)} the system must not flush or update the world, nor
 * add or remove a system ({@link IllegalStateException}); it may enable or disable systems, which takes effect at the
 * next step. An exception propagates out of {@link World#update(double)}; the world is undefined afterwards and the
 * loop fails (REQ-LOOP-010).
 * </p>
 *
 * <p>
 * One abstract method, so a lambda or a method reference is a system.
 * </p>
 *
 * @since 11.0.0
 */
public interface GameSystem
{
    /**
     * Runs the system for one step.
     *
     * @param stepSeconds The constant step of the loop, in seconds, positive and finite (ADR-0017).
     */
    void update(double stepSeconds);
}
