/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * Tools related to files and directories handling.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilStream
{
    /** Error temporary file. */
    private static final String ERROR_TEMP_FILE = "Unable to create temporary file for: ";
    /** Close stream failure. */
    private static final String ERROR_CLOSE_STREAM = "Unable to close stream !";
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
    public static void copy(InputStream source, OutputStream destination) throws IOException
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
     * Close and log exception if unable to close.
     * 
     * @param closeable The closeable to close.
     */
    public static void safeClose(Closeable closeable)
    {
        if (closeable != null)
        {
            try
            {
                closeable.close();
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception, ERROR_CLOSE_STREAM);
            }
        }
    }

    /**
     * Close and log exception if unable to close.
     * 
     * @param closeable The closeable to close.
     * @throws IOException If unable to close.
     */
    public static void close(Closeable closeable) throws IOException
    {
        if (closeable != null)
        {
            closeable.close();
        }
    }

    /**
     * Get of full copy of the input stream stored in a temporary file.
     * 
     * @param name The file name reference (to have a similar temporary file name).
     * @param input The input stream reference.
     * @return The temporary file created with copied content from stream.
     * @throws LionEngineException If <code>null</code> arguments or invalid stream.
     */
    public static File getCopy(String name, InputStream input)
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
     * Private constructor.
     */
    private UtilStream()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
