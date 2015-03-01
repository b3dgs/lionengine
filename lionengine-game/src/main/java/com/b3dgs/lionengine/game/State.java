/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.Updatable;

/**
 * States are designed to implement easily a gameplay by reducing implementation complexity.
 * <p>
 * In other word, gameplay can be represented as a finite state machine. This will ensure gameplay state validity,
 * instead of checking tons of boolean to catch the right state.
 * </p>
 * <p>
 * The only counter part is the number of class, which is usually one class per state. So typically, for a simple
 * gameplay (<code>Idle, Walk, Jump</code>), it will need 3 state classes, plus the {@link StateFactory}.
 * </p>
 * <p>
 * Usage is quite simple:
 * </p>
 * <ul>
 * <li>{@link #enter()} one time on the state (reset default state and prepare for update)</li>
 * <li>{@link #update(double)} the state for each game loop</li>
 * <li>{@link #handleInput(StateFactory, InputDevice)} will allow to return the next state, depending of the user
 * {@link InputDevice} usage. The state needs to implement {@link #handleInput(StateFactory, InputDevice)}</li>
 * <li>The {@link StateFactory} will allow to choose which state should be then returned if needed, and {@link #enter()}
 * will be called, and so on</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see StateFactory
 */
public interface State
        extends Updatable
{
    /**
     * Handle the input by retrieving its state.
     * 
     * @param factory The state factory reference.
     * @param input The input reference.
     * @return The new state (<code>null</code> if unchanged).
     */
    State handleInput(StateFactory factory, InputDevice input);

    /**
     * Called by the {@link StateFactory} when entering in the state.
     */
    void enter();

    /**
     * Get the corresponding state enum value.
     * 
     * @return The state enum value.
     */
    Enum<?> getState();
}
