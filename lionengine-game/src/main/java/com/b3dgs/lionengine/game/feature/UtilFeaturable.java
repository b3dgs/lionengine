/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Tools related to featurable.
 */
public final class UtilFeaturable
{
    /** Class attribute name. */
    public static final String ATT_CLASS = Constant.XML_PREFIX + "class";
    /** Class cache. */
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();
    /** Constructor not found error. */
    static final String ERROR_CONSTRUCTOR = "Constructor not found: ";
    /** Class not found error. */
    static final String ERROR_CLASS = "Class not found: ";
    /** Default class name. */
    private static final String DEFAULT_CLASS_NAME = "com.b3dgs.lionengine.game.feature.FeaturableModel";

    /**
     * Clear classes cache.
     */
    public static void clearCache()
    {
        CLASS_CACHE.clear();
    }

    /**
     * Get class attribute.
     * 
     * @param root The root node.
     * @return The class name.
     */
    public static String getClass(AttributesReader root)
    {
        if (root.hasNode(ATT_CLASS))
        {
            return root.getChild(ATT_CLASS).getText();
        }
        return DEFAULT_CLASS_NAME;
    }

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
     * Get the class reference from its name using cache.
     * 
     * @param <T> The class type.
     * @param loader The class loader reference.
     * @param className The class name.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    @SuppressWarnings("unchecked")
    static <T> Class<T> getClass(ClassLoader loader, String className)
    {
        if (CLASS_CACHE.containsKey(className))
        {
            return (Class<T>) CLASS_CACHE.get(className);
        }
        try
        {
            final Class<?> clazz = loader.loadClass(className);
            CLASS_CACHE.put(className, clazz);
            return (Class<T>) clazz;
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS + className);
        }
    }

    /**
     * Private constructor.
     */
    private UtilFeaturable()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
