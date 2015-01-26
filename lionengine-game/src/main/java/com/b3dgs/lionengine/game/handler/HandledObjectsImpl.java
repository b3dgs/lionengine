/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handler items implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class HandledObjectsImpl
        implements HandledObjects
{
    /** List of objects (key id the object id). */
    private final Map<Integer, ObjectGame> objects;
    /** List of typed items (key is the object id). */
    private final Map<Class<?>, Set<Object>> items;

    /**
     * Create the handler items.
     */
    public HandledObjectsImpl()
    {
        objects = new HashMap<>();
        items = new HashMap<>();
    }

    /**
     * Add a object.
     * 
     * @param object The object to add.
     */
    public void add(ObjectGame object)
    {
        final Integer id = object.getId();
        objects.put(id, object);

        for (final Class<?> trait : object.getTraitsType())
        {
            addType(trait, object.getTrait(trait));
        }
        for (final Class<?> types : object.getClass().getInterfaces())
        {
            addType(types, object);
        }
    }

    /**
     * Remove the object and all its references from its id.
     * 
     * @param id The id reference.
     */
    public void remove(Integer id)
    {
        final ObjectGame object = objects.get(id);
        for (final Class<?> trait : object.getTraitsType())
        {
            remove(trait, object.getTrait(trait));
        }
        for (final Class<?> types : object.getClass().getInterfaces())
        {
            if (items.containsKey(types))
            {
                remove(types, object);
            }
        }
        object.onRemoved();
    }

    /**
     * Get all objects id.
     * 
     * @return The id list.
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
            items.put(type, new HashSet<>());
        }
        items.get(type).add(object);
    }

    /*
     * HandlerItems
     */

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

    @Override
    public ObjectGame get(Integer id)
    {
        return objects.get(id);
    }

    @SuppressWarnings("unchecked")
    @Override
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
