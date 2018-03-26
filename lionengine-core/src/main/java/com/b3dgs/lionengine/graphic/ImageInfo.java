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
package com.b3dgs.lionengine.graphic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Get quick information from an image without reading all data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class ImageInfo
{
    /** Available formats. */
    private static final Collection<ImageHeaderReader> FORMATS = Arrays.asList(new ImageHeaderPng(),
                                                                               new ImageHeaderBmp(),
                                                                               new ImageHeaderGif(),
                                                                               new ImageHeaderTiff(),
                                                                               new ImageHeaderJpg());
    /** Read error. */
    private static final String ERROR_READ = "Cannot read image information";

    /**
     * Get the image info of the specified image media.
     * 
     * @param media The media (must not be <code>null</code>).
     * @return The image info instance.
     * @throws LionEngineException If media is <code>null</code> or cannot be read.
     */
    public static ImageHeader get(Media media)
    {
        Check.notNull(media);

        for (final ImageHeaderReader reader : FORMATS)
        {
            if (reader.is(media))
            {
                return read(media, reader);
            }
        }
        throw new LionEngineException(media, ERROR_READ);
    }

    /**
     * Check if the media is a valid image.
     * 
     * @param media The media reference (can be <code>null</code>).
     * @return <code>true</code> if is supported image, <code>false</code> else.
     */
    // CHECKSTYLE IGNORE LINE: ReturnCount
    public static boolean isImage(Media media)
    {
        try
        {
            for (final ImageHeaderReader reader : FORMATS)
            {
                if (reader.is(media))
                {
                    return true;
                }
            }
            return false;
        }
        catch (@SuppressWarnings("unused") final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Read image header.
     * 
     * @param media The media to read (must not be <code>null</code>).
     * @param reader The header reader (must not be <code>null</code>).
     * @return The header read.
     * @throws LionEngineException If invalid arguments or cannot be read.
     */
    private static ImageHeader read(Media media, ImageHeaderReader reader)
    {
        Check.notNull(media);
        Check.notNull(reader);

        try (InputStream input = media.getInputStream())
        {
            return reader.readHeader(input);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_READ);
        }
    }

    /**
     * Private constructor.
     */
    private ImageInfo()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
