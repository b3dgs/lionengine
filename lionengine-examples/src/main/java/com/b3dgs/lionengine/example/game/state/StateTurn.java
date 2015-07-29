/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.state;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateInputDirectionalUpdater;
import com.b3dgs.lionengine.game.state.StateTransition;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;
import com.b3dgs.lionengine.game.trait.mirrorable.Mirrorable;

/**
 * Turn state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class StateTurn extends StateGame implements StateInputDirectionalUpdater
{
    /** Movement force. */
    final Force movement;
    /** Mirrorable reference. */
    final Mirrorable mirrorable;
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;
    /** Movement side. */
    private double side;

    /**
     * Create the walk state.
     * 
     * @param mario The mario reference.
     * @param animation The associated animation.
     */
    public StateTurn(Mario mario, Animation animation)
    {
        super(MarioState.TURN);
        this.animation = animation;
        animator = mario.getSurface();
        movement = mario.getMovement();
        mirrorable = mario.getTrait(Mirrorable.class);
        addTransition(new TransitionTurnToIdle());
        addTransition(new TransitionTurnToWalk());
        addTransition(new TransitionTurnToJump());
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setVelocity(0.28);
        movement.setSensibility(0.005);
        side = 0;
    }

    @Override
    public void updateInput(InputDeviceDirectional input)
    {
        side = input.getHorizontalDirection();
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(side * 2, 0);
    }

    /**
     * Transition from {@link StateTurn} to {@link StateIdle}.
     */
    private final class TransitionTurnToIdle extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionTurnToIdle()
        {
            super(MarioState.IDLE);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return input.getHorizontalDirection() == 0
                   && movement.getDirectionHorizontal() == 0
                   && input.getVerticalDirection() == 0;
        }

        @Override
        public void exit()
        {
            mirrorable.mirror(mirrorable.getMirror() == Mirror.HORIZONTAL ? Mirror.NONE : Mirror.HORIZONTAL);
        }
    }

    /**
     * Transition from {@link StateTurn} to {@link StateWalk}.
     */
    private final class TransitionTurnToWalk extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionTurnToWalk()
        {
            super(MarioState.WALK);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return (input.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() < 0
                    || input.getHorizontalDirection() > 0 && movement.getDirectionHorizontal() > 0)
                   && input.getVerticalDirection() == 0;
        }
    }

    /**
     * Transition from {@link StateTurn} to {@link StateJump}.
     */
    private final class TransitionTurnToJump extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionTurnToJump()
        {
            super(MarioState.JUMP);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return input.getVerticalDirection() > 0;
        }
    }
}
