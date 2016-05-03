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

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.game.object.ObjectGame;

/**
 * State walk test implementation.
 */
public class StateWalk extends StateTest implements StateInputPointerUpdater
{
    /**
     * Create the state.
     */
    public StateWalk()
    {
        this(null, null);
    }

    /**
     * Create the state.
     * 
     * @param object The object reference.
     * @param animation The associated animation.
     */
    public StateWalk(@SuppressWarnings("unused") ObjectGame object, @SuppressWarnings("unused") Animation animation)
    {
        super(StateType.WALK);
        addTransition(new TransitionWalkToIdle());
    }

    @Override
    public void updateInput(InputDevicePointer input)
    {
        // Mock
    }

    /**
     * Transition from {@link StateWalk} to {@link StateIdle}.
     */
    private final class TransitionWalkToIdle extends StateTransition implements StateTransitionInputPointerChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToIdle()
        {
            super(StateType.IDLE);
        }

        @Override
        public boolean check(InputDevicePointer input)
        {
            return true;
        }
    }
}
