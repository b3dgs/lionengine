/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

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
 * System.out.println(media.getPath()); // print: resources/img/image.png
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Media
{
    /** Error path. */
    private static final String ERROR_PATH = "Path must not be null !";
    /** System separator. */
    private static String separator = File.separator;
    /** Media implementation. */
    private static Class<? extends Media> mediaImpl;

    /**
     * Create a media.
     * 
     * @param path The media path.
     * @return The media instance.
     */
    public static Media create(String path)
    {
        Check.notNull(path, Media.ERROR_PATH);
        try
        {
            return Media.mediaImpl.getConstructor(String.class).newInstance(path);
        }
        catch (InstantiationException
               | IllegalAccessException
               | IllegalArgumentException
               | InvocationTargetException
               | NoSuchMethodException
               | SecurityException exception)
        {
            throw new LionEngineException(exception, "Unable to load the media class !");
        }
    }

    /**
     * Set the file separator.
     * 
     * @param separator The file separator.
     */
    public static void setSeparator(String separator)
    {
        Media.separator = separator;
    }

    /**
     * Get the media separator.
     * 
     * @return The media separator.
     */
    public static String getSeparator()
    {
        return Media.separator;
    }

    /**
     * Construct a usable path using a list of string, automatically separated by the portable separator. The
     * constructed path will use local system file separator.
     * 
     * @param path The list of folders (if has) and file.
     * @return The full media path.
     */
    public static String getPath(String... path)
    {
        return Media.getPathSeparator(Media.separator, path);
    }

    /**
     * Construct a usable path using a list of string, automatically separated by the portable separator. The
     * constructed path will use local system file separator.
     * 
     * @param separator The separator to use.
     * @param path The list of folders (if has) and file.
     * @return The full media path.
     */
    public static String getPathSeparator(String separator, String... path)
    {
        Check.notNull(path, Media.ERROR_PATH);

        final StringBuilder fullPath = new StringBuilder(path.length);
        for (int i = 0; i < path.length; i++)
        {
            if (i == path.length - 1)
            {
                fullPath.append(path[i]);
            }
            else if (path[i] != null && path[i].length() > 0)
            {
                fullPath.append(path[i]);
                if (!fullPath.substring(fullPath.length() - 1, fullPath.length()).equals(separator))
                {
                    fullPath.append(separator);
                }
            }
        }
        return fullPath.toString();
    }

    /**
     * Set the media implementation.
     * 
     * @param mediaImpl The media implementation.
     */
    static void setMediaImpl(Class<? extends Media> mediaImpl)
    {
        Media.mediaImpl = mediaImpl;
    }

    /**
     * Get the media path.
     * 
     * @return The media path.
     */
    public abstract String getPath();

    /**
     * Get the file descriptor.
     * 
     * @return The file descriptor.
     */
    public abstract File getFile();

    /**
     * Get the media stream.
     * 
     * @return The media stream.
     */
    public abstract InputStream getStream();

    /**
     * Get the media output stream.
     * 
     * @return The media output stream.
     */
    public abstract OutputStream getOutputStream();
}
