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
package com.b3dgs.lionengine.examples.graphics;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.b3dgs.lionengine.graphics.ImageDecodingException;
import com.b3dgs.lionengine.graphics.PngDecoder;
import com.b3dgs.lionengine.graphics.PngWriter;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.ResourceProvider;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Runs {@link ImageDecodingExample} on images built in memory (REQ-DOC-001, REQ-SPRITE-009, REQ-SPR-001).
 */
final class ImageDecodingExampleTest
{
    /** Pixels of the image: a 3 by 2 image with transparency. */
    private static final int[] PIXELS =
    {
        0xFF102030, 0x80405060, 0x00708090, 0xFFFFFFFF, 0xFF000000, 0x40A0B0C0
    };

    /** Game data directory of each test. */
    @TempDir
    File directory;

    /**
     * Returns resources holding content in memory.
     *
     * @param key The key of the content.
     * @param content The content.
     * @return The resources.
     * @throws IOException If the content cannot be written.
     */
    private static Resources resources(ResourceKey key, byte[] content) throws IOException
    {
        final ResourceProvider memory = new MemoryProvider();
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(content);
        }
        return new Resources(memory);
    }

    /**
     * The image is decoded with its size and pixels; the texture holds the marked copy.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final Resources resources = resources(ImageDecodingExample.IMAGE, PngWriter.rgba(3, 2, PIXELS).write());
        final RecordingBackend backend = new RecordingBackend();

        final ImageDecodingExample.Result result = ImageDecodingExample.run(resources, backend.getTextureStore());

        assertEquals(3, result.width());
        assertEquals(2, result.height());
        assertEquals(PIXELS[0], result.topLeft());
        final int[] marked = PIXELS.clone();
        marked[0] = ImageDecodingExample.MARK;
        assertArrayEquals(marked, backend.getPixels(result.texture()));
        assertEquals(3, backend.getTextureWidth(result.texture()));
    }

    /**
     * A missing image is a resource error, and content that is not an image a decoding error.
     *
     * @throws IOException If the content cannot be written.
     */
    @Test
    void testErrors() throws IOException
    {
        final Resources empty = new Resources(new MemoryProvider());
        final ResourceKey key = ImageDecodingExample.IMAGE;
        final PngDecoder decoder = new PngDecoder();
        final Resources text = resources(key, "Not an image".getBytes(StandardCharsets.UTF_8));

        final ResourceException missing = assertThrows(ResourceException.class,
                                                       () -> ImageDecodingExample.load(empty, key, decoder));
        assertEquals(ResourceException.Reason.MISSING, missing.getReason());
        assertThrows(ImageDecodingException.class, () -> ImageDecodingExample.load(text, key, decoder));
    }

    /**
     * A failure while reading the image is reported as an inaccessible resource.
     */
    @Test
    void testReadFailure()
    {
        final ResourceKey key = ImageDecodingExample.IMAGE;
        final StreamProvider streams = new StreamProvider();
        streams.register(key, () -> new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException("Read failure");
            }
        });
        final Resources resources = new Resources(streams);
        final PngDecoder decoder = new PngDecoder();

        final ResourceException exception = assertThrows(ResourceException.class,
                                                         () -> ImageDecodingExample.load(resources, key, decoder));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
    }

    /**
     * The example decodes the image of a data directory, and prints its usage without one.
     *
     * @throws IOException If the image cannot be written.
     */
    @Test
    void testMain() throws IOException
    {
        final File sprites = new File(directory, "sprites");
        assertTrue(sprites.mkdir());
        Files.write(new File(sprites, "hero.png").toPath(), PngWriter.rgba(3, 2, PIXELS).write());

        assertDoesNotThrow(() -> ImageDecodingExample.main(new String[]
        {
            directory.getPath()
        }));
        assertDoesNotThrow(() -> ImageDecodingExample.main(new String[0]));
    }
}
