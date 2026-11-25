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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.ImageDecodingException;
import com.b3dgs.lionengine.graphics.PixelBuffer;
import com.b3dgs.lionengine.graphics.PngDecoder;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@code graphics} on Android API 21 (ADR-0044): the pixel buffer, the PNG decoder with its size limits
 * (ADR-0054), whose {@code java.util.zip.Inflater} and {@code CRC32} are verified by the probes, and the image cache
 * over the memory provider. The images are written here with the API 21 methods only, as the JVM fixture
 * {@code PngWriter} writes them; expected values are the ones of the JVM tests (PixelBufferTest, PngDecoderTest,
 * PngDecoderContractTest, ImageCacheTest).
 */
@RunWith(AndroidJUnit4.class)
public final class GraphicsTest
{
    /** Color type of grayscale images. */
    private static final int GRAY = 0;
    /** Color type of RGB images. */
    private static final int RGB = 2;
    /** Color type of palette images. */
    private static final int PALETTE = 3;
    /** Color type of RGBA images. */
    private static final int RGBA = 6;
    /** Number of filter types; row {@code y} uses the filter type {@code y % FILTERS}. */
    private static final int FILTERS = 5;
    /** Signature of a PNG stream. */
    private static final byte[] SIGNATURE =
    {
        (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'
    };

    /**
     * Returns the number of samples of a pixel.
     *
     * @param colorType The color type.
     * @return The number of samples.
     */
    private static int samplesPerPixel(int colorType)
    {
        if (colorType == RGB)
        {
            return 3;
        }
        return colorType == RGBA ? 4 : 1;
    }

    /**
     * Writes a whole chunk.
     *
     * @param output The output.
     * @param type The four letters of the type.
     * @param data The data.
     */
    private static void chunk(ByteArrayOutputStream output, String type, byte[] data)
    {
        final byte[] name = type.getBytes(StandardCharsets.ISO_8859_1);
        final CRC32 crc = new CRC32();
        crc.update(name, 0, name.length);
        crc.update(data, 0, data.length);
        writeInt(output, data.length);
        output.write(name, 0, name.length);
        output.write(data, 0, data.length);
        writeInt(output, (int) crc.getValue());
    }

    /**
     * Writes a big-endian integer.
     *
     * @param output The output.
     * @param value The integer.
     */
    private static void writeInt(ByteArrayOutputStream output, int value)
    {
        output.write(value >>> 24);
        output.write(value >>> 16);
        output.write(value >>> 8);
        output.write(value);
    }

    /**
     * Packs the samples of a row at the bit depth, most significant bits first.
     *
     * @param samples The samples of the image.
     * @param first The index of the first sample of the row.
     * @param count The number of samples of the row.
     * @param depth The bit depth.
     * @param rowBytes The number of bytes of the row.
     * @return The bytes of the row.
     */
    private static byte[] pack(int[] samples, int first, int count, int depth, int rowBytes)
    {
        final byte[] raw = new byte[rowBytes];
        for (int i = 0; i < count; i++)
        {
            final int sample = samples[first + i];
            if (depth == 16)
            {
                raw[2 * i] = (byte) (sample >>> 8);
                raw[2 * i + 1] = (byte) sample;
            }
            else
            {
                final int bit = i * depth;
                raw[bit / 8] |= (byte) (sample << 8 - depth - bit % 8);
            }
        }
        return raw;
    }

    /**
     * Returns the predictor of a filter type.
     *
     * @param type The filter type.
     * @param left The byte on the left.
     * @param up The byte above.
     * @param upLeft The byte above on the left.
     * @return The predictor.
     */
    private static int predictor(int type, int left, int up, int upLeft)
    {
        if (type == 1)
        {
            return left;
        }
        if (type == 2)
        {
            return up;
        }
        if (type == 3)
        {
            return (left + up) / 2;
        }
        if (type == 4)
        {
            final int estimate = left + up - upLeft;
            final int toLeft = Math.abs(estimate - left);
            final int toUp = Math.abs(estimate - up);
            final int toUpLeft = Math.abs(estimate - upLeft);
            if (toLeft <= toUp && toLeft <= toUpLeft)
            {
                return left;
            }
            return toUp <= toUpLeft ? up : upLeft;
        }
        return 0;
    }

    /**
     * Filters the rows, row {@code y} with the filter type {@code y % 5}, and compresses them.
     *
     * @param width The width.
     * @param height The height.
     * @param depth The bit depth.
     * @param colorType The color type.
     * @param samples The samples.
     * @return The zlib stream of the image data.
     */
    private static byte[] imageData(int width, int height, int depth, int colorType, int[] samples)
    {
        final int perRow = width * samplesPerPixel(colorType);
        final int rowBytes = (perRow * depth + 7) / 8;
        final int stride = Math.max(1, samplesPerPixel(colorType) * depth / 8);
        final byte[] rows = new byte[height * (rowBytes + 1)];
        byte[] previous = new byte[rowBytes];
        for (int y = 0; y < height; y++)
        {
            final byte[] raw = pack(samples, y * perRow, perRow, depth, rowBytes);
            final int type = y % FILTERS;
            final int start = y * (rowBytes + 1);
            rows[start] = (byte) type;
            for (int i = 0; i < rowBytes; i++)
            {
                final int left = i >= stride ? raw[i - stride] & 0xFF : 0;
                final int upLeft = i >= stride ? previous[i - stride] & 0xFF : 0;
                rows[start + 1 + i] = (byte) (raw[i] - predictor(type, left, previous[i] & 0xFF, upLeft));
            }
            previous = raw;
        }
        final Deflater deflater = new Deflater();
        deflater.setInput(rows);
        deflater.finish();
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final byte[] buffer = new byte[256];
        while (!deflater.finished())
        {
            output.write(buffer, 0, deflater.deflate(buffer));
        }
        deflater.end();
        return output.toByteArray();
    }

    /**
     * Writes a PNG image.
     *
     * @param width The width.
     * @param height The height.
     * @param depth The bit depth.
     * @param colorType The color type.
     * @param interlace The interlace method.
     * @param palette The palette entries {@code 0xRRGGBB}, {@code null} for none.
     * @param transparency The data of the {@code tRNS} chunk, {@code null} for none.
     * @param samples The samples.
     * @return The bytes of the image.
     */
    private static byte[] png(int width,
                              int height,
                              int depth,
                              int colorType,
                              int interlace,
                              int[] palette,
                              int[] transparency,
                              int... samples)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(SIGNATURE, 0, SIGNATURE.length);
        final ByteArrayOutputStream header = new ByteArrayOutputStream();
        writeInt(header, width);
        writeInt(header, height);
        header.write(depth);
        header.write(colorType);
        header.write(0);
        header.write(0);
        header.write(interlace);
        chunk(output, "IHDR", header.toByteArray());
        if (palette != null)
        {
            final byte[] entries = new byte[palette.length * 3];
            for (int i = 0; i < palette.length; i++)
            {
                entries[i * 3] = (byte) (palette[i] >>> 16);
                entries[i * 3 + 1] = (byte) (palette[i] >>> 8);
                entries[i * 3 + 2] = (byte) palette[i];
            }
            chunk(output, "PLTE", entries);
        }
        if (transparency != null)
        {
            final byte[] data = new byte[transparency.length];
            for (int i = 0; i < data.length; i++)
            {
                data[i] = (byte) transparency[i];
            }
            chunk(output, "tRNS", data);
        }
        chunk(output, "IDAT", imageData(width, height, depth, colorType, samples));
        chunk(output, "IEND", new byte[0]);
        return output.toByteArray();
    }

    /**
     * Decodes bytes.
     *
     * @param bytes The bytes.
     * @return The pixels.
     * @throws IOException If reading fails.
     */
    private static PixelBuffer decode(byte[] bytes) throws IOException
    {
        return new PngDecoder().decode(new ByteArrayInputStream(bytes));
    }

    /**
     * Checks that bytes are rejected with a decoding error.
     *
     * @param bytes The bytes.
     * @return The message of the error.
     */
    private static String assertRejected(byte[] bytes)
    {
        final PngDecoder decoder = new PngDecoder();
        final ImageDecodingException exception = assertThrows(ImageDecodingException.class,
                                                              () -> decoder.decode(new ByteArrayInputStream(bytes)));
        return exception.getMessage();
    }

    /**
     * Creates the test.
     */
    public GraphicsTest()
    {
        super();
    }

    /**
     * A pixel buffer is transparent black, row-major, never shares its arrays, and rejects pixels outside it.
     */
    @Test
    public void testPixelBuffer()
    {
        final PixelBuffer buffer = new PixelBuffer(3, 2);
        buffer.setPixel(0, 0, 0x80112233);
        buffer.setPixel(2, 0, 0xFF445566);
        buffer.setPixel(1, 1, 0x00778899);
        final int[] copy = buffer.copyPixels();
        copy[1] = 8;

        assertArrayEquals(new int[]
        {
            0x80112233, 0, 0xFF445566, 0, 0x00778899, 0
        }, buffer.copyPixels());
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getPixel(3, 0));
        assertThrows(IllegalArgumentException.class, () -> new PixelBuffer(65536, 32768));
    }

    /**
     * An RGBA image with every filter type decodes to its pixels.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testFilters() throws IOException
    {
        final int width = 4;
        final int height = 6;
        final int[] argb = new int[width * height];
        final int[] samples = new int[argb.length * 4];
        for (int i = 0; i < argb.length; i++)
        {
            argb[i] = i * 0x01234567 ^ 0x80F0E0D0;
            samples[i * 4] = argb[i] >>> 16 & 0xFF;
            samples[i * 4 + 1] = argb[i] >>> 8 & 0xFF;
            samples[i * 4 + 2] = argb[i] & 0xFF;
            samples[i * 4 + 3] = argb[i] >>> 24;
        }

        assertArrayEquals(argb, decode(png(width, height, 8, RGBA, 0, null, null, samples)).copyPixels());
    }

    /**
     * Samples below 8 bits are scaled so that the largest is 255, and 16-bit samples keep their most significant
     * byte.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testSampleScaling() throws IOException
    {
        assertArrayEquals(new int[]
        {
            0xFF000000, 0xFFFFFFFF
        }, decode(png(2, 1, 1, GRAY, 0, null, null, 0, 1)).copyPixels());
        assertArrayEquals(new int[]
        {
            0xFF000000, 0xFF555555, 0xFFAAAAAA, 0xFFFFFFFF
        }, decode(png(4, 1, 2, GRAY, 0, null, null, 0, 1, 2, 3)).copyPixels());
        assertArrayEquals(new int[]
        {
            0xFF000000, 0xFF555555, 0xFFFFFFFF
        }, decode(png(3, 1, 4, GRAY, 0, null, null, 0, 5, 15)).copyPixels());
        assertArrayEquals(new int[]
        {
            0xFF121212, 0xFFFFFFFF, 0xFF000000
        }, decode(png(3, 1, 16, GRAY, 0, null, null, 0x12AB, 0xFF00, 0x00FF)).copyPixels());
        assertArrayEquals(new int[]
        {
            0x80123456
        }, decode(png(1, 1, 16, RGBA, 0, null, null, 0x12FF, 0x3400, 0x56AB, 0x80FF)).copyPixels());
    }

    /**
     * Palette alpha and transparent colors of {@code tRNS}, compared at 16 bits before the reduction.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testTransparency() throws IOException
    {
        final int[] palette =
        {
            0x112233, 0x445566, 0x778899, 0xAABBCC
        };
        assertArrayEquals(new int[]
        {
            0x00112233, 0x80445566, 0xFF778899, 0xFFAABBCC
        }, decode(png(4, 1, 2, PALETTE, 0, palette, new int[]
        {
            0x00, 0x80
        }, 0, 1, 2, 3)).copyPixels());
        assertArrayEquals(new int[]
        {
            0xFF121212, 0x00121212
        }, decode(png(2, 1, 16, GRAY, 0, null, new int[]
        {
            0x12, 0xFF
        }, 0x1200, 0x12FF)).copyPixels());
        assertArrayEquals(new int[]
        {
            0xFF123456, 0x00123456
        }, decode(png(2, 1, 16, RGB, 0, null, new int[]
        {
            0x12, 0x00, 0x34, 0x00, 0x56, 0xFF
        }, 0x1200, 0x3400, 0x5600, 0x1200, 0x3400, 0x56FF)).copyPixels());
    }

    /**
     * Interlaced images, other content and every truncation of an image are rejected.
     */
    @Test
    public void testRejected()
    {
        final byte[] image = png(2, 2, 8, GRAY, 0, null, null, 1, 2, 3, 4);

        assertTrue(assertRejected(png(2, 2, 8, GRAY, 1, null, null, 1, 2, 3, 4)).contains("Interlaced"));
        assertTrue(assertRejected("Not an image".getBytes(StandardCharsets.UTF_8)).contains("signature"));
        for (int length = 0; length < image.length; length++)
        {
            assertEquals(String.valueOf(length), ImageDecodingException.class.getName(), rejectedName(image, length));
        }
    }

    /**
     * The size limits of ADR-0054: a width above 1,000,000 pixels, and more pixels than the limit of the decoder, are
     * refused from the header.
     *
     * @throws IOException If reading fails.
     */
    @Test
    public void testSizeLimits() throws IOException
    {
        final byte[] image = png(2, 2, 8, GRAY, 0, null, null, 1, 2, 3, 4);
        final PngDecoder belowLimit = new PngDecoder(3);
        final ByteArrayOutputStream wide = new ByteArrayOutputStream();
        wide.write(SIGNATURE, 0, SIGNATURE.length);
        final ByteArrayOutputStream header = new ByteArrayOutputStream();
        writeInt(header, PngDecoder.MAX_DIMENSION + 1);
        writeInt(header, 1);
        header.write(8);
        header.write(GRAY);
        header.write(0);
        header.write(0);
        header.write(0);
        chunk(wide, "IHDR", header.toByteArray());

        assertEquals(1_000_000, PngDecoder.MAX_DIMENSION);
        assertEquals(89_478_485, PngDecoder.DEFAULT_MAX_PIXELS);
        assertEquals(2, new PngDecoder(4).decode(new ByteArrayInputStream(image)).getWidth());
        assertThrows(ImageDecodingException.class, () -> belowLimit.decode(new ByteArrayInputStream(image)));
        assertTrue(assertRejected(wide.toByteArray()).contains("limits"));
    }

    /**
     * The image cache decodes an image once, shares its texture between handles, keeps a derived copy apart, and
     * releases each texture with the last handle of its image.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    public void testImageCache() throws IOException
    {
        final ResourceKey key = new ResourceKey("sprites/hero.png");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(2, 2, 8, GRAY, 0, null, null, 0, 85, 170, 255));
        }
        final Textures textures = new Textures();
        final ImageCache cache = new ImageCache(new Resources(memory), textures);

        final Image first = cache.load(key);
        final Image second = cache.load(key);
        final PixelBuffer copy = first.copyToBuffer();
        copy.setPixel(0, 0, 0xFFFF0000);
        final Image red = cache.add(key, "red", copy);

        assertEquals(first.getTexture(), second.getTexture());
        assertEquals(0xFF555555, first.getPixel(1, 0));
        assertEquals(0xFF000000, first.getPixel(0, 0));
        assertEquals(0xFFFF0000, red.getPixel(0, 0));
        assertEquals(2, cache.size());
        first.release();
        first.release();
        assertTrue(textures.alive.contains(Integer.valueOf(second.getTexture())));
        second.release();
        red.release();
        assertEquals(0, cache.size());
        assertTrue(textures.alive.isEmpty());
    }

    /**
     * The image cache counts its memory, refuses an image above its limit, and reloads a changed image (ADR-0055).
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    public void testImageCacheLimitAndReload() throws IOException
    {
        final ResourceKey key = new ResourceKey("sprites/hero.png");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(2, 2, 8, GRAY, 0, null, null, 0, 85, 170, 255));
        }
        final Textures textures = new Textures();
        final ImageCache cache = new ImageCache(new Resources(memory), textures, 20L);

        final Image hero = cache.load(key);
        final int previous = hero.getTexture();
        assertEquals(16L, cache.getUsedBytes());
        assertThrows(IllegalStateException.class, () -> cache.add(key, "red", new PixelBuffer(2, 1)));
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(1, 1, 8, GRAY, 0, null, null, 255));
        }
        cache.reload(key);

        assertEquals(1, hero.getWidth());
        assertEquals(0xFFFFFFFF, hero.getPixel(0, 0));
        assertFalse(textures.alive.contains(Integer.valueOf(previous)));
        assertEquals(4L, cache.getUsedBytes());
        hero.release();
        assertTrue(textures.alive.isEmpty());
    }

    /**
     * Returns the class name of the error raised by a truncated image.
     *
     * @param image The image.
     * @param length The length kept.
     * @return The class name of the error, or {@code "none"}.
     */
    private static String rejectedName(byte[] image, int length)
    {
        try
        {
            decode(Arrays.copyOf(image, length));
            return "none";
        }
        catch (final ImageDecodingException | IOException exception)
        {
            return exception.getClass().getName();
        }
    }

    /**
     * Texture store that records the textures alive.
     */
    private static final class Textures implements TextureStore
    {
        /** Textures created and not released. */
        private final Set<Integer> alive = new HashSet<>();
        /** Next texture handle. */
        private int next = 1;

        /**
         * Creates the store.
         */
        Textures()
        {
            super();
        }

        @Override
        public int create(int width, int height, int[] argb)
        {
            final int texture = next++;
            alive.add(Integer.valueOf(texture));
            return texture;
        }

        @Override
        public void prepare(int texture)
        {
            // Nothing to upload.
        }

        @Override
        public void release(int texture)
        {
            alive.remove(Integer.valueOf(texture));
        }
    }
}
