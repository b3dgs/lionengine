/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.Factory;
import com.b3dgs.lionengine.game.factory.SetupSurface;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;
import com.b3dgs.lionengine.network.message.NetworkMessage;
import com.b3dgs.lionengine.network.purview.Networkable;
import com.b3dgs.lionengine.network.purview.NetworkableModel;

/**
 * Abstract entity base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Entity
        extends EntityPlatform
        implements Networkable
{
    /**
     * Get an entity configuration file.
     * 
     * @param type The config associated class.
     * @return The media config.
     */
    protected static Media getConfig(Class<? extends Entity> type)
    {
        return Core.MEDIA.create(FactoryEntity.ENTITY_DIR, type.getSimpleName() + "."
                + Factory.FILE_DATA_EXTENSION);
    }

    /** Movement force force. */
    protected final Force movementForce;
    /** Movement force destination force. */
    protected final Force movementForceDest;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Extra time for jump before fall. */
    protected final Timing timerExtraJump;
    /** Send correct location timer. */
    protected final Timing networkLocation;
    /** Network model. */
    private final NetworkableModel networkableModel;
    /** Animation idle. */
    private final Animation animIdle;
    /** Animation walk. */
    private final Animation animWalk;
    /** Animation jump. */
    private final Animation animDie;
    /** Desired fps value. */
    protected int desiredFps;
    /** Client flag. */
    protected boolean server;
    /** Jump force. */
    protected double jumpForceValue;
    /** Movement max speed. */
    protected double movementSpeedValue;
    /** Key right state. */
    protected boolean right;
    /** Key left state. */
    protected boolean left;
    /** Key up state. */
    protected boolean up;
    /** Current animation. */
    protected Animation animCur;
    /** Entity state. */
    protected EntityState state;
    /** Old state. */
    protected EntityState stateOld;
    /** Collision state. */
    protected EntityCollision coll;
    /** Dead flag. */
    protected boolean dead;
    /** Map reference. */
    private Map map;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    Entity(SetupSurface setup)
    {
        super(setup);
        final Configurer configurer = setup.getConfigurer();
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        final ConfigCollisions configCollisions = ConfigCollisions.create(configurer);
        animIdle = configAnimations.getAnimation("idle");
        animWalk = configAnimations.getAnimation("walk");
        animDie = configAnimations.getAnimation("dead");
        networkableModel = new NetworkableModel();
        movementForce = new Force();
        movementForceDest = new Force();
        jumpForce = new Force();
        timerExtraJump = new Timing();
        networkLocation = new Timing();
        networkLocation.start();
        state = EntityState.IDLE;
        setMass(configurer.getDouble("mass", "data"));
        setCollision(configCollisions.getCollision("default"));
        setFrameOffsets(0, 1);
        addCollisionTile(EntityCollisionTileCategory.GROUND_CENTER, 0, 0);
        addCollisionTile(EntityCollisionTileCategory.KNEE_LEFT, -5, 9);
        addCollisionTile(EntityCollisionTileCategory.KNEE_RIGHT, 5, 9);
    }

    /**
     * Called when hit this entity.
     * 
     * @param entity The entity hit.
     */
    public abstract void onHitThat(Entity entity);

    /**
     * Called when hurt.
     * 
     * @param entity Entity that hurt this.
     * @param damages Hurt damages.
     */
    public abstract void onHurtBy(EntityGame entity, int damages);

    /**
     * Check if hero can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    public boolean canJump()
    {
        return isOnGround();
    }

    /**
     * Check if hero is jumping.
     * 
     * @return <code>true</code> if jumping, <code>false</code> else.
     */
    public boolean isJumping()
    {
        return getLocationY() > getLocationOldY();
    }

    /**
     * Check if hero is falling.
     * 
     * @return <code>true</code> if falling, <code>false</code> else.
     */
    public boolean isFalling()
    {
        return getLocationY() < getLocationOldY();
    }

    /**
     * Check if hero is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    public boolean isOnGround()
    {
        return coll == EntityCollision.GROUND && !isFalling() && !isJumping();
    }

    /**
     * Check if entity is dead.
     * 
     * @return <code>true</code> if dead, <code>false</code> else.
     */
    public boolean isDead()
    {
        return dead;
    }

    /**
     * Check the vertical collision.
     * 
     * @param category The collision category.
     */
    protected void checkVertical(EntityCollisionTileCategory category)
    {
        final Tile tile = getCollisionTile(map, category);
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            if (applyVerticalCollision(y))
            {
                jumpForce.setDirection(Direction.ZERO);
                resetGravity();
                coll = EntityCollision.GROUND;
                // Start timer to allow entity to have an extra jump area before falling
                timerExtraJump.start();
            }
            else
            {
                coll = EntityCollision.NONE;
            }
        }
    }

    /**
     * Select the animation from the state.
     * 
     * @param state The current state
     */
    protected void selectAnimCur(EntityState state)
    {
        switch (state)
        {
            case IDLE:
                animCur = animIdle;
                break;
            case WALK:
                animCur = animWalk;
                break;
            case DEAD:
                animCur = animDie;
                break;
            default:
                break;
        }
    }

    /**
     * Set to zero movement speed force.
     */
    protected void resetMovementSpeed()
    {
        movementForce.setDirection(Direction.ZERO);
        movementForceDest.setDirection(Direction.ZERO);
    }

    /**
     * Called when an horizontal collision occurred.
     */
    protected void onHorizontalCollision()
    {
        // Nothing to do by default
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
            resetMovementSpeed();
        }
        final int limitRight = map.getWidthInTile() * map.getTileWidth();
        if (getLocationX() > limitRight)
        {
            setLocationX(limitRight);
            resetMovementSpeed();
        }
    }

    /**
     * Update the forces depending of the pressed key.
     */
    private void updateForces()
    {
        movementForceDest.setDirection(Direction.ZERO);
        final double speed;
        if (right && !left)
        {
            speed = movementSpeedValue;
        }
        else if (left && !right)
        {
            speed = -movementSpeedValue;
        }
        else
        {
            speed = 0.0;
        }
        movementForceDest.setDirection(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setDirection(0.0, jumpForceValue);
            resetGravity();
            coll = EntityCollision.NONE;
            timerExtraJump.stop();
        }
    }

    /**
     * Update entity states.
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
        if (dead)
        {
            state = EntityState.DEAD;
        }
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
                resetMovementSpeed();
                onHorizontalCollision();
            }
        }
    }

    /*
     * EntityPlatform
     */

    @Override
    public void prepare(Services context)
    {
        map = context.get(Map.class);
        desiredFps = context.get(Integer.class).intValue();
        server = context.get(Boolean.class).booleanValue();
    }

    @Override
    protected void handleActions(double extrp)
    {
        updateForces();
        updateStates();
    }

    @Override
    protected void handleMovements(double extrp)
    {
        updateGravity(extrp, movementForce, jumpForce);
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        checkMapLimit();

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
            checkVertical(EntityCollisionTileCategory.GROUND_CENTER);
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        // Assign an animation for each state
        selectAnimCur(state);
        // Play the assigned animation
        if (animCur != null && stateOld != state)
        {
            play(animCur);
        }
        updateAnimation(extrp);
    }

    /*
     * Networkable
     */

    @Override
    public void applyMessage(NetworkMessage message)
    {
        final MessageEntity msg = (MessageEntity) message;
        // States
        if (msg.hasAction(MessageEntityElement.RIGHT))
        {
            right = msg.getActionBoolean(MessageEntityElement.RIGHT);
        }
        if (msg.hasAction(MessageEntityElement.LEFT))
        {
            left = msg.getActionBoolean(MessageEntityElement.LEFT);
        }
        if (msg.hasAction(MessageEntityElement.UP))
        {
            up = msg.getActionBoolean(MessageEntityElement.UP);
        }
        // Location correction
        if (msg.hasAction(MessageEntityElement.LOCATION_X))
        {
            setLocationX(msg.getActionInteger(MessageEntityElement.LOCATION_X));
        }
        if (msg.hasAction(MessageEntityElement.LOCATION_Y))
        {
            setLocationY(msg.getActionInteger(MessageEntityElement.LOCATION_Y));
        }
    }

    @Override
    public void addNetworkMessage(NetworkMessage message)
    {
        networkableModel.addNetworkMessage(message);
    }

    @Override
    public Collection<NetworkMessage> getNetworkMessages()
    {
        return networkableModel.getNetworkMessages();
    }

    @Override
    public void clearNetworkMessages()
    {
        networkableModel.clearNetworkMessages();
    }

    @Override
    public void setClientId(Byte id)
    {
        networkableModel.setClientId(id);
    }

    @Override
    public Byte getClientId()
    {
        return networkableModel.getClientId();
    }
}
