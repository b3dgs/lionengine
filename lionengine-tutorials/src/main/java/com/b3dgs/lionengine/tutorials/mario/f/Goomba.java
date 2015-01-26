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
package com.b3dgs.lionengine.tutorials.mario.f;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.factory.SetupSurface;

/**
 * Goomba implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Goomba
        extends Entity
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(Goomba.class);

    /** Die timer. */
    private final Timing timerDie;

    /**
     * Constructor.
     * 
     * @param setup setup reference.
     */
    public Goomba(SetupSurface setup)
    {
        super(setup);
        timerDie = new Timing();
        movement.setVelocity(0.3);
        movement.setSensibility(0.1);
        right = true;
    }

    @Override
    public void onHurtBy(EntityGame entity)
    {
        if (!dead)
        {
            dead = true;
            right = false;
            left = false;
            movement.reset();
            timerDie.start();
        }
    }

    @Override
    public void onHitThat(Entity entity)
    {
        // Nothing to do
    }

    @Override
    protected void handleActions(double extrp)
    {
        super.handleActions(extrp);
        if (dead && timerDie.elapsed(1000))
        {
            destroy();
        }
    }

    @Override
    protected void onHorizontalCollision()
    {
        // Invert movement in case of collision
        if (right)
        {
            right = false;
            left = true;
        }
        else
        {
            left = false;
            right = true;
        }
    }
}
