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
package com.b3dgs.lionengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Media implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
final class MediaDefault implements Media
{
    /** Error open media. */
    static final String ERROR_OPEN_MEDIA = "Cannot open the media !";
    /** Temp folder property. */
    private static final String USER_DIR = "user.dir";
    /** Temp folder property. */
    private static final String TEMP_DIR = "java.io.tmpdir";
    /** No parent. */
    private static final String NO_PARENT = Constant.EMPTY_STRING;
    /** Temp folder. */
    private static final String TEMP = Constant.getSystemProperty(TEMP_DIR, Constant.EMPTY_STRING);
    /** User folder. */
    private static final String USER = Constant.getSystemProperty(USER_DIR, Constant.EMPTY_STRING);
    /** Split regex. */
    private static final Pattern SLASH = Pattern.compile(Constant.SLASH);
    /** Jar file prefix. */
    private static final String JAR_FILE_PREFIX = "file:";
    /** Jar file suffix. */
    private static final String JAR_FILE_SUFFIX = ".jar!";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaDefault.class);

    /**
     * Check if path exists.
     * 
     * @param path The path to check.
     * @return The file descriptor if exists, <code>null</code> if not found.
     */
    private static File exists(String path)
    {
        final File file = new File(path);
        if (file.exists())
        {
            return file;
        }
        return null;
    }

    /** Separator. */
    private final String separator;
    /** Resources directory. */
    private final String resourcesDir;
    /** Class loader. */
    private final Class<?> resourcesClass;
    /** Media path. */
    private final String path;
    /** Media parent path. */
    private final String parent;
    /** Media name. */
    private final String name;

    /**
     * Internal constructor.
     * 
     * @param separator The separator used (must not be <code>null</code>).
     * @param resourcesDir The resources directory path (must not be <code>null</code>).
     * @param resourcesClass The class loader used (must not be <code>null</code>).
     * @param path The media path (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    MediaDefault(String separator, String resourcesDir, Class<?> resourcesClass, String path)
    {
        super();

        Check.notNull(separator);
        Check.notNull(resourcesDir);
        Check.notNull(resourcesClass);
        Check.notNull(path);

        this.separator = separator;
        this.resourcesDir = resourcesDir;
        this.resourcesClass = resourcesClass;
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
        name = path.substring(path.lastIndexOf(separator) + 1);
    }

    /**
     * Create media from file.
     * 
     * @param prefix The prefix path.
     * @param prefixLength The prefix length.
     * @param file The file to created as media.
     * @return The created media.
     */
    private Media create(String prefix, int prefixLength, File file)
    {
        final String currentPath = file.getPath();
        final String[] systemPath = SLASH.split(currentPath.substring(currentPath.indexOf(prefix) + prefixLength));
        return new MediaDefault(separator,
                                resourcesDir,
                                resourcesClass,
                                UtilFolder.getPathSeparator(separator, systemPath));
    }

    /**
     * Get the resources prefix.
     * 
     * @return The resources prefix.
     */
    private String getPrefix()
    {
        final String prefix;
        final File file = new File(getPathFromResourcesDir());
        if (file.exists())
        {
            prefix = resourcesDir;
        }
        else
        {
            prefix = resourcesClass.getPackage().getName().replace(Constant.DOT, separator);
        }
        return prefix;
    }

    /**
     * Find medias in jar.
     * 
     * @param medias The medias found.
     */
    private void fillMediasFromJar(Collection<Media> medias)
    {
        final String fullpath = getFile().getPath().replace(JAR_FILE_PREFIX, Constant.EMPTY_STRING);
        final int length = fullpath.indexOf(JAR_FILE_SUFFIX) + JAR_FILE_SUFFIX.length() - 1;
        final File zip = new File(fullpath.substring(0, length));
        final String root = resourcesClass.getName()
                                          .replace(resourcesClass.getSimpleName(), Constant.EMPTY_STRING)
                                          .replace(Constant.DOT, Constant.SLASH);
        final String folder = fullpath.substring(length
                                                 + 2
                                                 + resourcesClass.getName().length()
                                                 - resourcesClass.getSimpleName().length())
                                      .replace(File.separator, Constant.SLASH)
                              + Constant.SLASH;

        for (final ZipEntry entry : UtilZip.getEntries(zip, root + folder))
        {
            final Media media = Medias.create(entry.getName().replace(root, Constant.EMPTY_STRING));
            medias.add(media);
        }
    }

    /**
     * Find medias in folder.
     * 
     * @param folder The folder to search in.
     * @param medias The medias found.
     */
    private void fillMediasFromFolder(File folder, Collection<Media> medias)
    {
        final File[] files = folder.listFiles();
        if (files != null)
        {
            final String prefix;
            final int prefixLength;
            if (folder.getPath().startsWith(TEMP))
            {
                prefix = folder.getPath() + File.separator;
            }
            else
            {
                final String folderPath = folder.getPath();
                final String suffix = getPrefix().replace(separator, File.separator);
                prefix = folderPath.substring(0, folderPath.indexOf(suffix) + suffix.length()) + File.separator;
            }
            prefixLength = prefix.length();

            for (final File current : files)
            {
                final Media media = create(prefix, prefixLength, current);
                medias.add(media);
            }
        }
    }

    /**
     * Get the absolute media path.
     * 
     * @return The absolute media path.
     */
    private String getPathFromResourcesDir()
    {
        return UtilFolder.getPathSeparator(separator, resourcesDir, path);
    }

    /**
     * Get the temporary path equivalent.
     * 
     * @return The temporary path.
     */
    private String getPathFromTemp()
    {
        return UtilFolder.getPathSeparator(separator,
                                           TEMP,
                                           UtilFolder.getPath(Engine.isStarted() ? Engine.getProgramName()
                                                                                 : Constant.ENGINE_NAME,
                                                              path));
    }

    /**
     * Get the user path equivalent.
     * 
     * @return The user path.
     */
    private String getPathFromUser()
    {
        return UtilFolder.getPathSeparator(separator, USER, path);
    }

    /*
     * Media
     */

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public String getParentPath()
    {
        return parent;
    }

    /**
     * Get the first file descriptor found.
     * <p>
     * Here the following order:
     * </p>
     * <ul>
     * <li>Resources directory</li>
     * <li>User directory</li>
     * <li>Temp directory</li>
     * <li>JAR resources</li>
     * <li>Temp directory</li>
     * </ul>
     */
    @Override
    public File getFile()
    {
        final File file;
        File current = null;
        URL url = null;
        if ((current = exists(getPathFromResourcesDir())) != null // CHECKSTYLE IGNORE LINE: Assign|Comment
            || (current = exists(getPathFromUser())) != null // CHECKSTYLE IGNORE LINE: Assign|Comment
            || (current = exists(getPathFromTemp())) != null) // CHECKSTYLE IGNORE LINE: Assign|Comment
        {
            file = current;
        }
        else if ((url = resourcesClass.getResource(path)) != null) // CHECKSTYLE IGNORE LINE: Assign|Comment
        {
            file = new File(url.getFile());
        }
        else
        {
            file = new File(getPathFromTemp());
        }
        return file;
    }

    @Override
    public URL getUrl()
    {
        final URL url = resourcesClass.getResource(path);
        if (url != null)
        {
            return url;
        }
        throw new LionEngineException(this, ERROR_OPEN_MEDIA);
    }

    @Override
    public Collection<Media> getMedias()
    {
        final Collection<Media> medias = new ArrayList<>();
        final File file = getFile();
        final String filePath = file.getPath();
        if (filePath.contains(JAR_FILE_SUFFIX))
        {
            fillMediasFromJar(medias);
        }
        else
        {
            fillMediasFromFolder(file, medias);
        }
        return medias;
    }

    /**
     * Get the first input stream found.
     * <p>
     * Here the following order:
     * </p>
     * <ul>
     * <li>Resources directory</li>
     * <li>User directory</li>
     * <li>Temp directory</li>
     * <li>JAR resources</li>
     * </ul>
     */
    @Override
    public InputStream getInputStream()
    {
        File current = null;
        if ((current = exists(getPathFromResourcesDir())) == null // CHECKSTYLE IGNORE LINE: Assign|Comment
            && (current = exists(getPathFromUser())) == null // CHECKSTYLE IGNORE LINE: Assign|Comment
            && (current = exists(getPathFromTemp())) == null) // CHECKSTYLE IGNORE LINE: Assign|Comment
        {
            final InputStream i = resourcesClass.getResourceAsStream(UtilFolder.getPathSeparator(separator, getPath()));
            if (i != null)
            {
                return i;
            }
            throw new LionEngineException(this, ERROR_OPEN_MEDIA);
        }
        final File file = current;
        try
        {
            return new FileInputStream(file);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    /**
     * Get the first output stream available.
     * <p>
     * Here the following order:
     * </p>
     * <ul>
     * <li>Resources directory if already exists</li>
     * <li>User directory if already exists</li>
     * <li>Temp directory</li>
     * </ul>
     */
    @Override
    public OutputStream getOutputStream()
    {
        final File file;
        File current = null;
        // CHECKSTYLE IGNORE LINE: Assign
        if ((current = exists(getPathFromResourcesDir())) != null || (current = exists(getPathFromUser())) != null)
        {
            file = current;
        }
        else
        {
            file = new File(getPathFromTemp());
            if (file.getParentFile().mkdirs())
            {
                LOGGER.info("Temp path created: {}", file.getPath());
            }
        }
        try
        {
            return new FileOutputStream(file);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    @Override
    public boolean exists()
    {
        return resourcesClass.getResource(path) != null || getFile().exists();
    }

    @Override
    public boolean isJar()
    {
        return exists(getPathFromResourcesDir()) == null
               && exists(getPathFromUser()) == null
               && exists(getPathFromTemp()) == null
               && resourcesClass.getResource(path) != null;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        final int result = 1;
        return prime * result + path.hashCode();
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
        final MediaDefault media = (MediaDefault) object;
        return path.equals(media.path);
    }

    @Override
    public String toString()
    {
        return path;
    }
}
