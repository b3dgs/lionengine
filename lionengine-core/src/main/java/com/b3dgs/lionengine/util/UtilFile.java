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
package com.b3dgs.lionengine.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Tools related to files handling.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilFile
{
    /** Error directory. */
    private static final String ERROR_DIRECTORY = "Not a directory: ";
    /** Error delete file. */
    private static final String ERROR_DELETE_FILE = "File not deleted: ";

    /**
     * Get the file name without its extension.
     * <p>
     * Using <code>"image.png"</code> as argument returns <code>"image"</code>.
     * </p>
     * 
     * @param file The file name.
     * @return The file name without its extension.
     * @throws LionEngineException If <code>null</code> argument.
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
     * @param file The original file name.
     * @param extension The desired extension (will replace the other one if has).
     * @return The normalized file with its extension.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public static String normalizeExtension(String file, String extension)
    {
        Check.notNull(file);
        Check.notNull(extension);

        final StringBuilder builder = new StringBuilder(removeExtension(file)).append(Constant.DOT);
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
     * @param file The filename.
     * @return The extension without dot.
     * @throws LionEngineException If <code>null</code> argument.
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
     * @param file The file.
     * @return The extension without dot.
     * @throws LionEngineException If <code>null</code> argument.
     */
    public static String getExtension(File file)
    {
        Check.notNull(file);
        return getExtension(file.getName());
    }

    /**
     * Get the files list from directory.
     * 
     * @param directory The directory reference.
     * @return The directory content.
     * @throws LionEngineException If not a directory.
     */
    public static List<File> getFiles(File directory)
    {
        if (directory.isDirectory())
        {
            final File[] files = directory.listFiles();
            if (files != null)
            {
                return Arrays.asList(files);
            }
        }
        throw new LionEngineException(ERROR_DIRECTORY, directory.getPath());
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     * @return The files list.
     */
    public static List<File> getFilesByExtension(File path, String extension)
    {
        final List<File> filesList = new ArrayList<File>(1);
        getFilesByExtensionRecursive(filesList, path, extension);
        return filesList;
    }

    /**
     * Get all files existing in the path with the specified name.
     * 
     * @param path The path to check.
     * @param name The file name.
     * @return The files list (empty array if none).
     */
    public static List<File> getFilesByName(File path, String name)
    {
        final List<File> filesList = new ArrayList<File>(1);
        getFilesByNameRecursive(filesList, path, name);
        return filesList;
    }

    /**
     * Delete a file.
     * 
     * @param file The file to delete.
     * @throws LionEngineException If unable to remove file.
     */
    public static void deleteFile(File file)
    {
        Check.notNull(file);

        if (!file.delete())
        {
            throw new LionEngineException(ERROR_DELETE_FILE, file.getAbsolutePath());
        }
    }

    /**
     * Check if the path exists.
     * 
     * @param path The path to check.
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    public static boolean exists(String path)
    {
        if (path == null)
        {
            return false;
        }
        return new File(path).exists();
    }

    /**
     * Check if the path is a file.
     * 
     * @param path The path to check.
     * @return <code>true</code> if it is a file, <code>false</code> else.
     */
    public static boolean isFile(String path)
    {
        if (path == null)
        {
            return false;
        }
        return new File(path).isFile();
    }

    /**
     * Check if the following type is the expected type.
     * 
     * @param file The file to check.
     * @param extension The expected extension.
     * @return <code>true</code> if correct, <code>false</code> else.
     */
    public static boolean isType(File file, String extension)
    {
        if (file.isFile())
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
        final File[] files = path.listFiles();
        if (files != null)
        {
            for (final File content : files)
            {
                if (content.isDirectory())
                {
                    getFilesByExtensionRecursive(filesList, content, extension);
                }
                if (content.isFile() && extension.equals(getExtension(content)))
                {
                    filesList.add(content);
                }
            }
        }
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
        final File[] files = path.listFiles();
        if (files != null)
        {
            for (final File file : files)
            {
                if (file.isFile() && file.getName().equals(name))
                {
                    filesList.add(file);
                }
                else if (file.isDirectory())
                {
                    getFilesByNameRecursive(filesList, file, name);
                }
            }
        }
    }

    /**
     * Private constructor.
     */
    private UtilFile()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
