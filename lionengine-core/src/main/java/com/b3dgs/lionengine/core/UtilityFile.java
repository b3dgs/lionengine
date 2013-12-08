/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Locale;

/**
 * Static functions giving informations related to files and directory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilityFile
{
    /** System temp directory. */
    public static final String SYSTEM_TEMP_DIR = UtilityFile.assignSystemTempDirectory();
    
    /** Engine temporary directory. */
    private static String tmpDir;
    
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
     * Get a file extension.
     * 
     * @param file The file.
     * @return The extension.
     */
    public static String getExtension(File file)
    {
        return UtilityFile.getExtension(file.getName());
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
        int i = path.lastIndexOf(Media.getSeparator());
        if (i == -1)
        {
            i = path.lastIndexOf(Media.getSeparator());
        }
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
     * Get all files existing in the path.
     * 
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     * @return The files list.
     */
    public static String[] getFilesList(String path, String extension)
    {
        final File file = new File(path);
        if (!file.exists())
        {
            return new String[0];
        }

        final File[] files = file.listFiles();
        int numberOfFiles = 0;
        for (final File file2 : files)
        {
            if (file2.isFile() && UtilityFile.getExtension(file2).equals(extension))
            {
                numberOfFiles++;
            }
        }

        final String[] filesList = new String[numberOfFiles];
        for (int i = 0, id = 0; i < files.length; i++)
        {
            if (files[i].isFile() && UtilityFile.getExtension(files[i]).equals(extension))
            {
                filesList[id] = files[i].getName();
                id++;
            }
        }

        return filesList;
    }

    /**
     * Delete a directory and all of its content (be careful, it will erase all children, including child directory).
     * 
     * @param directory The directory to delete with all of its content.
     */
    public static void deleteDirectory(File directory)
    {
        if (directory.isDirectory())
        {
            final String[] children = directory.list();
            for (final String element : children)
            {
                UtilityFile.deleteDirectory(new File(directory, element));
            }
            if (!directory.delete())
            {
                Verbose.warning(UtilityFile.class, "deleteDirectory", "Directory not deleted: " + directory);
            }
        }
        else if (directory.isFile())
        {
            UtilityFile.deleteFile(directory);
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
        if (file.isFile() && !file.delete())
        {
            Verbose.warning(UtilityFile.class, "deleteDir", "File not deleted: " + file);
        }
    }
    
    /**
     * Get the program temp directory.
     * @return The program temp directory.
     */
    public static String getTempDir()
    {
        return UtilityFile.tmpDir;
    }

    /**
     * Set the temporary directory name from the program name.
     * 
     * @param programName The program name.
     */
    static void setTempDirectory(String programName)
    {
        final String dir = programName.replace(' ', '_').replaceAll("[\\W]", "").toLowerCase(Locale.getDefault());
        UtilityFile.tmpDir = Media.getPath(UtilityFile.SYSTEM_TEMP_DIR, dir);
    }
    
    /**
     * Get the system temp directory.
     * 
     * @return The system temp directory.
     */
    private static String assignSystemTempDirectory()
    {
        try
        {
            return System.getProperty("java.io.tmpdir");
        }
        catch (final SecurityException exception)
        {
            return "";
        }
    }
    
    /**
     * Private constructor.
     */
    private UtilityFile()
    {
        throw new RuntimeException();
    }
}
