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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.RandomSource;

/**
 * Test {@link PngDecoder} on images of every color type, bit depth and filter type, and on invalid and unsupported
 * images (REQ-SPRITE-009, ADR-0026, ADR-0033).
 *
 * <p>
 * The expected pixels are computed here from the samples written by {@link PngWriter}, with the rules of the PNG
 * specification and of ADR-0033: samples below 8 bits scaled so that the largest is {@code 255}, 16-bit samples reduced
 * to their most significant byte, transparency compared before that reduction.
 * </p>
 */
final class PngDecoderTest
{
    /** Width of the generated images, not a multiple of 8 so that rows of low depths end inside a byte. */
    private static final int WIDTH = 13;
    /** Height of the generated images, more than the number of filter types. */
    private static final int HEIGHT = 7;
    /** Seed of the generated samples. */
    private static final long SEED = 0x4C494F4EL;
    /** Index of the first byte after the header chunk in a PNG stream. */
    private static final int HEADER_END = 33;
    /** Index of the first byte of the header data in a PNG stream. */
    private static final int HEADER_DATA = 16;
    /** Number of bytes of the header data. */
    private static final int HEADER_LENGTH = 13;
    /** Number of bytes of a chunk without data: length, type and CRC. */
    private static final int CHUNK_FRAME = 12;
    /** Some chunk data. */
    private static final byte[] SOME =
    {
        1, 2, 3, 4, 5, 6, 7, 8
    };

    /**
     * Returns the valid pairs of color type and bit depth with every filter mode.
     *
     * @return The color type, the bit depth and the filter mode.
     */
    static Arguments[] formats()
    {
        final int[][] pairs =
        {
            {
                PngWriter.GRAY, 1
            },
            {
                PngWriter.GRAY, 2
            },
            {
                PngWriter.GRAY, 4
            },
            {
                PngWriter.GRAY, 8
            },
            {
                PngWriter.GRAY, 16
            },
            {
                PngWriter.RGB, 8
            },
            {
                PngWriter.RGB, 16
            },
            {
                PngWriter.PALETTE, 1
            },
            {
                PngWriter.PALETTE, 2
            },
            {
                PngWriter.PALETTE, 4
            },
            {
                PngWriter.PALETTE, 8
            },
            {
                PngWriter.GRAY_ALPHA, 8
            },
            {
                PngWriter.GRAY_ALPHA, 16
            },
            {
                PngWriter.RGBA, 8
            },
            {
                PngWriter.RGBA, 16
            }
        };
        final int[] filters =
        {
            PngWriter.NONE, PngWriter.SUB, PngWriter.UP, PngWriter.AVERAGE, PngWriter.PAETH, PngWriter.CYCLE
        };
        final Arguments[] arguments = new Arguments[pairs.length * filters.length];
        for (int i = 0; i < pairs.length; i++)
        {
            for (int j = 0; j < filters.length; j++)
            {
                arguments[i * filters.length + j] = Arguments.of(pairs[i][0], pairs[i][1], filters[j]);
            }
        }
        return arguments;
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
     * @return The error.
     */
    private static ImageDecodingException assertRejected(byte[] bytes)
    {
        final InputStream input = new ByteArrayInputStream(bytes);
        final PngDecoder decoder = new PngDecoder();
        return assertThrows(ImageDecodingException.class, () -> decoder.decode(input));
    }

    /**
     * Checks that bytes are rejected with a decoding error whose message contains a text.
     *
     * @param bytes The bytes.
     * @param text The text expected in the message.
     */
    private static void assertRejected(byte[] bytes, String text)
    {
        final String message = assertRejected(bytes).getMessage();
        assertTrue(message.contains(text), message);
    }

    /**
     * Checks that an image decodes to the expected pixels.
     *
     * @param writer The writer of the image.
     * @param expected The expected pixels, {@link #WIDTH} by {@link #HEIGHT} unless the length says otherwise.
     * @throws IOException If reading fails.
     */
    private static void assertDecoded(PngWriter writer, int[] expected) throws IOException
    {
        final PixelBuffer pixels = decode(writer.write());
        assertEquals(expected.length, pixels.getWidth() * pixels.getHeight());
        assertArrayEquals(expected, pixels.copyPixels());
    }

    /**
     * Returns random samples of a {@link #WIDTH} by {@link #HEIGHT} image.
     *
     * @param colorType The color type.
     * @param bound The exclusive upper bound of a sample.
     * @return The samples.
     */
    private static int[] samples(int colorType, int bound)
    {
        final RandomSource random = new RandomSource(SEED);
        final int[] samples = new int[WIDTH * HEIGHT * PngWriter.samplesPerPixel(colorType)];
        for (int i = 0; i < samples.length; i++)
        {
            samples[i] = random.nextInt(bound);
        }
        return samples;
    }

    /**
     * Returns a random palette.
     *
     * @param entries The number of entries.
     * @return The entries, {@code 0xRRGGBB}.
     */
    private static int[] palette(int entries)
    {
        final RandomSource random = new RandomSource(~SEED);
        final int[] palette = new int[entries];
        for (int i = 0; i < entries; i++)
        {
            palette[i] = random.nextInt(0x1000000);
        }
        return palette;
    }

    /**
     * Scales a sample to 8 bits.
     *
     * @param sample The sample.
     * @param depth The bit depth.
     * @return The sample on 8 bits.
     */
    private static int to8(int sample, int depth)
    {
        if (depth == 16)
        {
            return sample >> 8;
        }
        return sample * 255 / ((1 << depth) - 1);
    }

    /**
     * Packs a pixel.
     *
     * @param alpha The alpha.
     * @param red The red.
     * @param green The green.
     * @param blue The blue.
     * @return The pixel.
     */
    private static int argb(int alpha, int red, int green, int blue)
    {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * Returns the 16-bit value of two bytes of a transparency.
     *
     * @param transparency The bytes.
     * @param index The index of the first byte.
     * @return The value.
     */
    private static int value16(int[] transparency, int index)
    {
        return transparency[index] << 8 | transparency[index + 1];
    }

    /**
     * Returns the expected pixels of samples.
     *
     * @param colorType The color type.
     * @param depth The bit depth.
     * @param samples The samples.
     * @param palette The palette entries, {@code 0xRRGGBB}, {@code null} if none.
     * @param transparency The bytes of the {@code tRNS} chunk, empty if none.
     * @return The expected pixels.
     */
    private static int[] expected(int colorType, int depth, int[] samples, int[] palette, int[] transparency)
    {
        final int perPixel = PngWriter.samplesPerPixel(colorType);
        final int[] pixels = new int[samples.length / perPixel];
        for (int i = 0; i < pixels.length; i++)
        {
            final int first = i * perPixel;
            final int sample = samples[first];
            pixels[i] = switch (colorType)
            {
                case PngWriter.GRAY ->
                {
                    final boolean clear = transparency.length > 0 && sample == value16(transparency, 0);
                    final int gray = to8(sample, depth);
                    yield argb(clear ? 0 : 255, gray, gray, gray);
                }
                case PngWriter.RGB ->
                {
                    final boolean clear = transparency.length > 0
                                          && sample == value16(transparency, 0)
                                          && samples[first + 1] == value16(transparency, 2)
                                          && samples[first + 2] == value16(transparency, 4);
                    yield argb(clear ? 0 : 255,
                               to8(sample, depth),
                               to8(samples[first + 1], depth),
                               to8(samples[first + 2], depth));
                }
                case PngWriter.PALETTE ->
                {
                    final int alpha = sample < transparency.length ? transparency[sample] : 255;
                    yield alpha << 24 | palette[sample];
                }
                case PngWriter.GRAY_ALPHA ->
                {
                    final int gray = to8(sample, depth);
                    yield argb(to8(samples[first + 1], depth), gray, gray, gray);
                }
                default -> argb(to8(samples[first + 3], depth),
                                to8(sample, depth),
                                to8(samples[first + 1], depth),
                                to8(samples[first + 2], depth));
            };
        }
        return pixels;
    }

    /**
     * Returns the writer of a {@link #WIDTH} by {@link #HEIGHT} image of random samples, with a full random palette
     * for palette images.
     *
     * @param colorType The color type.
     * @param depth The bit depth.
     * @return The writer.
     */
    private static PngWriter writer(int colorType, int depth)
    {
        final PngWriter writer = new PngWriter(WIDTH, HEIGHT, depth, colorType);
        writer.setSamples(samples(colorType, 1 << depth));
        if (colorType == PngWriter.PALETTE)
        {
            writer.setPalette(palette(1 << depth));
        }
        return writer;
    }

    /**
     * Returns the expected pixels of {@link #writer(int, int)}.
     *
     * @param colorType The color type.
     * @param depth The bit depth.
     * @param transparency The bytes of the {@code tRNS} chunk, empty if none.
     * @return The expected pixels.
     */
    private static int[] expected(int colorType, int depth, int... transparency)
    {
        return expected(colorType, depth, samples(colorType, 1 << depth), palette(1 << depth), transparency);
    }

    /**
     * Changes a byte of the header data and updates the CRC of the header.
     *
     * @param png The PNG stream, changed.
     * @param offset The offset of the byte in the header data.
     * @param value The new value.
     * @return The PNG stream.
     */
    private static byte[] patchHeader(byte[] png, int offset, int value)
    {
        png[HEADER_DATA + offset] = (byte) value;
        final CRC32 crc = new CRC32();
        crc.update(png, HEADER_DATA - 4, 4 + HEADER_LENGTH);
        final int checksum = (int) crc.getValue();
        for (int i = 0; i < 4; i++)
        {
            png[HEADER_DATA + HEADER_LENGTH + i] = (byte) (checksum >>> 24 - i * 8);
        }
        return png;
    }

    /**
     * Returns the signature and the header of an image followed by other bytes.
     *
     * @param tail The bytes after the header.
     * @return The PNG stream.
     */
    private static byte[] headerThen(byte[]... tail)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(writer(PngWriter.RGBA, 8).write(), 0, HEADER_END);
        for (final byte[] bytes : tail)
        {
            output.write(bytes, 0, bytes.length);
        }
        return output.toByteArray();
    }

    /**
     * Every valid color type and bit depth decodes with every filter type.
     *
     * @param colorType The color type.
     * @param depth The bit depth.
     * @param filter The filter mode.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @MethodSource("formats")
    void testFormats(int colorType, int depth, int filter) throws IOException
    {
        assertDecoded(writer(colorType, depth).setFilter(filter), expected(colorType, depth));
    }

    /**
     * Samples below 8 bits are scaled so that the largest is 255, and 16-bit samples keep their most significant
     * byte.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testSampleScaling() throws IOException
    {
        assertDecoded(new PngWriter(2, 1, 1, PngWriter.GRAY).setSamples(0, 1), new int[]
        {
            0xFF000000, 0xFFFFFFFF
        });
        assertDecoded(new PngWriter(4, 1, 2, PngWriter.GRAY).setSamples(0, 1, 2, 3), new int[]
        {
            0xFF000000, 0xFF555555, 0xFFAAAAAA, 0xFFFFFFFF
        });
        assertDecoded(new PngWriter(3, 1, 4, PngWriter.GRAY).setSamples(0, 5, 15), new int[]
        {
            0xFF000000, 0xFF555555, 0xFFFFFFFF
        });
        assertDecoded(new PngWriter(3, 1, 16, PngWriter.GRAY).setSamples(0x12AB, 0xFF00, 0x00FF), new int[]
        {
            0xFF121212, 0xFFFFFFFF, 0xFF000000
        });
        assertDecoded(new PngWriter(1, 1, 16, PngWriter.RGBA).setSamples(0x12FF, 0x3400, 0x56AB, 0x80FF), new int[]
        {
            0x80123456
        });
    }

    /**
     * The transparent gray of {@code tRNS} clears the matching pixels at every depth.
     *
     * @param depth The bit depth.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        1, 2, 4, 8, 16
    })
    void testTransparentGray(int depth) throws IOException
    {
        final int key = samples(PngWriter.GRAY, 1 << depth)[0];
        final PngWriter writer = writer(PngWriter.GRAY, depth).setTransparency(key >>> 8, key & 0xFF);

        assertDecoded(writer, expected(PngWriter.GRAY, depth, key >>> 8, key & 0xFF));
    }

    /**
     * The transparent RGB color of {@code tRNS} clears the matching pixels.
     *
     * @param depth The bit depth.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        8, 16
    })
    void testTransparentRgb(int depth) throws IOException
    {
        final int[] samples = samples(PngWriter.RGB, 1 << depth);
        final int[] key =
        {
            samples[0] >>> 8, samples[0] & 0xFF, samples[1] >>> 8, samples[1] & 0xFF, samples[2] >>> 8,
            samples[2] & 0xFF
        };
        final PngWriter writer = writer(PngWriter.RGB, depth).setTransparency(key);

        assertDecoded(writer, expected(PngWriter.RGB, depth, key));
    }

    /**
     * A transparent color is compared at 16 bits, before the reduction to 8 bits.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testTransparent16Bits() throws IOException
    {
        assertDecoded(new PngWriter(2, 1, 16, PngWriter.GRAY).setSamples(0x1200, 0x12FF).setTransparency(0x12, 0xFF),
                      new int[]
                      {
                          0xFF121212, 0x00121212
                      });
        assertDecoded(new PngWriter(2, 1, 16, PngWriter.RGB).setSamples(0x1200, 0x3400, 0x5600, 0x1200, 0x3400, 0x56FF)
                                                            .setTransparency(0x12, 0x00, 0x34, 0x00, 0x56, 0xFF),
                      new int[]
                      {
                          0xFF123456, 0x00123456
                      });
    }

    /**
     * {@code tRNS} gives the alpha of the first palette entries; the others stay opaque.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testTransparentPalette() throws IOException
    {
        final PngWriter writer = new PngWriter(4, 1, 2, PngWriter.PALETTE).setSamples(0, 1, 2, 3)
                                                                          .setPalette(0x112233,
                                                                                      0x445566,
                                                                                      0x778899,
                                                                                      0xAABBCC)
                                                                          .setTransparency(0x00, 0x80);

        assertDecoded(writer, new int[]
        {
            0x00112233, 0x80445566, 0xFF778899, 0xFFAABBCC
        });
    }

    /**
     * A {@code tRNS} chunk of an image with an alpha channel is ignored.
     *
     * @param colorType The color type.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        PngWriter.GRAY_ALPHA, PngWriter.RGBA
    })
    void testIgnoredWithAlpha(int colorType) throws IOException
    {
        assertDecoded(writer(colorType, 8).setTransparency(1, 2), expected(colorType, 8));
    }

    /**
     * The suggested palettes of an RGB or RGBA image are ignored, even two of them.
     *
     * @param colorType The color type.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        PngWriter.RGB, PngWriter.RGBA
    })
    void testSuggestedPalette(int colorType) throws IOException
    {
        assertDecoded(writer(colorType, 8).setPalette(1, 2, 3).addBefore("PLTE", new byte[3]),
                      expected(colorType, 8));
    }

    /**
     * Ancillary chunks, known or not, are skipped before and after the image data.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testAncillaryChunks() throws IOException
    {
        final PngWriter writer = writer(PngWriter.PALETTE, 4).addFirst("gAMA", SOME)
                                                             .addBefore("tEXt", SOME)
                                                             .addBefore("zzzz", SOME)
                                                             .addAfter("tIME", SOME)
                                                             .addAfter("tRNS", SOME);

        assertDecoded(writer, expected(PngWriter.PALETTE, 4));
    }

    /**
     * Image data split into many chunks, empty ones included, decodes; empty data chunks after the image are
     * accepted.
     *
     * @param chunkSize The largest data size of a chunk.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        1, 7, 64
    })
    void testSplitData(int chunkSize) throws IOException
    {
        final PngWriter writer = writer(PngWriter.RGBA, 16).setChunkSize(chunkSize)
                                                           .addBefore("IDAT", new byte[0])
                                                           .addAfter("IDAT", new byte[0]);

        assertDecoded(writer, expected(PngWriter.RGBA, 16));
    }

    /**
     * An image of 8 bits per sample, wider than the input buffer of the decoder, decodes.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testLargeRows() throws IOException
    {
        final int width = 3000;
        final int[] argb = new int[width * 2];
        for (int i = 0; i < argb.length; i++)
        {
            argb[i] = i * 0x01010101 ^ 0x80000000;
        }

        assertDecoded(PngWriter.rgba(width, 2, argb).setFilter(PngWriter.PAETH), argb);
    }

    /**
     * The decoder stops after the end chunk and leaves what follows in the stream.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testStopsAtEnd() throws IOException
    {
        final byte[] image = writer(PngWriter.GRAY, 8).write();
        final byte[] stream = Arrays.copyOf(image, image.length + 1);
        stream[image.length] = 42;
        final InputStream input = new ByteArrayInputStream(stream);

        new PngDecoder().decode(input);

        assertEquals(42, input.read());
    }

    /**
     * Streams without the PNG signature are rejected.
     */
    @Test
    void testSignature()
    {
        final byte[] image = writer(PngWriter.GRAY, 8).write();
        final byte[] wrong = image.clone();
        wrong[1] = 'Q';

        assertRejected(new byte[0], "signature");
        assertRejected(Arrays.copyOf(image, 4), "signature");
        assertRejected(wrong, "signature");
    }

    /**
     * Every truncation of an image is rejected.
     */
    @Test
    void testTruncated()
    {
        final byte[] image = writer(PngWriter.RGB, 8).addBefore("tEXt", SOME).write();
        for (int length = 0; length < image.length; length++)
        {
            assertRejected(Arrays.copyOf(image, length));
        }
    }

    /**
     * An image without its end chunk is rejected.
     */
    @Test
    void testMissingEnd()
    {
        final byte[] image = writer(PngWriter.RGB, 8).write();

        assertRejected(Arrays.copyOf(image, image.length - CHUNK_FRAME), "truncated");
    }

    /**
     * A changed byte of the header, of its CRC, of an ancillary chunk or of the CRC of the image data is detected by
     * the CRC.
     *
     * @param index The index of the changed byte, from the end of the stream when negative.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        HEADER_DATA, HEADER_END - 1, HEADER_END + 8, -CHUNK_FRAME - 1
    })
    void testCrc(int index)
    {
        final byte[] image = writer(PngWriter.RGB, 8).addBefore("tEXt", SOME).write();
        final int changed = index < 0 ? image.length + index : index;
        image[changed] ^= 1;

        assertRejected(image, "CRC");
    }

    /**
     * The first chunk must be a header of 13 bytes.
     */
    @Test
    void testHeader()
    {
        final byte[] signature = Arrays.copyOf(writer(PngWriter.GRAY, 8).write(), 8);
        final ByteArrayOutputStream textFirst = new ByteArrayOutputStream();
        textFirst.write(signature, 0, signature.length);
        textFirst.writeBytes(PngWriter.chunk("tEXt", SOME));
        final ByteArrayOutputStream shortHeader = new ByteArrayOutputStream();
        shortHeader.write(signature, 0, signature.length);
        shortHeader.writeBytes(PngWriter.chunk("IHDR", new byte[12]));

        assertRejected(textFirst.toByteArray(), "IHDR");
        assertRejected(shortHeader.toByteArray(), "length");
    }

    /**
     * Empty and negative sizes are rejected.
     *
     * @param width The width written in the header.
     * @param height The height written in the header.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 1", "1, 0", "-1, 1", "1, -1", "0, 0"
    })
    void testSize(int width, int height)
    {
        assertRejected(new PngWriter(width, height, 8, PngWriter.GRAY).setImageData(SOME).write(), "size invalid");
    }

    /**
     * Sizes above the limits of ADR-0054 are rejected from the header: a width or a height above 1,000,000 pixels, or
     * more than 89,478,485 pixels by default.
     *
     * @param width The width written in the header.
     * @param height The height written in the header.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "1000001, 1", "1, 1000001", "2147483647, 1", "9460, 9460", "1000000, 90", "65536, 32768"
    })
    void testSizeLimits(int width, int height)
    {
        assertRejected(new PngWriter(width, height, 16, PngWriter.RGBA).setImageData(SOME).write(), "limits");
    }

    /**
     * The default pixel limit is the one of ADR-0054, and the largest width is accepted.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testLimitValues() throws IOException
    {
        final int[] samples = new int[PngDecoder.MAX_DIMENSION];
        samples[PngDecoder.MAX_DIMENSION - 1] = 0xFF;

        final PngWriter widest = new PngWriter(PngDecoder.MAX_DIMENSION, 1, 8, PngWriter.GRAY).setSamples(samples);
        final PixelBuffer pixels = decode(widest.write());

        assertEquals(1_000_000, PngDecoder.MAX_DIMENSION);
        assertEquals(89_478_485, PngDecoder.DEFAULT_MAX_PIXELS);
        assertEquals(0xFFFFFFFF, pixels.getPixel(PngDecoder.MAX_DIMENSION - 1, 0));
    }

    /**
     * A decoder created with a pixel limit accepts an image at the limit and rejects a larger one.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testPixelLimit() throws IOException
    {
        final byte[] image = writer(PngWriter.RGB, 8).write();
        final PngDecoder atLimit = new PngDecoder(WIDTH * HEIGHT);
        final PngDecoder belowLimit = new PngDecoder(WIDTH * HEIGHT - 1);
        final InputStream input = new ByteArrayInputStream(image);

        assertEquals(WIDTH, atLimit.decode(new ByteArrayInputStream(image)).getWidth());
        final ImageDecodingException exception = assertThrows(ImageDecodingException.class,
                                                              () -> belowLimit.decode(input));
        assertTrue(exception.getMessage().contains("limits"), exception.getMessage());
    }

    /**
     * A pixel limit that is not positive is rejected.
     *
     * @param maxPixels The pixel limit.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        0, -1, Integer.MIN_VALUE
    })
    void testInvalidPixelLimit(int maxPixels)
    {
        assertThrows(IllegalArgumentException.class, () -> new PngDecoder(maxPixels));
    }

    /**
     * Invalid pairs of color type and bit depth are rejected.
     *
     * @param colorType The color type.
     * @param depth The bit depth.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "1, 8", "5, 8", "7, 8", "255, 8", "0, 0", "0, 3", "0, 32", "0, 255", "2, 4", "3, 16", "4, 4", "6, 1"
    })
    void testFormat(int colorType, int depth)
    {
        assertRejected(new PngWriter(1, 1, depth, colorType).setImageData(SOME).write(), "color type");
    }

    /**
     * Unknown compression, filter and interlace methods are rejected.
     *
     * @param offset The offset of the method in the header data.
     * @param value The method.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "10, 1", "11, 1", "12, 2", "12, 200"
    })
    void testMethods(int offset, int value)
    {
        assertRejected(patchHeader(writer(PngWriter.GRAY, 8).write(), offset, value), "method");
    }

    /**
     * Interlaced images are rejected (ADR-0033).
     */
    @Test
    void testInterlaced()
    {
        assertRejected(writer(PngWriter.RGBA, 8).setInterlace(1).write(), "Interlaced");
    }

    /**
     * Animated images are rejected, whichever animation chunk comes first (ADR-0033).
     *
     * @param before {@code true} for a chunk before the image data.
     * @param type The animation chunk type.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "true, acTL", "true, fcTL", "false, fcTL", "false, fdAT"
    })
    void testAnimated(boolean before, String type)
    {
        final PngWriter writer = writer(PngWriter.RGBA, 8);
        if (before)
        {
            writer.addBefore(type, SOME);
        }
        else
        {
            writer.addAfter(type, SOME);
        }

        assertRejected(writer.write(), "Animated");
    }

    /**
     * Unknown critical chunks, and critical chunks out of place, are rejected.
     *
     * @param before {@code true} for a chunk before the image data.
     * @param type The chunk type.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "true, ABCD", "false, ABCD", "true, IHDR", "true, IEND", "false, PLTE", "false, IHDR"
    })
    void testCritical(boolean before, String type)
    {
        final PngWriter writer = writer(PngWriter.RGB, 8);
        if (before)
        {
            writer.addBefore(type, new byte[0]);
        }
        else
        {
            writer.addAfter(type, new byte[3]);
        }

        assertRejected(writer.write(), "unexpected: " + type);
    }

    /**
     * A chunk with a length above {@code 2^31 - 1} is rejected.
     */
    @Test
    void testChunkLength()
    {
        assertRejected(headerThen(new byte[]
        {
            (byte) 0x80, 0, 0, 0, 't', 'E', 'X', 't'
        }), "length");
    }

    /**
     * A chunk type made of other characters than ASCII letters is rejected.
     *
     * @param type The chunk type.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "I@DR", "IH[R", "IHD{", "IHD`", "1HDR", "IH R"
    })
    void testChunkType(String type)
    {
        assertRejected(headerThen(PngWriter.chunk(type, SOME)), "type");
    }

    /**
     * An image without data is rejected.
     */
    @Test
    void testNoData()
    {
        assertRejected(headerThen(PngWriter.chunk("IEND", new byte[0])), "IEND");
    }

    /**
     * A palette image needs its palette, and a grayscale image may not have one.
     */
    @Test
    void testPaletteRequiredOrForbidden()
    {
        assertRejected(new PngWriter(1, 1, 8, PngWriter.PALETTE).write(), "missing");
        assertRejected(writer(PngWriter.GRAY, 8).setPalette(1).write(), "PLTE");
        assertRejected(writer(PngWriter.GRAY_ALPHA, 8).setPalette(1).write(), "PLTE");
    }

    /**
     * Invalid palettes are rejected: empty, not whole entries, more than 256 entries, or twice.
     */
    @Test
    void testInvalidPalette()
    {
        assertRejected(writer(PngWriter.PALETTE, 8).addFirst("PLTE", new byte[0]).write(), "PLTE");
        assertRejected(writer(PngWriter.PALETTE, 8).addFirst("PLTE", new byte[4]).write(), "PLTE");
        assertRejected(writer(PngWriter.PALETTE, 8).addFirst("PLTE", new byte[257 * 3]).write(), "PLTE");
        assertRejected(writer(PngWriter.PALETTE, 8).addBefore("PLTE", new byte[3]).write(), "PLTE");
    }

    /**
     * Invalid transparencies are rejected: before the palette, longer than the palette, or of the wrong length for
     * a grayscale or RGB image.
     */
    @Test
    void testInvalidTransparency()
    {
        assertRejected(writer(PngWriter.PALETTE, 2).addFirst("tRNS", new byte[1]).write(), "tRNS");
        assertRejected(new PngWriter(1, 1, 8, PngWriter.PALETTE).setPalette(1, 2).setTransparency(1, 2, 3).write(),
                       "tRNS");
        assertRejected(writer(PngWriter.GRAY, 8).setTransparency(1).write(), "tRNS");
        assertRejected(writer(PngWriter.GRAY, 8).setTransparency(1, 2, 3).write(), "tRNS");
        assertRejected(writer(PngWriter.RGB, 8).setTransparency(1, 2).write(), "tRNS");
    }

    /**
     * A palette index outside the palette is rejected.
     */
    @Test
    void testPaletteIndex()
    {
        final PngWriter writer = new PngWriter(2, 1, 8, PngWriter.PALETTE).setPalette(1, 2, 3, 4).setSamples(3, 4);

        assertRejected(writer.write(), "index");
    }

    /**
     * A row with an invalid filter type is rejected.
     */
    @Test
    void testFilterType()
    {
        assertRejected(writer(PngWriter.RGB, 8).setFilter(5).write(), "filter");
    }

    /**
     * Invalid compressed data is rejected with the decompression error as cause.
     */
    @Test
    void testCorruptedData()
    {
        final byte[] data =
        {
            0x78, (byte) 0x9C, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        };
        final ImageDecodingException exception = assertRejected(writer(PngWriter.RGB, 8).setImageData(data).write());

        assertInstanceOf(DataFormatException.class, exception.getCause());
    }

    /**
     * Compressed data asking for a preset dictionary is rejected.
     */
    @Test
    void testPresetDictionary()
    {
        final byte[] data =
        {
            0x78, (byte) 0xBB, 0, 0, 0, 1, 0, 0
        };

        assertRejected(writer(PngWriter.RGB, 8).setImageData(data).write(), "corrupted");
    }

    /**
     * Compressed data holding fewer rows than the image is rejected.
     */
    @Test
    void testShortData()
    {
        final PngWriter writer = writer(PngWriter.RGB, 8);
        final byte[] rows = writer.filteredRows();
        writer.setImageData(PngWriter.zlib(Arrays.copyOf(rows, rows.length / 2)));

        assertRejected(writer.write(), "shorter");
    }

    /**
     * Image data interrupted by another chunk before the last row is rejected.
     */
    @Test
    void testInterruptedData()
    {
        final PngWriter writer = writer(PngWriter.RGB, 8);
        final byte[] data = PngWriter.zlib(writer.filteredRows());
        writer.setImageData(Arrays.copyOf(data, data.length / 2)).addAfter("tEXt", SOME);

        assertRejected(writer.write(), "shorter");
    }
}
