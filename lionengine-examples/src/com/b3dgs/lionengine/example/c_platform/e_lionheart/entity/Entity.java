package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.Coord;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.EntityPlatform;

/**
 * Abstract entity base implementation.
 */
public abstract class Entity
        extends EntityPlatform<TileCollision, Tile>
{
    /** Map reference. */
    protected final Map map;
    /** Desired fps value. */
    protected final int desiredFps;
    /** Entity status. */
    protected final EntityStatus status;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Dead timer. */
    protected final Timing timerDie;
    /** Movement force. */
    protected final Movement movement;
    /** Entity actions. */
    protected final EnumMap<EntityAction, Boolean> actions;
    /** Animations list. */
    private final EnumMap<EntityState, Animation> animations;
    /** Sensibility increase value. */
    private final double sensibilityIncrease;
    /** Sensibility decrease value. */
    private final double sensibilityDecrease;
    /** Movement max speed. */
    private final double movementSpeedMax;
    /** Jump force. */
    protected double jumpHeightMax;
    /** Dead step. */
    protected int stepDie;
    /** Die location. */
    protected Coord dieLocation;
    /** Dead flag. */
    private boolean dead;

    /**
     * Standard constructor.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    public Entity(SetupEntityGame setup, Map map, int desiredFps)
    {
        super(setup, map);
        this.map = map;
        this.desiredFps = desiredFps;
        status = new EntityStatus();
        movement = new Movement();
        actions = new EnumMap<>(EntityAction.class);
        animations = new EnumMap<>(EntityState.class);
        setFrameOffsets(getWidth() / 2, -8);
        setMass(getDataDouble("mass", "data"));
        setGravityMax(getDataDouble("gravityMax", "data"));
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        movement.setVelocity(getDataDouble("smooth", "data", "movement"));
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        jumpHeightMax = getDataDouble("heightMax", "data", "jump");
        dieLocation = new Coord();
        jumpForce = new Force();
        timerDie = new Timing();
        loadAnimations();
    }

    /**
     * Called when this is hit by another entity.
     * 
     * @param entity The entity hit.
     */
    public abstract void hitBy(Entity entity);

    /**
     * Called when this hit that.
     * 
     * @param entity The entity hit.
     */
    public abstract void hitThat(Entity entity);

    /**
     * Update entity states.
     */
    protected abstract void updateStates();

    /**
     * Update the entity in dead case.
     */
    protected abstract void updateDead();

    /**
     * Update the collisions detection.
     */
    protected abstract void updateCollisions();

    /**
     * Update the animations handling.
     */
    protected abstract void updateAnimations();

    /**
     * Kill entity.
     */
    public void kill()
    {
        dead = true;
        movement.reset();
        dieLocation.set(getLocationX(), getLocationY());
        stepDie = 0;
        timerDie.start();
    }

    /**
     * Respawn entity.
     */
    public void respawn()
    {
        setDead(false);
        resetGravity();
        movement.reset();
        mirror(false);
        updateMirror();
        status.setCollision(EntityCollision.GROUND);
        status.backupCollision();
    }

    /**
     * Check if entity can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    public boolean canJump()
    {
        return isOnGround();
    }

    /**
     * Check if entity is jumping.
     * 
     * @return <code>true</code> if jumping, <code>false</code> else.
     */
    public boolean isJumping()
    {
        return !isOnGround() && getLocationY() > getLocationOldY();
    }

    /**
     * Check if entity is falling.
     * 
     * @return <code>true</code> if falling, <code>false</code> else.
     */
    public boolean isFalling()
    {
        return !isOnGround() && getLocationY() < getLocationOldY();
    }

    /**
     * Check if entity is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    public boolean isOnGround()
    {
        return status.getCollision() == EntityCollision.GROUND;
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
     * Check vertical axis.
     * 
     * @param offset The offset value.
     */
    protected void checkCollisionVertical(int offset)
    {
        final Tile tile = collisionCheck(offset, 0, TileCollision.COLLISION_VERTICAL);
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            if (applyVerticalCollision(y))
            {
                resetGravity();
                jumpForce.setForce(Force.ZERO);
                status.setCollision(EntityCollision.GROUND);
            }
        }
    }

    /**
     * Check horizontal axis.
     * 
     * @param offset The offset value.
     */
    protected void checkCollisionHorizontal(int offset)
    {
        final Tile tile = collisionCheck(offset, 1, TileCollision.COLLISION_HORIZONTAL);
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
     * Set the dead status.
     * 
     * @param state <code>true</code> if dead, <code>false</code> else.
     */
    protected void setDead(boolean state)
    {
        dead = state;
    }

    /**
     * Get the horizontal force.
     * 
     * @return The horizontal force.
     */
    protected double getHorizontalForce()
    {
        return movement.getForce().getForceHorizontal();
    }

    /**
     * Check if the specified action is enabled.
     * 
     * @param action The action to check.
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    protected boolean isEnabled(EntityAction action)
    {
        return actions.get(action).booleanValue();
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
                animations.put(state, getAnimation(state.getAnimationName()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    /**
     * Update the actions.
     */
    private void updateActions()
    {
        final double speed;
        final double sensibility;
        if (isEnabled(EntityAction.MOVE_RIGHT) && !isEnabled(EntityAction.MOVE_LEFT))
        {
            speed = movementSpeedMax;
            sensibility = sensibilityIncrease;
        }
        else if (isEnabled(EntityAction.MOVE_LEFT) && !isEnabled(EntityAction.MOVE_RIGHT))
        {
            speed = -movementSpeedMax;
            sensibility = sensibilityIncrease;
        }
        else
        {
            speed = 0.0;
            sensibility = sensibilityDecrease;
        }
        movement.setSensibility(sensibility);
        movement.setForceToReach(speed, 0.0);

        if (isEnabled(EntityAction.JUMP) && canJump())
        {
            jumpForce.setForce(0.0, jumpHeightMax);
            status.setCollision(EntityCollision.NONE);
        }
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

    /*
     * EntityPlatform
     */

    @Override
    protected void handleActions(double extrp)
    {
        if (!isDead())
        {
            updateActions();
        }
        status.backupState();
        updateStates();
    }

    @Override
    protected void handleMovements(double extrp)
    {
        movement.update(extrp);
        updateGravity(extrp, desiredFps, jumpForce, movement.getForce());
        updateMirror();
        if (dead)
        {
            updateDead();
        }
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        status.backupCollision();
        if (!isDead())
        {
            checkMapLimit();
            updateCollisions();
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        updateAnimations();
        if (status.stateChanged())
        {
            play(animations.get(status.getState()));
        }
        updateAnimation(extrp);
    }
}
