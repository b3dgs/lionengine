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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.util.UtilFile;
import com.b3dgs.lionengine.util.UtilZip;

/**
 * Implementation provider for the {@link FactoryMedia}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class Medias
{
    /** Not in JAR resources. */
    private static final String JAR_LOADER_ERROR = "Load from JAR not enabled !";
    /** Factory media implementation. */
    private static FactoryMedia factoryMedia = new FactoryMediaDefault();
    /** Path separator. */
    private static volatile String separator = File.separator;
    /** Resources directory. */
    private static volatile String resourcesDir = Constant.EMPTY_STRING;
    /** Class loader. */
    private static volatile Optional<Class<?>> loader = Optional.empty();

    /**
     * Create a media.
     * 
     * @param path The media path (must not be <code>null</code>).
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String... path)
    {
        if (loader.isPresent())
        {
            return factoryMedia.create(separator, loader.get(), path);
        }
        return factoryMedia.create(separator, resourcesDir, path);
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
            resourcesDir = Constant.EMPTY_STRING + getSeparator();
        }
        else
        {
            resourcesDir = directory + getSeparator();
        }
        loader = Optional.empty();
    }

    /**
     * Activate or no the resources loading from *.jar. A <code>null</code> value will disable load from jar.
     * 
     * @param clazz The class loader reference resources entry point (may be <code>null</code>).
     */
    public static synchronized void setLoadFromJar(Class<?> clazz)
    {
        loader = Optional.ofNullable(clazz);
        if (loader.isPresent())
        {
            setSeparator(Constant.SLASH);
        }
        else
        {
            setSeparator(File.separator);
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

        final String filename = file.getPath();
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

        final File jar = getJarResources();
        final String prefix = getJarResourcesPrefix();
        final String fullPath = Medias.create(prefix, folder.getPath()).getPath();
        final int prefixLength = prefix.length() + 1;

        return getByExtension(jar, fullPath, prefixLength, extension);
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
    public static synchronized List<Media> getByExtension(File jar, String fullPath, int prefixLength, String extension)
    {
        if (jar.isDirectory())
        {
            return UtilFile.getFilesByExtension(new File(jar, fullPath), extension)
                           .stream()
                           .map(file -> Medias.create(file.getName()))
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
     * @param media The current media reference (must not be <code>null</code>)
     * @param suffix The suffix to add (must not be <code>null</code>)
     * @return The new media with suffix added.
     * @throws LionEngineException If invalid parameters.
     */
    public static synchronized Media getWithSuffix(Media media, String suffix)
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
    public static String getResourcesDirectory()
    {
        return resourcesDir;
    }

    /**
     * Get the resources loader.
     * 
     * @return The resources loader.
     */
    public static Optional<Class<?>> getResourcesLoader()
    {
        return loader;
    }

    /**
     * Get the running JAR resources. Load from JAR must be enabled.
     * 
     * @return The JAR file.
     * @throws LionEngineException If JAR not available.
     */
    public static synchronized File getJarResources()
    {
        if (!loader.isPresent())
        {
            throw new LionEngineException(JAR_LOADER_ERROR);
        }

        final Media media = Medias.create(Constant.EMPTY_STRING);
        final String path = media.getFile().getPath().replace(File.separator, Constant.SLASH);
        final String prefix = loader.get().getPackage().getName().replace(Constant.DOT, Constant.SLASH);
        final int jarSeparatorIndex = path.indexOf(prefix);
        final String jar = path.substring(0, jarSeparatorIndex);

        return new File(jar);
    }

    /**
     * Get the running JAR resources prefix folder. Load from JAR must be enabled.
     * 
     * @return The resources prefix folder.
     * @throws LionEngineException If JAR not available.
     */
    public static synchronized String getJarResourcesPrefix()
    {
        if (!loader.isPresent())
        {
            throw new LionEngineException(JAR_LOADER_ERROR);
        }
        final Media media = Medias.create(Constant.EMPTY_STRING);
        final String path = media.getFile().getPath().replace(File.separator, Constant.SLASH);
        final String prefix = loader.get().getPackage().getName().replace(Constant.DOT, Constant.SLASH);
        final int jarSeparatorIndex = path.indexOf(prefix);

        return path.substring(jarSeparatorIndex).replace(File.separator, Constant.SLASH);
    }

    /**
     * Set the path separator.
     * 
     * @param separator The path separator (must not be <code>null</code>).
     * @throws LionEngineException If invalid parameter.
     */
    public static void setSeparator(String separator)
    {
        Check.notNull(separator);

        Medias.separator = separator;
    }

    /**
     * Get the path separator.
     * 
     * @return The path separator.
     */
    public static String getSeparator()
    {
        return separator;
    }

    /**
     * Private constructor.
     */
    private Medias()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
