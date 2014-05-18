/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.bind.ValidationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.EngineCore;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Static functions giving informations related to files and directory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilFile
{
    /** System temp directory. */
    public static final String SYSTEM_TEMP_DIR = EngineCore.getSystemProperty("java.io.tmpdir");

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
     * Construct a usable path using a list of string, automatically separated by the portable separator. The
     * constructed path will use local system file separator.
     * 
     * @param path The list of folders (if has) and file.
     * @return The full media path.
     */
    public static String getPath(String... path)
    {
        return UtilFile.getPathSeparator(File.separator, path);
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
        return UtilFile.getExtension(file.getName());
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
        return UtilFile.getExtension(file).equals(extension);
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
        final int i = path.lastIndexOf(Core.MEDIA.getSeparator());
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
    public static List<File> getFilesByExtension(String path, String extension)
    {
        final List<File> filesList = new ArrayList<>(1);
        UtilFile.getFilesByExtensionRecursive(filesList, path, extension);
        return filesList;
    }

    /**
     * Get all files existing in the path considering the extension.
     * 
     * @param filesList The files list.
     * @param path The path to check.
     * @param extension The extension (without dot; eg: png).
     */
    private static void getFilesByExtensionRecursive(List<File> filesList, String path, String extension)
    {
        final File file = new File(path);
        if (file.exists())
        {
            final File[] files = file.listFiles();
            if (files != null)
            {
                for (final File content : files)
                {
                    if (content.isDirectory())
                    {
                        UtilFile.getFilesByExtensionRecursive(filesList, content.getPath(), extension);
                    }
                    if (content.isFile() && extension.equals(UtilFile.getExtension(content)))
                    {
                        filesList.add(content);
                    }
                }
            }
        }
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
        final List<File> filesList = new ArrayList<>(1);
        UtilFile.getFilesByNameRecursive(filesList, path, name);
        return filesList;
    }

    /**
     * Get all files existing in the path with the specified name.
     * 
     * @param filesList The files list.
     * @param path The path to check.
     * @param name The file name.
     */
    private static void getFilesByNameRecursive(List<File> filesList, File path, String name)
    {
        for (final File file : path.listFiles())
        {
            if (file.isFile() && file.getName().equals(name))
            {
                filesList.add(file);
            }
            else if (file.isDirectory())
            {
                UtilFile.getFilesByNameRecursive(filesList, file, name);
            }
        }
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
        if (directory.isDirectory())
        {
            final String[] children = directory.list();
            for (final String element : children)
            {
                UtilFile.deleteDirectory(new File(directory, element));
            }
            if (!directory.delete())
            {
                Verbose.warning(UtilFile.class, "deleteDirectory", "Directory not deleted: " + directory);
            }
        }
        else if (directory.isFile())
        {
            UtilFile.deleteFile(directory);
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
            Verbose.warning(UtilFile.class, "deleteDir", "File not deleted: " + file);
        }
    }

    /**
     * Get the program temp directory.
     * 
     * @return The program temp directory.
     */
    public static String getTempDir()
    {
        return UtilFile.tmpDir;
    }

    /**
     * Set the temporary directory name from the program name.
     * 
     * @param programName The program name.
     */
    public static void setTempDirectory(String programName)
    {
        if (programName != null)
        {
            final String dir = programName.replace(' ', '_').replaceAll("[\\W]", "").toLowerCase(Locale.getDefault());
            UtilFile.tmpDir = UtilFile.getPath(UtilFile.SYSTEM_TEMP_DIR, dir);
        }
        else
        {
            UtilFile.tmpDir = null;
        }
    }

    /**
     * Check if the XML is valid, regarding the XSD file.
     * 
     * @param xsd The XSD file.
     * @param xml The XML file.
     * @throws ValidationException If an error occurred when validating the XML.
     */
    public static void validateXml(URI xsd, File xml) throws ValidationException
    {
        try
        {
            final URL schemaFile = xsd.toURL();
            final Source xmlFile = new StreamSource(xml);
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Schema schema = schemaFactory.newSchema(schemaFile);
            final Validator validator = schema.newValidator();
            validator.validate(xmlFile);
        }
        catch (final SAXException
                     | IOException exception)
        {
            throw new ValidationException(exception);
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
