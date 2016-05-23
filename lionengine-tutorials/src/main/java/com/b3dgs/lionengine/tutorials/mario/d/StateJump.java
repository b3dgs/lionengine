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
package com.b3dgs.lionengine.tutorials.mario.d;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b3dgs.lionengine.Mirror;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.collision.tile.Axis;
import com.b3dgs.lionengine.game.collision.tile.TileCollidable;
import com.b3dgs.lionengine.game.collision.tile.TileCollidableListener;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.object.feature.body.Body;
import com.b3dgs.lionengine.game.object.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.state.StateGame;
import com.b3dgs.lionengine.game.state.StateInputDirectionalUpdater;
import com.b3dgs.lionengine.game.state.StateTransition;
import com.b3dgs.lionengine.game.state.StateTransitionInputDirectionalChecker;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Jump state implementation.
 */
class StateJump extends StateGame implements StateInputDirectionalUpdater, TileCollidableListener
{
    private final AtomicBoolean ground = new AtomicBoolean();
    private final Transformable transformable;
    private final Body body;
    private final Mirrorable mirrorable;
    private final Animator animator;
    private final Animation animation;
    private final TileCollidable tileCollidable;
    private final Force movement;
    private final Force jump;

    /** Movement side. */
    private double side;

    /**
     * Create the state.
     * 
     * @param handlable The handlable reference.
     * @param animation The associated animation.
     */
    public StateJump(Handlable handlable, Animation animation)
    {
        super(EntityState.JUMP);

        this.animation = animation;
        transformable = handlable.getFeature(Transformable.class);
        body = handlable.getFeature(Body.class);
        mirrorable = handlable.getFeature(Mirrorable.class);
        tileCollidable = handlable.getFeature(TileCollidable.class);

        final EntityModel model = handlable.getFeature(EntityModel.class);
        animator = model.getSurface();
        movement = model.getMovement();
        jump = model.getJump();

        addTransition(new TransitionJumpToIdle());
    }

    @Override
    public void enter()
    {
        movement.setVelocity(0.5);
        movement.setSensibility(0.1);
        animator.play(animation);
        tileCollidable.addListener(this);
        ground.set(false);
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
            body.resetGravity();
            if (transformable.getY() < transformable.getOldY())
            {
                ground.set(true);
            }
        }
    }

    /**
     * Transition from {@link StateJump} to {@link StateIdle}.
     */
    private final class TransitionJumpToIdle extends StateTransition implements StateTransitionInputDirectionalChecker
    {
        /**
         * Create the transition.
         */
        public TransitionJumpToIdle()
        {
            super(EntityState.IDLE);
        }

        @Override
        public boolean check(InputDeviceDirectional input)
        {
            return ground.get();
        }
    }
}
