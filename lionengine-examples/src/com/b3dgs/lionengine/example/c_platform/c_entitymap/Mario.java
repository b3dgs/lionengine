package com.b3dgs.lionengine.example.c_platform.c_entitymap;

import java.util.EnumMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.EntityPlatform;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Implementation of our controllable entity.
 */
class Mario
        extends EntityPlatform<TileCollision, Tile>
{
    /** Jump force. */
    private final double jumpSpeed;
    /** Movement max speed. */
    private final double movementSpeed;
    /** Map reference. */
    private final Map map;
    /** Desired fps value. */
    private final int desiredFps;
    /** Movement force force. */
    private final Force movementForce;
    /** Movement force destination force. */
    private final Force movementForceDest;
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
    /** Collision state. */
    private EntityCollision coll;

    /**
     * Standard constructor.
     * 
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    Mario(Map map, int desiredFps)
    {
        super(new SetupEntityGame(Media.get("entity", "mario.xml")), map);
        this.map = map;
        this.desiredFps = desiredFps;
        movementForce = new Force();
        movementForceDest = new Force();
        jumpForce = new Force();
        jumpSpeed = getDataDouble("jumpSpeed", "data");
        movementSpeed = getDataDouble("movementSpeed", "data");
        animations = new EnumMap<>(EntityState.class);
        state = EntityState.IDLE;
        setMass(getDataDouble("mass", "data"));
        // Mario leg = ground referential
        setFrameOffsets(getWidth() / 2, 1);
        setLocation(80, 32);
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
     * Update the forces depending of the pressed key.
     */
    private void updateForces()
    {
        movementForceDest.setForce(Force.ZERO);
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
        movementForceDest.setForce(speed, 0.0);

        if (up && canJump())
        {
            jumpForce.setForce(0.0, jumpSpeed);
            coll = EntityCollision.NONE;
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

    /**
     * Reset to 0 the movement speed.
     */
    private void resetMovementSpeed()
    {
        movementForce.setForce(Force.ZERO);
        movementForceDest.setForce(Force.ZERO);
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

    /*
     * EntityPlatform
     */

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
                resetMovementSpeed();
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
            }
            else
            {
                coll = EntityCollision.NONE;
            }
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
        movementForce.reachForce(extrp, movementForceDest, speed, sensibility);
        updateGravity(extrp, desiredFps, movementForce, jumpForce);
        updateMirror();
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        checkMapLimit();

        // Respawn when die
        if (getLocationY() < 0)
        {
            setLocation(80, 32);
            resetMovementSpeed();
            jumpForce.setForce(Force.ZERO);
            resetGravity();
        }

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
            setAnimSpeed(Math.abs(movementForce.getForceHorizontal()) / 12.0);
        }
        // Play the assigned animation
        if (stateOld != state)
        {
            play(animations.get(state));
        }
        updateAnimation(extrp);
    }
}
