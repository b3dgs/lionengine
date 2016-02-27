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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.InputDevice;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.core.InputDevicePointer;

/**
 * Handle the {@link State} and their {@link StateTransition}.
 * <p>
 * Usage example:
 * </p>
 * <ul>
 * <li>{@link #addInput(InputDevice)}</li>
 * <li>{@link #start(Enum)}</li>
 * <li>{@link #update(double)}</li>
 * </ul>
 */
public class StateHandler implements Updatable
{
    /** Inputs used. */
    private final Collection<InputDevice> inputs = new ArrayList<InputDevice>();
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
        this.factory = factory;
    }

    /**
     * Start with the first state.
     * 
     * @param state The first state used.
     */
    public void start(Enum<?> state)
    {
        current = factory.getState(state);
    }

    /**
     * Set the input device used.
     * 
     * @param input The input device reference.
     * @throws LionEngineException If input is <code>null</code>.
     */
    public void addInput(InputDevice input)
    {
        Check.notNull(input);
        inputs.add(input);
    }

    /**
     * Change the current state.
     * 
     * @param next The next state.
     */
    public void changeState(Enum<?> next)
    {
        current.exit();
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
            for (final InputDevice input : inputs)
            {
                updateInput(StateInputDirectionalUpdater.class, InputDeviceDirectional.class, input);
                updateInput(StateInputPointerUpdater.class, InputDevicePointer.class, input);
            }
            current.update(extrp);

            final State old = current;
            for (final InputDevice input : inputs)
            {
                current = checkNext(InputDeviceDirectional.class, input);
                current = checkNext(InputDevicePointer.class, input);
            }
            if (!old.equals(current))
            {
                old.exit();
            }
        }
    }

    /**
     * Check the next state depending of the input used.
     * 
     * @param updaterType The input updater type.
     * @param inputType The input type.
     * @param input The input reference.
     * @param <I> The input device type.
     */
    private <I extends InputDevice> void updateInput(Class<? extends StateInputUpdater<I>> updaterType,
                                                     Class<I> inputType,
                                                     InputDevice input)
    {
        if (updaterType.isAssignableFrom(current.getClass()) && inputType.isAssignableFrom(input.getClass()))
        {
            updaterType.cast(current).updateInput(inputType.cast(input));
        }
    }

    /**
     * Check the next state depending of the input used.
     * 
     * @param inputType The input type.
     * @param input The input reference.
     * @return The next state.
     */
    private State checkNext(Class<? extends InputDevice> inputType, InputDevice input)
    {
        if (inputType.isAssignableFrom(input.getClass()))
        {
            final Enum<?> type = current.checkTransitions(inputType.cast(input));
            if (type != null)
            {
                return factory.getState(type);
            }
        }
        return current;
    }
}
