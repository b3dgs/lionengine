package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * Handle the status of an entity.
 */
public final class EntityStatus
{
    /** Entity state. */
    private State state;
    /** Entity old state. */
    private State stateOld;
    /** Collision state. */
    private EntityCollisionTile collision;
    /** Collision old state. */
    private EntityCollisionTile collisionOld;

    /**
     * Constructor.
     */
    EntityStatus()
    {
        state = EntityState.IDLE;
        stateOld = null;
        collision = EntityCollisionTile.NONE;
        collisionOld = collision;
    }

    /**
     * Backup the state.
     */
    public void backupState()
    {
        stateOld = state;
    }

    /**
     * Backup the collision.
     */
    public void backupCollision()
    {
        collisionOld = collision;
    }

    /**
     * Check if the state changed.
     * 
     * @return <code>true</code> if the state changed, <code>false</code> else.
     */
    public boolean stateChanged()
    {
        return stateOld != state;
    }

    /**
     * Check if the collision evolved from a value to another.
     * 
     * @param from The initial collision value.
     * @param to The expected collision value.
     * @return <code>true</code> if changed as expected, <code>false</code> else.
     */
    public boolean collisionChangedFromTo(EntityCollisionTile from, EntityCollisionTile to)
    {
        return collisionOld == from && collision == to;
    }

    /**
     * Set the entity state.
     * 
     * @param state The entity state.
     */
    public void setState(State state)
    {
        this.state = state;
    }

    /**
     * Set the entity state.
     * 
     * @param collision The entity state.
     */
    public void setCollision(EntityCollisionTile collision)
    {
        this.collision = collision;
    }

    /**
     * Get the entity state.
     * 
     * @return The entity state.
     */
    public State getState()
    {
        return state;
    }

    /**
     * Get the entity collision.
     * 
     * @return The entity collision.
     */
    public EntityCollisionTile getCollision()
    {
        return collision;
    }

    /**
     * Check if the state is one of them.
     * 
     * @param states The states.
     * @return <code>true</code> if one of them.
     */
    public boolean isState(State... states)
    {
        for (final State state : states)
        {
            if (getState() == state)
            {
                return true;
            }
        }
        return false;
    }
}
