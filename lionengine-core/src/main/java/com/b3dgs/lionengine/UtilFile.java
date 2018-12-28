/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Tools related to files handling.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilFile
{
    /** Error directory. */
    static final String ERROR_DIRECTORY = "Not a directory: ";
    /** Error delete file. */
    static final String ERROR_DELETE_FILE = "File not deleted: ";

    /**
     * Get the file name without its extension.
     * <p>
     * Using <code>"image.png"</code> as argument returns <code>"image"</code>.
     * </p>
     * 
     * @param file The file name (must not be <code>null</code>).
     * @return The file name without its extension.
     * @throws LionEngineException If invalid arguments.
     */
    public static String removeExtension(String file)
    {
        Check.notNull(file);

        final int extensionBegin = file.lastIndexOf(Constant.DOT);
        if (extensionBegin > 0)
        {
            return file.substring(0, extensionBegin);
        }
        return file;
    }

    /**
     * Normalize the file extension by ensuring it has the required one.
     * 
     * @param file The original file name (must not be <code>null</code>).
     * @param extension The desired extension, will replace the other one if has (must not be <code>null</code>).
     * @return The normalized file with its extension.
     * @throws LionEngineException If invalid arguments.
     */
    public static String normalizeExtension(String file, String extension)
    {
        Check.notNull(file);
        Check.notNull(extension);

        final int length = file.length() + Constant.DOT.length() + extension.length();
        final StringBuilder builder = new StringBuilder(length).append(removeExtension(file)).append(Constant.DOT);
        if (extension.startsWith(Constant.DOT))
        {
            builder.append(getExtension(extension));
        }
        else
        {
            builder.append(extension);
        }
        return builder.toString();
    }

    /**
     * Get extension from a string (search first dot).
     * 
     * @param file The filename (must not be <code>null</code>).
     * @return The extension without dot.
     * @throws LionEngineException If invalid arguments.
     */
    public static String getExtension(String file)
    {
        Check.notNull(file);

        String ext = Constant.EMPTY_STRING;
        final int i = file.lastIndexOf(Constant.DOT);
        if (i >= 0 && i < file.length() - 1)
        {
            ext = file.substring(i + 1).toLowerCase(Locale.ENGLISH);
        }
        return ext;
    }

    /**
     * Get a file extension.
     * 
     * @param file The file (must not be <code>null</code>).
     * @return The extension without dot.
     * @throws LionEngineException If invalid arguments.
     */
    public static String getExtension(File file)
    {
        Check.notNull(file);

        return getExtension(file.getName());
    }

    /**
     * Get the files list from directory.
     * 
     * @param directory The directory reference (must not be <code>null</code>).
     * @return The directory content.
     * @throws LionEngineException If invalid argument or not a directory.
     */
    public static List<File> getFiles(File directory)
    {
        Check.notNull(directory);

        if (directory.isDirectory())
        {
            final File[] files = directory.listFiles();
            if (files != null)
            {
                return Arrays.asList(files);
            }
        }
        throw new LionEngineException(ERROR_DIRECTORY + directory.getPath());
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param path The path to check (must not be <code>null</code>).
     * @param extension The extension without dot; eg: png (must not be <code>null</code>).
     * @return The files list.
     * @throws LionEngineException If invalid arguments.
     */
    public static List<File> getFilesByExtension(File path, String extension)
    {
        Check.notNull(path);
        Check.notNull(extension);

        final List<File> filesList = new ArrayList<>(1);
        getFilesByExtensionRecursive(filesList, path, extension);
        return filesList;
    }

    /**
     * Get all files existing in the path with the specified name.
     * 
     * @param path The path to check (must not be <code>null</code>).
     * @param name The file name (must not be <code>null</code>).
     * @return The files list (empty array if none).
     * @throws LionEngineException If invalid arguments.
     */
    public static List<File> getFilesByName(File path, String name)
    {
        Check.notNull(path);
        Check.notNull(name);

        final List<File> filesList = new ArrayList<>(1);
        getFilesByNameRecursive(filesList, path, name);
        return filesList;
    }

    /**
     * Delete a file.
     * 
     * @param file The file to delete (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument or unable to remove file.
     */
    public static void deleteFile(File file)
    {
        Check.notNull(file);

        try
        {
            Files.delete(file.toPath());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_DELETE_FILE + file.getAbsolutePath());
        }
    }

    /**
     * Check if the path exists.
     * 
     * @param path The path to check (can be <code>null</code>).
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    public static boolean exists(String path)
    {
        return path != null && new File(path).exists();
    }

    /**
     * Check if the path is a file.
     * 
     * @param path The path to check (can be <code>null</code>).
     * @return <code>true</code> if it is a file, <code>false</code> else.
     */
    public static boolean isFile(String path)
    {
        return path != null && new File(path).isFile();
    }

    /**
     * Check if the following type is the expected type.
     * 
     * @param file The file to check (can be <code>null</code>).
     * @param extension The expected extension (must not be <code>null</code>).
     * @return <code>true</code> if correct, <code>false</code> else.
     * @throws LionEngineException If invalid argument.
     */
    public static boolean isType(File file, String extension)
    {
        Check.notNull(extension);

        if (file != null && file.isFile())
        {
            final String current = getExtension(file);
            return current.equals(extension.replace(Constant.DOT, Constant.EMPTY_STRING));
        }
        return false;
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param filesList The files list.
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     */
    private static void getFilesByExtensionRecursive(Collection<File> filesList, File path, String extension)
    {
        Optional.ofNullable(path.listFiles()).ifPresent(files -> Arrays.asList(files).forEach(content ->
        {
            if (content.isDirectory())
            {
                getFilesByExtensionRecursive(filesList, content, extension);
            }
            if (content.isFile() && extension.equals(getExtension(content)))
            {
                filesList.add(content);
            }
        }));
    }

    /**
     * Get all files existing in the path with the specified name.
     * 
     * @param filesList The files list.
     * @param path The path to check.
     * @param name The file name.
     */
    private static void getFilesByNameRecursive(Collection<File> filesList, File path, String name)
    {
        Optional.ofNullable(path.listFiles()).ifPresent(files -> Arrays.asList(files).forEach(content ->
        {
            if (content.isFile() && content.getName().equals(name))
            {
                filesList.add(content);
            }
            else if (content.isDirectory())
            {
                getFilesByNameRecursive(filesList, content, name);
            }
        }));
    }

    /**
     * Private constructor.
     */
    private UtilFile()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
