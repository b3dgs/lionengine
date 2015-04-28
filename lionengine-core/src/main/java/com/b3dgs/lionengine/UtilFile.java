/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Collection;
import java.util.Locale;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Static functions giving informations related to files and directory.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilFile
{
    /**
     * Check if the path is a directory.
     * 
     * @param path The path to check.
     * @return <code>true</code> if it is a directory, <code>false</code> else.
     */
    public static boolean isDir(String path)
    {
        if (path == null)
        {
            return false;
        }
        return new File(path).isDirectory();
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
     * Construct a usable path using a list of string, automatically separated by the portable separator. The
     * constructed path will use local system file separator.
     * 
     * @param path The list of folders (if has) and file.
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
     * Get a file extension.
     * 
     * @param file The file.
     * @return The extension.
     */
    public static String getExtension(File file)
    {
        return getExtension(file.getName());
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
        return getExtension(file).equals(extension);
    }

    /**
     * Get extension from a string (search first dot).
     * 
     * @param file The filename.
     * @return The extension.
     */
    public static String getExtension(String file)
    {
        String ext = "";
        final int i = file.lastIndexOf('.');
        if (i > 0 && i < file.length() - 1)
        {
            ext = file.substring(i + 1).toLowerCase(Locale.getDefault());
        }
        return ext;
    }

    /**
     * Get the filename from a path (last part of a path, after the last separator).
     * 
     * @param path The path used to extract filename.
     * @return The filename extracted from path.
     */
    public static String getFilenameFromPath(String path)
    {
        final int i = path.lastIndexOf(Medias.getSeparator());
        return path.substring(i + 1, path.length());
    }

    /**
     * Get all directory existing in the path.
     * 
     * @param path The path to check.
     * @return The directory list.
     */
    public static String[] getDirsList(String path)
    {
        final File file = new File(path);
        if (!file.exists())
        {
            return new String[0];
        }

        final File[] files = file.listFiles();
        int numberOfDirs = 0;
        for (final File file2 : files)
        {
            if (file2.isDirectory())
            {
                numberOfDirs++;
            }
        }

        final String[] dirsList = new String[numberOfDirs];
        for (int i = 0, id = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                dirsList[id] = files[i].getName();
                id++;
            }
        }

        return dirsList;
    }

    /**
     * Get all files existing in the path.
     * 
     * @param path The path to check.
     * @return The files list.
     */
    public static String[] getFilesList(String path)
    {
        final File file = new File(path);
        if (!file.exists())
        {
            return new String[0];
        }
        final File[] files = file.listFiles();
        String[] filesList;

        int numberOfFiles = 0;
        for (final File file2 : files)
        {
            if (file2.isFile())
            {
                numberOfFiles++;
            }
        }

        filesList = new String[numberOfFiles];
        for (int i = 0, id = 0; i < files.length; i++)
        {
            if (files[i].isFile())
            {
                filesList[id] = files[i].getName();
                id++;
            }
        }

        return filesList;
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     * @return The files list.
     */
    public static Collection<File> getFilesByExtension(String path, String extension)
    {
        final Collection<File> filesList = new ArrayList<>(1);
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
    public static Collection<File> getFilesByName(File path, String name)
    {
        final Collection<File> filesList = new ArrayList<>(1);
        getFilesByNameRecursive(filesList, path, name);
        return filesList;
    }

    /**
     * Get the file name without its extension.
     * 
     * @param file The file name.
     * @return The file name without its extension.
     */
    public static String removeExtension(String file)
    {
        return file.substring(0, file.lastIndexOf('.'));
    }

    /**
     * Delete a directory and all of its content (be careful, it will erase all children, including child directory).
     * 
     * @param directory The directory to delete with all of its content.
     */
    public static void deleteDirectory(File directory)
    {
        Check.notNull(directory);

        if (directory.isDirectory())
        {
            final String[] children = directory.list();
            for (final String element : children)
            {
                deleteDirectory(new File(directory, element));
            }
            try
            {
                Files.delete(directory.toPath());
            }
            catch (final IOException exception)
            {
                Verbose.exception(UtilFile.class, "deleteDirectory", exception, "Directory not deleted: " + directory);
            }
        }
        else if (directory.isFile())
        {
            deleteFile(directory);
            Verbose.info("File deleted: " + directory);
        }
    }

    /**
     * Delete a file.
     * 
     * @param file The file to delete.
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
            Verbose.warning(UtilFile.class, "deleteFile", "File not deleted: " + file);
        }
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param filesList The files list.
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     */
    private static void getFilesByExtensionRecursive(Collection<File> filesList, String path, String extension)
    {
        final File file = new File(path);
        if (file.exists())
        {
            final File[] files = file.listFiles();
            for (final File content : files)
            {
                if (content.isDirectory())
                {
                    getFilesByExtensionRecursive(filesList, content.getPath(), extension);
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
        for (final File file : path.listFiles())
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

    /**
     * Private constructor.
     */
    private UtilFile()
    {
        throw new RuntimeException();
    }
}
