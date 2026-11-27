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
 * The description of a trait type: how its pooled instances are created and reset (REQ-ECS-004, ADR-0011, ADR-0030,
 * ADR-0037, ADR-0075).
 *
 * <p>
 * A trait is data only: a plain class of fields, with no logic, which lives in systems. The game implements this
 * interface once per trait type, usually as a constant, and registers it explicitly in each world that uses it with
 * {@link World#register(TraitType)}; the world gives it an {@code int} type ID and a {@link TraitStorage}. Nothing is
 * found by reflection, class scanning or bytecode generation (ADR-0020).
 * </p>
 *
 * <p>
 * The world owns the instances: it creates them ahead with {@link #create()}, hands one out at each addition, and
 * returns it to its pool with {@link #reset(Object)} when the trait is removed, so the steady add and remove paths
 * allocate nothing. An implementation must therefore not keep a reference to an instance, and a game must not keep a
 * reference to a trait after its removal: the instance is reused by another object.
 * </p>
 *
 * <p>
 * Called on the runtime thread only, by the world that registered the type; both methods must not change the world.
 * </p>
 *
 * @param <T> The trait class.
 * @since 11.0.0
 */
public interface TraitType<T>
{
    /**
     * Creates an instance in its reset state, the state {@link #reset(Object)} gives back.
     *
     * <p>
     * Called when the type is registered, to fill the pool up to the trait capacity, by
     * {@link TraitStorage#reserve(int)}, and when an addition finds the pool empty (a warm-up allocation).
     * </p>
     *
     * @return A new instance, not {@code null}, referenced by nothing else.
     */
    T create();

    /**
     * Resets an instance to the state of a new one, when its trait is removed from an object, replaced, or discarded
     * because its object was removed before the flush.
     *
     * @param trait The instance, never {@code null}, back in the pool after this call.
     */
    void reset(T trait);
}
