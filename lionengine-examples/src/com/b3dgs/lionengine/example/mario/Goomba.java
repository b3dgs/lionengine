/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.mario;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Goomba implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Goomba
        extends Entity
{
    /** Die timer. */
    private final Timing timerDie;

    /**
     * Constructor.
     * 
     * @param setup setup reference.
     * @param map The map reference.
     * @param desiredFps desired fps.
     */
    Goomba(SetupSurfaceGame setup, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
        timerDie = new Timing();
        movement.setVelocity(0.3);
        movement.setSensibility(0.1);
        right = true;
    }

    /*
     * Entity
     */

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
