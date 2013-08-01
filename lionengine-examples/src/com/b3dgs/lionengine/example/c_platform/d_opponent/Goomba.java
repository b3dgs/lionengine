package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Goomba implementation.
 */
class Goomba
        extends Entity
{
    /** Die timer. */
    private final Timing timerDie;

    /**
     * Standard constructor.
     * 
     * @param setup setup reference.
     * @param map The map reference.
     * @param desiredFps desired fps.
     */
    Goomba(SetupEntityGame setup, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
        timerDie = new Timing();
        right = true;
    }

    /*
     * Entity
     */

    @Override
    public void onHurtBy(EntityGame entity)
    {
        if (!dead)
        {
            dead = true;
            right = false;
            left = false;
            resetMovementSpeed();
            timerDie.start();
        }
    }

    @Override
    public void onHitThat(Entity entity)
    {
        // Nothing to do
    }

    @Override
    protected void handleActions(double extrp)
    {
        super.handleActions(extrp);
        if (dead && timerDie.elapsed(1000))
        {
            destroy();
        }
    }

    @Override
    protected void handleMovements(double extrp)
    {
        super.handleMovements(extrp);
        movementForce.reachForce(extrp, movementForceDest, 0.3, 0.1);
    }

    @Override
    protected void onHorizontalCollision()
    {
        // Invert movement in case of collision
        if (right)
        {
            right = false;
            left = true;
        }
        else
        {
            left = false;
            right = true;
        }
    }
}
