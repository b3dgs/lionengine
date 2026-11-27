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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

/**
 * Contract suite of {@link ImageDecoder} (ADR-0007, ADR-0019, ADR-0026, ADR-0045, ADR-0054, REQ-SPRITE-009).
 *
 * <p>
 * Published in the {@code graphics} test JAR: every decoder binds it by extending this class, creating the decoder and
 * giving one valid image of its format with the pixels it must produce, so that all decoders behave the same for the
 * code that loads images through them: a new buffer per call, the stream left open, read failures reported as
 * {@link IOException}, invalid content as {@link ImageDecodingException}, the pixel limit given at creation, and
 * decoding on several threads at once.
 * </p>
 */
public abstract class ImageDecoderContract
{
    /** Number of threads of the concurrency test. */
    private static final int THREADS = 2;
    /** Number of decodings per thread in the concurrency test. */
    private static final int DECODINGS = 50;
    /** Time limit of the concurrency test, in seconds. */
    private static final long TIMEOUT_SECONDS = 30L;

    /**
     * Creates the decoder under test, with the default pixel limit of ADR-0054.
     *
     * @return The decoder.
     */
    protected abstract ImageDecoder createDecoder();

    /**
     * Creates the decoder under test with a pixel limit.
     *
     * @param maxPixels The largest pixel count of a decoded image.
     * @return The decoder.
     */
    protected abstract ImageDecoder createDecoder(int maxPixels);

    /**
     * Returns a valid image in the format of the decoder, with at least two different pixels.
     *
     * @return The bytes of the image.
     */
    protected abstract byte[] getImage();

    /**
     * Returns the pixels that {@link #getImage()} decodes to.
     *
     * @return The expected pixels.
     */
    protected abstract PixelBuffer getExpected();

    /**
     * Decodes bytes with a new decoder.
     *
     * @param bytes The bytes.
     * @return The pixels.
     * @throws IOException If reading fails.
     */
    private PixelBuffer decode(byte[] bytes) throws IOException
    {
        return createDecoder().decode(new ByteArrayInputStream(bytes));
    }

    /**
     * Checks that a buffer holds the expected pixels.
     *
     * @param pixels The buffer.
     */
    private void assertExpected(PixelBuffer pixels)
    {
        final PixelBuffer expected = getExpected();
        assertEquals(expected.getWidth(), pixels.getWidth());
        assertEquals(expected.getHeight(), pixels.getHeight());
        assertArrayEquals(expected.copyPixels(), pixels.copyPixels());
    }

    /**
     * The image decodes to the expected pixels.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testDecode() throws IOException
    {
        assertExpected(decode(getImage()));
    }

    /**
     * A decoder decodes an image of exactly its pixel limit, and refuses it with one pixel less (ADR-0054).
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testPixelLimit() throws IOException
    {
        final PixelBuffer expected = getExpected();
        final int pixels = expected.getWidth() * expected.getHeight();
        final ImageDecoder below = createDecoder(pixels - 1);
        final InputStream input = new ByteArrayInputStream(getImage());

        assertExpected(createDecoder(pixels).decode(new ByteArrayInputStream(getImage())));
        assertThrows(ImageDecodingException.class, () -> below.decode(input));
    }

    /**
     * Each call returns a new buffer, owned by the caller: changing it changes nothing in the next decoding.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testNewBuffer() throws IOException
    {
        final ImageDecoder decoder = createDecoder();
        final PixelBuffer first = decoder.decode(new ByteArrayInputStream(getImage()));
        first.setPixel(0, 0, ~first.getPixel(0, 0));
        final PixelBuffer second = decoder.decode(new ByteArrayInputStream(getImage()));

        assertNotSame(first, second);
        assertExpected(second);
    }

    /**
     * The decoder reads the stream but does not close it: the caller does.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testStreamLeftOpen() throws IOException
    {
        final CloseSpy input = new CloseSpy(getImage());
        createDecoder().decode(input);

        assertFalse(input.closed);
    }

    /**
     * A failure of the stream is an {@link IOException}, not a decoding error, so that the caller reports an
     * inaccessible resource (ADR-0007).
     */
    @Test
    void testReadFailure()
    {
        final ImageDecoder decoder = createDecoder();
        final InputStream input = new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException("Read failure");
            }
        };

        assertThrows(IOException.class, () -> decoder.decode(input));
    }

    /**
     * An empty stream is a decoding error.
     */
    @Test
    void testEmpty()
    {
        final ImageDecoder decoder = createDecoder();
        final InputStream input = new ByteArrayInputStream(new byte[0]);

        assertThrows(ImageDecodingException.class, () -> decoder.decode(input));
    }

    /**
     * Content that is not an image is a decoding error.
     */
    @Test
    void testNotAnImage()
    {
        final ImageDecoder decoder = createDecoder();
        final InputStream input = new ByteArrayInputStream("Not an image, only text.".getBytes(StandardCharsets.UTF_8));

        assertThrows(ImageDecodingException.class, () -> decoder.decode(input));
    }

    /**
     * A {@code null} stream is rejected.
     */
    @Test
    void testNull()
    {
        final ImageDecoder decoder = createDecoder();

        assertThrows(IllegalArgumentException.class, () -> decoder.decode(null));
    }

    /**
     * One decoder decodes on two threads at once, as loading workers do (ADR-0019).
     *
     * @throws InterruptedException If interrupted.
     * @throws ExecutionException If a decoding fails.
     * @throws TimeoutException If the decodings take too long.
     */
    @Test
    void testConcurrent() throws InterruptedException, ExecutionException, TimeoutException
    {
        final ImageDecoder decoder = createDecoder();
        final byte[] image = getImage();
        final Callable<List<PixelBuffer>> task = () ->
        {
            final List<PixelBuffer> results = new ArrayList<>();
            for (int i = 0; i < DECODINGS; i++)
            {
                results.add(decoder.decode(new ByteArrayInputStream(image)));
            }
            return results;
        };
        final ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        try
        {
            final List<Future<List<PixelBuffer>>> futures = new ArrayList<>();
            for (int i = 0; i < THREADS; i++)
            {
                futures.add(executor.submit(task));
            }
            for (final Future<List<PixelBuffer>> future : futures)
            {
                for (final PixelBuffer pixels : future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS))
                {
                    assertExpected(pixels);
                }
            }
        }
        finally
        {
            executor.shutdownNow();
        }
    }

    /**
     * Stream that records whether it was closed.
     */
    private static final class CloseSpy extends ByteArrayInputStream
    {
        /** {@code true} once closed. */
        private boolean closed;

        /**
         * Creates the stream.
         *
         * @param bytes The content.
         */
        CloseSpy(byte[] bytes)
        {
            super(bytes);
        }

        @Override
        public void close() throws IOException
        {
            closed = true;
            super.close();
        }
    }
}
