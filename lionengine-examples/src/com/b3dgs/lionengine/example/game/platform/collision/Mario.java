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
package com.b3dgs.lionengine.example.game.platform.collision;

import com.b3dgs.lionengine.Keyboard;
import com.b3dgs.lionengine.core.Key;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;

/**
 * Implementation of our controllable entity.
 * 
 * @see com.b3dgs.lionengine.example.game.entity
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Mario
        extends EntityPlatform
{
    /** Map reference. */
    private final Map map;
    /** Desired fps value. */
    private final int desiredFps;
    /** Jump force. */
    private final double jumpSpeed;
    /** Movement max speed. */
    private final double movementSpeed;
    /** Movement force. */
    private final Movement movement;
    /** Movement jump force. */
    private final Force jumpForce;
    /** Collision state. */
    private EntityCollision coll;
    /** Key right state. */
    private boolean right;
    /** Key left state. */
    private boolean left;
    /** Key up state. */
    private boolean up;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    Mario(Map map, int desiredFps)
    {
        super(new SetupSurfaceGame(Media.get("mario.xml")));
        this.map = map;
        this.desiredFps = desiredFps;
        movement = new Movement();
        jumpForce = new Force();
        jumpSpeed = getDataDouble("jumpSpeed", "data");
        movementSpeed = getDataDouble("movementSpeed", "data");
        setMass(getDataDouble("mass", "data"));
        setFrameOffsets(0, 9);
        addCollisionTile(EntityCollisionTileCategory.GROUND_CENTER, 0, 0);
        addCollisionTile(EntityCollisionTileCategory.LEG_LEFT, -5, 0);
        addCollisionTile(EntityCollisionTileCategory.LEG_RIGHT, 5, 0);
        addCollisionTile(EntityCollisionTileCategory.KNEE_LEFT, -5, 9);
        addCollisionTile(EntityCollisionTileCategory.KNEE_RIGHT, 5, 9);
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
     * Respawn the hero.
     */
    public void respawn()
    {
        teleport(550, 26);
        movement.reset();
        jumpForce.setForce(Force.ZERO);
        mirror(false);
        resetGravity();
    }

    /**
     * Check the map limit and apply collision if necessary.
     */
    private void checkMapLimit()
    {
        final int limitLeft = 0;
        if (getLocationX() < limitLeft)
        {
            setLocationX(limitLeft);
            movement.reset();
        }
        final int limitRight = map.getWidthInTile() * map.getTileWidth();
        if (getLocationX() > limitRight)
        {
            setLocationX(limitRight);
            movement.reset();
        }
    }

    /**
     * Check if hero can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    private boolean canJump()
    {
        return isOnGround();
    }

    /**
     * Check if hero is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    private boolean isOnGround()
    {
        return coll == EntityCollision.GROUND;
    }

    /**
     * Check the horizontal collision.
     * 
     * @param category The collision category.
     */
    private void checkHorizontal(EntityCollisionTileCategory category)
    {
        final Tile tile = getCollisionTile(map, category);
        if (tile != null)
        {
            final Double x = tile.getCollisionX(this);
            if (applyHorizontalCollision(x))
            {
                movement.reset();
            }
        }
    }

    /**
     * Check the vertical collision.
     * 
     * @param category The collision category.
     */
    private void checkVertical(EntityCollisionTileCategory category)
    {
        final Tile tile = getCollisionTile(map, category);
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            if (applyVerticalCollision(y))
            {
                jumpForce.setForce(Force.ZERO);
                resetGravity();
                coll = EntityCollision.GROUND;
            }
            else
            {
                coll = EntityCollision.NONE;
            }
        }
    }

    /*
     * EntityPlatform
     */

    @Override
    protected void handleActions(double extrp)
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
            resetGravity();
            coll = EntityCollision.NONE;
        }
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
        checkMapLimit();
        coll = EntityCollision.NONE;

        // Horizontal collision
        if (getDiffHorizontal() < 0)
        {
            checkHorizontal(EntityCollisionTileCategory.KNEE_LEFT);
        }
        else if (getDiffHorizontal() > 0)
        {
            checkHorizontal(EntityCollisionTileCategory.KNEE_RIGHT);
        }

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkVertical(EntityCollisionTileCategory.LEG_LEFT);
            checkVertical(EntityCollisionTileCategory.LEG_RIGHT);
            checkVertical(EntityCollisionTileCategory.GROUND_CENTER);
        }

        // Respawn when die
        if (getLocationY() < 0)
        {
            respawn();
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        updateAnimation(extrp);
    }
}
