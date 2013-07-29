package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
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
    /** Dead timer. */
    protected final Timing timerDie;
    /** Movement force force. */
    protected final Force movementForce;
    /** Animations list. */
    private final EnumMap<EntityState, Animation> animations;
    /** Movement force destination force. */
    private final Force movementForceDest;
    /** Smooth speed value. */
    private final double smoothSpeed;
    /** Sensibility increase value. */
    private final double sensibilityIncrease;
    /** Sensibility decrease value. */
    private final double sensibilityDecrease;
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
    /** Entity state. */
    protected EntityState state;
    /** Collision state. */
    protected EntityCollision coll;
    /** Collision old state. */
    protected EntityCollision collOld;
    /** Die location. */
    protected Coord dieLocation;
    /** Entity old state. */
    private EntityState stateOld;
    /** Movement max speed. */
    private double movementSpeedMax;
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
        setFrameOffsets(getWidth() / 2, -8);
        setMass(getDataDouble("mass", "data"));
        setGravityMax(getDataDouble("gravityMax", "data"));
        movementSpeedMax = getDataDouble("speedMax", "data", "movement");
        smoothSpeed = getDataDouble("smooth", "data", "movement");
        sensibilityIncrease = getDataDouble("sensibilityIncrease", "data", "movement");
        sensibilityDecrease = getDataDouble("sensibilityDecrease", "data", "movement");
        jumpHeightMax = getDataDouble("heightMax", "data", "jump");
        dieLocation = new Coord();
        movementForce = new Force();
        movementForceDest = new Force();
        jumpForce = new Force();
        timerDie = new Timing();
        animations = new EnumMap<>(EntityState.class);
        state = EntityState.IDLE;
        stateOld = state;
        coll = EntityCollision.NONE;
        collOld = coll;
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
        return isOnGround();
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
        return !isOnGround() && getLocationY() < getLocationOldY();
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
                coll = EntityCollision.GROUND;
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
                resetMovementSpeed();
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
        return movementForce.getForceHorizontal();
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
     * Load all existing animations defined in the xml file.
     */
    private void loadAnimations()
    {
        for (EntityState state : EntityState.values())
        {
            try
            {
                animations.put(state, getAnimation(state.getAnimationName()));
            }
            catch (LionEngineException exception)
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

    /*
     * EntityPlatform
     */

    @Override
    protected void handleActions(double extrp)
    {
        updateForces();

        stateOld = state;
        final double diffHorizontal = getDiffHorizontal();
        if (!isDead() && diffHorizontal != 0.0)
        {
            mirror(diffHorizontal < 0.0);
        }
        updateStates();
        if (dead)
        {
            state = EntityState.DEAD;
        }
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
        if (!isDead())
        {
            collOld = coll;
            checkMapLimit();
            updateCollisions();
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        updateAnimations();
        if (state != stateOld)
        {
            play(animations.get(state));
        }
        updateAnimation(extrp);
    }
}
