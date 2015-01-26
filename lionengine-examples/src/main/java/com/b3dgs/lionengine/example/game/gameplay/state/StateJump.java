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
package com.b3dgs.lionengine.example.game.gameplay.state;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.InputDeviceDirectional;
import com.b3dgs.lionengine.game.trait.Mirrorable;
import com.b3dgs.lionengine.game.trait.Movable;

/**
 * Turn state implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <E> The entity type used.
 */
public class StateJump<E extends Movable & Localizable & Mirrorable & Animator>
        extends EntityState<E>
{
    /** Animation reference. */
    private final Animation animation;
    /** Movement side. */
    private int side;
    /** Falling. */
    private boolean fall;

    /**
     * Create the walk state.
     * 
     * @param animation The associated animation.
     */
    public StateJump(Animation animation)
    {
        this.animation = animation;
    }

    /*
     * EntityState
     */

    @Override
    public void clear()
    {
        side = 0;
        fall = false;
    }

    @Override
    public void updateState(E entity)
    {
        entity.setMoveToReach(side * entity.getMoveSpeedMax(), 0);
        if (entity.getDirectionHorizontal() != 0.0)
        {
            entity.mirror(entity.getDirectionHorizontal() < 0);
        }
        if (!fall)
        {
            fall = entity.getLocationY() < entity.getLocationOldY();
        }
    }

    @Override
    public void enter(E entity)
    {
        entity.play(animation);
        entity.setJumpDirection(0.0, entity.getJumpHeightMax());
    }

    @Override
    protected EntityState<E> handleInput(E entity, EntityStateFactory<E> factory, InputDeviceDirectional input)
    {
        side = input.getHorizontalDirection();
        if (fall && entity.getJumpDirection().getDirectionVertical() == 0.0)
        {
            return factory.getState(EntityStateType.IDLE);
        }
        return null;
    }
}
