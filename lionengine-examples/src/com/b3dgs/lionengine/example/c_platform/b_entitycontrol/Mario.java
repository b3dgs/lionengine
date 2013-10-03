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
package com.b3dgs.lionengine.example.c_platform.b_entitycontrol;

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Implementation of our controllable entity.
 */
final class Mario
        extends EntityPlatform
{
    /** Mario configuration full path. */
    private static final Media MARIO_CONFIG = Media.get("entities", "mario.xml");
    /** Jump force. */
    private static final double JUMP_FORCE = 8.0;
    /** Ground location y. */
    private static final int GROUND = 32;
    /** Movement max speed. */
    private static final double MOVEMENT_SPEED = 3.0;
    /** Desired fps value. */
    private final int desiredFps;
    /** Movement force. */
    private final Movement movement;
    /** Movement jump force. */
    private final Force jumpForce;
    /** Animations list. */
    private final EnumMap<EntityState, Animation> animations;
    /** Key right state. */
    private boolean right;
    /** Key left state. */
    private boolean left;
    /** Key up state. */
    private boolean up;
    /** Mario state. */
    private EntityState state;
    /** Old state. */
    private EntityState stateOld;

    /**
     * Constructor.
     * 
     * @param desiredFps The desired fps.
     */
    Mario(int desiredFps)
    {
        super(new SetupSurfaceGame(Mario.MARIO_CONFIG));
        this.desiredFps = desiredFps;
        movement = new Movement();
        jumpForce = new Force();
        animations = new EnumMap<>(EntityState.class);
        state = EntityState.IDLE;
        stateOld = state;
        setLocation(100, 32);
        setMass(getDataDouble("mass", "data"));
        loadAnimations();
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
     */
    private void loadAnimations()
    {
        for (final EntityState state : EntityState.values())
        {
            try
            {
                animations.put(state, getDataAnimation(state.getAnimationName()));
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
            speed = Mario.MOVEMENT_SPEED;
        }
        else if (left && !right)
        {
            speed = -Mario.MOVEMENT_SPEED;
        }
        else
        {
            speed = 0.0;
        }
        movement.setForceToReach(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setForce(0.0, Mario.JUMP_FORCE);
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
        updateGravity(extrp, desiredFps, movement.getForce(), jumpForce);
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
