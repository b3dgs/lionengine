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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordingBackend;

/**
 * Test {@link ImageCache#bake(Image, ColorTransform)}: the CPU path of the color transforms, a variant computed once,
 * cached under the source and the transform, shared and released by its handles (REQ-SPRITE-004, REQ-SPRITE-002,
 * ADR-0026, ADR-0055, ADR-0056).
 */
final class ColorBakeTest
{
    /** Key of the hero image: 2 by 1 pixels, red then half-transparent green. */
    private static final ResourceKey HERO = new ResourceKey("sprites/hero.png");
    /** Pixels of the hero image. */
    private static final int[] PIXELS =
    {
        0xFFFF0000, 0x8000FF00
    };
    /** The hit flash: a white silhouette. */
    private static final ColorTransform FLASH = ColorTransform.NONE.withMask(0xFFFFFF, 1.0);

    /** The provider of the images. */
    private FakeResourceProvider provider;
    /** The render backend. */
    private RecordingBackend backend;
    /** The image cache. */
    private ImageCache cache;

    /**
     * Creates the cache of each test, with the hero image.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(HERO, PngWriter.rgba(2, 1, PIXELS.clone()).write());
        backend = new RecordingBackend();
        cache = new ImageCache(new Resources(provider), backend);
    }

    /**
     * A bake caches a variant derived from the key of the source, under the discriminator of the source followed by
     * {@code +} and the canonical text of the transform, with the pixels computed by the transform and its own
     * texture; the source never changes.
     */
    @Test
    void testBake()
    {
        final Image hero = cache.load(HERO);

        final Image flash = cache.bake(hero, FLASH);

        assertEquals(HERO, flash.getKey());
        assertEquals("+mask(#FFFFFF,1.0)", flash.getDiscriminator());
        assertEquals(2, flash.getWidth());
        assertEquals(0xFFFFFFFF, flash.getPixel(0, 0));
        assertEquals(0x80FFFFFF, flash.getPixel(1, 0));
        assertEquals(0xFFFFFFFF, backend.getPixels(flash.getTexture())[0]);
        assertNotEquals(hero.getTexture(), flash.getTexture());
        assertEquals(PIXELS[0], hero.getPixel(0, 0));
        assertEquals(2, cache.size());
        assertEquals(2L * 2L * 4L, cache.getUsedBytes());
        assertTrue(cache.has(HERO, "+mask(#FFFFFF,1.0)"));
    }

    /**
     * Every code baking the same transform of the same image shares one variant computed once; the variant leaves the
     * cache with its last handle, independently of the source.
     */
    @Test
    void testShared()
    {
        final Image hero = cache.load(HERO);
        final Image first = cache.bake(hero, FLASH);
        final Image second = cache.bake(hero, ColorTransform.NONE.withMask(0xFFFFFF, 1.0));

        assertNotSame(first, second);
        assertEquals(first.getTexture(), second.getTexture());
        assertEquals(2, cache.size());

        hero.release();
        first.release();

        assertEquals(1, cache.size());
        assertEquals(0xFFFFFFFF, second.getPixel(0, 0));
        final int texture = second.getTexture();
        second.release();
        assertFalse(backend.isAlive(texture));
        assertEquals(0, cache.size());
    }

    /**
     * An identity transform gives a new handle on the source image itself.
     */
    @Test
    void testIdentity()
    {
        final Image hero = cache.load(HERO);

        final Image same = cache.bake(hero, ColorTransform.NONE);

        assertNotSame(hero, same);
        assertEquals(hero.getTexture(), same.getTexture());
        assertEquals(ImageCache.DECODED, same.getDiscriminator());
        assertEquals(1, cache.size());
        hero.release();
        assertFalse(same.isReleased());
        assertEquals(1, cache.size());
    }

    /**
     * A derived image is baked like a decoded one, under its own discriminator followed by the transform; a variant
     * can be baked again.
     */
    @Test
    void testDerived()
    {
        final PixelBuffer pixels = new PixelBuffer(1, 1);
        pixels.setPixel(0, 0, 0xFF000000);
        final Image damaged = cache.add(HERO, "damaged", pixels);

        final Image flash = cache.bake(damaged, FLASH);
        final Image faded = cache.bake(flash, ColorTransform.NONE.withAlpha(0.5));

        assertEquals("damaged+mask(#FFFFFF,1.0)", flash.getDiscriminator());
        assertEquals("damaged+mask(#FFFFFF,1.0)+alpha(0.5)", faded.getDiscriminator());
        assertEquals(0xFFFFFFFF, flash.getPixel(0, 0));
        assertEquals(0x80FFFFFF, faded.getPixel(0, 0));
        assertEquals(3, cache.size());
    }

    /**
     * A development reload of the source leaves its variants unchanged.
     */
    @Test
    void testReload()
    {
        final Image hero = cache.load(HERO);
        final Image gray = cache.bake(hero, ColorTransform.NONE.withGrading(ColorMatrix.saturation(0.0)));

        provider.put(HERO, PngWriter.rgba(1, 1, new int[]
        {
            0xFF0000FF
        }).write());
        cache.reload(HERO);

        assertEquals(1, hero.getWidth());
        assertEquals(2, gray.getWidth());
        assertEquals(0xFF363636, gray.getPixel(0, 0));
    }

    /**
     * A variant that would exceed the limit of the cache is refused and nothing is cached.
     */
    @Test
    void testLimit()
    {
        final ImageCache limited = new ImageCache(new Resources(provider), backend, 2L * 4L + 4L);
        final Image hero = limited.load(HERO);

        assertThrows(IllegalStateException.class, () -> limited.bake(hero, FLASH));
        assertEquals(1, limited.size());
        assertEquals(2L * 4L, limited.getUsedBytes());
    }

    /**
     * {@code null} arguments, a released image and an image of another cache are refused.
     */
    @Test
    void testInvalid()
    {
        final Image hero = cache.load(HERO);
        final Image other = new ImageCache(new Resources(provider), backend).load(HERO);
        final Image released = cache.load(HERO);
        released.release();

        assertThrows(IllegalArgumentException.class, () -> cache.bake(null, FLASH));
        assertThrows(IllegalArgumentException.class, () -> cache.bake(hero, null));
        assertThrows(IllegalArgumentException.class, () -> cache.bake(other, FLASH));
        assertThrows(IllegalStateException.class, () -> cache.bake(released, FLASH));
        assertEquals(1, cache.size());
    }

    /**
     * A bake on another thread than the owner of the cache is refused before anything is computed (ADR-0055).
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testOtherThread() throws InterruptedException
    {
        final Image hero = cache.load(HERO);
        final IllegalStateException[] failure = new IllegalStateException[1];
        final Thread thread = new Thread(() ->
        {
            try
            {
                cache.bake(hero, FLASH);
            }
            catch (final IllegalStateException exception)
            {
                failure[0] = exception;
            }
        }, "worker");

        thread.start();
        thread.join();

        assertInstanceOf(IllegalStateException.class, failure[0]);
        assertEquals(1, cache.size());
    }
}
