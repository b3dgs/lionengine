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
package com.b3dgs.lionengine.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.b3dgs.lionengine.Verbose;

/**
 * Used to know the number of code line, and number of files in current project.
 */
public final class UtilityProjectStats
{
    /** Number of files. */
    private static int numberOfFiles;
    /** Number of lines. */
    private static int numberOfLines;

    /**
     * Start statistics analysis from input directory.
     * 
     * @param sourcesDir The specified directory to analyse.
     */
    public static void start(String sourcesDir)
    {
        UtilityProjectStats.numberOfFiles = 0;
        UtilityProjectStats.numberOfLines = 0;

        final File mainDir = new File(sourcesDir + java.io.File.separator);
        UtilityProjectStats.exploreDir(mainDir.getAbsolutePath());

        final StringBuilder builder = new StringBuilder("Project statistics:\n");
        builder.append("Number of files: ").append(UtilityProjectStats.numberOfFiles).append("\n");
        builder.append("Number of lines: ").append(UtilityProjectStats.numberOfLines).append("\n");
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
                UtilityProjectStats.exploreDir(current.getAbsolutePath());
            }
            else if (current.isFile())
            {
                final String filename = current.getAbsolutePath();
                if (UtilityProjectStats.getExtension(filename).equals("java"))
                {
                    UtilityProjectStats.countFileLines(filename);
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
                Charset.forName("UTF-8")));)
        {
            String s;
            do
            {
                s = in.readLine();
                UtilityProjectStats.numberOfLines++;
            } while (s != null);
        }
        catch (final IOException exception)
        {
            Verbose.warning(UtilityProjectStats.class, "countFileLines", exception.getMessage());
        }
        UtilityProjectStats.numberOfFiles++;
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
    private UtilityProjectStats()
    {
        throw new RuntimeException();
    }
}
