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
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

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
     * @param path The path to check.
     * @return The directories list.
     */
    public static List<File> getDirectories(File path)
    {
        final File[] files = path.listFiles();
        final List<File> directories = new ArrayList<>();
        if (files != null)
        {
            for (final File current : files)
            {
                if (current.isDirectory())
                {
                    directories.add(current);
                }
            }
        }
        return directories;
    }

    /**
     * Construct a usable path using a list of string, automatically separated by the portable separator. The
     * constructed path will use local system file separator.
     * 
     * @param path The list of directories (if has) and file.
     * @return The full media path.
     */
    public static String getPath(String... path)
    {
        return getPathSeparator(File.separator, path);
    }

    /**
     * Construct a usable path using a list of string, automatically separated by the portable separator.
     * 
     * @param separator The separator to use.
     * @param path The list of directories (if has) and file.
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
            else if (path[i] != null && path[i].length() > 0)
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
     * Delete a directory and all of its content (be careful, it will erase all children, including child directory).
     * 
     * @param element The directory to delete with all of its content.
     */
    public static void deleteDirectory(File element)
    {
        Check.notNull(element);

        if (element.isDirectory())
        {
            final File[] files = element.listFiles();
            if (files != null)
            {
                for (final File file : files)
                {
                    deleteDirectory(file);
                }
            }
            if (!element.delete())
            {
                Verbose.warning(ERROR_DELETE_DIRECTORY, element.getAbsolutePath());
            }
        }
        else if (element.isFile() && !element.delete())
        {
            Verbose.warning(ERROR_DELETE_FILE, element.getAbsolutePath());
        }
    }

    /**
     * Check if the path is a directory.
     * 
     * @param path The path to check.
     * @return <code>true</code> if it is a directory, <code>false</code> else.
     */
    public static boolean isDirectory(String path)
    {
        if (path == null)
        {
            return false;
        }
        return new File(path).isDirectory();
    }

    /**
     * Private constructor.
     */
    private UtilFolder()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
