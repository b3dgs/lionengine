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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Tools related to files and directories handling.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilFile
{
    /** File deleted. */
    private static final String FILE_DELETED = "File deleted: ";
    /** Error directory. */
    private static final String ERROR_DIRECTORY = "Not a directory: ";
    /** Error delete directory. */
    private static final String ERROR_DELETE_DIRECTORY = "Directory not deleted: ";
    /** Error delete file. */
    private static final String ERROR_DELETE_FILE = "File not deleted: ";
    /** Error temporary file. */
    private static final String ERROR_TEMP_FILE = "Unable to create temporary file for: ";
    /** Temporary file prefix. */
    private static final String PREFIX_TEMP = "temp";
    /** Copy buffer. */
    private static final int BUFFER_COPY = 65535;

    /**
     * Copy a stream onto another.
     * 
     * @param source The source stream.
     * @param destination The destination stream.
     * @throws IOException If error.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public static void copy(InputStream source, OutputStream destination) throws IOException, LionEngineException
    {
        Check.notNull(source);
        Check.notNull(destination);

        final byte[] buffer = new byte[BUFFER_COPY];
        while (true)
        {
            final int read = source.read(buffer);
            if (read == -1)
            {
                break;
            }
            destination.write(buffer, 0, read);
        }
    }

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
    public static String removeExtension(String file) throws LionEngineException
    {
        Check.notNull(file);

        final int extensionBegin = file.lastIndexOf(Constant.DOT);
        return file.substring(0, extensionBegin);
    }

    /**
     * Get of full copy of the input stream stored in a temporary file.
     * 
     * @param name The file name reference (to have a similar temporary file name).
     * @param input The input stream reference.
     * @return The temporary file created with copied content from stream.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public static File getCopy(String name, InputStream input) throws LionEngineException
    {
        Check.notNull(name);
        Check.notNull(input);

        final String prefix;
        final String suffix;
        final int minimumPrefix = 3;
        final int i = name.lastIndexOf(Constant.DOT);
        if (i > minimumPrefix)
        {
            prefix = name.substring(0, i);
            suffix = name.substring(i);
        }
        else
        {
            if (name.length() > minimumPrefix)
            {
                prefix = name;
            }
            else
            {
                prefix = PREFIX_TEMP;
            }
            suffix = null;
        }
        try
        {
            final File temp = File.createTempFile(prefix, suffix);
            final OutputStream output = new BufferedOutputStream(new FileOutputStream(temp));
            try
            {
                copy(input, output);
            }
            finally
            {
                output.close();
            }
            return temp;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_TEMP_FILE, name);
        }
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
            else if (path[i] != null && path[i].length() > 0 && !Medias.getSeparator().equals(path[i]))
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
     * Get extension from a string (search first dot).
     * 
     * @param file The filename.
     * @return The extension.
     */
    public static String getExtension(String file)
    {
        String ext = Constant.EMPTY_STRING;
        final int i = file.lastIndexOf(Constant.DOT);
        if (i > 0 && i < file.length() - 1)
        {
            ext = file.substring(i + 1).toLowerCase(Locale.ENGLISH);
        }
        return ext;
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
     * Get all directories existing in the path.
     * 
     * @param path The path to check.
     * @return The directories list.
     */
    public static String[] getDirectoriesList(String path)
    {
        final File directoryMain = new File(path);
        final File[] directories = directoryMain.listFiles();
        if (directories == null)
        {
            return new String[0];
        }

        int directoriesNumber = 0;
        for (final File directory : directories)
        {
            if (directory.isDirectory())
            {
                directoriesNumber++;
            }
        }
        final String[] directoriesFound = new String[directoriesNumber];
        int id = 0;
        for (final File directory : directories)
        {
            if (directory.isDirectory())
            {
                directoriesFound[id] = directory.getName();
                id++;
            }
        }

        return directoriesFound;
    }

    /**
     * Get the files list from directory.
     * 
     * @param directory The directory reference.
     * @return The directory content.
     * @throws LionEngineException If not a directory.
     */
    public static Collection<File> getFiles(File directory) throws LionEngineException
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
     * Get all files existing in the path.
     * 
     * @param path The path to check.
     * @return The files list.
     */
    public static String[] getFilesList(String path)
    {
        final File directory = new File(path);
        final File[] files = directory.listFiles();
        if (files == null)
        {
            return new String[0];
        }

        int filesNumber = 0;
        for (final File file : files)
        {
            if (file.isFile())
            {
                filesNumber++;
            }
        }
        final String[] filesList = new String[filesNumber];
        int id = 0;
        for (final File file : files)
        {
            if (file.isFile())
            {
                filesList[id] = file.getName();
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
        final Collection<File> filesList = new ArrayList<File>(1);
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
        final Collection<File> filesList = new ArrayList<File>(1);
        getFilesByNameRecursive(filesList, path, name);
        return filesList;
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
            final File[] files = directory.listFiles();
            if (files != null)
            {
                for (final File file : files)
                {
                    deleteDirectory(file);
                }
            }
            if (!directory.delete())
            {
                Verbose.warning(UtilFile.class, "deleteDirectory", ERROR_DELETE_DIRECTORY + directory);
            }
        }
        else if (directory.isFile())
        {
            deleteFile(directory);
            Verbose.info(FILE_DELETED + directory);
        }
    }

    /**
     * Delete a file.
     * 
     * @param file The file to delete.
     * @throws LionEngineException If unable to remove file.
     */
    public static void deleteFile(File file) throws LionEngineException
    {
        Check.notNull(file);
        if (!file.delete())
        {
            throw new LionEngineException(ERROR_DELETE_FILE + file);
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
    private static void getFilesByExtensionRecursive(Collection<File> filesList, String path, String extension)
    {
        final File[] files = new File(path).listFiles();
        if (files != null)
        {
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
