package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * Handle the status of an entity.
 */
public final class EntityStatus
{
    /** Entity state. */
    private EntityState state;
    /** Entity old state. */
    private EntityState stateOld;
    /** Collision state. */
    private EntityCollision collision;
    /** Collision old state. */
    private EntityCollision collisionOld;

    /**
     * Constructor.
     */
    EntityStatus()
    {
        state = EntityState.IDLE;
        stateOld = state;
        collision = EntityCollision.NONE;
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
    public boolean collisionChangedFromTo(EntityCollision from, EntityCollision to)
    {
        return collisionOld == from && collision == to;
    }

    /**
     * Set the entity state.
     * 
     * @param state The entity state.
     */
    public void setState(EntityState state)
    {
        this.state = state;
    }

    /**
     * Set the entity state.
     * 
     * @param collision The entity state.
     */
    public void setCollision(EntityCollision collision)
    {
        this.collision = collision;
    }

    /**
     * Get the entity state.
     * 
     * @return The entity state.
     */
    public EntityState getState()
    {
        return state;
    }

    /**
     * Get the entity collision.
     * 
     * @return The entity collision.
     */
    public EntityCollision getCollision()
    {
        return collision;
    }
}
