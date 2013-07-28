package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Tile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.Coord;
import com.b3dgs.lionengine.game.Force;
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
    /** Movement jump force. */
    protected final Force jumpForce;
    /** Extra time for jump before fall. */
    protected final Timing timerExtraJump;
    /** Dead timer. */
    protected final Timing timerDie;
    /** Fall timer (timer used to know when its the falling state). */
    private final Timing timerFall;
    /** Fallen timer. */
    private final Timing timerFallen;
    /** Movement force force. */
    private final Force movementForce;
    /** Movement force destination force. */
    private final Force movementForceDest;
    /** Smooth speed value. */
    private final double smoothSpeed;
    /** Sensibility increase value. */
    private final double sensibilityIncrease;
    /** Sensibility decrease value. */
    private final double sensibilityDecrease;
    /** Fallen duration in milli. */
    private final int fallenDuration;
    /** Animation idle. */
    private final Animation animIdle;
    /** Animation walk. */
    private final Animation animWalk;
    /** Animation jump. */
    private final Animation animJump;
    /** Animation fall. */
    private final Animation animFall;
    /** Animation fallen. */
    private final Animation animFallen;
    /** Animation die. */
    private final Animation animDie;
    /** Jump force. */
    protected double jumpHeightMax;
    /** Key right state. */
    protected boolean right;
    /** Key left state. */
    protected boolean left;
    /** Key up state. */
    protected boolean up;
    /** Dead step. */
    protected int stepDie;
    /** Current animation. */
    private Animation animCur;
    /** Entity state. */
    private EntityState state;
    /** Entity old state. */
    private EntityState stateOld;
    /** Collision state. */
    private EntityCollision coll;
    /** Collision old state. */
    private EntityCollision collOld;
    /** Movement max speed. */
    private double movementSpeedMax;
    /** Dead flag. */
    private boolean dead;
    /** Die location. */
    private Coord dieLocation;

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
        setFrameOffsets(getWidth() / 2, -8);
        setMass(getDataDouble("mass", "data"));
        setGravityMax(16);
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        smoothSpeed = getDataDouble("smooth", "data", "movement");
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        jumpHeightMax = getDataDouble("heightMax", "data", "jump");
        fallenDuration = getDataInteger("fallenDuration", "data");
        animIdle = getAnimation("idle");
        animWalk = getAnimation("walk");
        animJump = getAnimation("jump");
        animFall = getAnimation("fall");
        animFallen = getAnimation("fallen");
        animDie = getAnimation("die");
        dieLocation = new Coord();
        movementForce = new Force();
        movementForceDest = new Force();
        jumpForce = new Force();
        timerExtraJump = new Timing();
        timerDie = new Timing();
        timerFall = new Timing();
        timerFallen = new Timing();
        state = EntityState.IDLE;
        stateOld = state;
        coll = EntityCollision.NONE;
        collOld = coll;
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
     * Update the entity in dead case.
     */
    protected abstract void updateDead();

    /**
     * Kill entity.
     */
    public void kill()
    {
        dead = true;
        resetMovementSpeed();
        dieLocation.set(getLocationX(), getLocationY());
        stepDie = 0;
        timerDie.start();
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
        return !isOnGround() && getLocationY() > getLocationOldY();
    }

    /**
     * Check if hero is falling.
     * 
     * @return <code>true</code> if falling, <code>false</code> else.
     */
    public boolean isFalling()
    {
        return !isOnGround() && getLocationY() < getLocationOldY() && timerFall.elapsed(100);
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
     * Set the dead status.
     * 
     * @param state <code>true</code> if dead, <code>false</code> else.
     */
    protected void setDead(boolean state)
    {
        dead = state;
    }

    /**
     * Get the die location. This is where the entity is dead.
     * 
     * @return The die location.
     */
    protected Coord getDieLocation()
    {
        return dieLocation;
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
     * Update the forces depending of the pressed key.
     */
    private void updateForces()
    {
        movementForceDest.setForce(Force.ZERO);
        final double speed;
        if (right && !left)
        {
            speed = movementSpeedMax;
        }
        else if (left && !right)
        {
            speed = -movementSpeedMax;
        }
        else
        {
            speed = 0.0;
        }
        movementForceDest.setForce(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setForce(0.0, jumpHeightMax);
            coll = EntityCollision.NONE;
            timerExtraJump.stop();
        }
    }

    /**
     * Update the movement by using the defined forces.
     * 
     * @param extrp The extrapolation value.
     */
    private void updateMovement(double extrp)
    {
        // Smooth walking speed...
        final double sensibility;
        if (right || left)
        {
            sensibility = sensibilityIncrease;
        }
        // ...but quick stop
        else
        {
            sensibility = sensibilityDecrease;
        }
        movementForce.reachForce(extrp, movementForceDest, smoothSpeed, sensibility);
    }

    /**
     * Update the fall calculation (timer used to know when the entity is truly falling).
     */
    private void updateFall()
    {
        final double diffVertical = getDiffVertical();
        if (!timerFall.isStarted())
        {
            if (diffVertical < 0.0)
            {
                timerFall.start();
            }
        }
        else if (diffVertical >= 0.0)
        {
            timerFall.stop();
        }
    }

    /**
     * Update the fallen calculation (timer used to know the fallen time duration).
     */
    private void updateFallen()
    {
        if (!timerFallen.isStarted())
        {
            if (collOld == EntityCollision.NONE && coll == EntityCollision.GROUND)
            {
                timerFallen.start();
            }
        }
        else if (timerFallen.elapsed(fallenDuration))
        {
            timerFallen.stop();
        }
    }

    /**
     * Update entity states.
     */
    private void updateStates()
    {
        final double diffHorizontal = getDiffHorizontal();
        stateOld = state;

        if (!isDead() && diffHorizontal != 0.0)
        {
            mirror(diffHorizontal < 0.0);
        }

        updateFall();
        updateFallen();

        final boolean mirror = getMirror();
        if (isFalling())
        {
            state = EntityState.FALL;
        }
        else if (isJumping())
        {
            state = EntityState.JUMP;
        }
        else if (timerFallen.isStarted())
        {
            state = EntityState.FALLEN;
        }
        else if (isOnGround())
        {
            if ((mirror && right && diffHorizontal < 0.0 || !mirror && left && diffHorizontal > 0.0))
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

    /*
     * Entity
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
        updateGravity(extrp, desiredFps, movementForce, jumpForce);
        updateMirror();
        if (!dead)
        {
            updateMovement(extrp);
        }
        else
        {
            updateDead();
        }
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        collOld = coll;
        if (getLocationY() < getLocationOldY() && timerFall.elapsed(100))
        {
            coll = EntityCollision.NONE;
        }
        checkMapLimit();

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(0);
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
            case FALLEN:
                animCur = animFallen;
                break;
            case WALK:
            case TURN:
                animCur = animWalk;
                setAnimSpeed(Math.abs(movementForce.getForceHorizontal()) / 9.0);
                break;
            case JUMP:
                animCur = animJump;
                break;
            case FALL:
                animCur = animFall;
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
}
