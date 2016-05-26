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
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.feature.Featurable;

/**
 * State idle test implementation.
 */
public class StateIdle extends StateTest implements StateInputDirectionalUpdater
{
    /**
     * Create the state.
     */
    public StateIdle()
    {
        this(null, null);
    }

    /**
     * Create the state.
     * 
     * @param featurable The featurable reference.
     * @param animation The associated animation.
     */
    public StateIdle(@SuppressWarnings("unused") Featurable featurable, @SuppressWarnings("unused") Animation animation)
    {
        super(StateType.IDLE);
        addTransition(new TransitionIdleToWalk());

    }

    @Override
    public void updateInput(InputDeviceDirectional input)
    {
        // Mock
    }

    /**
     * Transition from {@link StateIdle} to {@link StateWalk}.
     */
    private final class TransitionIdleToWalk extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionIdleToWalk()
        {
            super(StateType.WALK);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return true;
        }
    }
}
