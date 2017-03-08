/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.state;

/**
 * Represents the transition between two {@link State}.
 * <p>
 * It is possible to listen to {@link com.b3dgs.lionengine.InputDevice}, by implementing children of
 * {@link StateTransitionInputChecker}.
 * </p>
 * 
 * @see State
 * @see StateTransitionInputChecker
 */
public abstract class StateTransition
{
    /** State enum. */
    private final Enum<?> state;

    /**
     * Create the transition.
     * 
     * @param state The state enum.
     */
    protected StateTransition(Enum<?> state)
    {
        this.state = state;
    }

    /**
     * Called when transition has been performed. Does nothing by default.
     */
    public void exit()
    {
        // Nothing by default
    }

    /**
     * Get the next state after the transition.
     * 
     * @return The next state.
     */
    public Enum<?> getNext()
    {
        return state;
    }
}
