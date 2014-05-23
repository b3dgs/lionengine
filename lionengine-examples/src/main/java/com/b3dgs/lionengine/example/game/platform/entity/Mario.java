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
package com.b3dgs.lionengine.example.game.platform.entity;

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Keyboard;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;
import com.b3dgs.lionengine.game.purview.Configurable;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Mario
        extends EntityPlatform
{
    /** Setup. */
    private static final SetupSurfaceGame SETUP = new SetupSurfaceGame(Core.MEDIA.create("mario.xml"));
    /** Ground location y. */
    private static final int GROUND = 32;
    /** Desired fps value. */
    private final int desiredFps;
    /** Movement force. */
    private final Movement movement;
    /** Movement jump force. */
    private final Force jumpForce;
    /** Animations list. */
    private final EnumMap<EntityState, Animation> animations;
    /** Movement max speed. */
    private final double movementSpeed;
    /** Jump force. */
    private final double jumpSpeed;
    /** Mario state. */
    private EntityState state;
    /** Old state. */
    private EntityState stateOld;
    /** Key right state. */
    private boolean right;
    /** Key left state. */
    private boolean left;
    /** Key up state. */
    private boolean up;

    /**
     * Constructor.
     * 
     * @param desiredFps The desired fps.
     */
    Mario(int desiredFps)
    {
        super(new SetupSurfaceGame(Core.MEDIA.create("mario.xml")));
        this.desiredFps = desiredFps;
        movement = new Movement();
        jumpForce = new Force();
        animations = new EnumMap<>(EntityState.class);
        final Configurable configurable = SETUP.getConfigurable();
        movementSpeed = configurable.getDouble("movementSpeed", "data");
        jumpSpeed = configurable.getDouble("jumpSpeed", "data");
        state = EntityState.IDLE;
        stateOld = state;
        setLocation(100, 32);
        setMass(configurable.getDouble("mass", "data"));
        loadAnimations(configurable);
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        right = keyboard.isPressed(Keyboard.RIGHT);
        left = keyboard.isPressed(Keyboard.LEFT);
        up = keyboard.isPressed(Keyboard.UP);
    }

    /**
     * Check if hero can jump.
     * 
     * @return true if can jump.
     */
    private boolean canJump()
    {
        return getLocationIntY() == Mario.GROUND;
    }

    /**
     * Check if hero is on ground.
     * 
     * @return true if on ground.
     */
    private boolean isOnGround()
    {
        return getLocationIntY() == Mario.GROUND;
    }

    /**
     * Load all existing animations defined in the xml file.
     * 
     * @param configurable The configurable reference.
     */
    private void loadAnimations(Configurable configurable)
    {
        for (final EntityState state : EntityState.values())
        {
            try
            {
                animations.put(state, configurable.getAnimation(state.getAnimationName()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    /**
     * Update the forces depending of the pressed key.
     */
    private void updateForces()
    {
        movement.setForceToReach(Force.ZERO);
        final double speed;
        if (right && !left)
        {
            speed = movementSpeed;
        }
        else if (left && !right)
        {
            speed = -movementSpeed;
        }
        else
        {
            speed = 0.0;
        }
        movement.setForceToReach(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setForce(0.0, jumpSpeed);
        }
    }

    /**
     * Update mario states.
     */
    private void updateStates()
    {
        final double diffHorizontal = getDiffHorizontal();
        stateOld = state;

        if (diffHorizontal != 0.0)
        {
            mirror(diffHorizontal < 0.0);
        }

        final boolean mirror = getMirror();
        if (!isOnGround())
        {
            state = EntityState.JUMP;
        }
        else if (mirror && right && diffHorizontal < 0.0)
        {
            state = EntityState.TURN;
        }
        else if (!mirror && left && diffHorizontal > 0.0)
        {
            state = EntityState.TURN;
        }
        else if (diffHorizontal != 0.0)
        {
            state = EntityState.WALK;
        }
        else
        {
            state = EntityState.IDLE;
        }
    }

    /*
     * EntityPlatform
     */

    @Override
    protected void handleActions(double extrp)
    {
        updateForces();
        updateStates();
    }

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

        // Update final movement
        movement.setVelocity(speed);
        movement.setSensibility(sensibility);
        movement.update(extrp);
        updateGravity(extrp, desiredFps, jumpForce, movement.getForce());
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        // Block player to avoid infinite falling
        if (getLocationY() < Mario.GROUND)
        {
            jumpForce.setForce(Force.ZERO);
            resetGravity();
            teleportY(Mario.GROUND);
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        // Assign an animation for each state
        if (state == EntityState.WALK)
        {
            setAnimSpeed(Math.abs(movement.getForce().getForceHorizontal()) / 12.0);
        }
        // Play the assigned animation
        if (stateOld != state)
        {
            play(animations.get(state));
        }
        updateAnimation(extrp);
    }
}
