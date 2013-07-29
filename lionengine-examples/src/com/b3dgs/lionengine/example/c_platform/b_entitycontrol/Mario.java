package com.b3dgs.lionengine.example.c_platform.b_entitycontrol;

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
    /** Animation idle. */
    private final Animation animIdle;
    /** Animation walk. */
    private final Animation animWalk;
    /** Animation turn. */
    private final Animation animTurn;
    /** Animation jump. */
    private final Animation animJump;
    /** Key right state. */
    private boolean right;
    /** Key left state. */
    private boolean left;
    /** Key up state. */
    private boolean up;
    /** Current animation. */
    private Animation animCur;
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
        animIdle = getAnimation("idle");
        animWalk = getAnimation("walk");
        animTurn = getAnimation("turn");
        animJump = getAnimation("jump");
        state = EntityState.IDLE;
        stateOld = state;
        setLocation(100, 32);
        setMass(2.5);
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

    @Override
    protected void handleActions(double extrp)
    {
        // Update movement key
        if (right)
        {
            movementForceDest.setForce(Mario.MOVEMENT_SPEED, 0.0);
            mirror(getDiffHorizontal() < 0.0);
        }
        if (left)
        {
            movementForceDest.setForce(-Mario.MOVEMENT_SPEED, 0.0);
            mirror(getDiffHorizontal() < 0.0);
        }

        // Update jump key
        if (up)
        {
            if (canJump())
            {
                jumpForce.setForce(0.0, Mario.JUMP_FORCE);
            }
        }

        // Update states
        stateOld = state;
        if (!isOnGround())
        {
            state = EntityState.JUMP;
        }
        else if (getMirror() && right && getDiffHorizontal() < 0.0)
        {
            state = EntityState.TURN;
        }
        else if (!getMirror() && left && getDiffHorizontal() > 0.0)
        {
            state = EntityState.TURN;
        }
        else if (getDiffHorizontal() != 0.0)
        {
            state = EntityState.WALK;
        }
        else
        {
            state = EntityState.IDLE;
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        // Smooth walking speed...
        if (right || left)
        {
            movementForce.reachForce(extrp, movementForceDest, 0.3, 0.1);
        }
        // ...but quick stop
        else
        {
            movementForce.reachForce(extrp, Force.ZERO, 0.5, 0.1);
        }

        // Update final movement
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
        switch (state)
        {
            case IDLE:
                animCur = animIdle;
                break;
            case WALK:
                animCur = animWalk;
                setAnimSpeed(Math.abs(getDiffHorizontal() / 12.0));
                break;
            case TURN:
                animCur = animTurn;
                break;
            case JUMP:
                animCur = animJump;
                break;
            default:
                animCur = animIdle;
                break;
        }
        // Play the assigned animation
        if (stateOld != state)
        {
            this.play(animCur);
        }
        updateAnimation(extrp);
    }
}
