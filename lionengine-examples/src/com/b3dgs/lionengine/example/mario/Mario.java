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
package com.b3dgs.lionengine.example.mario;

import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Mario
        extends Entity
{
    /** Dead timer. */
    private final Timing timerDie;
    /** Dead step. */
    private int stepDie;
    /** Die location. */
    private double locationDie;

    /**
     * Constructor.
     * 
     * @param setup setup reference.
     */
    public Mario(SetupEntity setup)
    {
        super(setup);
        timerDie = new Timing();
        addCollisionTile(EntityCollisionTileCategory.LEG_LEFT, -5, 0);
        addCollisionTile(EntityCollisionTileCategory.LEG_RIGHT, 5, 0);
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        if (!dead)
        {
            right = keyboard.isPressed(Key.RIGHT);
            left = keyboard.isPressed(Key.LEFT);
            up = keyboard.isPressed(Key.UP);
        }
    }

    /**
     * Kill mario.
     */
    public void kill()
    {
        dead = true;
        movement.reset();
        locationDie = getLocationY();
        stepDie = 0;
        timerDie.start();
    }

    /**
     * Respawn mario.
     */
    public void respawn()
    {
        mirror(false);
        teleport(80, 25);
        timerDie.stop();
        stepDie = 0;
        dead = false;
        movement.reset();
        jumpForce.setForce(Force.ZERO);
        resetGravity();
    }

    /*
     * Entity
     */

    @Override
    public void onHurtBy(EntityGame entity)
    {
        if (!dead)
        {
            kill();
        }
    }

    @Override
    public void onHitThat(Entity entity)
    {
        if (!isJumping())
        {
            jumpForce.setForce(0.0, jumpForceValue / 1.5);
            resetGravity();
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        if (!dead)
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
        }

        // Die
        if (dead)
        {
            if (timerDie.elapsed(500))
            {
                // Die effect
                if (stepDie == 0)
                {
                    jumpForce.setForce(0.0, jumpForceValue);
                    stepDie = 1;
                }
                // Respawn
                if (stepDie == 1 && timerDie.elapsed(2000))
                {
                    respawn();
                }
            }
            // Lock mario
            else
            {
                resetGravity();
                setLocationY(locationDie);
            }
        }
        super.handleMovements(extrp);
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        if (!dead)
        {
            super.handleCollisions(extrp);

            // Vertical collision
            if (getDiffVertical() < 0 || isOnGround())
            {
                checkVertical(EntityCollisionTileCategory.LEG_LEFT);
                checkVertical(EntityCollisionTileCategory.LEG_RIGHT);
            }

            // Kill when fall down
            if (getLocationY() < 0)
            {
                kill();
            }
        }
    }
}
