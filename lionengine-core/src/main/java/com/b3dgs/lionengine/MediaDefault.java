/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
        final File file = new File(getPathAbsolute());
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
                prefixLength = prefix.length();
            }
            else
            {
                final String folderPath = folder.getPath();
                final String suffix = getPrefix().replace(separator, File.separator);
                prefix = folderPath.substring(0, folderPath.indexOf(suffix) + suffix.length()) + File.separator;
                prefixLength = prefix.length();
            }

            for (final File current : files)
            {
                final Media media = create(prefix, prefixLength, current);
                medias.add(media);
            }
        }
    }

    /**
     * Get input stream from JAR by default, try in temporary folder if not found in JAR.
     * 
     * @return The input stream found.
     * @throws FileNotFoundException If no stream found.
     */
    // CHECKSTYLE IGNORE LINE: ReturnCount
    private InputStream getInputFromJarOrTemp() throws FileNotFoundException
    {
        final InputStream input = resourcesClass.getResourceAsStream(UtilFolder.getPathSeparator(separator, getPath()));
        if (input == null)
        {
            if (new File(getPathUser()).exists())
            {
                return new FileInputStream(getPathUser());
            }
            return new FileInputStream(getPathTemp());
        }
        return input;
    }

    /**
     * Get the absolute media path.
     * 
     * @return The absolute media path.
     */
    private String getPathAbsolute()
    {
        return UtilFolder.getPathSeparator(separator, resourcesDir, path);
    }

    /**
     * Get the temporary path equivalent.
     * 
     * @return The temporary path.
     */
    private String getPathTemp()
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
    private String getPathUser()
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

    @Override
    public File getFile()
    {
        final File current = new File(getPathAbsolute());
        if (current.exists())
        {
            return current;
        }

        final File file;
        final URL url = resourcesClass.getResource(path);
        if (url == null)
        {
            final File fileUser = new File(getPathUser());
            // CHECKSTYLE IGNORE LINE: NestedIfDepth
            if (fileUser.exists())
            {
                file = fileUser;
            }
            else
            {
                file = new File(getPathTemp());
            }
        }
        else
        {
            file = new File(url.getFile());
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

    @Override
    public InputStream getInputStream()
    {
        try
        {
            final File file = new File(getPathAbsolute());
            if (file.exists())
            {
                return new FileInputStream(file);
            }
            return getInputFromJarOrTemp();
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    @Override
    public OutputStream getOutputStream()
    {
        try
        {
            return new FileOutputStream(getPathAbsolute());
        }
        catch (@SuppressWarnings("unused") final FileNotFoundException exception)
        {
            final String outputPath = getPathTemp();
            final File folder = new File(outputPath).getParentFile();
            if (folder.mkdirs())
            {
                Verbose.info("Temp path created: ", folder.getPath());
            }
            try
            {
                return new FileOutputStream(outputPath);
            }
            catch (final FileNotFoundException exception2)
            {
                throw new LionEngineException(exception2, this, ERROR_OPEN_MEDIA);
            }
        }
    }

    @Override
    public boolean exists()
    {
        final String jarPath = UtilFolder.getPathSeparator(separator, getPath());
        return getFile().exists()
               || resourcesClass.getResource(jarPath) != null
               || UtilFile.exists(getPathTemp())
               || UtilFile.exists(getPathUser());
    }

    @Override
    public boolean isJar()
    {
        return !getFile().exists()
               && resourcesClass.getResource(UtilFolder.getPathSeparator(separator, getPath())) != null;
    }

    /*
     * Object
     */

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
        final MediaDefault media = (MediaDefault) object;
        return path.equals(media.path);
    }

    @Override
    public String toString()
    {
        return path;
    }
}
