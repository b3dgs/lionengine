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
package com.b3dgs.lionengine.game.feature.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * State base implementation.
 * 
 * @see State
 */
public abstract class StateAbstract implements State
{
    /** Transitions list. */
    private final Map<Class<? extends State>, StateChecker> transitions = new HashMap<>();

    /**
     * Create the state.
     */
    protected StateAbstract()
    {
        super();
    }

    /*
     * State
     */

    @Override
    public final void addTransition(Class<? extends State> next, StateChecker checker)
    {
        transitions.put(next, checker);
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
    public Class<? extends State> checkTransitions()
    {
        for (final Entry<Class<? extends State>, StateChecker> entry : transitions.entrySet())
        {
            final StateChecker checker = entry.getValue();
            if (checker.getAsBoolean())
            {
                checker.exit();
                return entry.getKey();
            }
        }
        return null;
    }
}
