package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * Handle the status of an entity.
 */
public final class EntityStatus
{
    /** Entity state. */
    private TypeEntityState state;
    /** Entity old state. */
    private TypeEntityState stateOld;
    /** Collision state. */
    private TypeEntityCollision collision;
    /** Collision old state. */
    private TypeEntityCollision collisionOld;

    /**
     * Constructor.
     */
    EntityStatus()
    {
        state = TypeEntityState.IDLE;
        stateOld = state;
        collision = TypeEntityCollision.NONE;
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
    public boolean collisionChangedFromTo(TypeEntityCollision from, TypeEntityCollision to)
    {
        return collisionOld == from && collision == to;
    }

    /**
     * Set the entity state.
     * 
     * @param state The entity state.
     */
    public void setState(TypeEntityState state)
    {
        this.state = state;
    }

    /**
     * Set the entity state.
     * 
     * @param collision The entity state.
     */
    public void setCollision(TypeEntityCollision collision)
    {
        this.collision = collision;
    }

    /**
     * Get the entity state.
     * 
     * @return The entity state.
     */
    public TypeEntityState getState()
    {
        return state;
    }

    /**
     * Get the entity collision.
     * 
     * @return The entity collision.
     */
    public TypeEntityCollision getCollision()
    {
        return collision;
    }
}
