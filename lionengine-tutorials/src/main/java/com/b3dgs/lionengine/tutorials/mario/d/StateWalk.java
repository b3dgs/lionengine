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
package com.b3dgs.lionengine.tutorials.mario.d;

import java.util.concurrent.atomic.AtomicBoolean;

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
import com.b3dgs.lionengine.game.trait.transformable.Transformable;

/**
 * Walk state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class StateWalk extends StateGame implements StateInputDirectionalUpdater, TileCollidableListener
{
    /** Horizontal collision. */
    final AtomicBoolean collide = new AtomicBoolean(false);
    /** Can jump flag. */
    final AtomicBoolean canJump = new AtomicBoolean(false);
    /** Movement force. */
    final Force movement;
    /** Jump force. */
    final Force jump;
    /** Mirrorable reference. */
    private final Mirrorable mirrorable;
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;
    /** Transformable reference. */
    private final Transformable transformable;
    /** Tile collidable reference. */
    private final TileCollidable tileCollidable;
    /** Movement side. */
    private double side;
    /** Played flag. */
    private boolean played;

    /**
     * Create the state.
     * 
     * @param entity The entity reference.
     * @param animation The associated animation.
     */
    public StateWalk(Entity entity, Animation animation)
    {
        super(EntityState.WALK);
        this.animation = animation;
        mirrorable = entity.getTrait(Mirrorable.class);
        tileCollidable = entity.getTrait(TileCollidable.class);
        transformable = entity.getTrait(Transformable.class);
        animator = entity.surface;
        movement = entity.movement;
        jump = entity.jump;
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
        else if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            canJump.set(true);
        }
    }

    /**
     * Transition from {@link StateWalk} to {@link StateIdle}.
     */
    private final class TransitionWalkToIdle extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToIdle()
        {
            super(EntityState.IDLE);
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
    private final class TransitionWalkToTurn extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToTurn()
        {
            super(EntityState.TURN);
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
    private final class TransitionWalkToJump extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionWalkToJump()
        {
            super(EntityState.JUMP);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return input.getVerticalDirection() > 0 && canJump.get();
        }

        @Override
        public void exit()
        {
            Sfx.JUMP.play();
            jump.setDirection(0.0, 8.0);
            canJump.set(false);
        }
    }
}
