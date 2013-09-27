package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Implementation of our controllable entity.
 */
final class Mario
        extends Entity
{
    /** Dead timer. */
    private final Timing timerDie;
    /** Dead step. */
    private int stepDie;
    /** Die location. */
    private double locationDie;

    /**
     * Constructor.
     * 
     * @param setup setup reference.
     * @param map The map reference.
     * @param desiredFps desired fps.
     */
    Mario(SetupSurfaceGame setup, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
        timerDie = new Timing();
        addCollisionTile(EntityCollisionTileCategory.LEG_LEFT, -5, 0);
        addCollisionTile(EntityCollisionTileCategory.LEG_RIGHT, 5, 0);
    }

    /**
     * Update the mario controls.
     * 
     * @param keyboard The keyboard reference.
     */
    public void updateControl(Keyboard keyboard)
    {
        if (!dead)
        {
            right = keyboard.isPressed(Keyboard.RIGHT);
            left = keyboard.isPressed(Keyboard.LEFT);
            up = keyboard.isPressed(Keyboard.UP);
        }
    }

    /**
     * Kill mario.
     */
    public void kill()
    {
        dead = true;
        movement.reset();
        locationDie = getLocationY();
        stepDie = 0;
        timerDie.start();
    }

    /**
     * Respawn mario.
     */
    public void respawn()
    {
        mirror(false);
        teleport(80, 25);
        timerDie.stop();
        stepDie = 0;
        dead = false;
        movement.reset();
        jumpForce.setForce(Force.ZERO);
        resetGravity();
    }

    /*
     * Entity
     */

    @Override
    public void onHurtBy(EntityGame entity)
    {
        if (!dead)
        {
            kill();
        }
    }

    @Override
    public void onHitThat(Entity entity)
    {
        if (!isJumping())
        {
            jumpForce.setForce(0.0, jumpForceValue / 1.5);
            resetGravity();
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        if (!dead)
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
            movement.setVelocity(speed);
            movement.setSensibility(sensibility);
        }

        // Die
        if (dead)
        {
            if (timerDie.elapsed(500))
            {
                // Die effect
                if (stepDie == 0)
                {
                    jumpForce.setForce(0.0, jumpForceValue);
                    stepDie = 1;
                }
                // Respawn
                if (stepDie == 1 && timerDie.elapsed(2000))
                {
                    respawn();
                }
            }
            // Lock mario
            else
            {
                resetGravity();
                setLocationY(locationDie);
            }
        }
        super.handleMovements(extrp);
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        if (!dead)
        {
            super.handleCollisions(extrp);

            // Vertical collision
            if (getDiffVertical() < 0 || isOnGround())
            {
                checkVertical(EntityCollisionTileCategory.LEG_LEFT);
                checkVertical(EntityCollisionTileCategory.LEG_RIGHT);
            }

            // Kill when fall down
            if (getLocationY() < 0)
            {
                kill();
            }
        }
    }
}
