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
import java.nio.charset.StandardCharsets;

import com.b3dgs.lionengine.core.Verbose;

/**
 * Used to know the number of code line, and number of files in current project.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilProjectStats
{
    /** Number of files. */
    private static int numberOfFiles;
    /** Number of lines. */
    private static int numberOfLines;

    /**
     * Start statistics analysis from input directory.
     * 
     * @param sourcesDir The specified directory to analyze.
     */
    public static void start(String sourcesDir)
    {
        numberOfFiles = 0;
        numberOfLines = 0;

        final File mainDir = new File(sourcesDir + File.separator);
        exploreDir(mainDir.getAbsolutePath());

        final StringBuilder builder = new StringBuilder("Project statistics:\n");
        builder.append("Number of files: ").append(numberOfFiles).append("\n");
        builder.append("Number of lines: ").append(numberOfLines).append("\n");
        Verbose.info(builder.toString());
    }

    /**
     * Check each directory.
     * 
     * @param dirName The current directory.
     */
    private static void exploreDir(String dirName)
    {
        final File dir = new File(dirName);
        final File[] files = dir.listFiles();

        for (final File current : files)
        {
            if (current.isDirectory())
            {
                exploreDir(current.getAbsolutePath());
            }
            else if (current.isFile())
            {
                final String filename = current.getAbsolutePath();
                if (getExtension(filename).equals("java"))
                {
                    countFileLines(filename);
                }
            }
        }

    }

    /**
     * Count lines of file.
     * 
     * @param fileName The file name.
     */
    private static void countFileLines(String fileName)
    {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),
                StandardCharsets.UTF_8)))
        {
            String s;
            boolean stop = false;
            while (!stop)
            {
                s = in.readLine();
                if (s != null)
                {
                    numberOfLines++;
                }
                else
                {
                    stop = true;
                }
            }
        }
        catch (final IOException exception)
        {
            Verbose.exception(UtilProjectStats.class, "countFileLines", exception);
        }
        numberOfFiles++;
    }

    /**
     * Get file extension.
     * 
     * @param file The file.
     * @return The extension.
     */
    private static String getExtension(String file)
    {
        return file.substring(file.lastIndexOf('.') + 1);
    }

    /**
     * Private constructor.
     */
    private UtilProjectStats()
    {
        throw new RuntimeException();
    }
}
