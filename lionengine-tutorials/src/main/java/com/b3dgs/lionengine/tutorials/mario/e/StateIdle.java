/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.e;

import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.State;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.trait.TileCollidable;
import com.b3dgs.lionengine.game.trait.TileCollidableListener;
import com.b3dgs.lionengine.game.trait.Transformable;

/**
 * Idle state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class StateIdle
        extends State
        implements TileCollidableListener
{
    /** Transformable reference. */
    private final Transformable transformable;
    /** Animator reference. */
    private final Animator animator;
    /** Animation reference. */
    private final Animation animation;
    /** Tile collidable reference. */
    private final TileCollidable tileCollidable;
    /** Movement force. */
    private final Force movement;
    /** Jump force. */
    private final Force jump;
    /** Can jump flag. */
    private boolean canJump;

    /**
     * Create the walk state.
     * 
     * @param entity The entity reference.
     * @param animation The associated animation.
     */
    public StateIdle(Entity entity, Animation animation)
    {
        this.animation = animation;
        transformable = entity.getTrait(Transformable.class);
        tileCollidable = entity.getTrait(TileCollidable.class);
        animator = entity.getSurface();
        movement = entity.getMovement();
        jump = entity.getJump();
    }

    @Override
    public void enter()
    {
        tileCollidable.addListener(this);
        movement.setDestination(0.0, 0.0);
        movement.setVelocity(0.3);
        movement.setSensibility(0.01);
        animator.play(animation);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

    @Override
    public void clear()
    {
        // Nothing to do
    }

    @Override
    protected State handleInput(StateFactory factory, InputDeviceDirectional input)
    {
        if (input.getVerticalDirection() > 0 && canJump)
        {
            Sfx.JUMP.play();
            jump.setDirection(0.0, 8.0);
            canJump = false;
            tileCollidable.removeListener(this);
            return factory.getState(EntityState.JUMP);
        }
        if (input.getHorizontalDirection() != 0)
        {
            tileCollidable.removeListener(this);
            return factory.getState(EntityState.WALK);
        }
        return null;
    }

    @Override
    public void notifyTileCollided(Tile tile, Axis axis)
    {
        if (Axis.Y == axis && transformable.getY() < transformable.getOldY())
        {
            canJump = true;
        }
    }
}
