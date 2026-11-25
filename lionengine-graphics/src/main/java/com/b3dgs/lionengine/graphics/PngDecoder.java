/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;

/**
 * Built-in pure-Java PNG decoder, identical on every platform and in headless tests (ADR-0026, ADR-0033,
 * REQ-SPRITE-009).
 *
 * <p>
 * Accepted: non-interlaced images of every color type (grayscale, RGB, palette, grayscale with alpha, RGBA) at every
 * bit depth of the PNG specification: 1, 2, 4 and 8 bits per sample, and 16 bits reduced to 8 by keeping the most
 * significant byte; transparency from {@code tRNS} (alpha of palette entries, or one transparent gray or RGB color,
 * compared at the depth of the image). Samples of 1, 2 and 4 bits are scaled to 8 bits, so that the largest one
 * becomes {@code 255}. Gamma, color profiles and the other ancillary chunks are ignored.
 * </p>
 *
 * <p>
 * Rejected with an {@link ImageDecodingException}: a stream that is not a PNG image, a truncated or corrupted one (the
 * CRC of every chunk is checked), an invalid header, color type or bit depth, Adam7 interlacing, an animated PNG
 * (chunks {@code acTL}, {@code fcTL}, {@code fdAT}), a palette image without palette or with an index outside it, an
 * unknown critical chunk, and an image of more than {@link Integer#MAX_VALUE} pixels (the size of one
 * {@link PixelBuffer}).
 * </p>
 *
 * <p>
 * The decoder reads the stream up to the {@code IEND} chunk and does not close it. It decodes row by row, holding two
 * rows besides the result, and uses {@link Inflater} from {@code java.util.zip}, available on Android (ADR-0026). It
 * keeps no state: one instance may decode on several threads at once (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
public final class PngDecoder implements ImageDecoder
{
    /** Error message of a {@code null} stream. */
    private static final String ERROR_INPUT = "Input null";
    /** Error message of an animated image. */
    private static final String ERROR_ANIMATED = "Animated PNG (APNG) not supported";
    /** Error message of a critical chunk out of place or unknown. */
    private static final String ERROR_CRITICAL = "PNG chunk unexpected: ";

    /**
     * Reads the chunks before the image data: palette, transparency, and skipped ancillary chunks.
     *
     * @param chunks The chunks, after the header.
     * @param colors The converter that receives the palette and the transparency.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If a chunk is invalid, unexpected or unsupported, or the image has no data.
     */
    private static void readBeforeData(PngChunks chunks, PngColors colors) throws IOException
    {
        int type = chunks.next();
        while (type != PngChunks.IDAT)
        {
            if (type == PngChunks.PLTE)
            {
                colors.readPalette(chunks);
            }
            else if (type == PngChunks.TRNS)
            {
                colors.readTransparency(chunks);
            }
            else
            {
                checkSkipped(type);
            }
            chunks.end();
            type = chunks.next();
        }
        colors.checkComplete();
    }

    /**
     * Decodes the rows of the image.
     *
     * @param chunks The chunks, at the data of the first {@code IDAT} chunk.
     * @param header The header.
     * @param colors The converter.
     * @return The pixels.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the image data is invalid.
     */
    private static PixelBuffer readData(PngChunks chunks, PngHeader header, PngColors colors) throws IOException
    {
        final PixelBuffer pixels = new PixelBuffer(header.width(), header.height());
        final int[] row = new int[header.width()];
        final Inflater inflater = new Inflater();
        try
        {
            final PngScanlines scanlines = new PngScanlines(chunks, inflater, header);
            for (int y = 0; y < header.height(); y++)
            {
                colors.convert(scanlines.next(), row);
                pixels.setRow(y, row);
            }
        }
        finally
        {
            inflater.end();
        }
        return pixels;
    }

    /**
     * Reads the chunks after the image data, up to the {@code IEND} chunk included.
     *
     * @param chunks The chunks, in the last {@code IDAT} chunk read.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If a chunk is unexpected or unsupported, or the stream ends.
     */
    private static void readAfterData(PngChunks chunks) throws IOException
    {
        chunks.end();
        int type = chunks.next();
        while (type != PngChunks.IEND)
        {
            if (type != PngChunks.IDAT)
            {
                checkSkipped(type);
            }
            chunks.end();
            type = chunks.next();
        }
        chunks.end();
    }

    /**
     * Checks that a chunk can be skipped.
     *
     * @param type The chunk type.
     * @throws ImageDecodingException If the chunk is critical, or belongs to an animated image.
     */
    private static void checkSkipped(int type)
    {
        if (type == PngChunks.ACTL || type == PngChunks.FCTL || type == PngChunks.FDAT)
        {
            throw new ImageDecodingException(ERROR_ANIMATED);
        }
        if (!PngChunks.isAncillary(type))
        {
            throw new ImageDecodingException(ERROR_CRITICAL + PngChunks.name(type));
        }
    }

    /**
     * Creates the decoder.
     */
    public PngDecoder()
    {
        super();
    }

    @Override
    public PixelBuffer decode(InputStream input) throws IOException
    {
        if (input == null)
        {
            throw new IllegalArgumentException(ERROR_INPUT);
        }
        final PngChunks chunks = new PngChunks(input);
        chunks.readSignature();
        final PngHeader header = PngHeader.read(chunks);
        final PngColors colors = new PngColors(header);
        readBeforeData(chunks, colors);
        final PixelBuffer pixels = readData(chunks, header, colors);
        readAfterData(chunks);
        return pixels;
    }
}
