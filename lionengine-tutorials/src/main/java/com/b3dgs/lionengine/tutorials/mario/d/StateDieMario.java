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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Animator;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.state.StateGame;

/**
 * Mario die state implementation.
 */
class StateDieMario extends StateGame
{
    private final Force movement;
    private final Force jump;
    private final Animator animator;
    private final Animation animation;

    /**
     * Create the state.
     * 
     * @param featurable The featurable reference.
     * @param animation The associated animation.
     */
    public StateDieMario(Featurable featurable, Animation animation)
    {
        super(MarioState.DEATH);

        this.animation = animation;

        final EntityModel model = featurable.getFeature(EntityModel.class);
        animator = model.getSurface();
        movement = model.getMovement();
        jump = model.getJump();
    }

    @Override
    public void enter()
    {
        animator.play(animation);
        movement.setDestination(0.0, 0.0);
        jump.setDirection(0.0, 9.0);
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }
}
