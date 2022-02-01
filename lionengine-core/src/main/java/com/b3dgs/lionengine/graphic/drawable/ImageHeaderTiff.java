/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.graphic.ImageFormat;

/**
 * Read TIFF image info.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
final class ImageHeaderTiff extends ImageHeaderReaderAbstract
{
    /** Header size. */
    private static final int HEADER_SIZE = 4;
    /** Header 1. */
    private static final int HEADER_M = 'M';
    /** Header 2. */
    private static final int HEADER_I = 'I';
    /** Header 3. */
    private static final int HEADER_0 = 0;
    /** Header 4. */
    private static final int HEADER_42 = 42;
    /** Val offset field 1. */
    private static final int VAL_OFFSET_FIELD_1 = 3;
    /** Val offset field 2. */
    private static final int VAL_OFFSET_FIELD_2 = 8;
    /** Val offset bytes. */
    private static final int VAL_OFFSET_BYTES = 4;
    /** Tag width. */
    private static final int TAG_WIDTH = 256;
    /** Tag width. */
    private static final int TAG_HEIGHT = 257;

    /** Invalid Jpg. */
    private static final String ERROR_TIFF = "Invalid TIFF file !";

    /**
     * Internal constructor.
     */
    ImageHeaderTiff()
    {
        super(() -> new int[]
        {
            HEADER_M, HEADER_M, HEADER_0, HEADER_42
        }, () -> new int[]
        {
            HEADER_I, HEADER_I, HEADER_42, HEADER_0
        });
    }

    /*
     * ImageHeaderReader
     */

    @Override
    public ImageHeader readHeader(InputStream input) throws IOException
    {
        final long toSkip = 8L;
        final boolean bigEndian = 'M' == input.read();
        checkSkippedError(input.skip(HEADER_SIZE - 1L), HEADER_SIZE - 1);
        final int ifd = readInt(input, 4, bigEndian);
        input.skip(ifd - toSkip);
        // checkSkippedError(skipped, ifd - toSkip); fail when reading from JAR, not needed
        final int entries = readInt(input, 2, bigEndian);

        int width = -1;
        int height = -1;
        for (int i = 1; i <= entries; i++)
        {
            final int tag = readInt(input, 2, bigEndian);
            final int fieldType = readInt(input, 2, bigEndian);
            readInt(input, VAL_OFFSET_BYTES, bigEndian);
            final int valOffset;
            if (VAL_OFFSET_FIELD_1 == fieldType || VAL_OFFSET_FIELD_2 == fieldType)
            {
                valOffset = readInt(input, 2, bigEndian);
                final long skipped = input.skip(2);
                checkSkippedError(skipped, 2);
            }
            else
            {
                valOffset = readInt(input, VAL_OFFSET_BYTES, bigEndian);
            }
            if (TAG_WIDTH == tag)
            {
                width = valOffset;
            }
            else if (TAG_HEIGHT == tag)
            {
                height = valOffset;
            }
            if (-1 != width && -1 != height)
            {
                return new ImageHeaderModel(width, height, ImageFormat.TIFF);
            }
        }
        throw new IOException(ERROR_TIFF);
    }
}
