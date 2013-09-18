package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.io.IOException;
import java.util.HashMap;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.Coord;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.platform.EntityPlatform;

/**
 * Abstract entity base implementation.
 */
public abstract class Entity
        extends EntityPlatform
{
    /** Entity type. */
    public final TypeEntity type;
    /** Map reference. */
    protected final Map map;
    /** Animations list. */
    protected final HashMap<TypeState, Animation> animations;
    /** Collisions data. */
    protected final HashMap<Enum<?>, CollisionData> collisions;
    /** Entity status. */
    public final EntityStatus status;
    /** Dead timer. */
    protected final Timing timerDie;
    /** Desired fps value. */
    private final int desiredFps;
    /** Forces used. */
    private final Force[] forces;
    /** Mouse over state. */
    protected boolean over;
    /** Selected state. */
    protected boolean selected;
    /** Dead step. */
    protected int stepDie;
    /** Die location. */
    protected Coord dieLocation;
    /** Dead flag. */
    private boolean dead;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    protected Entity(Context context, TypeEntity type)
    {
        super(context.factoryEntity.getSetup(type));
        this.type = type;
        map = context.map;
        desiredFps = context.desiredFps;
        status = new EntityStatus();
        animations = new HashMap<>(4);
        collisions = new HashMap<>(4);
        timerDie = new Timing();
        dieLocation = new Coord();
        forces = new Force[0];
        loadCollisions(TypeEntityCollision.values());
        loadAnimations(TypeEntityState.values());
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
     * @see TypeEntityState
     */
    protected abstract void updateStates();

    /**
     * Update the entity in dead case.
     */
    protected abstract void updateDead();

    /**
     * Update the collisions detection.
     * 
     * @see TypeEntityCollisionTile
     */
    protected abstract void updateCollisions();

    /**
     * Update the animations handling.
     * 
     * @param extrp The Extrapolation value.
     */
    protected abstract void updateAnimations(double extrp);

    /**
     * Called when all entities are loaded.
     */
    public void prepare()
    {
        // Nothing by default
    }

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
        status.setCollision(TypeEntityCollisionTile.GROUND);
        status.backupCollision();
    }

    /**
     * Save entity.
     * 
     * @param file The file output.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeShort((short) getLocationIntX());
        file.writeShort((short) getLocationIntY());
    }

    /**
     * Load entity.
     * 
     * @param file The file input.
     * @throws IOException If error.
     */
    public void load(FileReading file) throws IOException
    {
        final int tx = file.readShort();
        final int ty = file.readShort();
        teleport(tx, ty);
    }

    /**
     * Set selection state.
     * 
     * @param selected The selected state.
     */
    public void setSelection(boolean selected)
    {
        this.selected = selected;
    }

    /**
     * Set over flag.
     * 
     * @param over The over flag.
     */
    public void setOver(boolean over)
    {
        this.over = over;
    }

    /**
     * Check if is over.
     * 
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver()
    {
        return over;
    }

    /**
     * Check if is selected.
     * 
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected()
    {
        return selected;
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
     * Called when entity has been updated.
     */
    public void onUpdated()
    {
        // Nothing by default
    }

    /**
     * Load all existing animations defined in the config file.
     * 
     * @param states The states to load.
     */
    protected final void loadAnimations(TypeState[] states)
    {
        for (final TypeState state : states)
        {
            try
            {
                animations.put(state, getDataAnimation(state.getAnimationName()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
    }

    /**
     * Load all collisions data.
     * 
     * @param values The collisions list.
     */
    protected final void loadCollisions(Enum<?>[] values)
    {
        for (final Enum<?> collision : values)
        {
            try
            {
                collisions.put(collision, getDataCollision(collision.toString()));
            }
            catch (final LionEngineException exception)
            {
                continue;
            }
        }
        setCollision(collisions.get(TypeEntityCollision.DEFAULT));
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
