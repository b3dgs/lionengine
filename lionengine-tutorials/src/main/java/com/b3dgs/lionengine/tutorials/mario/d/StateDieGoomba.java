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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.state.StateGame;

/**
 * Goomba die state implementation.
 */
class StateDieGoomba extends StateGame
{
    private final Featurable featurable;
    private final Force movement;
    private final Animator animator;
    private final Animation animation;

    /**
     * Create the state.
     * 
     * @param featurable The featurable reference.
     * @param animation The associated animation.
     */
    public StateDieGoomba(Featurable featurable, Animation animation)
    {
        super(GoombaState.DEATH);

        this.featurable = featurable;
        this.animation = animation;

        final EntityModel model = featurable.getFeature(EntityModel.class);
        animator = model.getSurface();
        movement = model.getMovement();
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setDestination(0.0, 0.0);
    }

    @Override
    public void update(double extrp)
    {
        if (AnimState.FINISHED == animator.getAnimState())
        {
            featurable.getFeature(Identifiable.class).destroy();
        }
    }
}
