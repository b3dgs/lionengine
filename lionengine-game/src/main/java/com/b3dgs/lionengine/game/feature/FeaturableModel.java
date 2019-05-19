/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.List;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Feature;

/**
 * Featurable model implementation.
 */
public class FeaturableModel implements Featurable
{
    /** Class not found error. */
    static final String ERROR_CLASS_PRESENCE = "Class not found: ";
    /** In. */
    private static final String IN = " in ";
    /** Inject service error. */
    private static final String ERROR_INJECT = "Error during service injection !";

    /**
     * Get all with that require an injected service.
     * 
     * @param object The object which requires injected services.
     * @return The field requiring injected services.
     */
    private static List<Field> getServiceFields(Object object)
    {
        final List<Field> toInject = new ArrayList<>();
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            final Field[] fields = clazz.getDeclaredFields();
            final int length = fields.length;
            for (int i = 0; i < length; i++)
            {
                final Field field = fields[i];
                if (field.isAnnotationPresent(FeatureGet.class))
                {
                    toInject.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return toInject;
    }

    /** Features provider. */
    private final Features features = new Features();
    /** Associated media (<code>null</code> if none). */
    private final Media media;

    /**
     * Create model.
     */
    public FeaturableModel()
    {
        super();

        media = null;
        addFeature(new Recycler());
        addFeature(new IdentifiableModel());
    }

    /**
     * Create model. All features are loaded from setup.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public FeaturableModel(Services services, Setup setup)
    {
        super();

        media = setup.getMedia();
        addFeature(new Recycler());
        addFeature(new IdentifiableModel());
    }

    /**
     * Fill services fields with their right instance.
     * 
     * @param object The object to update.
     * @throws LionEngineException If error on setting service.
     */
    private void fillServices(Object object)
    {
        final List<Field> fields = getServiceFields(object);
        final int length = fields.size();
        for (int i = 0; i < length; i++)
        {
            final Field field = fields.get(i);
            UtilReflection.setAccessible(field, true);

            final Class<?> type = field.getType();
            setField(field, object, type);
        }
    }

    /**
     * Set the field service only if currently <code>null</code>.
     * 
     * @param field The field to set.
     * @param object The object to update.
     * @param type The service type.
     * @throws LionEngineException If error on setting service.
     */
    private void setField(Field field, Object object, Class<?> type)
    {
        try
        {
            if (field.get(object) == null)
            {
                final Class<? extends Feature> clazz;
                // CHECKSTYLE IGNORE LINE: InnerAssignment
                if (Feature.class.isAssignableFrom(type) && hasFeature(clazz = type.asSubclass(Feature.class)))
                {
                    field.set(object, getFeature(clazz));
                }
                else if (media != null)
                {
                    throw new LionEngineException(media, ERROR_CLASS_PRESENCE + String.valueOf(type) + IN + object);
                }
                else
                {
                    throw new LionEngineException(ERROR_CLASS_PRESENCE + String.valueOf(type) + IN + object);
                }
            }
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception,
                                          ERROR_INJECT + type.getSimpleName() + Constant.SLASH + field.getName());
        }
    }

    /*
     * Featurable
     */

    /**
     * {@inheritDoc}
     * <p>
     * Does nothing by default.
     * </p>
     */
    @Override
    public void checkListener(Object listener)
    {
        // Nothing by default
    }

    @Override
    public final void addFeature(Feature feature)
    {
        fillServices(feature);
        if (feature instanceof Recyclable)
        {
            ((Recyclable) feature).recycle();
        }
        feature.prepare(this);
        features.add(feature);
    }

    @Override
    public final <T extends Feature> T addFeatureAndGet(T feature)
    {
        addFeature(feature);
        return feature;
    }

    @Override
    public final <C extends Feature> C getFeature(Class<C> feature)
    {
        return features.get(feature);
    }

    @Override
    public final Iterable<Feature> getFeatures()
    {
        return features.getFeatures();
    }

    @Override
    public final Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return features.getFeaturesType();
    }

    @Override
    public final boolean hasFeature(Class<? extends Feature> feature)
    {
        return features.contains(feature);
    }

    @Override
    public Media getMedia()
    {
        return media;
    }
}
