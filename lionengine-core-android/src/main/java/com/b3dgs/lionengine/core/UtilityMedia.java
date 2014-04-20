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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.net.Uri;

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
public final class UtilityMedia
{
    /** System temp directory. */
    public static final String SYSTEM_TEMP_DIR = UtilityMedia.assignSystemTempDirectory();
    /** Error message internal. */
    private static final String ERROR_MEDIA = "The media does not exist !";
    /** System separator. */
    private static String separator = File.separator;
    /** Engine temporary directory. */
    private static String tmpDir;
    /** Asset manager. */
    private static AssetManager assetManager;
    /** Content resolver. */
    private static ContentResolver contentResolver;

    /**
     * Create a full path and each directory.
     * 
     * @param source The source directory.
     * @param path The full path.
     * @return <code>true</code> if created, <code>false</code> else.
     */
    public static boolean createPath(String source, String... path)
    {
        final StringBuilder string = new StringBuilder(source);
        for (final String name : path)
        {
            string.append(name).append(UtilityMedia.separator);
        }
        return new File(string.toString()).mkdir();
    }

    /**
     * Get a media from its path (start by default in the resources directory).
     * <p>
     * Example: <code>Media.get("sprites", "hero.png")</code> will return <code>resources/sprites/hero.png</code>. This
     * function is OS independent !
     * </p>
     * 
     * @param path The list of folders (if has) and file.
     * @return The full media path.
     */
    public static Media get(String... path)
    {
        Check.notNull(path);
        return new MediaAndroid(UtilityMedia.getPathSeparator(UtilityMedia.separator, path));
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
        return UtilityMedia.getPathSeparator(UtilityMedia.separator, path);
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
     * Get stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @param from The from function.
     * @return The opened input stream.
     */
    public static InputStream getStream(Media media, String from)
    {
        return UtilityMedia.getStream(media, from, true);
    }

    /**
     * Get stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @param from The from function.
     * @param logger The logger flag.
     * @return The opened input stream.
     */
    public static InputStream getStream(Media media, String from, boolean logger)
    {
        Check.notNull(media, UtilityMedia.ERROR_MEDIA);
        final String path = media.getPath();
        if (logger)
        {
            Verbose.info("getStream from " + from, ": \"", path, "\"");
        }
        try
        {
            return UtilityMedia.assetManager.open(media.getPath());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "Error on getting stream of: \"", path, "\"");
        }
    }

    /**
     * Get output stream of specified path.
     * 
     * @param media The input media path, pointing to a file.
     * @param from The from function.
     * @param logger The logger flag.
     * @return The opened input stream.
     */
    public static OutputStream getOutputStream(Media media, String from, boolean logger)
    {
        Check.notNull(media, UtilityMedia.ERROR_MEDIA);
        final String path = media.getPath();
        if (logger)
        {
            Verbose.info("getOutputStream from " + from, ": \"", path, "\"");
        }
        try
        {
            return UtilityMedia.contentResolver.openOutputStream(Uri.parse(Uri.encode(path)));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "Error on getting stream of: \"", path, "\"");
        }
    }

    /**
     * Get the filename from a path (last part of a path, after the last separator).
     * 
     * @param path The path used to extract filename.
     * @return The filename extracted from path.
     */
    public static String getFilenameFromPath(String path)
    {
        int i = path.lastIndexOf(UtilityMedia.separator);
        if (i == -1)
        {
            i = path.lastIndexOf(UtilityMedia.separator);
        }
        return path.substring(i + 1, path.length());
    }

    /**
     * Get temporary directory (where are stored files from jar).
     * 
     * @return The temporary directory (<code>/tmp, .../AppData/Local/Temp, ...</code>)
     */
    public static String getTempDir()
    {
        return UtilityMedia.tmpDir;
    }

    /**
     * Get current separator character (equals / in case of applet, else OS dependent).
     * 
     * @return The path separator representation.
     */
    public static String getSeparator()
    {
        return UtilityMedia.separator;
    }

    /**
     * Set the asset manager.
     * 
     * @param assetManager The asset manager.
     */
    static void setAssertManager(AssetManager assetManager)
    {
        UtilityMedia.assetManager = assetManager;
    }

    /**
     * Set the content resolver.
     * 
     * @param contentResolver The content resolver.
     */
    static void setContentResolver(ContentResolver contentResolver)
    {
        UtilityMedia.contentResolver = contentResolver;
    }

    /**
     * Get the system temp directory.
     * 
     * @return The system temp directory.
     */
    private static String assignSystemTempDirectory()
    {
        try
        {
            return System.getProperty("java.io.tmpdir");
        }
        catch (final SecurityException exception)
        {
            return "";
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
