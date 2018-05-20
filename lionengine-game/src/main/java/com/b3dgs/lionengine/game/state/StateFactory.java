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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * State object factory. Provide {@link State} instance from its enum value.
 * 
 * @see State
 * @see StateHandler
 */
public class StateFactory
{
    /** List of available states. */
    private final Map<Enum<?>, State> states = new HashMap<>();

    /**
     * Create the factory.
     */
    public StateFactory()
    {
        super();
    }

    /**
     * Add a supported state with its corresponding enum value.
     * 
     * @param state The state instance.
     */
    public void addState(State state)
    {
        final Enum<?> type = state.getState();
        Check.notNull(type);
        states.put(type, state);
    }

    /**
     * Clear the handled states.
     */
    public void clear()
    {
        states.clear();
    }

    /**
     * Get the state instance from its type. If found, {@link State#enter()} is called.
     * 
     * @param type The state type.
     * @return The state instance.
     * @throws LionEngineException If state does not exist.
     */
    public State getState(Enum<?> type)
    {
        final State state = states.get(type);
        if (state == null)
        {
            throw new LionEngineException(type);
        }
        state.enter();
        return state;
    }
}
