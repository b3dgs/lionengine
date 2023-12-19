/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.utility;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Allows to retrieve easily extension point from their ID.
 */
public final class UtilExtension
{
    /** Class extension attribute. */
    private static final String ATT_CLASS = "class";
    /** Priority extension attribute. */
    private static final String ATT_PRIORITY = "priority";
    /** Error on loading extension. */
    private static final String ERROR_EXTENSION = "Error on loading extension point: ";
    /** Create class error. */
    private static final String ERROR_CLASS_CREATE = "Unable to create the following class: ";
    /** Extensions point cache. */
    private static final Map<Class<?>, Object> EXTENSIONS_CACHE = new HashMap<>();
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilExtension.class);

    /**
     * Clear existing cache.
     */
    public static void clearCache()
    {
        EXTENSIONS_CACHE.clear();
    }

    /**
     * Get the declared extensions from their ID.
     * 
     * @param <T> The extensions type.
     * @param type The class type.
     * @param id The extension id.
     * @param params The optional constructor parameters.
     * @return The properties instance from extension point.
     * @throws LionEngineException If error on loading extensions.
     */
    public static <T> Collection<T> get(Class<T> type, String id, Object... params)
    {
        final Collection<Extension<T>> extensions = new ArrayList<>();
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(id);
        for (final IConfigurationElement element : elements)
        {
            try
            {
                final Bundle bundle = Platform.getBundle(element.getDeclaringExtension().getContributor().getName());
                final Class<?> rawClazz = bundle.loadClass(element.getAttribute(ATT_CLASS));
                final int priority = getPriority(element);
                final Class<? extends T> clazz = rawClazz.asSubclass(type);
                final T instance = getCachedInstance(clazz, params);
                extensions.add(new Extension<>(instance, priority));
            }
            catch (final InvalidRegistryObjectException | ReflectiveOperationException exception)
            {
                throw new LionEngineException(exception, ERROR_EXTENSION + element.getValue());
            }
        }
        return extensions.stream().sorted().map(Extension::getExtension).collect(Collectors.toList());
    }

    /**
     * Get the extension priority if defined.
     * 
     * @param element The element reference.
     * @return The priority value.
     */
    private static int getPriority(IConfigurationElement element)
    {
        final String priority = element.getAttribute(ATT_PRIORITY);
        if (priority != null)
        {
            try
            {
                return Integer.parseInt(priority);
            }
            catch (final NumberFormatException exception)
            {
                LOGGER.error("getPriority error", exception);
            }
        }
        return 0;
    }

    /**
     * Get the cached instance, or a new one if not existing and cache it.
     * 
     * @param <T> The class type.
     * @param clazz The class reference.
     * @param params The optional constructor parameters.
     * @return The extension point instance.
     * @throws ReflectiveOperationException If error on instantiating.
     */
    @SuppressWarnings("unchecked")
    private static <T> T getCachedInstance(Class<? extends T> clazz, Object... params)
            throws ReflectiveOperationException
    {
        final T instance;
        if (!EXTENSIONS_CACHE.containsKey(clazz))
        {
            instance = createClass(clazz, params);
            EXTENSIONS_CACHE.put(clazz, instance);
        }
        else
        {
            instance = (T) EXTENSIONS_CACHE.get(clazz);
        }
        return instance;
    }

    /**
     * Create a class from its name and call its corresponding constructor.
     * 
     * @param <T> The class type.
     * @param clazz The class reference to create.
     * @param params The constructor parameters.
     * @return The class instance.
     * @throws ReflectiveOperationException If error when creating the class.
     */
    private static <T> T createClass(Class<T> clazz, Object... params) throws ReflectiveOperationException
    {
        for (final Constructor<?> constructor : clazz.getConstructors())
        {
            final Class<?>[] constructorParams = constructor.getParameterTypes();
            final int required = params.length;
            int found = 0;
            for (final Class<?> constructorParam : constructorParams)
            {
                if (found >= params.length || !constructorParam.isAssignableFrom(params[found].getClass()))
                {
                    break;
                }
                found++;
            }
            if (found == required)
            {
                return clazz.cast(constructor.newInstance(params));
            }
        }
        throw new ClassNotFoundException(ERROR_CLASS_CREATE + clazz.getName());
    }

    /**
     * Private.
     */
    private UtilExtension()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
