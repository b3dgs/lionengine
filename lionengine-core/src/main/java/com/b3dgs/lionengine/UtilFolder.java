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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tools related to directories handling.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilFolder
{
    /** Error delete file. */
    private static final String ERROR_DELETE_FILE = "File not deleted: ";
    /** Error delete directory. */
    private static final String ERROR_DELETE_DIRECTORY = "Directory not deleted: ";

    /**
     * Get all directories existing in the path.
     * 
     * @param path The path to check (must not be <code>null</code>).
     * @return The directories list.
     * @throws LionEngineException If invalid parameter.
     */
    public static List<File> getDirectories(File path)
    {
        Check.notNull(path);

        return Optional.ofNullable(path.listFiles())
                       .map(files -> Arrays.asList(files)
                                           .stream()
                                           .filter(File::isDirectory)
                                           .collect(Collectors.toList()))
                       .orElseGet(Collections::emptyList);
    }

    /**
     * Construct a usable path using a list of string, automatically separated by the portable separator.
     * 
     * @param path The list of directories, if has, and file (must not be <code>null</code>).
     * @return The full media path.
     * @throws LionEngineException If invalid parameters.
     */
    public static String getPath(String... path)
    {
        return getPathSeparator(Constant.SLASH, path);
    }

    /**
     * Construct a usable path using a list of string, automatically separated by the portable separator.
     * 
     * @param separator The separator to use (must not be <code>null</code>).
     * @param path The list of directories, if has, and file (must not be <code>null</code>).
     * @return The full media path.
     * @throws LionEngineException If invalid parameters.
     */
    public static String getPathSeparator(String separator, String... path)
    {
        Check.notNull(separator);
        Check.notNull(path);

        final StringBuilder fullPath = new StringBuilder(path.length);
        for (int i = 0; i < path.length; i++)
        {
            if (i == path.length - 1)
            {
                fullPath.append(path[i].replace(File.separator, separator));
            }
            else if (path[i] != null && path[i].length() > 0)
            {
                fullPath.append(path[i].replace(File.separator, separator));
                if (!fullPath.substring(fullPath.length() - 1, fullPath.length()).equals(separator))
                {
                    fullPath.append(separator);
                }
            }
        }
        return fullPath.toString();
    }

    /**
     * Delete a directory and all of its content (be careful, it will erase all children including child directory).
     * 
     * @param element The directory to delete with all of its content (must not be <code>null</code>).
     * @throws LionEngineException If invalid parameter.
     */
    public static void deleteDirectory(File element)
    {
        Check.notNull(element);

        if (element.isDirectory())
        {
            Optional.ofNullable(element.listFiles())
                    .map(Arrays::asList)
                    .ifPresent(files -> files.forEach(UtilFolder::deleteDirectory));
            try
            {
                Files.delete(element.toPath());
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception, ERROR_DELETE_DIRECTORY, element.getAbsolutePath());
            }
        }
        else if (element.isFile())
        {
            try
            {
                Files.delete(element.toPath());
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception, ERROR_DELETE_FILE, element.getAbsolutePath());
            }
        }
    }

    /**
     * Check if the path is a directory.
     * 
     * @param path The path to check (can be <code>null</code>).
     * @return <code>true</code> if it is a directory, <code>false</code> else.
     */
    public static boolean isDirectory(String path)
    {
        return path != null && new File(path).isDirectory();
    }

    /**
     * Private constructor.
     */
    private UtilFolder()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
