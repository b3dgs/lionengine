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
package com.b3dgs.lionengine.tutorials.mario.c;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.StateTransition;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableListener;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateInputDirectionalUpdater;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;
import com.b3dgs.lionengine.io.InputDeviceDirectional;

/**
 * Walk state implementation.
 */
class StateWalk extends StateGame implements StateInputDirectionalUpdater, TileCollidableListener
{
    /** Horizontal collision. */
    private final AtomicBoolean collide = new AtomicBoolean();

    private final Force movement;
    private final Mirrorable mirrorable;
    private final Animator animator;
    private final Animation animation;
    private final TileCollidable tileCollidable;

    /** Movement side. */
    private double side;
    /** Played flag. */
    private boolean played;

    /**
     * Create the state.
     * 
     * @param featurable The featurable reference.
     * @param animation The associated animation.
     */
    public StateWalk(Featurable featurable, Animation animation)
    {
        super(MarioState.WALK);

        this.animation = animation;

        mirrorable = featurable.getFeature(Mirrorable.class);
        tileCollidable = featurable.getFeature(TileCollidable.class);

        final MarioModel model = featurable.getFeature(MarioModel.class);
        animator = model.getSurface();
        movement = model.getMovement();

        addTransition(new TransitionWalkToIdle());
        addTransition(new TransitionWalkToTurn());
        addTransition(new TransitionWalkToJump());
    }

    @Override
    public void enter()
    {
        movement.setVelocity(0.5);
        movement.setSensibility(0.1);
        tileCollidable.addListener(this);
        side = 0;
        played = false;
        collide.set(false);
    }

    @Override
    public void exit()
    {
        tileCollidable.addListener(this);
    }

    @Override
    public void updateInput(InputDeviceDirectional input)
    {
        side = input.getHorizontalDirection();
    }

    @Override
    public void update(double extrp)
    {
        if (!played && movement.getDirectionHorizontal() != 0)
        {
            animator.play(animation);
            played = true;
        }
        movement.setDestination(side * 3, 0);
        animator.setAnimSpeed(Math.abs(movement.getDirectionHorizontal()) / 12.0);

        if (side < 0 && movement.getDirectionHorizontal() < 0)
        {
            mirrorable.mirror(Mirror.HORIZONTAL);
        }
        else if (side > 0 && movement.getDirectionHorizontal() > 0)
        {
            mirrorable.mirror(Mirror.NONE);
        }
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.X == axis)
        {
            movement.setDirection(Direction.ZERO);
            collide.set(true);
        }
    }

    /**
     * Transition from {@link StateWalk} to {@link StateIdle}.
     */
    private class TransitionWalkToIdle extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToIdle()
        {
            super(MarioState.IDLE);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return collide.get() || input.getHorizontalDirection() == 0 && input.getVerticalDirection() == 0;
        }
    }

    /**
     * Transition from {@link StateWalk} to {@link StateTurn}.
     */
    private class TransitionWalkToTurn extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToTurn()
        {
            super(MarioState.TURN);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return input.getHorizontalDirection() < 0 && movement.getDirectionHorizontal() > 0
                   || input.getHorizontalDirection() > 0 && movement.getDirectionHorizontal() < 0;
        }
    }

    /**
     * Transition from {@link StateWalk} to {@link StateJump}.
     */
    private class TransitionWalkToJump extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToJump()
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
