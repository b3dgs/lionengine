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
package com.b3dgs.lionengine.game.feature;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Featurable model default implementation.
 */
public class FeaturableModel implements Featurable
{
    /** Inject service error. */
    private static final String ERROR_INJECT_SERVICE = "Error during service injection !";

    /** Features to prepare. */
    private final Collection<Feature> featuresToPrepare = new ArrayList<Feature>();
    /** Features provider. */
    private final Features features = new Features();

    /**
     * Create model.
     */
    public FeaturableModel()
    {
        super();
    }

    @Override
    public void prepareFeatures(Handlable owner, Services services)
    {
        fillServices(this, services);
        for (final Feature feature : featuresToPrepare)
        {
            fillServices(feature, services);
            feature.prepare(owner, services);
        }
        featuresToPrepare.clear();
    }

    /**
     * Fill services fields with their right instance.
     * 
     * @param object The object to update.
     * @param services The services reference.
     */
    private void fillServices(Object object, Services services)
    {
        for (final Field field : getServiceFields(object))
        {
            final boolean accessible = field.isAccessible();
            if (!accessible)
            {
                UtilReflection.setAccessible(field, true);
            }

            final Class<?> type = field.getType();
            try
            {
                setField(field, object, services, type);
            }
            catch (final IllegalAccessException exception)
            {
                throw new LionEngineException(exception,
                                              ERROR_INJECT_SERVICE,
                                              type.getSimpleName(),
                                              Constant.SLASH,
                                              field.getName());
            }

            if (!accessible)
            {
                UtilReflection.setAccessible(field, false);
            }
        }
    }

    /**
     * Get all with that require an injected service.
     * 
     * @param object The object which requires injected services.
     * @return The field requiring injected services.
     */
    private Collection<Field> getServiceFields(Object object)
    {
        final Collection<Field> toInject = new HashSet<Field>();
        for (final Field field : object.getClass().getDeclaredFields())
        {
            if (field.isAnnotationPresent(Service.class))
            {
                toInject.add(field);
            }
        }
        return toInject;
    }

    /**
     * Set the field service if not already defined.
     * 
     * @param field The field to set.
     * @param object The object to update.
     * @param services The services reference.
     * @param type The service type.
     * @throws IllegalAccessException If error on setting service.
     */
    private void setField(Field field, Object object, Services services, Class<?> type) throws IllegalAccessException
    {
        if (field.get(object) == null)
        {
            if (Feature.class.isAssignableFrom(type) && hasFeature(type.asSubclass(Feature.class)))
            {
                field.set(object, getFeature(type.asSubclass(Feature.class)));
            }
            else
            {
                field.set(object, services.get(type));
            }
        }
    }

    /*
     * Featurable
     */

    @Override
    public void addFeature(Feature feature)
    {
        featuresToPrepare.add(feature);
        features.add(feature);
    }

    @Override
    public <C extends Feature> C getFeature(Class<C> feature)
    {
        final C found;
        if (feature.isAssignableFrom(getClass()))
        {
            found = feature.cast(this);
        }
        else
        {
            found = features.get(feature);
        }
        return found;
    }

    @Override
    public Iterable<Feature> getFeatures()
    {
        return features.getFeatures();
    }

    @Override
    public Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return features.getFeaturesType();
    }

    @Override
    public boolean hasFeature(Class<? extends Feature> feature)
    {
        final boolean hasTrait;
        if (feature.isAssignableFrom(getClass()))
        {
            hasTrait = true;
        }
        else
        {
            hasTrait = features.contains(feature);
        }
        return hasTrait;
    }
}
