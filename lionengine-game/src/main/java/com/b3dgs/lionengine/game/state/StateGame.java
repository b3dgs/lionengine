/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.core.InputDevicePointer;

/**
 * State base implementation.
 * 
 * @see State
 */
public abstract class StateGame implements State
{
    /**
     * Check the next state depending of the input used.
     * 
     * @param <I> The input device type.
     * @param checkerType The checker type.
     * @param inputType The input type.
     * @param input The input reference.
     * @param transition The state transition reference.
     * @return <code>true</code> if checker valid, <code>false</code> else.
     */
    private static <I extends InputDevice> boolean check(Class<? extends StateTransitionInputChecker<I>> checkerType,
                                                         Class<I> inputType,
                                                         InputDevice input,
                                                         StateTransition transition)
    {
        if (checkerType.isAssignableFrom(transition.getClass()) && inputType.isAssignableFrom(input.getClass()))
        {
            return checkerType.cast(transition).check(inputType.cast(input));
        }
        return false;
    }

    /** Transitions list. */
    private final Collection<StateTransition> transitions = new ArrayList<StateTransition>();
    /** The enum state. */
    private final Enum<?> state;

    /**
     * Create the state.
     * 
     * @param state The corresponding enum.
     */
    protected StateGame(Enum<?> state)
    {
        this.state = state;
    }

    /*
     * State
     */

    @Override
    public final void addTransition(StateTransition transition)
    {
        transitions.add(transition);
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
    public Enum<?> checkTransitions(InputDevice input)
    {
        for (final StateTransition transition : transitions)
        {
            if (check(StateTransitionInputDirectionalChecker.class, InputDeviceDirectional.class, input, transition)
                || check(StateTransitionInputPointerChecker.class, InputDevicePointer.class, input, transition))
            {
                transition.exit();
                return transition.getNext();
            }
        }
        return null;
    }

    @Override
    public Enum<?> getState()
    {
        return state;
    }
}
