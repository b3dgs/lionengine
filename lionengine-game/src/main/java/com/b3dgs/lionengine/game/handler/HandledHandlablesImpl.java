/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Handler handlables implementation.
 */
final class HandledHandlablesImpl implements Handlables
{
    /** Handlable not found error. */
    private static final String ERROR_OBJECT_NOT_FOUND = "Object not found: ";

    /** List of handlables (key is the handlable ID). */
    private final Map<Integer, Handlable> handlables;
    /** List of typed items (key is the feature type). */
    private final Map<Class<?>, Set<Object>> items;

    /**
     * Create the objects handler.
     */
    HandledHandlablesImpl()
    {
        handlables = new HashMap<Integer, Handlable>();
        items = new HashMap<Class<?>, Set<Object>>();
    }

    /**
     * Add a handlable.
     * 
     * @param handlable The handlable to add.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} handlables at the same time.
     */
    public void add(Handlable handlable)
    {
        handlables.put(handlable.getId(), handlable);

        for (final Class<?> type : handlable.getClass().getInterfaces())
        {
            addType(type, handlable);
        }

        for (final Class<? extends Feature> feature : handlable.getFeaturesType())
        {
            final Feature object = handlable.getFeature(feature);
            addType(feature, object);
            for (final Class<?> other : UtilReflection.getInterfaces(feature, Feature.class))
            {
                addType(other, object);
            }
        }
        addType(handlable.getClass(), handlable);
        addSuperClass(handlable, handlable.getClass());
    }

    /**
     * Remove the handlable and all its references.
     * 
     * @param handlable The handlable reference.
     */
    public void remove(Handlable handlable)
    {
        for (final Class<?> type : handlable.getClass().getInterfaces())
        {
            remove(type, handlable);
        }

        for (final Class<? extends Feature> feature : handlable.getFeaturesType())
        {
            final Feature object = handlable.getFeature(feature);
            remove(feature, object);
            for (final Class<?> other : UtilReflection.getInterfaces(feature, Feature.class))
            {
                remove(other, object);
            }

        }
        remove(handlable.getClass(), handlable);
        removeSuperClass(handlable, handlable.getClass());

        handlables.remove(handlable.getId());
    }

    /**
     * Get all handlables ID.
     * 
     * @return The IDs list.
     */
    public Collection<Integer> getIds()
    {
        return handlables.keySet();
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
            remove(types, object);
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
        final Set<?> set = items.get(type);
        if (set != null)
        {
            set.remove(object);
        }
    }

    /*
     * HandledHandlables
     */

    @Override
    public Handlable get(Integer id)
    {
        final Handlable handlable = handlables.get(id);
        if (handlable != null)
        {
            return handlable;
        }
        throw new LionEngineException(ERROR_OBJECT_NOT_FOUND, String.valueOf(id));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I> Iterable<I> get(Class<I> type)
    {
        final Set<?> handlables = items.get(type);
        if (handlables != null)
        {
            return (Iterable<I>) handlables;
        }
        return Collections.emptySet();
    }

    @Override
    public Iterable<Handlable> values()
    {
        return handlables.values();
    }
}
