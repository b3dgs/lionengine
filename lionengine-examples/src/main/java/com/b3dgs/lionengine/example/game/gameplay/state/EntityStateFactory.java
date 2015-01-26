/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.game.gameplay.state;

import java.util.EnumMap;
import java.util.Map;

import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.game.trait.Mirrorable;
import com.b3dgs.lionengine.game.trait.Movable;

/**
 * Entity state factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <E> The entity type used.
 */
public class EntityStateFactory<E extends Movable & Mirrorable & Animator>
{
    /** List of available states. */
    private final Map<EntityStateType, EntityState<E>> states;

    /**
     * Create the factory.
     */
    public EntityStateFactory()
    {
        states = new EnumMap<>(EntityStateType.class);
    }

    /**
     * Add a supported state.
     * 
     * @param type The state type.
     * @param state The state instance.
     */
    public void addState(EntityStateType type, EntityState<E> state)
    {
        states.put(type, state);
    }

    /**
     * Get the state instance from its type.
     * 
     * @param type The state type.
     * @return The state instance.
     */
    public EntityState<E> getState(EntityStateType type)
    {
        final EntityState<E> state = states.get(type);
        state.clear();
        return state;
    }
}
