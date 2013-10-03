/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Locale;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.utility.UtilityFile;

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
 * // Will get from execution directory: resources/img/image.png
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Media
{
    /** System temp directory. */
    public static final String SYSTEM_TEMP_DIR = Media.assignSystemTempDirectory();
    /** Engine working directory. */
    public static final String WORKING_DIR = Media.assignWorkingDirectory();
    /** Error message internal. */
    private static final String ERROR_MEDIA = "The media does not exist !";
    /** System separator. */
    private static String separator = File.separator;
    /** Resources directory. */
    private static String resourcesDir = "";
    /** From jar flag. */
    private static boolean fromJar = false;
    /** Class loader. */
    private static Class<?> loader = null;
    /** Engine temporary directory. */
    private static String tmpDir;

    /**
     * Check if the media exists. Throws a {@link LionEngineException} if not.
     * 
     * @param media The media to test.
     */
    public static void exist(Media media)
    {
        Check.notNull(media, Media.ERROR_MEDIA);
        if (!UtilityFile.exists(Media.WORKING_DIR + Media.separator + media.getPath()))
        {
            throw new LionEngineException(media, Media.ERROR_MEDIA);
        }
    }

    /**
     * Create a full path and each directory.
     * 
     * @param source The source directory.
     * @param path The full path.
     */
    public static void createPath(String source, String... path)
    {
        final StringBuilder string = new StringBuilder(source);
        for (final String name : path)
        {
            string.append(name).append(Media.separator);
            new File(string.toString()).mkdir();
        }
    }

    /**
     * Get a media from its path (start by default in the resources directory). Example: Media.get("sprites",
     * "hero.png") will return resources/sprites/hero.png. This function is OS independent !
     * 
     * @param path The list of folders (if has) and file.
     * @return The full media path.
     */
    public static Media get(String... path)
    {
        Check.notNull(path);
        return new Media(Media.getPathSeparator(Media.separator, Media.resourcesDir,
                Media.getPathSeparator(Media.separator, path)));
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
        final String localFile = filename.substring(Media.getRessourcesDir().length()
                + filename.lastIndexOf(Media.getRessourcesDir()));
        return Media.get(localFile);
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
        return Media.getPathSeparator(File.separator, path);
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
            else
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
        return Media.getStream(media, from, true);
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
        final String path = media.getPath();
        try
        {
            if (Media.fromJar)
            {
                if (logger)
                {
                    Verbose.info("(getStream from " + from, ") - getting stream from jar: \"", path, "\"");
                }
                return Media.loader.getResourceAsStream(path);
            }
            if (logger)
            {
                Verbose.info("(getStream from " + from, ") - getting stream: \"", path, "\"");
            }
            return new FileInputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException("Error on getting stream of: \"", path, "\"");
        }
    }

    /**
     * Copy stream into a temporary file and return this file (localised on current system).
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
            Verbose.critical(Media.class, "getFile", "Temporary file error on: \"", outfile, "\"");
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
        final String filename = media.getPath();
        if (!Media.fromJar && new File(filename).exists())
        {
            // Engine.verbose("getTempFile", "read from local: ", filename);
            return new File(filename);
        }
        if (cache)
        {
            final String cacheFile = Media.getPath(Media.tmpDir, Media.getFilenameFromPath(filename));
            if (new File(cacheFile).exists())
            {
                // Engine.verbose("getTempFile", "read from cache: ", filename);
                return new File(cacheFile);
            }
        }
        Check.argument(!mustExist, "The temp file must exist: \"", filename, "\"");
        final String str = filename.replace(Media.separator.charAt(0), ';');
        final String[] slp = str.split(";");
        final String n = slp[slp.length - 1];

        return Media.getFile(Media.getPath(Media.tmpDir, n), Media.getStream(media, "getTempFile"));
    }

    /**
     * Get the filename from a path (last part of a path, after the last separator).
     * 
     * @param path The path used to extract filename.
     * @return The filename extracted from path.
     */
    public static String getFilenameFromPath(String path)
    {
        int i = path.lastIndexOf(Media.separator);
        if (i == -1)
        {
            i = path.lastIndexOf(Media.separator);
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
        return Media.tmpDir;
    }

    /**
     * Get current working directory (execution directory).
     * 
     * @return The working directory.
     */
    public static String getWorkingDir()
    {
        return Media.WORKING_DIR;
    }

    /**
     * Get current resource directory.
     * 
     * @return The resource directory.
     */
    public static String getRessourcesDir()
    {
        return Media.resourcesDir;
    }

    /**
     * Get current separator character (equals / in case of applet, else OS dependent).
     * 
     * @return The path separator representation.
     */
    public static String getSeparator()
    {
        return Media.separator;
    }

    /**
     * Activate or no the resources loading from *.jar.
     * 
     * @param clazz The class loader reference (resources entry point).
     * @param enabled The activation state (<code>true</code> to enable, <code>false</code> to disable).
     */
    static void setLoadFromJar(Class<?> clazz, boolean enabled)
    {
        Media.fromJar = enabled;
        if (enabled)
        {
            Media.loader = clazz;
            Media.separator = "/";
        }
        else
        {
            Media.loader = null;
            Media.separator = File.separator;
        }
    }

    /**
     * Define resources directory. Root for all game medias.
     * 
     * @param dir The main root directory.
     */
    static void setResourcesDirectory(String dir)
    {
        Media.resourcesDir = dir + Media.separator;
    }

    /**
     * Set the temporary directory name from the program name.
     * 
     * @param programName The program name.
     */
    static void setTempDirectory(String programName)
    {
        final String dir = programName.replace(' ', '_').replaceAll("[\\W]", "").toLowerCase(Locale.getDefault());
        Media.tmpDir = Media.getPath(Media.SYSTEM_TEMP_DIR, dir);
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

    /** Media path. */
    private final String path;

    /**
     * Constructor.
     * 
     * @param path The media path (must not be null).
     */
    public Media(String path)
    {
        Check.notNull(path, Media.ERROR_MEDIA);
        this.path = path;
    }

    /**
     * Get the media path.
     * 
     * @return The media path.
     */
    public String getPath()
    {
        return path;
    }
}
