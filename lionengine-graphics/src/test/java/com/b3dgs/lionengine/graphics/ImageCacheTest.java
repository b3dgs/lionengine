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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Test {@link ImageCache} and {@link Image}: decoding through the registered decoders, sharing, textures, derived
 * images, released handles and errors (REQ-SPRITE-002, REQ-SPR-001, REQ-RES-004, REQ-RES-005, ADR-0026, ADR-0047).
 */
final class ImageCacheTest
{
    /** Key of the test image. */
    private static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Pixels of the test image, 3 by 2. */
    private static final int[] PIXELS =
    {
        0xFF102030, 0x80405060, 0x00708090, 0xFFFFFFFF, 0xFF000000, 0x40A0B0C0
    };
    /** Bytes of the test image: 3 by 2 pixels, 4 bytes each. */
    private static final long HERO_BYTES = 24L;
    /** Discriminator of a derived image. */
    private static final String RED = "red";

    /** Provider of the images. */
    private FakeResourceProvider provider;
    /** Texture store. */
    private RecordingBackend backend;
    /** Cache under test. */
    private ImageCache cache;

    /**
     * Creates the cache of each test over a provider holding {@link #HERO}.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(HERO, PngWriter.rgba(3, 2, PIXELS).write());
        backend = new RecordingBackend();
        cache = new ImageCache(new Resources(provider), backend);
    }

    /**
     * A loaded image gives its key, size, pixels and a texture holding its pixels.
     */
    @Test
    void testLoad()
    {
        final Image image = cache.load(HERO);

        assertEquals(HERO, image.getKey());
        assertEquals(ImageCache.DECODED, image.getDiscriminator());
        assertEquals(3, image.getWidth());
        assertEquals(2, image.getHeight());
        assertEquals(PIXELS[1], image.getPixel(1, 0));
        assertEquals(PIXELS[5], image.getPixel(2, 1));
        assertArrayEquals(PIXELS, backend.getPixels(image.getTexture()));
        assertEquals(3, backend.getTextureWidth(image.getTexture()));
        assertFalse(image.isReleased());
        assertTrue(cache.has(HERO, ImageCache.DECODED));
    }

    /**
     * The texture is shared by every handle, prepared on request, and released with the last handle only.
     */
    @Test
    void testTexture()
    {
        final Image first = cache.load(HERO);
        final Image second = cache.load(HERO);
        final int texture = first.getTexture();

        assertEquals(texture, second.getTexture());
        first.prepare();
        assertTrue(backend.isPrepared(texture));

        first.release();
        assertTrue(backend.isAlive(texture));
        second.release();
        assertFalse(backend.isAlive(texture));
        assertFalse(cache.has(HERO, ImageCache.DECODED));
        assertEquals(0, cache.size());
    }

    /**
     * A released handle keeps its key and discriminator only.
     */
    @Test
    void testReleasedHandle()
    {
        final Image image = cache.load(HERO);
        final Image other = cache.load(HERO);
        image.release();

        assertTrue(image.isReleased());
        assertEquals(HERO, image.getKey());
        assertEquals(ImageCache.DECODED, image.getDiscriminator());
        assertThrows(IllegalStateException.class, image::getWidth);
        assertThrows(IllegalStateException.class, image::getHeight);
        assertThrows(IllegalStateException.class, () -> image.getPixel(0, 0));
        assertThrows(IllegalStateException.class, image::copyToBuffer);
        assertThrows(IllegalStateException.class, image::getTexture);
        assertThrows(IllegalStateException.class, image::prepare);
        assertEquals(3, other.getWidth());
    }

    /**
     * A pixel outside the image is rejected.
     *
     * @param x The column.
     * @param y The row.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "-1, 0", "3, 0", "0, -1", "0, 2"
    })
    void testPixelOutside(int x, int y)
    {
        final Image image = cache.load(HERO);

        assertThrows(IndexOutOfBoundsException.class, () -> image.getPixel(x, y));
    }

    /**
     * A modifiable copy changes nothing in the image, and becomes a derived image through a copy (REQ-SPR-001).
     */
    @Test
    void testDerivedImage()
    {
        final Image hero = cache.load(HERO);
        final PixelBuffer copy = hero.copyToBuffer();
        copy.setPixel(0, 0, 0xFFFF0000);

        final Image red = cache.add(HERO, RED, copy);
        copy.setPixel(1, 0, 0xFFFF0000);

        assertEquals(PIXELS[0], hero.getPixel(0, 0));
        assertEquals(0xFFFF0000, red.getPixel(0, 0));
        assertEquals(PIXELS[1], red.getPixel(1, 0));
        assertEquals(RED, red.getDiscriminator());
        assertNotEquals(hero.getTexture(), red.getTexture());
        assertTrue(cache.has(HERO, RED));
        assertEquals(2, cache.size());
        assertEquals(1, provider.getOpened());
    }

    /**
     * A cached image, decoded or derived, is acquired again without reading anything; one not cached is refused.
     */
    @Test
    void testAcquire()
    {
        final Image hero = cache.load(HERO);
        final Image red = cache.add(HERO, RED, hero.copyToBuffer());
        final ResourceKey absent = new ResourceKey("absent.png");

        assertEquals(hero.getTexture(), cache.acquire(HERO, ImageCache.DECODED).getTexture());
        assertEquals(red.getTexture(), cache.acquire(HERO, RED).getTexture());
        assertEquals(1, provider.getOpened());
        assertThrows(IllegalStateException.class, () -> cache.acquire(HERO, "blue"));
        assertThrows(IllegalStateException.class, () -> cache.acquire(absent, RED));
    }

    /**
     * A derived image is added once per key and discriminator, and never with the discriminator of the decoded image.
     */
    @Test
    void testAddRules()
    {
        final PixelBuffer pixels = new PixelBuffer(1, 1);
        cache.add(HERO, RED, pixels);

        assertThrows(IllegalStateException.class, () -> cache.add(HERO, RED, pixels));
        assertThrows(IllegalArgumentException.class, () -> cache.add(HERO, ImageCache.DECODED, pixels));
    }

    /**
     * A derived image leaves the cache with its last handle, independently of the decoded image.
     */
    @Test
    void testDerivedRelease()
    {
        final Image hero = cache.load(HERO);
        final Image red = cache.add(HERO, RED, hero.copyToBuffer());
        final int texture = red.getTexture();

        red.release();

        assertFalse(cache.has(HERO, RED));
        assertFalse(backend.isAlive(texture));
        assertTrue(cache.has(HERO, ImageCache.DECODED));
    }

    /**
     * A key without a registered decoder is refused with the key in the message, and caches nothing.
     *
     * @param path The path of the key.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "sprites/hero.jpg", "sprites/hero", "sprites.v2/hero", "sprites/hero.PNG"
    })
    void testNoDecoder(String path)
    {
        final ResourceKey key = new ResourceKey(path);
        provider.put(key, PngWriter.rgba(1, 1, 0).write());

        final ImageDecodingException exception = assertThrows(ImageDecodingException.class, () -> cache.load(key));
        assertTrue(exception.getMessage().endsWith(path), exception.getMessage());
        assertEquals(0, cache.size());
        assertEquals(0, provider.getOpened());
    }

    /**
     * A registered decoder handles its extension, and replaces the default PNG decoder.
     */
    @Test
    void testSetDecoder()
    {
        final ResourceKey other = new ResourceKey("sprites/hero.raw");
        provider.put(other, new byte[0]);
        cache.setDecoder("raw", input -> new PixelBuffer(4, 4));
        cache.setDecoder("png", new PngDecoder(5));

        assertEquals(4, cache.load(other).getWidth());
        final ImageDecodingException exception = assertThrows(ImageDecodingException.class, () -> cache.load(HERO));
        assertTrue(exception.getMessage().startsWith(HERO.path() + ": "), exception.getMessage());
        assertTrue(exception.getMessage().contains("limits"), exception.getMessage());
        assertInstanceOf(ImageDecodingException.class, exception.getCause());
    }

    /**
     * Invalid extensions and decoders are rejected.
     */
    @Test
    void testInvalidDecoder()
    {
        final ImageDecoder decoder = new PngDecoder();

        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder("", decoder));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder(".png", decoder));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder(null, decoder));
        assertThrows(IllegalArgumentException.class, () -> cache.setDecoder("png", null));
    }

    /**
     * Invalid content is a decoding error whose message starts with the key and whose cause is the decoder's error.
     */
    @Test
    void testInvalidContent()
    {
        provider.put(HERO, "Not a PNG image".getBytes(StandardCharsets.UTF_8));

        final ImageDecodingException exception = assertThrows(ImageDecodingException.class, () -> cache.load(HERO));
        assertTrue(exception.getMessage().startsWith(HERO.path() + ": "), exception.getMessage());
        assertInstanceOf(ImageDecodingException.class, exception.getCause());
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A stream failing while it is read is an inaccessible resource, with the read failure as cause.
     */
    @Test
    void testReadFailure()
    {
        final StreamProvider streams = new StreamProvider();
        final IOException failure = new IOException("Read failure");
        streams.register(HERO, () -> new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw failure;
            }
        });
        final ImageCache failing = new ImageCache(new Resources(streams), backend);

        final ResourceException exception = assertThrows(ResourceException.class, () -> failing.load(HERO));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
        assertSame(failure, exception.getCause());
        assertEquals(0, failing.size());
    }

    /**
     * {@code null} arguments are rejected.
     */
    @Test
    void testNullArguments()
    {
        final Resources resources = new Resources(provider);
        final PixelBuffer pixels = new PixelBuffer(1, 1);

        assertThrows(IllegalArgumentException.class, () -> new ImageCache(null, backend));
        assertThrows(IllegalArgumentException.class, () -> new ImageCache(resources, null));
        assertThrows(IllegalArgumentException.class, () -> cache.load(null));
        assertThrows(IllegalArgumentException.class, () -> cache.add(null, RED, pixels));
        assertThrows(IllegalArgumentException.class, () -> cache.add(HERO, null, pixels));
        assertThrows(IllegalArgumentException.class, () -> cache.add(HERO, RED, null));
        assertThrows(IllegalArgumentException.class, () -> cache.acquire(null, RED));
        assertThrows(IllegalArgumentException.class, () -> cache.acquire(HERO, null));
        assertThrows(IllegalArgumentException.class, () -> cache.has(null, RED));
        assertThrows(IllegalArgumentException.class, () -> cache.has(HERO, null));
        assertThrows(IllegalArgumentException.class, () -> cache.reload(null));
    }

    /**
     * The cache counts 4 bytes per cached pixel, decoded and derived, and has no limit by default (ADR-0055).
     */
    @Test
    void testUsedBytes()
    {
        assertEquals(ImageCache.NO_LIMIT, cache.getMaxBytes());
        assertEquals(0L, cache.getUsedBytes());

        final Image hero = cache.load(HERO);
        final Image again = cache.load(HERO);
        assertEquals(HERO_BYTES, cache.getUsedBytes());
        final Image red = cache.add(HERO, RED, new PixelBuffer(2, 2));
        assertEquals(HERO_BYTES + 16L, cache.getUsedBytes());

        hero.release();
        assertEquals(HERO_BYTES + 16L, cache.getUsedBytes());
        again.release();
        red.release();
        assertEquals(0L, cache.getUsedBytes());
    }

    /**
     * With a limit, an image that would exceed it is refused and nothing is cached; an image that fits is accepted,
     * also when it reaches the limit exactly (ADR-0055).
     */
    @Test
    void testLimit()
    {
        final ImageCache limited = new ImageCache(new Resources(provider), backend, HERO_BYTES + 8L);
        final Image hero = limited.load(HERO);

        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                                                             () -> limited.add(HERO, RED, new PixelBuffer(3, 1)));
        assertEquals("Image cache limit exceeded by sprites/hero.png: 12 bytes needed, 24 of 32 bytes used",
                     exception.getMessage());
        assertFalse(limited.has(HERO, RED));
        limited.add(HERO, RED, new PixelBuffer(2, 1));
        assertEquals(HERO_BYTES + 8L, limited.getUsedBytes());
        assertEquals(HERO_BYTES + 8L, limited.getMaxBytes());

        hero.release();
        final ResourceKey other = new ResourceKey("sprites/other.png");
        provider.put(other, PngWriter.rgba(4, 2, new int[8]).write());
        assertThrows(IllegalStateException.class, () -> limited.load(other));
        assertFalse(limited.has(other, ImageCache.DECODED));
        assertEquals(0, provider.getUnclosed());
        assertEquals(8L, limited.getUsedBytes());
    }

    /**
     * A limit must be positive.
     *
     * @param maxBytes The limit.
     */
    @ParameterizedTest
    @ValueSource(longs =
    {
        0L, -1L, Long.MIN_VALUE
    })
    void testInvalidLimit(long maxBytes)
    {
        final Resources resources = new Resources(provider);

        assertThrows(IllegalArgumentException.class, () -> new ImageCache(resources, backend, maxBytes));
    }

    /**
     * A reload gives every handle the new size, pixels and texture; the previous texture is released, derived images
     * and copies keep their pixels (ADR-0019, ADR-0055).
     */
    @Test
    void testReload()
    {
        final Image hero = cache.load(HERO);
        final Image red = cache.add(HERO, RED, hero.copyToBuffer());
        final PixelBuffer copy = hero.copyToBuffer();
        final int previous = hero.getTexture();
        provider.put(HERO, PngWriter.rgba(1, 2, 0xFF00FF00, 0xFF0000FF).write());

        cache.reload(HERO);

        assertEquals(1, hero.getWidth());
        assertEquals(2, hero.getHeight());
        assertEquals(0xFF0000FF, hero.getPixel(0, 1));
        assertNotEquals(previous, hero.getTexture());
        assertFalse(backend.isAlive(previous));
        assertArrayEquals(new int[]
        {
            0xFF00FF00, 0xFF0000FF
        }, backend.getPixels(hero.getTexture()));
        assertEquals(PIXELS[5], red.getPixel(2, 1));
        assertEquals(PIXELS[5], copy.getPixel(2, 1));
        assertEquals(HERO_BYTES + 8L, cache.getUsedBytes());
        hero.prepare();
        assertTrue(backend.isPrepared(hero.getTexture()));

        hero.release();
        assertEquals(HERO_BYTES, cache.getUsedBytes());
    }

    /**
     * A reload that would exceed the limit is refused and leaves the image unchanged; a smaller image always fits.
     */
    @Test
    void testReloadLimit()
    {
        final ImageCache limited = new ImageCache(new Resources(provider), backend, HERO_BYTES);
        final Image hero = limited.load(HERO);
        final int texture = hero.getTexture();
        provider.put(HERO, PngWriter.rgba(4, 2, new int[8]).write());

        assertThrows(IllegalStateException.class, () -> limited.reload(HERO));
        assertEquals(3, hero.getWidth());
        assertEquals(texture, hero.getTexture());
        assertTrue(backend.isAlive(texture));

        provider.put(HERO, PngWriter.rgba(1, 1, 0).write());
        limited.reload(HERO);
        assertEquals(4L, limited.getUsedBytes());
    }

    /**
     * Only the decoded image of a key is reloaded: a key holding only derived images is refused.
     */
    @Test
    void testReloadDerivedOnly()
    {
        cache.add(HERO, RED, new PixelBuffer(1, 1));

        assertThrows(IllegalStateException.class, () -> cache.reload(HERO));
        assertEquals(0, provider.getOpened());
    }

    /**
     * Another thread than the owner cannot use the cache or release and prepare its handles; reading the immutable
     * pixels of a handle stays possible (ADR-0018, ADR-0055, REQ-RT-003).
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testOtherThread() throws InterruptedException
    {
        final Image hero = cache.load(HERO);
        final PixelBuffer pixels = new PixelBuffer(1, 1);
        final ImageDecoder decoder = new PngDecoder();
        final int[] read = new int[1];

        assertOtherThreadRefused(() -> cache.add(HERO, RED, pixels));
        assertOtherThreadRefused(() -> cache.acquire(HERO, ImageCache.DECODED));
        assertOtherThreadRefused(() -> cache.has(HERO, ImageCache.DECODED));
        assertOtherThreadRefused(() -> cache.setDecoder("png", decoder));
        assertOtherThreadRefused(cache::getUsedBytes);
        assertOtherThreadRefused(hero::prepare);
        assertOtherThreadRefused(hero::release);
        final Thread reader = new Thread(() -> read[0] = hero.getPixel(2, 1));
        reader.start();
        reader.join();

        assertEquals(PIXELS[5], read[0]);
        assertFalse(hero.isReleased());
        assertFalse(backend.isPrepared(hero.getTexture()));
        assertEquals(1, cache.size());
    }

    /**
     * Runs an action on another thread and checks that it throws an {@link IllegalStateException}.
     *
     * @param action The action.
     * @throws InterruptedException If interrupted.
     */
    private static void assertOtherThreadRefused(Runnable action) throws InterruptedException
    {
        final IllegalStateException[] failure = new IllegalStateException[1];
        final Thread thread = new Thread(() ->
        {
            try
            {
                action.run();
            }
            catch (final IllegalStateException exception)
            {
                failure[0] = exception;
            }
        }, "worker");
        thread.start();
        thread.join();

        assertInstanceOf(IllegalStateException.class, failure[0]);
        assertTrue(failure[0].getMessage().startsWith("ImageCache used on the thread worker"),
                   failure[0].getMessage());
    }
}
