package com.b3dgs.lionengine.example.c_platform.d_opponent;

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.EntityPlatform;

/**
 * Abstract entity base implementation.
 */
abstract class Entity
        extends EntityPlatform<TileCollision, Tile>
{
    /** Map reference. */
    protected final Map map;
    /** Desired fps value. */
    protected final int desiredFps;
    /** Movement force. */
    protected final Movement movement;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Extra time for jump before fall. */
    protected final Timing timerExtraJump;
    /** Animations list. */
    private final EnumMap<EntityState, Animation> animations;
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
    /** Entity state. */
    protected EntityState state;
    /** Old state. */
    protected EntityState stateOld;
    /** Collision state. */
    protected EntityCollision coll;
    /** Dead flag. */
    protected boolean dead;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    Entity(SetupEntityGame setup, Map map, int desiredFps)
    {
        super(setup, map);
        this.map = map;
        this.desiredFps = desiredFps;
        animations = new EnumMap<>(EntityState.class);
        jumpForceValue = getDataDouble("jumpSpeed", "data");
        movementSpeedValue = getDataDouble("movementSpeed", "data");
        movement = new Movement();
        jumpForce = new Force();
        timerExtraJump = new Timing();
        state = EntityState.IDLE;
        setMass(getDataDouble("mass", "data"));
        setFrameOffsets(getWidth() / 2, 1);
        loadAnimations();
    }

    /**
     * Called when hit this entity.
     * 
     * @param entity The entity hit.
     */
    public abstract void onHitThat(Entity entity);

    /**
     * Called when get hurt.
     * 
     * @param entity Entity hitting this.
     */
    protected abstract void onHurtBy(EntityGame entity);

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
     * Check if entity is on ground.
     * 
     * @return <code>true</code> if on ground, <code>false</code> else.
     */
    public boolean isOnGround()
    {
        return coll == EntityCollision.GROUND;
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
     * Called when horizontal collision occurred.
     */
    protected void onHorizontalCollision()
    {
        // Nothing by default
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
     * Update the forces depending of the pressed key.
     */
    private void updateForces()
    {
        movement.setForceToReach(Force.ZERO);
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
        movement.setForceToReach(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setForce(0.0, jumpForceValue);
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
     * @param offsetX The horizontal offset (leg).
     */
    private void checkHorizontal(int offsetX)
    {
        final Tile tile = collisionCheck(offsetX, 1, TileCollision.COLLISION_HORIZONTAL);
        if (tile != null)
        {
            final Double x = tile.getCollisionX(this);
            if (applyHorizontalCollision(x))
            {
                movement.reset();
                onHorizontalCollision();
            }
        }
    }

    /**
     * Check the vertical collision.
     * 
     * @param offsetX The horizontal offset (leg).
     */
    private void checkVertical(int offsetX)
    {
        final Tile tile = collisionCheck(offsetX, 0, TileCollision.COLLISION_VERTICAL);
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            if (applyVerticalCollision(y))
            {
                jumpForce.setForce(Force.ZERO);
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
        movement.update(extrp);
        updateGravity(extrp, desiredFps, movement.getForce(), jumpForce);
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        checkMapLimit();

        // Horizontal collision
        if (getDiffHorizontal() < 0)
        {
            checkHorizontal(-5); // Left leg
        }
        else if (getDiffHorizontal() > 0)
        {
            checkHorizontal(5); // Right leg
        }

        // Vertical collision
        if (getDiffVertical() < 0)
        {
            checkVertical(-5); // Left leg
            checkVertical(5); // Right leg
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
