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
package com.b3dgs.lionengine.core;

import java.io.File;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Implementation provider for the {@link FactoryMedia}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Medias
{
    /** Resources directory. */
    private static volatile String resourcesDir = Constant.EMPTY_STRING;
    /** Class loader. */
    private static volatile Class<?> loader;
    /** Factory media implementation. */
    private static volatile FactoryMedia factoryMedia = new FactoryMediaDefault();

    /**
     * Get a media from an existing file descriptor.
     * 
     * @param file The file descriptor.
     * @return The media instance.
     */
    public static synchronized Media get(File file)
    {
        final String filename = file.getPath();
        final String localFile = filename.substring(resourcesDir.length() + filename.lastIndexOf(resourcesDir));
        return create(localFile);
    }

    /**
     * Create a media.
     * 
     * @param path The media path.
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String path) throws LionEngineException
    {
        return factoryMedia.create(path);
    }

    /**
     * Create a media from an abstract path.
     * 
     * @param path The media path.
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String... path) throws LionEngineException
    {
        return factoryMedia.create(path);
    }

    /**
     * Set the media factory used.
     * 
     * @param factoryMedia The media factory used.
     */
    public static synchronized void setFactoryMedia(FactoryMedia factoryMedia)
    {
        Medias.factoryMedia = factoryMedia;
    }

    /**
     * Define resources directory. Root for all medias.
     * 
     * @param directory The main resources directory (may be <code>null</code>).
     */
    public static synchronized void setResourcesDirectory(String directory)
    {
        if (directory == null)
        {
            resourcesDir = Constant.EMPTY_STRING;
        }
        else
        {
            resourcesDir = directory + getSeparator();
        }
    }

    /**
     * Activate or no the resources loading from *.jar. A <code>null</code> value will disable load from jar.
     * 
     * @param clazz The class loader reference (resources entry point).
     */
    public static synchronized void setLoadFromJar(Class<?> clazz)
    {
        loader = clazz;
        if (loader != null)
        {
            setSeparator(Constant.SLASH);
        }
        else
        {
            setSeparator(File.separator);
        }
    }

    /**
     * Set the path separator.
     * 
     * @param separator The path separator.
     */
    public static synchronized void setSeparator(String separator)
    {
        factoryMedia.setSeparator(separator);
    }

    /**
     * Get current resource directory.
     * 
     * @return The resource directory.
     */
    public static synchronized String getResourcesDir()
    {
        return resourcesDir;
    }

    /**
     * Get the resources class loader.
     * 
     * @return The class loader to gather resources (can be <code>null</code>).
     */
    public static synchronized Class<?> getClassResources()
    {
        return loader;
    }

    /**
     * Get the path separator.
     * 
     * @return The path separator.
     */
    public static synchronized String getSeparator()
    {
        return factoryMedia.getSeparator();
    }

    /**
     * Private constructor.
     */
    private Medias()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
