package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

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
    /** Entity status. */
    protected final EntityStatus status;
    /** Dead timer. */
    protected final Timing timerDie;
    /** Desired fps value. */
    private final int desiredFps;
    /** Animations list. */
    private final EnumMap<EntityState, Animation> animations;
    /** Forces used. */
    private final Force[] forces;
    /** Dead step. */
    protected int stepDie;
    /** Die location. */
    protected Coord dieLocation;
    /** Dead flag. */
    private boolean dead;

    /**
     * Constructor.
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
        status = new EntityStatus();
        animations = new EnumMap<>(EntityState.class);
        timerDie = new Timing();
        dieLocation = new Coord();
        forces = new Force[0];
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
     * 
     * @see EntityState
     */
    protected abstract void updateStates();

    /**
     * Update the entity in dead case.
     */
    protected abstract void updateDead();

    /**
     * Update the collisions detection.
     * 
     * @see EntityCollision
     */
    protected abstract void updateCollisions();

    /**
     * Update the animations handling.
     * 
     * @param extrp The Extrapolation value.
     */
    protected abstract void updateAnimations(double extrp);

    /**
     * Kill entity.
     */
    public void kill()
    {
        dead = true;
        dieLocation.set(getLocationX(), getLocationY());
        stepDie = 0;
        timerDie.start();
    }

    /**
     * Respawn entity.
     */
    public void respawn()
    {
        dead = false;
        resetGravity();
        mirror(false);
        updateMirror();
        status.setCollision(EntityCollision.GROUND);
        status.backupCollision();
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
     * Get forces involved in gravity and movement. Return empty array by default.
     * 
     * @return The forces list.
     */
    protected Force[] getForces()
    {
        return forces;
    }

    /**
     * Load all existing animations defined in the config file.
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

    /*
     * EntityPlatform
     */

    @Override
    protected void handleActions(double extrp)
    {
        status.backupState();
        updateStates();
    }

    @Override
    protected void handleMovements(double extrp)
    {
        updateGravity(extrp, desiredFps, getForces());
        updateMirror();
        if (dead)
        {
            updateDead();
        }
    }

    @Override
    protected void handleCollisions(double extrp)
    {
        status.backupCollision();
        if (!isDead())
        {
            updateCollisions();
        }
    }

    @Override
    protected void handleAnimations(double extrp)
    {
        updateAnimations(extrp);
        if (status.stateChanged())
        {
            play(animations.get(status.getState()));
        }
        updateAnimation(extrp);
    }
}
