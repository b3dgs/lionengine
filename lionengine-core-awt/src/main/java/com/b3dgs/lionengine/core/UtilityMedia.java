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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilityFile;

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
public final class UtilityMedia
{
    /** Engine working directory. */
    public static final String WORKING_DIR = EngineCore.getSystemProperty("user.dir");
    /** Resources directory. */
    private static String resourcesDir = "";
    /** From jar flag. */
    private static boolean fromJar = false;
    /** Class loader. */
    private static Class<?> loader = null;

    /**
     * Get a media from an existing file descriptor.
     * 
     * @param file The file descriptor.
     * @return The media instance.
     */
    public static Media get(File file)
    {
        final String filename = file.getPath();
        final String localFile = filename.substring(UtilityMedia.getRessourcesDir().length()
                + filename.lastIndexOf(UtilityMedia.getRessourcesDir()));
        return Core.MEDIA.create(localFile);
    }

    /**
     * Get current resource directory.
     * 
     * @return The resource directory.
     */
    public static String getRessourcesDir()
    {
        return UtilityMedia.resourcesDir;
    }

    /**
     * Activate or no the resources loading from *.jar.
     * 
     * @param clazz The class loader reference (resources entry point).
     */
    static void setLoadFromJar(Class<?> clazz)
    {
        UtilityMedia.fromJar = clazz != null;
        if (UtilityMedia.fromJar)
        {
            UtilityMedia.loader = clazz;
            Core.MEDIA.setSeparator("/");
        }
        else
        {
            UtilityMedia.loader = null;
            Core.MEDIA.setSeparator(File.separator);
        }
    }

    /**
     * Define resources directory. Root for all game medias.
     * 
     * @param dir The main root directory.
     */
    static void setResourcesDirectory(String dir)
    {
        if (dir == null)
        {
            UtilityMedia.resourcesDir = "";
        }
        else
        {
            UtilityMedia.resourcesDir = dir + Core.MEDIA.getSeparator();
        }
    }

    /**
     * Get input stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @param from The from function.
     * @param logger The logger flag.
     * @return The opened input stream.
     * @throws LionEngineException If the class is not found.
     */
    static InputStream getInputStream(Media media, String from, boolean logger)
    {
        final String path = UtilityFile.getPath(UtilityMedia.resourcesDir, media.getPath());
        try
        {
            if (UtilityMedia.fromJar)
            {
                if (logger)
                {
                    Verbose.info("getStream from " + from, ": \"", path, "\"");
                }
                return UtilityMedia.loader.getResourceAsStream(path);
            }
            if (logger)
            {
                Verbose.info("getStream from " + from, ": \"", path, "\"");
            }
            return new FileInputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException("File not found: \"", path, "\"");
        }
    }

    /**
     * Get output stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @param from The from function.
     * @param logger The logger flag.
     * @return The opened input stream.
     * @throws LionEngineException If the file can not be openened.
     */
    static OutputStream getOutputStream(Media media, String from, boolean logger)
    {
        final String path = UtilityFile.getPath(UtilityMedia.resourcesDir, media.getPath());
        try
        {
            if (logger)
            {
                Verbose.info("getOutputStream from " + from, ": \"", path, "\"");
            }
            return new FileOutputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException("Cannot open the file: \"", path, "\"");
        }
    }

    /**
     * Private constructor.
     */
    private UtilityMedia()
    {
        throw new RuntimeException();
    }
}
