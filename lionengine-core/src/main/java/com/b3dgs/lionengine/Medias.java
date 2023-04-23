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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Implementation provider for the {@link FactoryMedia}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Medias
{
    /** Default resources directory. */
    public static final String DEFAULT_RESOURCES_DIR = "assets";
    /** Path separator. */
    private static final String SEPARATOR = Constant.SLASH;
    /** Factory media implementation. */
    private static FactoryMedia factoryMedia = new FactoryMediaDefault();
    /** Resources directory. */
    private static String resourcesDir = DEFAULT_RESOURCES_DIR + getSeparator();
    /** Class loader. */
    private static Class<?> loader = Engine.class;

    /**
     * Create a media.
     * 
     * @param path The media path (must not be <code>null</code>).
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String... path)
    {
        return factoryMedia.create(SEPARATOR, resourcesDir, loader, path);
    }

    /**
     * Set the media factory used (must not be <code>null</code>).
     * 
     * @param factoryMedia The media factory used.
     * @throws LionEngineException If invalid parameter.
     */
    public static synchronized void setFactoryMedia(FactoryMedia factoryMedia)
    {
        Check.notNull(factoryMedia);

        Medias.factoryMedia = factoryMedia;
    }

    /**
     * Define resources directory. Root for all medias. Disable the load from JAR.
     * 
     * @param directory The main resources directory (can be <code>null</code>).
     */
    public static synchronized void setResourcesDirectory(String directory)
    {
        if (directory == null)
        {
            resourcesDir = DEFAULT_RESOURCES_DIR + getSeparator();
        }
        else
        {
            resourcesDir = directory + getSeparator();
        }
    }

    /**
     * Set class resources root loading from Jar.
     * 
     * @param clazz The class loader reference resources entry point (can be <code>null</code>).
     */
    public static synchronized void setLoadFromJar(Class<?> clazz)
    {
        if (clazz == null)
        {
            loader = Engine.class;
        }
        else
        {
            loader = clazz;
        }
    }

    /**
     * Get a media from an existing file descriptor. {@link #setResourcesDirectory(String)} must be activated.
     * 
     * @param file The file descriptor (must not be <code>null</code>).
     * @return The media instance.
     * @throws LionEngineException If invalid parameter.
     */
    public static synchronized Media get(File file)
    {
        Check.notNull(file);

        final String filename = file.getPath().replace(File.separator, SEPARATOR);
        final String localFile = filename.substring(resourcesDir.length() + filename.lastIndexOf(resourcesDir));

        return create(localFile);
    }

    /**
     * Get all media by extension found in the direct path (does not search in sub folders).
     * 
     * @param extension The extension without dot; eg: png (must not be <code>null</code>).
     * @param folder The folder to search in (must not be <code>null</code>).
     * @return The medias found.
     * @throws LionEngineException If invalid parameters.
     */
    public static synchronized List<Media> getByExtension(String extension, Media folder)
    {
        Check.notNull(extension);
        Check.notNull(folder);

        if (folder.isJar())
        {
            return getFilesByExtension(folder, extension);
        }

        final Media media = Medias.create(Constant.EMPTY_STRING);
        final String prefix = media.getFile().getPath();
        final String fullPath = Medias.create(prefix, folder.getPath()).getPath();
        final int prefixLength = prefix.length() + 1;

        return getByExtension(new File(prefix), fullPath, prefixLength, extension);
    }

    /**
     * Get all media by extension found in the direct JAR path (does not search in sub folders).
     * 
     * @param jar The JAR file (must not be <code>null</code>).
     * @param fullPath The full path in JAR (must not be <code>null</code>).
     * @param prefixLength The prefix length in JAR (must not be <code>null</code>).
     * @param extension The extension without dot; eg: png (must not be <code>null</code>).
     * @return The medias found.
     * @throws LionEngineException If invalid parameters.
     */
    public static List<Media> getByExtension(File jar, String fullPath, int prefixLength, String extension)
    {
        if (jar.isDirectory())
        {
            return UtilFile.getFilesByExtension(new File(fullPath), extension)
                           .stream()
                           .map(file -> Medias.create(file.getPath()
                                                          .substring(prefixLength)
                                                          .split("\\" + File.separator)))
                           .collect(Collectors.toList());
        }
        final Collection<ZipEntry> entries = UtilZip.getEntriesByExtension(jar, fullPath, extension);
        final List<Media> medias = new ArrayList<>(entries.size());
        for (final ZipEntry entry : entries)
        {
            final Media media = create(entry.getName().substring(prefixLength));
            medias.add(media);
        }
        return medias;
    }

    /**
     * Get the media with an additional suffix, just before the dot of the extension if has.
     * 
     * @param media The current media reference (must not be <code>null</code>).
     * @param suffix The suffix to add (must not be <code>null</code>).
     * @return The new media with suffix added.
     * @throws LionEngineException If invalid parameters.
     */
    public static Media getWithSuffix(Media media, String suffix)
    {
        Check.notNull(media);
        Check.notNull(suffix);

        final String path = media.getPath();
        final int dotIndex = path.lastIndexOf(Constant.DOT);
        if (dotIndex > -1)
        {
            return Medias.create(path.substring(0, dotIndex) + Constant.UNDERSCORE + suffix + path.substring(dotIndex));
        }
        return Medias.create(path + Constant.UNDERSCORE + suffix);
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
     * Get the path separator.
     * 
     * @return The path separator.
     */
    public static String getSeparator()
    {
        return SEPARATOR;
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     * @return The files list.
     */
    private static List<Media> getFilesByExtension(Media path, String extension)
    {
        final List<Media> filesList = new ArrayList<>(1);
        getFilesByExtensionRecursive(filesList, path, extension);
        return filesList;
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param filesList The files list.
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     */
    private static void getFilesByExtensionRecursive(Collection<Media> filesList, Media path, String extension)
    {
        for (final Media content : path.getMedias())
        {
            if (extension.equals(UtilFile.getExtension(content.getPath())))
            {
                filesList.add(content);
            }
        }
    }

    /**
     * Private constructor.
     */
    private Medias()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
