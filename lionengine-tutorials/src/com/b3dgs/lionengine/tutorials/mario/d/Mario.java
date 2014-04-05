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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.game.Force;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Mario
        extends Entity
{
    /**
     * Constructor.
     * 
     * @param setup setup reference.
     */
    public Mario(SetupEntity setup)
    {
        super(setup);
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        right = keyboard.isPressed(Key.RIGHT);
        left = keyboard.isPressed(Key.LEFT);
        up = keyboard.isPressed(Key.UP);
    }

    /**
     * Respawn mario.
     */
    public void respawn()
    {
        mirror(false);
        teleport(80, 32);
        movement.reset();
        jumpForce.setForce(Force.ZERO);
        resetGravity();
    }

    /*
     * Entity
     */

    @Override
    protected void handleMovements(double extrp)
    {
        // Smooth walking speed...
        final double speed;
        final double sensibility;
        if (right || left)
        {
            speed = 0.3;
            sensibility = 0.01;
        }
        // ...but quick stop
        else
        {
            speed = 0.5;
            sensibility = 0.1;
        }
        movement.setVelocity(speed);
        movement.setSensibility(sensibility);
        super.handleMovements(extrp);
    }
}
