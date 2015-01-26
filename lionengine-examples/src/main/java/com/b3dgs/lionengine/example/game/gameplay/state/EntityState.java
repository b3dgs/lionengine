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

import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.trait.Mirrorable;
import com.b3dgs.lionengine.game.trait.Movable;

/**
 * Entity state interface.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <E> The object type used.
 */
public abstract class EntityState<E extends Movable & Mirrorable & Animator>
{
    /**
     * Handle the input by retrieving its state.
     * 
     * @param entity The entity reference.
     * @param factory The state factory reference.
     * @param input The input reference.
     * @return The new state (<code>null</code> if unchanged).
     */
    public EntityState<E> handleInput(E entity, EntityStateFactory<E> factory, InputDevice input)
    {
        if (input instanceof InputDeviceDirectional)
        {
            return handleInput(entity, factory, (InputDeviceDirectional) input);
        }
        return null;
    }

    /**
     * Clear the state to its default.
     */
    public abstract void clear();

    /**
     * Update the entity state.
     * 
     * @param entity The entity reference.
     */
    public abstract void updateState(E entity);

    /**
     * Called when entering in the state.
     * 
     * @param entity The entity reference.
     */
    public abstract void enter(E entity);

    /**
     * Handle the input by retrieving its direction.
     * 
     * @param entity The entity reference.
     * @param factory The state factory reference.
     * @param input The input reference.
     * @return The new state (<code>null</code> if unchanged).
     */
    protected abstract EntityState<E> handleInput(E entity, EntityStateFactory<E> factory, InputDeviceDirectional input);
}
