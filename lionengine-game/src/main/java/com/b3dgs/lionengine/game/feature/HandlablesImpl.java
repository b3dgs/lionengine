/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Feature;

/**
 * Handlables implementation.
 */
final class HandlablesImpl implements Handlables
{
    /** List of featurables (key is the featurable ID). */
    private final Map<Integer, Featurable> featurables = new HashMap<>();
    /** List of typed items (key is the feature type). */
    private final Map<Class<?>, Set<Object>> items = new HashMap<>();

    /**
     * Create the handlables.
     */
    HandlablesImpl()
    {
        super();
    }

    /**
     * Add a featurable.
     * 
     * @param featurable The featurable to add.
     */
    public void add(Featurable featurable)
    {
        featurables.put(featurable.getFeature(Identifiable.class).getId(), featurable);

        for (final Class<?> type : featurable.getClass().getInterfaces())
        {
            addType(type, featurable);
        }

        for (final Class<? extends Feature> feature : featurable.getFeaturesType())
        {
            final Feature object = featurable.getFeature(feature);
            addType(feature, object);
            for (final Class<?> other : UtilReflection.getInterfaces(feature, Feature.class))
            {
                addType(other, object);
            }
        }
        addType(featurable.getClass(), featurable);
        addSuperClass(featurable, featurable.getClass());
    }

    /**
     * Remove the featurable and all its references.
     * 
     * @param featurable The featurable reference.
     * @param id The featurable ID.
     */
    public void remove(Featurable featurable, Integer id)
    {
        for (final Class<?> type : featurable.getClass().getInterfaces())
        {
            remove(type, featurable);
        }

        for (final Class<? extends Feature> feature : featurable.getFeaturesType())
        {
            final Feature object = featurable.getFeature(feature);
            remove(feature, object);
            for (final Class<?> other : UtilReflection.getInterfaces(feature, Feature.class))
            {
                remove(other, object);
            }

        }
        remove(featurable.getClass(), featurable);
        removeSuperClass(featurable, featurable.getClass());

        featurables.remove(id);
    }

    /**
     * Get all featurables ID.
     * 
     * @return The IDs list.
     */
    public Collection<Integer> getIds()
    {
        return featurables.keySet();
    }

    /**
     * Add a type from its interface.
     * 
     * @param type The type interface.
     * @param object The type value.
     */
    private void addType(Class<?> type, Object object)
    {
        Set<Object> set = items.get(type);
        if (set == null)
        {
            set = new HashSet<>();
            items.put(type, set);
        }
        set.add(object);
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
     * Handlables
     */

    @Override
    public Featurable get(Integer id)
    {
        return featurables.get(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I> Iterable<I> get(Class<I> type)
    {
        final Set<?> typeFeaturables = items.get(type);
        if (typeFeaturables != null)
        {
            return (Iterable<I>) typeFeaturables;
        }
        return Collections.emptySet();
    }

    @Override
    public Iterable<Featurable> values()
    {
        return featurables.values();
    }
}
