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

/**
 * Read BMP image info.
 */
final class ImageHeaderBmp extends ImageHeaderReaderAbstract
{
    /** Header 1. */
    private static final int HEADER_1 = 66;
    /** Header 2. */
    private static final int HEADER_2 = 77;

    /**
     * Internal constructor.
     */
    ImageHeaderBmp()
    {
        super(HEADER_1, HEADER_2);
    }

    /*
     * ImageHeaderReader
     */

    @Override
    public ImageHeader readHeader(InputStream input) throws IOException
    {
        final int toSkip = 15 + 3;
        long skipped = input.skip(toSkip);
        checkSkippedError(skipped, toSkip);

        final int width = readInt(input, 2, false);
        skipped = input.skip(2);
        checkSkippedError(skipped, 2);

        final int height = readInt(input, 2, false);

        return new ImageHeaderModel(width, height, ImageFormat.BMP);
    }
}
