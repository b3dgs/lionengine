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
package com.b3dgs.lionengine.tutorials.mario.c;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateInputDirectionalUpdater;
import com.b3dgs.lionengine.game.state.StateTransition;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidable;
import com.b3dgs.lionengine.game.trait.collidable.TileCollidableListener;
import com.b3dgs.lionengine.game.trait.mirrorable.Mirrorable;

/**
 * Jump state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class StateJump
        extends StateGame
        implements StateInputDirectionalUpdater, TileCollidableListener
{
    /** Jump force. */
    final Force jump;
    /** Mirrorable reference. */
    private final Mirrorable mirrorable;
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;
    /** Tile collidable reference. */
    private final TileCollidable tileCollidable;
    /** Movement force. */
    private final Force movement;
    /** Movement side. */
    private double side;

    /**
     * Create the state.
     * 
     * @param mario The mario reference.
     * @param animation The associated animation.
     */
    public StateJump(Mario mario, Animation animation)
    {
        super(MarioState.JUMP);
        this.animation = animation;
        mirrorable = mario.getTrait(Mirrorable.class);
        tileCollidable = mario.getTrait(TileCollidable.class);
        animator = mario.getSurface();
        movement = mario.getMovement();
        jump = mario.getJump();
        addTransition(new TransitionJumpToIdle());
    }

    @Override
    public void enter()
    {
        movement.setVelocity(0.5);
        movement.setSensibility(0.1);
        animator.play(animation);
        jump.setDirection(0.0, 8.0);
        tileCollidable.addListener(this);
        side = 0;
    }

    @Override
    public void exit()
    {
        tileCollidable.removeListener(this);
    }

    @Override
    public void updateInput(InputDeviceDirectional input)
    {
        side = input.getHorizontalDirection();
    }

    @Override
    public void update(double extrp)
    {
        movement.setDestination(side * 3, 0);
        if (movement.getDirectionHorizontal() != 0)
        {
            mirrorable.mirror(movement.getDirectionHorizontal() < 0 ? Mirror.HORIZONTAL : Mirror.NONE);
        }
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis)
        {
            jump.setDirection(Direction.ZERO);
        }
    }

    /**
     * Transition from {@link StateJump} to {@link StateIdle}.
     */
    private final class TransitionJumpToIdle
            extends StateTransition
            implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionJumpToIdle()
        {
            super(MarioState.IDLE);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return jump.getDirectionVertical() == 0;
        }
    }
}
