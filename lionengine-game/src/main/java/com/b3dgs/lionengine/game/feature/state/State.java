/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.state;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;

/**
 * States are designed to implement easily a gameplay by reducing implementation complexity.
 * <p>
 * In other word, gameplay can be represented as a finite state machine. This will ensure gameplay state validity,
 * instead of checking tons of boolean to catch the right state.
 * </p>
 * <p>
 * The only counter part is the number of class, which is usually one class per state. So typically, for a simple
 * gameplay (<code>Idle, Walk, Jump</code>), it will need 3 state classes, plus the {@link StateHandler}.
 * </p>
 * <p>
 * Usage is quite simple:
 * </p>
 * <ul>
 * <li>{@link #enter()} one time on the state (reset default state and prepare for update)</li>
 * <li>{@link #update(double)} the state for each game loop</li>
 * <li>The {@link StateHandler} will allow to choose which state should be then returned if needed, and {@link #enter()}
 * will be called, and so on</li>
 * </ul>
 * 
 * @see StateHandler
 */
public interface State extends Updatable
{
    /**
     * Add a transition with another state.
     * 
     * @param next The next state.
     * @param checker The transition checker.
     * @throws LionEngineException If adding transition to itself.
     * @see StateLast
     */
    void addTransition(Class<? extends State> next, StateChecker checker);

    /**
     * Clear all transitions defined.
     */
    void clearTransitions();

    /**
     * Called by the {@link StateHandler} when entering in the state.
     */
    void enter();

    /**
     * Called by the {@link StateHandler} when exiting the state.
     */
    void exit();

    /**
     * Check the transitions in order to find the next state.
     * 
     * @param last The last state (<code>null</code> if none).
     * @return The next state type (<code>null</code> if none).
     */
    Class<? extends State> checkTransitions(Class<? extends State> last);
}
