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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;

/**
 * Handle the {@link State}.
 * <p>
 * Usage example:
 * </p>
 * <ul>
 * <li>{@link #changeState(Enum)}</li>
 * <li>{@link #update(double)}</li>
 * </ul>
 */
public class StateHandler implements Updatable
{
    /** State factory reference. */
    private final StateFactory factory;
    /** Current state pointer (<code>null</code> if none). */
    private State current;

    /**
     * Create the handler.
     * 
     * @param factory The state factory reference.
     */
    public StateHandler(StateFactory factory)
    {
        super();

        this.factory = factory;
    }

    /**
     * Change the current state.
     * 
     * @param next The next state.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public void changeState(Enum<?> next)
    {
        Check.notNull(next);

        if (current != null)
        {
            current.exit();
        }
        current = factory.getState(next);
    }

    /**
     * Check the current state.
     * 
     * @param state The state to check.
     * @return <code>true</code> if it is this state, <code>false</code> else.
     */
    public boolean isState(Enum<?> state)
    {
        if (current != null)
        {
            return current.getState().equals(state);
        }
        return false;
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        if (current != null)
        {
            current.update(extrp);

            final Enum<?> next = current.checkTransitions();
            if (next != null)
            {
                changeState(next);
            }
        }
    }
}
