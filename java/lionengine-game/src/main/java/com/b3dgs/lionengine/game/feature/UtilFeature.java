/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Tools related to featurable.
 */
final class UtilFeature
{
    /** Constructor not found error. */
    static final String ERROR_CONSTRUCTOR = "Constructor not found: ";

    /**
     * Create and add feature to featurable.
     * 
     * @param <T> The feature type.
     * @param clazz The feature class.
     * @param featurable The featurable owner.
     * @param services The services reference.
     * @param setup The setup reference.
     * @param config The feature configuration node.
     * @return The created feature.
     */
    static <T extends Feature> T createAndAdd(Class<T> clazz,
                                              Featurable featurable,
                                              Services services,
                                              Setup setup,
                                              AttributesReader config)
    {

        try
        {
            return checkConstructors(clazz, featurable, services, setup, config);
        }
        catch (final ReflectiveOperationException | IllegalArgumentException | LionEngineException exception)
        {
            throw new LionEngineException(exception, setup.getMedia() + " for " + clazz);
        }
    }

    private static <T extends Feature> T checkConstructors(Class<T> clazz,
                                                           Featurable featurable,
                                                           Services services,
                                                           Setup setup,
                                                           AttributesReader config)
            throws ReflectiveOperationException
    {
        final Constructor<?>[] constructors = clazz.getConstructors();
        for (int i = 0; i < constructors.length; i++)
        {
            final Parameter[] parameters = constructors[i].getParameters();

            // At least services and setup arguments
            if (parameters.length > 1
                && Services.class.equals(parameters[0].getType())
                && Setup.class.isAssignableFrom(parameters[1].getType()))
            {
                final List<Object> args = new ArrayList<>();
                args.add(services);
                args.add(setup);
                if (config != null
                    && parameters.length > 2
                    && AttributesReader.class.isAssignableFrom(parameters[2].getType()))
                {
                    args.add(config);
                }

                addConstructorArgs(featurable, parameters, args);

                @SuppressWarnings("unchecked")
                final T feature = (T) constructors[i].newInstance(args.toArray());
                featurable.addFeature(feature);

                return feature;
            }
        }
        throw new LionEngineException(ERROR_CONSTRUCTOR + setup.getMedia());
    }

    private static void addConstructorArgs(Featurable featurable, Parameter[] parameters, List<Object> args)
    {
        // Start after services setup and config arguments
        final int standardArgsCount = args.size();
        for (int j = standardArgsCount; j < parameters.length; j++)
        {
            final Class<?> type = parameters[j].getType();
            if (FeatureProvider.class.isAssignableFrom(type))
            {
                args.add(featurable.getFeature(type.asSubclass(FeatureProvider.class)));
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilFeature()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
