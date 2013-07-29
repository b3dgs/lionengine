package com.b3dgs.lionengine.example.c_platform.b_entitycontrol;

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
    /** Mario configuration full path. */
    private static final Media MARIO_CONFIG = Media.get("entity", "mario.xml");
    /** Jump force. */
    private static final double JUMP_FORCE = 8.0;
    /** Ground location y. */
    private static final int GROUND = 32;
    /** Movement max speed. */
    private static final double MOVEMENT_SPEED = 3.0;
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

    /**
     * Standard constructor.
     * 
     * @param desiredFps The desired fps.
     */
    Mario(int desiredFps)
    {
        super(new SetupEntityGame(Mario.MARIO_CONFIG), null);
        this.desiredFps = desiredFps;
        movementForce = new Force();
        movementForceDest = new Force();
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
        movementForceDest.setForce(Force.ZERO);
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
        movementForceDest.setForce(speed, 0.0);

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
        movementForce.reachForce(extrp, movementForceDest, speed, sensibility);
        updateGravity(extrp, desiredFps, movementForce, jumpForce);
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
            setLocationY(Mario.GROUND);
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
