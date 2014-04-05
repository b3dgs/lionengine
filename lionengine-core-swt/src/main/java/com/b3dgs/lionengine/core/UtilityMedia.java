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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    /** Engine working directory. */
    public static final String WORKING_DIR = UtilityMedia.assignWorkingDirectory();
    /** Error message internal. */
    private static final String ERROR_MEDIA = "The media does not exist !";
    /** Resources directory. */
    private static String resourcesDir = "";
    /** From jar flag. */
    private static boolean fromJar = false;
    /** Class loader. */
    private static Class<?> loader = null;

    /**
     * Check if the media exists. Throws a {@link LionEngineException} if not.
     * 
     * @param media The media to test.
     */
    public static void exist(Media media)
    {
        Check.notNull(media, UtilityMedia.ERROR_MEDIA);
        if (!UtilityMedia.checkExist(media))
        {
            throw new LionEngineException(media, UtilityMedia.ERROR_MEDIA);
        }
    }

    /**
     * Check if the media exists.
     * 
     * @param media The media to test.
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    public static boolean checkExist(Media media)
    {
        Check.notNull(media, UtilityMedia.ERROR_MEDIA);
        return UtilityFile.exists(UtilityMedia.WORKING_DIR + Media.getSeparator() + media.getPath());
    }

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
            string.append(name).append(Media.getSeparator());
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
        return new MediaImpl(Media.getPath(path));
    }

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
        return UtilityMedia.get(localFile);
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
        final String path = Media.getPath(UtilityMedia.resourcesDir, media.getPath());
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
     */
    public static OutputStream getOutputStream(Media media, String from, boolean logger)
    {
        Check.notNull(media, UtilityMedia.ERROR_MEDIA);
        final String path = Media.getPath(UtilityMedia.resourcesDir, media.getPath());
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
     * Copy stream into a temporary file and return this file (localized on current system).
     * 
     * @param outfile The file which will store stream.
     * @param stream The input stream.
     * @return The file containing a copy of the input stream.
     */
    public static File getFile(String outfile, InputStream stream)
    {
        final File file = new File(outfile);
        try (OutputStream out = new FileOutputStream(file);)
        {
            try
            {
                final byte[] bytes = new byte[1024];
                int read;

                while ((read = stream.read(bytes)) != -1)
                {
                    out.write(bytes, 0, read);
                }
                out.flush();
                Verbose.info("File temporary created: ", outfile);
            }
            finally
            {
                stream.close();
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(Media.class, "getFile", exception, "Temporary file error on: \"", outfile, "\"");
        }
        return file;
    }

    /**
     * Get a temporary file, including cache check. The file will be cached only in a JAR context.
     * 
     * @param media The original media path (may be inside a JAR).
     * @param cache The cache state (<code>true</code> will check if file is not already cached).
     * @param mustExist <code>true</code> if the file must already exist (will throw a {@link LionEngineException} if
     *            not existing), <code>false</code> else.
     * @return The copy of original file (may be cached file reference).
     */
    public static File getTempFile(Media media, boolean cache, boolean mustExist)
    {
        Check.notNull(media, UtilityMedia.ERROR_MEDIA);
        final String filename = media.getPath();
        if (!UtilityMedia.fromJar && new File(filename).exists())
        {
            return new File(filename);
        }
        if (cache)
        {
            final String cacheFile = Media.getPath(UtilityFile.getTempDir(), UtilityFile.getFilenameFromPath(filename));
            if (new File(cacheFile).exists())
            {
                return new File(cacheFile);
            }
        }
        Check.argument(!mustExist, "The temp file must exist: \"", filename, "\"");
        final String str = filename.replace(Media.getSeparator().charAt(0), ';');
        final String[] slp = str.split(";");
        final String n = slp[slp.length - 1];

        return UtilityMedia.getFile(Media.getPath(UtilityFile.getTempDir(), n),
                UtilityMedia.getStream(media, "getTempFile"));
    }

    /**
     * Get current working directory (execution directory).
     * 
     * @return The working directory.
     */
    public static String getWorkingDir()
    {
        return UtilityMedia.WORKING_DIR;
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
     * @param enabled The activation state (<code>true</code> to enable, <code>false</code> to disable).
     */
    static void setLoadFromJar(Class<?> clazz, boolean enabled)
    {
        UtilityMedia.fromJar = enabled;
        if (enabled)
        {
            UtilityMedia.loader = clazz;
            Media.setSeparator("/");
        }
        else
        {
            UtilityMedia.loader = null;
            Media.setSeparator(File.separator);
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
            UtilityMedia.resourcesDir = dir + Media.getSeparator();
        }
    }

    /**
     * Get the working directory.
     * 
     * @return The working directory.
     */
    private static String assignWorkingDirectory()
    {
        try
        {
            return System.getProperty("user.dir");
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
