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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.b3dgs.lionengine.core.Verbose;

/**
 * Used to know the number of code line, and number of files in current project.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilProjectStats
{
    /** Java file extension. */
    private static final String JAVA_FILE_EXTENSION = "java";
    /** Documentation regex. */
    private static final String REGEX_DOC = "[\\s]*(/|\\*)+.*";
    /** Spaces regex. */
    private static final String REGEX_SPACES = "[\\s]*";
    /** Error directory. */
    private static final String ERROR_DIR = "Not a directory: ";

    /** Number of files. */
    private static int numberOfFiles;
    /** Number of code lines. */
    private static int numberOfLinesCode;
    /** Number of doc lines. */
    private static int numberOfLinesDoc;
    /** Number of empty lines. */
    private static int numberOfLinesEmpty;
    /** Number of lines. */
    private static int numberOfLines;

    /**
     * Start statistics analysis from input directory.
     * 
     * @param sourcesDir The specified directory to analyze.
     * @throws LionEngineException If an error occurred on check.
     */
    public static void start(String sourcesDir) throws LionEngineException
    {
        numberOfFiles = 0;
        numberOfLinesCode = 0;
        numberOfLinesDoc = 0;
        numberOfLinesEmpty = 0;
        numberOfLines = 0;

        final File mainDir = new File(sourcesDir);
        try
        {
            final String path = mainDir.getCanonicalPath();
            exploreDir(path);

            final StringBuilder builder = new StringBuilder("Project statistics: ");
            builder.append(Constant.QUOTE).append(path).append(Constant.QUOTE).append(Constant.NEW_LINE);
            builder.append("Files = ").append(numberOfFiles).append(Constant.NEW_LINE);
            builder.append("Code lines = ").append(numberOfLinesCode).append(Constant.NEW_LINE);
            builder.append("Documentation lines = ").append(numberOfLinesDoc).append(Constant.NEW_LINE);
            builder.append("Empty lines = ").append(numberOfLinesEmpty).append(Constant.NEW_LINE);
            builder.append("Total lines = ").append(numberOfLines).append(Constant.NEW_LINE);
            Verbose.info(builder.toString());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_DIR, sourcesDir);
        }
    }

    /**
     * Count lines of file.
     * 
     * @param fileName The file name.
     */
    public static void countFileLines(String fileName)
    {
        final String name = "countFileLines";
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Constant.UTF_8));
            String s;
            boolean stop = false;
            while (!stop)
            {
                s = in.readLine();
                if (s != null)
                {
                    if (s.matches(REGEX_DOC))
                    {
                        numberOfLinesDoc++;
                    }
                    else if (s.matches(REGEX_SPACES))
                    {
                        numberOfLinesEmpty++;
                    }
                    else
                    {
                        numberOfLinesCode++;
                    }
                    numberOfLines++;
                }
                else
                {
                    stop = true;
                }
            }
            numberOfFiles++;
        }
        catch (final IOException exception)
        {
            Verbose.exception(UtilProjectStats.class, name, exception);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (final IOException exception2)
                {
                    Verbose.exception(UtilProjectStats.class, name, exception2);
                }
            }
        }
    }

    /**
     * Check each directory.
     * 
     * @param dirName The current directory.
     * @throws LionEngineException If directory is not valid.
     */
    private static void exploreDir(String dirName) throws LionEngineException
    {
        final File dir = new File(dirName);
        final File[] files = dir.listFiles();

        if (files == null)
        {
            throw new LionEngineException(ERROR_DIR, dirName);
        }
        for (final File current : files)
        {
            if (current.isDirectory())
            {
                exploreDir(current.getAbsolutePath());
            }
            else if (current.isFile())
            {
                final String filename = current.getAbsolutePath();
                if (JAVA_FILE_EXTENSION.equals(getExtension(filename)))
                {
                    countFileLines(filename);
                }
            }
        }
    }

    /**
     * Get file extension.
     * 
     * @param file The file.
     * @return The extension.
     */
    private static String getExtension(String file)
    {
        return file.substring(file.lastIndexOf(Constant.DOT) + 1);
    }

    /**
     * Private constructor.
     */
    private UtilProjectStats()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
