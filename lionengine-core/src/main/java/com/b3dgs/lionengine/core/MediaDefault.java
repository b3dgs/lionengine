/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Media implementation.
 */
final class MediaDefault implements Media
{
    /** Temp folder property. */
    private static final String TEMP_DIR = "java.io.tmpdir";
    /** No parent. */
    private static final String NO_PARENT = Constant.EMPTY_STRING;
    /** Unable to create temp directory. */
    private static final String ERROR_CREATE_TEMP_DIR = "Unable to create temp dir ";
    /** Error open media. */
    private static final String ERROR_OPEN_MEDIA = "Cannot open the media";
    /** Error open media in JAR. */
    private static final String ERROR_OPEN_MEDIA_JAR = "Resource in JAR not found";
    /** Invalid path directory. */
    private static final String ERROR_PATH_DIR = "Invalid directory: ";
    /** Temp folder. */
    private static final String TEMP = Constant.getSystemProperty(TEMP_DIR, Constant.EMPTY_STRING);

    /**
     * Create the temp directory relative to loader class name.
     * 
     * @param loader The class loader.
     * @return The temp directory absolute path.
     */
    private static String getTempDir(Class<?> loader)
    {
        final File temp = new File(TEMP, loader.getSimpleName());
        final String path = temp.getAbsolutePath();
        if (!temp.exists() && !temp.mkdir())
        {
            Verbose.warning(ERROR_CREATE_TEMP_DIR, path);
        }
        return path;
    }

    /** Separator. */
    private final String separator;
    /** Resources directory. */
    private final String resourcesDir;
    /** Class loader. */
    private final Class<?> loader;
    /** Media path. */
    private final String path;
    /** Media parent path. */
    private final String parent;
    /** Temp path. */
    private String tempPath;

    /**
     * Internal constructor.
     * 
     * @param separator The separator used.
     * @param resourcesDir The resources directory path.
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaDefault(String separator, String resourcesDir, String path)
    {
        this(separator, resourcesDir, null, path);
    }

    /**
     * Internal constructor.
     * 
     * @param separator The separator used.
     * @param loader The class loader used (can be <code>null</code> if not used).
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaDefault(String separator, Class<?> loader, String path)
    {
        this(separator, getTempDir(loader), loader, path);
    }

    /**
     * Internal constructor.
     * 
     * @param separator The separator used.
     * @param resourcesDir The resources directory path (can be <code>null</code> if not used).
     * @param loader The class loader used (can be <code>null</code> if not used).
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    private MediaDefault(String separator, String resourcesDir, Class<?> loader, String path)
    {
        Check.notNull(path);

        this.separator = separator;
        this.resourcesDir = resourcesDir;
        this.loader = loader;
        this.path = path;
        final int index = path.lastIndexOf(separator);
        if (index > -1)
        {
            parent = path.substring(0, index);
        }
        else
        {
            parent = NO_PARENT;
        }
    }

    /**
     * Get the resources prefix.
     * 
     * @return The resources prefix.
     */
    private String getPrefix()
    {
        final String prefix;
        if (loader != null)
        {
            prefix = loader.getPackage().getName().replace(Constant.DOT, File.separator);
        }
        else
        {
            prefix = resourcesDir;
        }
        return prefix;
    }

    /*
     * Media
     */

    @Override
    public String getPath()
    {
        if (tempPath != null)
        {
            return tempPath;
        }
        return path;
    }

    @Override
    public String getParentPath()
    {
        return parent;
    }

    @Override
    public File getFile()
    {
        final File file;
        if (loader != null && tempPath == null)
        {
            final URL url = loader.getResource(path);
            if (url == null)
            {
                file = new File(getPath());
            }
            else
            {
                file = new File(url.getFile());
            }
        }
        else
        {
            file = new File(UtilFolder.getPathSeparator(separator, resourcesDir, getPath()));
        }
        return file;
    }

    @Override
    public Collection<Media> getMedias()
    {
        final File file = getFile();
        final File[] files = file.listFiles();
        if (files != null)
        {
            final Collection<Media> medias = new ArrayList<Media>(files.length);
            final String prefix = getPrefix();
            final int prefixLength = prefix.length();

            for (final File current : files)
            {
                final String path = current.getPath();
                final Media media = Medias.create(path.substring(path.indexOf(prefix) + prefixLength)
                                                      .replace(File.separator, Constant.SLASH)
                                                      .split(Constant.SLASH));
                medias.add(media);
            }
            return medias;
        }
        throw new LionEngineException(this, ERROR_PATH_DIR);
    }

    @Override
    public InputStream getInputStream()
    {
        try
        {
            if (loader != null && tempPath == null)
            {
                final InputStream input = loader.getResourceAsStream(UtilFolder.getPathSeparator(separator, getPath()));
                if (input == null)
                {
                    throw new LionEngineException(this, ERROR_OPEN_MEDIA_JAR);
                }
                return input;
            }
            final String realPath;
            if (tempPath != null)
            {
                realPath = UtilFolder.getPathSeparator(File.separator, resourcesDir, tempPath);
            }
            else
            {
                realPath = UtilFolder.getPathSeparator(separator, resourcesDir, path);
            }
            return new FileInputStream(realPath);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    @Override
    public OutputStream getOutputStream()
    {
        final String systemPath = path.replace(separator, File.separator);
        final String outputPath = UtilFolder.getPathSeparator(File.separator, resourcesDir, systemPath);
        if (new File(outputPath).getParentFile().mkdirs())
        {
            Verbose.info("Temp path created: ", outputPath);
        }
        try
        {
            final OutputStream output = new FileOutputStream(outputPath);
            tempPath = systemPath;
            return output;
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    @Override
    public boolean exists()
    {
        if (loader != null)
        {
            final String jarPath = UtilFolder.getPathSeparator(separator, getPath());
            return loader.getResource(jarPath) != null;
        }
        return getFile().exists();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + path.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Media media = (Media) object;
        return media.getPath().equals(path);
    }

    @Override
    public String toString()
    {
        return getPath();
    }
}
