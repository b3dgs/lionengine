package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.EntityPlatform;

/**
 * Abstract entity base implementation.
 */
public abstract class Entity
        extends EntityPlatform<TileCollision, Tile>
{
    /** Desired fps value. */
    protected final int desiredFps;
    /** Jump force. */
    protected double jumpForceValue;
    /** Movement max speed. */
    protected double movementSpeedValue;
    /** Movement force force. */
    protected final Force movementForce;
    /** Movement force destination force. */
    protected final Force movementForceDest;
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Map reference. */
    protected final Map map;
    /** Animation idle. */
    private final Animation animIdle;
    /** Animation walk. */
    private final Animation animWalk;
    /** Animation jump. */
    private final Animation animJump;
    /** Animation die. */
    private final Animation animDie;
    /** Extra time for jump before fall. */
    protected final Timing timerExtraJump;
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
    /** Entity old state. */
    protected EntityState stateOld;
    /** Collision state. */
    protected EntityCollision coll;
    /** Dead flag. */
    protected boolean dead;

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
        animIdle = getAnimation("idle");
        animWalk = getAnimation("walk");
        animJump = getAnimation("jump");
        animDie = getAnimation("die");
        jumpForceValue = getDataDouble("jumpSpeed", "data");
        movementSpeedValue = getDataDouble("movementSpeed", "data");
        this.map = map;
        this.desiredFps = desiredFps;
        movementForce = new Force();
        movementForceDest = new Force();
        jumpForce = new Force();
        timerExtraJump = new Timing();
        state = EntityState.IDLE;
        stateOld = state;
        setMass(getDataDouble("mass", "data"));
        setFrameOffsets(getWidth() / 2, -8);
    }

    /**
     * Update the forces depending of the pressed key.
     */
    private void updateForces()
    {
        movementForceDest.setForce(Force.ZERO);
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
        movementForceDest.setForce(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setForce(0.0, jumpForceValue);
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

    @Override
    protected void handleActions(double extrp)
    {
        updateForces();
        updateStates();

    }

    @Override
    protected void handleMovements(double extrp)
    {
        updateGravity(extrp, desiredFps, movementForce, jumpForce);
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        checkMapLimit();

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(0);
        }
    }

    /**
     * Check vertical axis.
     * 
     * @param offset The offset value.
     */
    private void checkCollisionVertical(int offset)
    {
        final Tile tile = collisionCheck(offset, 0, TileCollision.COLLISION_VERTICAL);
        if (tile != null)
        {
            final Double y = tile.getCollisionY(this);
            if (applyVerticalCollision(y))
            {
                resetGravity();
                jumpForce.setForce(Force.ZERO);
                coll = EntityCollision.GROUND;
                // Start timer to allow entity to have an extra jump area before falling
                timerExtraJump.start();
            }
        }
    }

    /**
     * Check horizontal axis.
     * 
     * @param offset The offset value.
     */
    private void checkCollisionHorizontal(int offset)
    {
        final Tile tile = collisionCheck(offset, 1, TileCollision.COLLISION_HORIZONTAL);
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

    @Override
    protected void handleAnimations(double extrp)
    {
        // Assign an animation for each state
        switch (state)
        {
            case IDLE:
                animCur = animIdle;
                break;
            case WALK:
                animCur = animWalk;
                setAnimSpeed(Math.abs(movementForce.getForceHorizontal() / 9.0));
                break;
            case JUMP:
                animCur = animJump;
                break;
            case DEAD:
                animCur = animDie;
                break;
            default:
                break;
        }
        // Play the assigned animation
        if (animCur != null && state != stateOld)
        {
            play(animCur);
        }
        updateAnimation(extrp);
    }

    /**
     * Check if hero can jump.
     * 
     * @return <code>true</code> if can jump, <code>false</code> else.
     */
    public boolean canJump()
    {
        return isOnGround() || timerExtraJump.isStarted() && !timerExtraJump.elapsed(50);
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
     * Set to zero movement speed force.
     */
    protected void resetMovementSpeed()
    {
        movementForce.setForce(Force.ZERO);
        movementForceDest.setForce(Force.ZERO);
    }

    /**
     * Called when an horizontal collision occurred.
     */
    protected void onHorizontalCollision()
    {
        // Nothing to do
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
}
