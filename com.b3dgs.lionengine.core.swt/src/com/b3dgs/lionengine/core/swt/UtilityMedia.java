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
package com.b3dgs.lionengine.core.swt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * A media represents a path to a resources located outside. This abstraction allows to load a resource from any kind of
 * location, such as <code>HDD</code>, <code>JAR</code>...
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Engine.start(&quot;First Code&quot;, Version.create(1, 0, 0), &quot;resources&quot;);
 * final Media media = Media.get(&quot;img&quot;, &quot;image.png&quot;);
 * print(media.getPath()); // print: resources/img/image.png
 * </pre>
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilityMedia
{
    /** Error open media. */
    private static final String ERROR_OPEN_MEDIA = "Cannot open the media";
    /** From jar flag. */
    private static volatile boolean fromJar;
    /** Resources directory. */
    private static volatile String resourcesDir = Constant.EMPTY_STRING;
    /** Class loader. */
    private static volatile Class<?> loader;

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
        return Medias.create(localFile);
    }

    /**
     * Get current resource directory.
     * 
     * @return The resource directory.
     */
    public static synchronized String getRessourcesDir()
    {
        return resourcesDir;
    }

    /**
     * Activate or no the resources loading from *.jar. A <code>null</code> value will disable load from jar.
     * 
     * @param clazz The class loader reference (resources entry point).
     */
    public static synchronized void setLoadFromJar(Class<?> clazz)
    {
        fromJar = clazz != null;
        if (fromJar)
        {
            loader = clazz;
            Medias.setSeparator(Constant.SLASH);
        }
        else
        {
            loader = null;
            Medias.setSeparator(File.separator);
        }
    }

    /**
     * Define resources directory. Root for all game medias.
     * 
     * @param dir The main root directory.
     */
    public static synchronized void setResourcesDirectory(String dir)
    {
        if (dir == null)
        {
            resourcesDir = Constant.EMPTY_STRING;
        }
        else
        {
            resourcesDir = dir + Medias.getSeparator();
        }
    }

    /**
     * Get input stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @return The opened input stream.
     * @throws LionEngineException If the media is not found.
     */
    static synchronized InputStream getInputStream(Media media) throws LionEngineException
    {
        final String path = UtilFile.getPath(resourcesDir, media.getPath());
        try
        {
            if (fromJar)
            {
                final InputStream inputStream = loader.getResourceAsStream(path);
                if (inputStream == null)
                {
                    throw new LionEngineException(media, "Resource in JAR not found");
                }
                return inputStream;
            }
            return new FileInputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, media, ERROR_OPEN_MEDIA);
        }
    }

    /**
     * Get output stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @return The opened input stream.
     * @throws LionEngineException If the file can not be openened.
     */
    static synchronized OutputStream getOutputStream(Media media) throws LionEngineException
    {
        final String path = UtilFile.getPath(resourcesDir, media.getPath());
        try
        {
            return new FileOutputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, media, ERROR_OPEN_MEDIA);
        }
    }

    /**
     * Check if media exists.
     * 
     * @param media The media to check.
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    static synchronized boolean exists(Media media)
    {
        final boolean exists;
        if (fromJar)
        {
            try (final InputStream stream = getInputStream(media))
            {
                exists = true;
            }
            catch (final IOException | LionEngineException exception)
            {
                return false;
            }
        }
        else
        {
            exists = media.getFile().exists();
        }
        return exists;
    }

    /**
     * Private constructor.
     */
    private UtilityMedia()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
