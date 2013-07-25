package com.b3dgs.lionengine.game.rts.ability;

import java.util.HashSet;
import java.util.Set;

/**
 * Ability model implementation.
 * 
 * @param <L> Ability listener type.
 * @param <U> Services used type.
 */
public abstract class AbilityModel<L, U extends L>
{
    /** Listener list. */
    protected final Set<L> listeners;
    /** User reference. */
    protected final U user;

    /**
     * Constructor.
     * 
     * @param user The user services reference.
     */
    public AbilityModel(U user)
    {
        this.user = user;
        listeners = new HashSet<>(1);
        addListener(user);
    }

    /**
     * Add the listener.
     * 
     * @param listener The listener to add.
     */
    public final void addListener(L listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove the listener.
     * 
     * @param listener The listener to remove.
     */
    public final void removeListener(L listener)
    {
        listeners.remove(listener);
    }
}
