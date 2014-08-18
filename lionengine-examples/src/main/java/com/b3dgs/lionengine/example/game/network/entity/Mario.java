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
package com.b3dgs.lionengine.example.game.network.entity;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.network.message.NetworkMessage;

/**
 * Implementation of our controllable entity.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Mario
        extends Entity
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(Mario.class);

    /** Animation turn. */
    private final Animation animTurn;
    /** Animation jump. */
    private final Animation animJump;
    /** Dead timer. */
    private final Timing timerDie;
    /** Dead step. */
    private int stepDie;
    /** Die location. */
    private double locationDie;
    /** Name. */
    private String name;

    /**
     * Standard constructor.
     * 
     * @param setup setup reference.
     */
    public Mario(SetupSurfaceGame setup)
    {
        super(setup);
        final Configurable configurable = setup.getConfigurable();
        animTurn = configurable.getAnimation("turn");
        animJump = configurable.getAnimation("jump");
        timerDie = new Timing();
        jumpForceValue = 8.0;
        movementSpeedValue = 3.0;
        addCollisionTile(EntityCollisionTileCategory.LEG_LEFT, -5, 0);
        addCollisionTile(EntityCollisionTileCategory.LEG_RIGHT, 5, 0);
    }

    /**
     * Kill mario.
     */
    public void kill()
    {
        dead = true;
        resetMovementSpeed();
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
        teleport(80, 32);
        dead = false;
    }

    /**
     * Jump mario.
     */
    public void jump()
    {
        jumpForce.setForce(0.0, jumpForceValue / 1.5);
        resetGravity();
    }

    /**
     * Set the displayed name.
     * 
     * @param name The name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the displayed name.
     * 
     * @return The name.
     */
    public String getName()
    {
        return name;
    }

    /*
     * Entity
     */

    @Override
    public void onHurtBy(EntityGame entity, int damages)
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
            final MessageEntity message = new MessageEntity(getClientId());
            message.addAction(MessageEntityElement.LOCATION_X, getLocationIntX());
            message.addAction(MessageEntityElement.LOCATION_Y, getLocationIntY());
            message.addAction(MessageEntityElement.JUMP, true);
            addNetworkMessage(message);
            jump();
        }
    }

    @Override
    public void applyMessage(NetworkMessage message)
    {
        if (!(message instanceof MessageEntity))
        {
            return;
        }
        final MessageEntity msg = (MessageEntity) message;
        if (message.getClientId() == getClientId().byteValue())
        {
            super.applyMessage(message);
            if (msg.hasAction(MessageEntityElement.JUMP))
            {
                jump();
            }
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        super.handleMovements(extrp);
        if (!dead)
        {
            // Smooth walking speed, but quick stop
            if (right || left)
            {
                movementForce.reachForce(extrp, movementForceDest, 0.3, 0.1);
            }
            else
            {
                movementForce.reachForce(extrp, movementForceDest, 0.5, 0.1);
            }
        }
        // Die
        if (dead)
        {
            if (timerDie.elapsed(500))
            {
                // Die effect
                if (stepDie == 0)
                {
                    jumpForce.setForce(0.0, jumpForceValue * 1.5);
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
    }

    @Override
    protected void selectAnimCur(EntityState state)
    {
        super.selectAnimCur(state);
        switch (state)
        {
            case TURN:
                animCur = animTurn;
                break;
            case JUMP:
                animCur = animJump;
                break;
            case WALK:
                setAnimSpeed(Math.abs(getDiffHorizontal() / 12.0));
                break;
            default:
                break;
        }
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
        // Send correct location if moving, or just idle
        if (server)
        {
            // Network location timer
            if (networkLocation.elapsed(250) && (state == EntityState.WALK || state == EntityState.IDLE))
            {
                final MessageEntity message = new MessageEntity(getClientId());
                message.addAction(MessageEntityElement.LOCATION_X, getLocationIntX());
                message.addAction(MessageEntityElement.LOCATION_Y, getLocationIntY());
                addNetworkMessage(message);
                networkLocation.restart();
            }
        }
    }
}
