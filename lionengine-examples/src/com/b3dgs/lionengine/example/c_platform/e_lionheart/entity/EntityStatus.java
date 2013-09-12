package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

/**
 * Handle the status of an entity.
 */
public final class EntityStatus
{
    /** Entity state. */
    private TypeState state;
    /** Entity old state. */
    private TypeState stateOld;
    /** Collision state. */
    private TypeEntityCollisionTile collision;
    /** Collision old state. */
    private TypeEntityCollisionTile collisionOld;

    /**
     * Constructor.
     */
    EntityStatus()
    {
        state = TypeEntityState.IDLE;
        stateOld = null;
        collision = TypeEntityCollisionTile.NONE;
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
    public boolean collisionChangedFromTo(TypeEntityCollisionTile from, TypeEntityCollisionTile to)
    {
        return collisionOld == from && collision == to;
    }

    /**
     * Set the entity state.
     * 
     * @param state The entity state.
     */
    public void setState(TypeState state)
    {
        this.state = state;
    }

    /**
     * Set the entity state.
     * 
     * @param collision The entity state.
     */
    public void setCollision(TypeEntityCollisionTile collision)
    {
        this.collision = collision;
    }

    /**
     * Get the entity state.
     * 
     * @return The entity state.
     */
    public TypeState getState()
    {
        return state;
    }

    /**
     * Get the entity collision.
     * 
     * @return The entity collision.
     */
    public TypeEntityCollisionTile getCollision()
    {
        return collision;
    }
    
    /**
     * Check if the state is one of them.
     * 
     * @param states The states.
     * @return <code>true</code> if one of them.
     */
    public boolean isState(TypeState... states)
    {
        for (TypeState state : states)
        {
            if (getState() == state)
            {
                return true;
            }
        }
        return false;
    }
}
