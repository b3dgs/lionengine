package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Valdyn entity implementation.
 */
public class Valdyn
        extends Entity
{
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

    @Override
    protected void updateDead()
    {
        if (timerDie.elapsed(500))
        {
            // Die effect
            if (stepDie == 0)
            {
                jumpForce.setForce(0.0, jumpHeightMax * 1.5);
                stepDie = 1;
            }
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
            setLocationY(locationDie);
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        super.handleMovements(extrp);
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        if (!dead)
        {
            super.handleCollisions(extrp);

            // Kill when fall down
            if (getLocationY() < 0)
            {
                kill();
                setLocationY(0);
            }
        }
    }

    @Override
    public void hitBy(Entity entity)
    {
        if (!dead)
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

    /**
     * Respawn player.
     */
    public void respawn()
    {
        setLocation(80, 32);
        dead = false;
    }

    /**
     * Called when hurt.
     * 
     * @param source Hurt source.
     * @param damages Damages value.
     */
    public void onHurtBy(EntityGame source, int damages)
    {
        // Nothing to do
    }
}
