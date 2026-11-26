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

import com.b3dgs.lionengine.DefinitionException;
import com.b3dgs.lionengine.graphics.Animation;
import com.b3dgs.lionengine.graphics.AnimationLoop;
import com.b3dgs.lionengine.graphics.ColorMapping;
import com.b3dgs.lionengine.graphics.ColorMatrix;
import com.b3dgs.lionengine.graphics.ColorTransform;
import com.b3dgs.lionengine.graphics.Image;
import com.b3dgs.lionengine.graphics.ImageCache;
import com.b3dgs.lionengine.graphics.ImageDecodingException;
import com.b3dgs.lionengine.graphics.PixelBuffer;
import com.b3dgs.lionengine.graphics.PngDecoder;
import com.b3dgs.lionengine.graphics.SheetGrid;
import com.b3dgs.lionengine.graphics.SheetGroup;
import com.b3dgs.lionengine.graphics.SheetLoader;
import com.b3dgs.lionengine.graphics.Sprite;
import com.b3dgs.lionengine.graphics.SpriteSheet;
import com.b3dgs.lionengine.render.ColorCapability;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.TextureStore;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@code graphics} on Android API 21 (ADR-0044): the pixel buffer, the PNG decoder with its size limits
 * (ADR-0054), whose {@code java.util.zip.Inflater} and {@code CRC32} are verified by the probes, the image cache over
 * the memory provider, the sheets, the animations, the sprites, the color transforms and the template color variants.
 * The images are
 * written here with the API 21 methods only, as the JVM fixture {@code PngWriter} writes them; expected values are the
 * ones of the JVM tests
 * (PixelBufferTest, PngDecoderTest, PngDecoderContractTest, ImageCacheTest, SpriteTest, ColorTransformTest,
 * ColorMappingTest, TemplateVariantTest, AnimationTest).
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
     * A sheet group cuts an image into cells, refuses a duplicate and an unknown id, and releases the image of every
     * sheet with its last handle (REQ-SHEET-001, REQ-SHEET-002).
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    public void testSpriteSheet() throws IOException
    {
        final ResourceKey key = new ResourceKey("tiles/forest.png");
        final ResourceKey id = new ResourceKey("tiles/forest.tsj");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(5, 2, 8, GRAY, 0, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        final Textures textures = new Textures();
        final ImageCache cache = new ImageCache(new Resources(memory), textures);
        final SheetGroup group = new SheetGroup(cache);

        final SpriteSheet sheet = group.add(id, key, new SheetGrid(2, 2, SheetGrid.DERIVED, SheetGrid.DERIVED, 0, 1));
        final SheetGroup map = group.acquire();

        assertEquals(2, sheet.getColumns());
        assertEquals(1, sheet.getRows());
        assertEquals(3, sheet.getCellX(1));
        assertThrows(IndexOutOfBoundsException.class, () -> sheet.getCellX(2));
        assertThrows(IllegalStateException.class, () -> group.add(id, key, new SheetGrid(1, 1)));
        assertThrows(IllegalStateException.class, () -> group.get(key));
        group.release();
        assertEquals(1, cache.size());
        map.release();
        assertEquals(0, cache.size());
        assertTrue(textures.alive.isEmpty());
    }

    /**
     * A sprite over a sheet cell emits one command with its frame, transform, pivot and layer, and cannot be drawn
     * once its group is released (REQ-SPRITE-003, REQ-SPRITE-006, REQ-SPRITE-007).
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    public void testSprite() throws IOException
    {
        final ResourceKey key = new ResourceKey("tiles/forest.png");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(5, 2, 8, GRAY, 0, null, null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        final ImageCache cache = new ImageCache(new Resources(memory), new Textures());
        final SheetGroup group = new SheetGroup(cache);
        final SheetGrid grid = new SheetGrid(2, 2, SheetGrid.DERIVED, SheetGrid.DERIVED, 0, 1);
        final Sprite sprite = new Sprite(group.add(new ResourceKey("tiles/forest.tsj"), key, grid));
        final CommandBuffer commands = new CommandBuffer(16, 16, 1);

        sprite.setFrame(1);
        sprite.setScale(-2.0, 1.0);
        sprite.setRotation(-450.0);
        sprite.setLayer(2);
        sprite.render(commands, 4.5, 6.0);

        assertEquals(1, commands.size());
        assertEquals(3, commands.getSourceX(0));
        assertEquals(2, commands.getSourceWidth(0));
        assertEquals(4.5F, commands.getX(0), 0.0F);
        assertEquals(-2.0F, commands.getScaleX(0), 0.0F);
        assertEquals(-450.0F, commands.getRotation(0), 0.0F);
        assertEquals(1.0F, commands.getPivotX(0), 0.0F);
        assertEquals(2, commands.getLayer(0));
        assertThrows(IndexOutOfBoundsException.class, () -> sprite.setFrame(2));
        group.release();
        assertThrows(IllegalStateException.class, () -> sprite.render(commands));
        assertEquals(1, commands.size());
    }

    /**
     * Sprites over two handles of one cached image draw one texture; a modified copy is a source of its own; the
     * texture leaves with the last handle (REQ-SPRITE-002, REQ-SPR-001).
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    public void testSpriteSharing() throws IOException
    {
        final ResourceKey key = new ResourceKey("sprites/coin.png");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(2, 1, 8, GRAY, 0, null, null, 0, 0));
        }
        final Textures textures = new Textures();
        final ImageCache cache = new ImageCache(new Resources(memory), textures);
        final Image level = cache.load(key);
        final Image display = cache.load(key);
        final PixelBuffer pixels = level.copyToBuffer();
        pixels.setPixel(0, 0, 0xFFFF0000);
        final Image damaged = cache.add(key, "damaged", pixels);
        final CommandBuffer commands = new CommandBuffer(16, 16, 1);

        new Sprite(level).render(commands);
        new Sprite(display).render(commands);
        new Sprite(damaged).render(commands);
        level.release();
        final Sprite icon = new Sprite(display);
        icon.render(commands);

        assertEquals(commands.getTexture(0), commands.getTexture(1));
        assertEquals(commands.getTexture(0), commands.getTexture(3));
        assertTrue(commands.getTexture(0) != commands.getTexture(2));
        assertEquals(0xFF000000, display.getPixel(0, 0));
        assertEquals(2, cache.size());
        display.release();
        damaged.release();
        assertEquals(0, cache.size());
        assertTrue(textures.alive.isEmpty());
        assertThrows(IllegalStateException.class, () -> icon.render(commands));
    }

    /**
     * Color transforms give the JVM values: grading, mask, tint and alpha on the CPU, the split for a backend without
     * per-frame effects, and a variant baked into the cache (REQ-SPRITE-004, ADR-0056).
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    public void testColorTransform() throws IOException
    {
        final ResourceKey key = new ResourceKey("sprites/hero.png");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(png(1, 1, 8, GRAY, 0, null, null, 0));
        }
        final ImageCache cache = new ImageCache(new Resources(memory), new Textures());
        final Image hero = cache.load(key);
        final ColorTransform hit = ColorTransform.NONE.withMask(0xFFFFFF, 1.0).withTint(0x80FF0000).withAlpha(0.5);
        final ColorCapability none = effect -> false;

        assertEquals(0x80363636, ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.0)).apply(0x80FF0000));
        assertEquals(0xFF800000, ColorTransform.NONE.withMask(0xFF0000, 0.5).apply(0xFF000000));
        assertEquals(0x40FF0000, ColorTransform.NONE.withTint(0x80FF0000).withAlpha(0.5).apply(0xFFFFFFFF));
        assertEquals(hit, hit.getBakedPart(none));
        assertTrue(hit.getFramePart(none).isIdentity());
        final Image variant = cache.bake(hero, hit.getBakedPart(none));
        assertEquals(0x40FF0000, variant.getPixel(0, 0));
        assertEquals("+mask(#FFFFFF,1.0),tint(#80FF0000),alpha(0.5)", variant.getDiscriminator());
        assertEquals(2, cache.size());
        variant.release();
        hero.release();
        assertEquals(0, cache.size());
    }

    /**
     * The animations of a sheet definition become animation values, frame durations overriding the frame duration;
     * an animation built in code checks its values (REQ-ANIM-001).
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    public void testAnimation() throws IOException
    {
        final ResourceKey image = new ResourceKey("sprites/hero.png");
        final ResourceKey key = new ResourceKey("sheets/hero.json");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(image))
        {
            output.write(png(4, 2, 8, GRAY, 0, null, null, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        final String definition = "{\"format\": \"lionengine.sheet\", \"version\": 1, "
                                  + "\"image\": \"sprites/hero.png\", \"cellWidth\": 2, \"cellHeight\": 2, "
                                  + "\"animations\": {\"walk\": {\"frames\": [0, 1], \"frameDuration\": 0.1, "
                                  + "\"loop\": \"loop\"}, \"hit\": {\"frames\": [1, 0], \"frameDuration\": 0.1, "
                                  + "\"frameDurations\": [0.05, 0.3], \"loop\": \"once\"}}}";
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(definition.getBytes(StandardCharsets.UTF_8));
        }
        final Resources resources = new Resources(memory);
        final SheetGroup group = new SheetGroup(new ImageCache(resources, new Textures()));
        final SpriteSheet sheet = new SheetLoader(resources).load(group, key);

        assertEquals(Arrays.asList("walk", "hit"), sheet.getAnimationNames());
        assertEquals(new Animation(new int[]
        {
            0, 1
        }, 0.1, AnimationLoop.LOOP), sheet.getAnimation("walk"));
        final Animation hit = sheet.getAnimation("hit");
        assertEquals(1, hit.getFrame(0));
        assertEquals(0.05, hit.getFrameDuration(0), 0.0);
        assertEquals(0.3, hit.getFrameDuration(1), 0.0);
        assertEquals(AnimationLoop.ONCE, hit.getLoop());
        assertEquals("Animation[frames=[1, 0], durations=[0.05, 0.3], loop=once]", hit.toString());
        assertThrows(IllegalArgumentException.class, () -> new Animation(new int[0], 0.1, AnimationLoop.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new Animation(new int[]
        {
            0
        }, Double.NaN, AnimationLoop.LOOP));
        group.release();
    }

    /**
     * The templates of a sheet definition become color mappings, matched exactly on the four channels in one pass,
     * and baked into variants shared by mapping, whatever the template name (REQ-SPRITE-005, ADR-0057).
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    public void testTemplateVariant() throws IOException
    {
        final ResourceKey image = new ResourceKey("sprites/soldier.png");
        final ResourceKey key = new ResourceKey("sheets/soldier.json");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(image))
        {
            output.write(png(2, 1, 8, RGBA, 0, null, null, 255, 0, 255, 255, 255, 0, 255, 128));
        }
        final String definition = "{\"format\": \"lionengine.sheet\", \"version\": 1, "
                                  + "\"image\": \"sprites/soldier.png\", \"cellWidth\": 1, \"cellHeight\": 1, "
                                  + "\"templates\": {"
                                  + "\"red\": {\"#FF00FF\": \"#FF0000\", \"#80FF00FF\": \"#80FF0000\"}, "
                                  + "\"blue\": {\"#FF00FF\": \"#0000FF\"}, "
                                  + "\"crimson\": {\"#80ff00ff\": \"#80ff0000\", \"#FFFF00FF\": \"#FFFF0000\"}}}";
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(definition.getBytes(StandardCharsets.UTF_8));
        }
        final Resources resources = new Resources(memory);
        final ImageCache cache = new ImageCache(resources, new Textures());
        final SheetGroup group = new SheetGroup(cache);
        final SpriteSheet sheet = new SheetLoader(resources).load(group, key);
        final ColorMapping red = sheet.getTemplate("red");
        final ColorMapping swap = ColorMapping.NONE.with(0xFFFF0000, 0xFF0000FF).with(0xFF0000FF, 0xFFFF0000);

        assertEquals(Arrays.asList("red", "blue", "crimson"), sheet.getTemplateNames());
        assertEquals(red, sheet.getTemplate("crimson"));
        assertEquals(0xFFFF0000, red.apply(0xFFFF00FF));
        assertEquals(0x7FFF00FF, red.apply(0x7FFF00FF));
        assertEquals(0xFF0000FF, swap.apply(0xFFFF0000));
        assertEquals(0xFFFF0000, swap.apply(0xFF0000FF));
        assertEquals("mapping(#80FF00FF=#80FF0000,#FFFF00FF=#FFFF0000)", red.toString());
        final Image redVariant = cache.bake(sheet.getImage(), ColorTransform.NONE.withMapping(red));
        final ColorTransform crimsonColors = ColorTransform.NONE.withMapping(sheet.getTemplate("crimson"));
        final Image crimson = cache.bake(sheet.getImage(), crimsonColors);
        final Image blue = cache.bake(sheet.getImage(), ColorTransform.NONE.withMapping(sheet.getTemplate("blue")));
        assertEquals(0xFFFF0000, redVariant.getPixel(0, 0));
        assertEquals(0x80FF0000, redVariant.getPixel(1, 0));
        assertEquals(0xFF0000FF, blue.getPixel(0, 0));
        assertEquals(0x80FF00FF, blue.getPixel(1, 0));
        assertEquals(redVariant.getTexture(), crimson.getTexture());
        assertEquals(0xFF363636,
                     ColorTransform.NONE.withMapping(red).withGrading(ColorMatrix.saturation(0.0)).apply(0xFFFF00FF));
        assertEquals(2, new Sprite(sheet, blue).getFrameCount());
        assertEquals(3, cache.size());
        redVariant.release();
        crimson.release();
        blue.release();
        group.release();
        assertEquals(0, cache.size());
    }

    /**
     * The sheet loader reads a definition into a group, and refuses a frame outside the sheet with its position
     * (REQ-SHEET-003).
     *
     * @throws IOException If a resource cannot be written.
     */
    @Test
    public void testSheetLoader() throws IOException
    {
        final ResourceKey image = new ResourceKey("tiles/forest.png");
        final ResourceKey key = new ResourceKey("sheets/forest.json");
        final ResourceKey broken = new ResourceKey("sheets/broken.json");
        final MemoryProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(image))
        {
            output.write(png(4, 2, 8, GRAY, 0, null, null, 0, 0, 0, 0, 0, 0, 0, 0));
        }
        final String header = "{\"format\": \"lionengine.sheet\", \"version\": 1, \"image\": \"tiles/forest.png\", "
                              + "\"cellWidth\": 2, \"cellHeight\": 2, ";
        try (OutputStream output = memory.openWrite(key))
        {
            output.write((header + "\"pivotY\": 2, \"animations\": {\"wave\": {\"frames\": [0, 1], "
                          + "\"frameDuration\": 0.5, \"loop\": \"ping-pong\"}}}").getBytes(StandardCharsets.UTF_8));
        }
        try (OutputStream output = memory.openWrite(broken))
        {
            output.write((header + "\"animations\": {\"wave\": {\"frames\": [2], \"frameDuration\": 0.5, "
                          + "\"loop\": \"loop\"}}}").getBytes(StandardCharsets.UTF_8));
        }
        final Resources resources = new Resources(memory);
        final SheetGroup group = new SheetGroup(new ImageCache(resources, new Textures()));
        final SheetLoader loader = new SheetLoader(resources);

        final SpriteSheet sheet = loader.load(group, key);
        final DefinitionException error = assertThrows(DefinitionException.class, () -> loader.load(group, broken));

        assertEquals(2, sheet.getColumns());
        assertEquals(2.0, sheet.getPivotY(), 0.0);
        assertEquals("$.animations.wave.frames[0]", error.getPath());
        assertEquals(1, group.size());
        group.release();
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
