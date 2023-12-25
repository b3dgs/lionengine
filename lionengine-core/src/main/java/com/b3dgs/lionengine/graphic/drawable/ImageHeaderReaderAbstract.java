/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphic.drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;

/**
 * Image header reader interface.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
abstract class ImageHeaderReaderAbstract implements ImageHeaderReader
{
    /** Message skipped. */
    private static final String MESSAGE_SKIPPED = "Skipped ";
    /** Message bytes instead of. */
    private static final String MESSAGE_BYTES_INSTEAD_OF = " bytes instead of ";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageHeaderReaderAbstract.class);

    /**
     * Read integer in image data.
     * 
     * @param input The stream.
     * @param bytesNumber The number of bytes to read.
     * @param bigEndian The big endian flag.
     * @return The integer read.
     * @throws IOException if error on reading.
     */
    protected static int readInt(InputStream input, int bytesNumber, boolean bigEndian) throws IOException
    {
        final int oneByte = 8;
        int ret = 0;
        int sv;
        if (bigEndian)
        {
            sv = (bytesNumber - 1) * oneByte;
        }
        else
        {
            sv = 0;
        }
        final int cnt;
        if (bigEndian)
        {
            cnt = -oneByte;
        }
        else
        {
            cnt = oneByte;
        }
        for (int i = 0; i < bytesNumber; i++)
        {
            ret |= input.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    /**
     * Skipped message error.
     * 
     * @param skipped The skipped value.
     * @param instead The instead value.
     * @throws IOException If not skipped the right number of bytes.
     */
    protected static void checkSkippedError(long skipped, int instead) throws IOException
    {
        if (skipped != instead)
        {
            throw new IOException(MESSAGE_SKIPPED + skipped + MESSAGE_BYTES_INSTEAD_OF + instead);
        }
    }

    /**
     * Check header data.
     * 
     * @param input The input stream to check.
     * @param header The expected header.
     * @return <code>true</code> if right header, <code>false</code> else.
     * @throws IOException If unable to read header.
     */
    private static boolean checkHeader(InputStream input, int[] header) throws IOException
    {
        for (final int b : header)
        {
            if (b != input.read())
            {
                return false;
            }
        }
        return true;
    }

    /** Format header. */
    private final Collection<HeaderProvider> providers;

    /**
     * Create header reader.
     * 
     * @param headers The associated headers.
     */
    protected ImageHeaderReaderAbstract(int... headers)
    {
        this(() -> headers);
    }

    /**
     * Create header reader.
     * 
     * @param providers The associated headers.
     */
    protected ImageHeaderReaderAbstract(HeaderProvider... providers)
    {
        this.providers = Arrays.asList(providers);
    }

    @Override
    public boolean is(Media media)
    {
        Check.notNull(media);

        for (final HeaderProvider provider : providers)
        {
            try (InputStream input = media.getInputStream())
            {
                if (checkHeader(input, provider.getHeader()))
                {
                    return true;
                }
            }
            catch (final IOException exception)
            {
                LOGGER.error("is image error", exception);
            }
        }
        return false;
    }

    /**
     * Provide image header data.
     */
    protected interface HeaderProvider
    {
        /**
         * Return header data.
         * 
         * @return The image header data.
         */
        int[] getHeader();
    }
}
