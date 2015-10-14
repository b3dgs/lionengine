/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.object;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.trait.Trait;

/**
 * Handler items implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class HandledObjectsImpl implements HandledObjects
{
    /** Free id error. */
    private static final String ERROR_FREE_ID = "No more free id available !";
    /** Id used (list of active id used). */
    private static final Collection<Integer> IDS = new HashSet<Integer>(16);
    /** Recycle id (reuse previous removed object id). */
    private static final Queue<Integer> RECYCLE = new LinkedList<Integer>();
    /** Last id used (last maximum id value). */
    private static int lastId;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} at the same time.
     */
    static Integer getFreeId() throws LionEngineException
    {
        if (!RECYCLE.isEmpty())
        {
            final Integer id = RECYCLE.poll();
            IDS.add(id);
            return id;
        }
        if (IDS.size() >= Integer.MAX_VALUE)
        {
            throw new LionEngineException(ERROR_FREE_ID);
        }
        while (IDS.contains(Integer.valueOf(lastId)))
        {
            lastId++;
        }
        final Integer id = Integer.valueOf(lastId);
        IDS.add(id);
        return id;
    }

    /** List of objects (key id the object id). */
    private final Map<Integer, ObjectGame> objects;
    /** List of typed items (key is the object id). */
    private final Map<Class<?>, Set<Object>> items;

    /**
     * Create the objects handler.
     */
    HandledObjectsImpl()
    {
        objects = new HashMap<Integer, ObjectGame>();
        items = new HashMap<Class<?>, Set<Object>>();
    }

    /**
     * Add an object.
     * 
     * @param object The object to add.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} at the same time.
     */
    public void add(ObjectGame object) throws LionEngineException
    {
        objects.put(object.getId(), object);

        for (final Class<? extends Trait> trait : object.getTraitsType())
        {
            addType(trait, object.getTrait(trait));
        }
        addType(object.getClass(), object);
        addSuperClass(object, object.getClass());
    }

    /**
     * Remove the object and all its references from its id.
     * 
     * @param id The id reference.
     */
    public void remove(Integer id)
    {
        final ObjectGame object = objects.get(id);
        for (final Class<? extends Trait> trait : object.getTraitsType())
        {
            remove(trait, object.getTrait(trait));
        }
        removeSuperClass(object, object.getClass());
        IDS.remove(object.getId());
        RECYCLE.add(object.getId());
    }

    /**
     * Get all objects id.
     * 
     * @return The ids list.
     */
    public Collection<Integer> getIds()
    {
        return objects.keySet();
    }

    /**
     * Add a type from its interface.
     * 
     * @param type The type interface.
     * @param object The type value.
     */
    private void addType(Class<?> type, Object object)
    {
        if (!items.containsKey(type))
        {
            items.put(type, new HashSet<Object>());
        }
        items.get(type).add(object);
    }

    /**
     * Add object parent super type.
     * 
     * @param object The current object to check.
     * @param type The current class level to check.
     */
    private void addSuperClass(Object object, Class<?> type)
    {
        for (final Class<?> types : type.getInterfaces())
        {
            addType(types, object);
        }
        final Class<?> parent = type.getSuperclass();
        if (parent != null)
        {
            addSuperClass(object, parent);
        }
    }

    /**
     * Remove object parent super type.
     * 
     * @param object The current object to check.
     * @param type The current class level to check.
     */
    private void removeSuperClass(Object object, Class<?> type)
    {
        for (final Class<?> types : type.getInterfaces())
        {
            if (items.containsKey(types))
            {
                remove(types, object);
            }
        }
        final Class<?> parent = type.getSuperclass();
        if (parent != null)
        {
            removeSuperClass(object, parent);
        }
    }

    /**
     * Remove the object from its type list.
     * 
     * @param type The type reference.
     * @param object The object reference.
     */
    private void remove(Class<?> type, Object object)
    {
        if (items.containsKey(type))
        {
            items.get(type).remove(object);
        }
    }

    /*
     * HandledObjects
     */

    @Override
    public ObjectGame get(Integer id)
    {
        return objects.get(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I> Iterable<I> get(Class<I> type)
    {
        if (!items.containsKey(type))
        {
            return Collections.emptySet();
        }
        return (Iterable<I>) items.get(type);
    }

    @Override
    public Iterable<ObjectGame> values()
    {
        return objects.values();
    }
}
