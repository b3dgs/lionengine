/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.state;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;

/**
 * State base implementation.
 * 
 * @see State
 */
public abstract class StateAbstract implements State
{
    /** Error add itself. */
    static final String ERROR_ADD_ITSELF = "Add transition to itself not allowed !";

    /** Transitions list. */
    private final List<Class<? extends State>> transitions = new ArrayList<>();
    /** Checkers list. */
    private final List<StateChecker> checkers = new ArrayList<>();

    /**
     * Create the state.
     */
    protected StateAbstract()
    {
        super();
    }

    /**
     * Called once update is done and no next transition found. Does nothing by default.
     */
    protected void postUpdate()
    {
        // Nothing by default
    }

    @Override
    public final void addTransition(Class<? extends State> next, StateChecker checker)
    {
        if (next == getClass())
        {
            throw new LionEngineException(ERROR_ADD_ITSELF);
        }
        transitions.add(next);
        checkers.add(checker);
    }

    @Override
    public final void clearTransitions()
    {
        transitions.clear();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Does nothing by default. Can be overridden.
     * </p>
     */
    @Override
    public void exit()
    {
        // Nothing by default
    }

    @Override
    public Class<? extends State> checkTransitions(Class<? extends State> last)
    {
        final int n = transitions.size();
        for (int i = 0; i < n; i++)
        {
            final StateChecker checker = checkers.get(i);
            if (checker.getAsBoolean())
            {
                checker.exit();
                final Class<? extends State> next;
                if (transitions.get(i) == StateLast.class)
                {
                    next = last;
                }
                else
                {
                    next = transitions.get(i);
                }
                return next;
            }
        }
        postUpdate();
        return null;
    }
}
