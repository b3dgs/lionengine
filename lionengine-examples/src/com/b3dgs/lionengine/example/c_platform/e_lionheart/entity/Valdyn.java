package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.EntityCollision;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.EntityState;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Valdyn entity implementation.
 */
public class Valdyn
        extends Entity
{
    /** Fall timer (timer used to know when its the falling state). */
    private final Timing timerFall;
    /** Fallen timer. */
    private final Timing timerFallen;
    /** Fallen duration in milli. */
    private final int fallenDuration;

    /**
     * Standard constructor.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    public Valdyn(SetupEntityGame setup, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
        fallenDuration = getDataInteger("fallenDuration", "data");
        timerFall = new Timing();
        timerFallen = new Timing();
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        if (!isDead())
        {
            right = keyboard.isPressed(Keyboard.RIGHT);
            left = keyboard.isPressed(Keyboard.LEFT);
            up = keyboard.isPressed(Keyboard.UP);
        }
    }

    /**
     * Respawn player.
     */
    public void respawn()
    {
        setLocation(512, 128);
        setDead(false);
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

    /*
     * Entity
     */

    @Override
    public void hitBy(Entity entity)
    {
        if (!isDead())
        {
            kill();
        }
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (!isJumping())
        {
            jumpForce.setForce(0.0, jumpHeightMax * 1.5);
        }
    }

    @Override
    public boolean isFalling()
    {
        return super.isFalling() && timerFall.elapsed(100);
    }

    @Override
    protected void updateStates()
    {
        final boolean mirror = getMirror();
        final double diffHorizontal = getDiffHorizontal();
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
        updateFall();
        updateFallen();
    }

    @Override
    protected void updateDead()
    {
        if (timerDie.elapsed(500))
        {
            // Die effect
            if (stepDie == 0)
            {
                jumpForce.setForce(-0.75, 1.5);
                stepDie = 1;
            }
            resetGravity();
            // Respawn
            if (stepDie == 1 && timerDie.elapsed(2000))
            {
                respawn();
            }
        }
        // Lock player
        else
        {
            resetGravity();
            setLocationX(dieLocation.getX());
            setLocationY(dieLocation.getY());
        }
    }

    @Override
    protected void updateCollisions()
    {
        if (getLocationY() < getLocationOldY() && timerFall.elapsed(100))
        {
            coll = EntityCollision.NONE;
        }

        // Vertical collision
        if (getDiffVertical() < 0 || isOnGround())
        {
            checkCollisionVertical(0);
        }

        // Kill when fall down
        if (getLocationY() < 0)
        {
            kill();
            setLocationY(0);
        }
    }

    @Override
    protected void updateAnimations()
    {
        if (state == EntityState.WALK || state == EntityState.TURN)
        {
            setAnimSpeed(Math.abs(getHorizontalForce()) / 9.0);
        }
    }
}
