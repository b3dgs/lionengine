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
package com.b3dgs.lionengine.core;

import java.io.File;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Implementation provider for the {@link FactoryMedia}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Medias
{
    /** Unable to create media. */
    private static final String ERROR_CREATE = "Unable to create media from path: ";
    /** Factory media implementation. */
    private static volatile FactoryMedia factoryMedia = new FactoryMediaDefault();
    /** Path separator. */
    private static volatile String separator = File.separator;
    /** Resources directory. */
    private static volatile String resourcesDir = Constant.EMPTY_STRING;
    /** Class loader. */
    private static volatile Class<?> loader;

    /**
     * Create a media.
     * 
     * @param path The media path.
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String... path)
    {
        if (loader != null)
        {
            return factoryMedia.create(separator, loader, path);
        }
        else if (resourcesDir != null)
        {
            return factoryMedia.create(separator, resourcesDir, path);
        }
        throw new LionEngineException(ERROR_CREATE, UtilFolder.getPath(path));
    }

    /**
     * Get a media from an existing file descriptor. {@link #setResourcesDirectory(String)} must be activated.
     * 
     * @param file The file descriptor.
     * @return The media instance.
     */
    public static synchronized Media get(File file)
    {
        Check.notNull(resourcesDir);

        final String filename = file.getPath();
        final String localFile = filename.substring(resourcesDir.length() + filename.lastIndexOf(resourcesDir));

        return create(localFile);
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
     * Define resources directory. Root for all medias. Disable the load from JAR.
     * 
     * @param directory The main resources directory (may be <code>null</code>).
     */
    public static synchronized void setResourcesDirectory(String directory)
    {
        if (directory == null)
        {
            resourcesDir = null;
        }
        else
        {
            resourcesDir = directory + getSeparator();
        }
        loader = null;
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
     * Get the resources directory.
     * 
     * @return The resources directory.
     */
    public static synchronized String getResourcesDirectory()
    {
        return resourcesDir;
    }

    /**
     * Get the resources loader.
     * 
     * @return The resources loader.
     */
    public static synchronized Class<?> getResourcesLoader()
    {
        return loader;
    }

    /**
     * Set the path separator.
     * 
     * @param separator The path separator.
     */
    public static synchronized void setSeparator(String separator)
    {
        Medias.separator = separator;
    }

    /**
     * Get the path separator.
     * 
     * @return The path separator.
     */
    public static synchronized String getSeparator()
    {
        return separator;
    }

    /**
     * Private constructor.
     */
    private Medias()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
