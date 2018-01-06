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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * Used to know the number of code line, and number of files in current project.
 */
public final class UtilProjectStats
{
    /** Java file extension. */
    private static final String JAVA_FILE_EXTENSION = "java";
    /** Documentation regex. */
    private static final String REGEX_DOC = "[\\s]*(/|\\*)+.*";
    /** Spaces regex. */
    private static final String REGEX_SPACES = "[\\s]*";
    /** Title. */
    private static final String TITLE = Constant.HUNDRED + "Project statistics: " + Constant.QUOTE;
    /** Files. */
    private static final String FILES = Constant.QUOTE + Constant.NEW_LINE + "Files = ";
    /** Code. */
    private static final String CODE = Constant.NEW_LINE + "Code lines = ";
    /** Documentation. */
    private static final String DOC = Constant.NEW_LINE + "Documentation lines = ";
    /** Empty. */
    private static final String EMPTY = Constant.NEW_LINE + "Empty lines = ";
    /** Total. */
    private static final String TOTAL = Constant.NEW_LINE + "Total lines = ";
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
    public static void start(String sourcesDir)
    {
        numberOfFiles = 0;
        numberOfLinesCode = 0;
        numberOfLinesDoc = 0;
        numberOfLinesEmpty = 0;
        numberOfLines = 0;

        final File mainDir = new File(sourcesDir);
        final String path = mainDir.getAbsolutePath();
        exploreDir(path);

        final StringBuilder builder = new StringBuilder(100);
        builder.append(TITLE)
               .append(path)
               .append(FILES)
               .append(numberOfFiles)
               .append(CODE)
               .append(numberOfLinesCode)
               .append(DOC)
               .append(numberOfLinesDoc)
               .append(EMPTY)
               .append(numberOfLinesEmpty)
               .append(TOTAL)
               .append(numberOfLines)
               .append(Constant.NEW_LINE);

        Verbose.info(builder.toString());
    }

    /**
     * Count lines of file.
     * 
     * @param fileName The file name.
     */
    public static void countFileLines(String fileName)
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),
                                                                          Constant.UTF_8)))
        {
            boolean stop = false;
            while (!stop)
            {
                final String line = in.readLine();
                if (line != null)
                {
                    countLineTypes(line);
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
            Verbose.exception(exception);
        }
    }

    /**
     * Count the line type.
     * 
     * @param line The line reference.
     */
    private static void countLineTypes(String line)
    {
        if (line.matches(REGEX_DOC))
        {
            numberOfLinesDoc++;
        }
        else if (line.matches(REGEX_SPACES))
        {
            numberOfLinesEmpty++;
        }
        else
        {
            numberOfLinesCode++;
        }
        numberOfLines++;
    }

    /**
     * Check each directory.
     * 
     * @param dirName The current directory.
     * @throws LionEngineException If directory is not valid.
     */
    private static void exploreDir(String dirName)
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
            else
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
