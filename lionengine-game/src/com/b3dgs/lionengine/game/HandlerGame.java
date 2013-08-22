package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Design to handle objects.
 * 
 * @param <K> The key type used.
 * @param <E> The object type used.
 */
public abstract class HandlerGame<K, E>
{
    /** List of entities handled. */
    private final Map<K, E> objects;
    /** List of entities to delete. */
    private final List<E> toDelete;
    /** List of entities to add. */
    private final List<E> toAdd;
    /** Will delete flag. */
    private boolean willDelete;
    /** Will add flag. */
    private boolean willAdd;

    /**
     * Constructor.
     */
    public HandlerGame()
    {
        objects = new HashMap<>(8);
        toDelete = new ArrayList<>(1);
        toAdd = new ArrayList<>(1);
        willDelete = false;
        willAdd = false;
    }

    /**
     * Get the object key.
     * 
     * @param object The object reference.
     * @return The object key.
     */
    protected abstract K getKey(E object);

    /**
     * Add an object to the handler list. Don't forget to call {@link #updateAdd()} at the begin of the update to add
     * them properly.
     * 
     * @param object The object to add.
     */
    public void add(E object)
    {
        toAdd.add(object);
        willAdd = true;
    }

    /**
     * Get the object from its id.
     * 
     * @param key The object key.
     * @return The object reference.
     */
    public E get(K key)
    {
        return objects.get(key);
    }

    /**
     * Add an object to the remove list. Don't forget to call {@link #updateRemove()} at the end of the update to clear
     * them properly.
     * 
     * @param object The object to remove.
     */
    public void remove(E object)
    {
        toDelete.add(object);
        willDelete = true;
    }

    /**
     * Get the number of objects handled.
     * 
     * @return The number of objects handled.
     */
    public int size()
    {
        return objects.size();
    }

    /**
     * Get the list reference of handled objects.
     * 
     * @return The list reference of handled objects.
     */
    public Collection<E> list()
    {
        return objects.values();
    }

    /**
     * Update the add list. Should be called before main update.
     */
    protected void updateAdd()
    {
        if (willAdd)
        {
            for (final E object : toAdd)
            {
                objects.put(getKey(object), object);
            }
            toAdd.clear();
            willAdd = false;
        }
    }

    /**
     * Update the remove list. Should be called after main update.
     */
    protected void updateRemove()
    {
        if (willDelete)
        {
            for (final E object : toDelete)
            {
                objects.remove(getKey(object));
            }
            toDelete.clear();
            willDelete = false;
        }
    }
}
