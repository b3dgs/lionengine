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
package com.b3dgs.lionengine.resources;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Cache convention suite of the modules that decode resource content and cache it: graphics, audio, tile maps
 * (ADR-0005, ADR-0007, ADR-0019, ADR-0045, REQ-RES-004, REQ-RES-005, REQ-RT-005).
 *
 * <p>
 * Published in the {@code resources} test JAR: every consuming module binds it by extending this class with the type
 * of the handle its cache returns, creating its cache over the given {@link Resources}, and giving content it decodes.
 * The suite checks the convention shared by every cache: a key equal to another one finds the same decoded content,
 * which is decoded once and shared; each acquisition is released by its acquirer, and releasing a handle is idempotent;
 * the content leaves the cache with its last release and nothing is evicted before; a missing or unreadable resource
 * raises the {@link ResourceException} of the key, invalid content the decoding exception of the module, and neither
 * leaves anything cached; every stream opened is closed. Keys with a discriminator, and the immutability of the
 * decoded content, are checked by each module with its own types.
 * </p>
 *
 * @param <H> The type of the handle returned by an acquisition.
 */
public abstract class CacheConventionContract<H>
{
    /** Number of keys of the eviction test. */
    private static final int MANY = 64;

    /** The provider of the resources, holding the content of the test. */
    private FakeResourceProvider provider;

    /**
     * Creates the cache under test over resources, replacing the previous one.
     *
     * @param resources The resources the cache reads.
     */
    protected abstract void createCache(Resources resources);

    /**
     * Returns the key of a resource the cache can decode, for example with the extension of its format.
     *
     * @param name The name of the resource, without extension.
     * @return The key.
     */
    protected abstract ResourceKey key(String name);

    /**
     * Returns valid content.
     *
     * @param variant {@code 0} or {@code 1}: two different contents.
     * @return The bytes of the content.
     */
    protected abstract byte[] getContent(int variant);

    /**
     * Returns content that the cache cannot decode.
     *
     * @return The bytes of the content.
     */
    protected abstract byte[] getInvalidContent();

    /**
     * Returns the decoding exception of the module.
     *
     * @return The exception type raised for invalid content.
     */
    protected abstract Class<? extends RuntimeException> getDecodingException();

    /**
     * Acquires the decoded content of a key.
     *
     * @param key The key.
     * @return A new handle.
     */
    protected abstract H acquire(ResourceKey key);

    /**
     * Releases a handle.
     *
     * @param handle The handle.
     */
    protected abstract void release(H handle);

    /**
     * Tells whether two handles share the same decoded content.
     *
     * @param first The first handle.
     * @param second The second handle.
     * @return {@code true} if they share it.
     */
    protected abstract boolean isShared(H first, H second);

    /**
     * Returns the number of decoded contents in the cache.
     *
     * @return The number of cached contents.
     */
    protected abstract int getCachedCount();

    /**
     * Creates the provider and the cache of each test.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(key("first"), getContent(0));
        provider.put(key("second"), getContent(1));
        createCache(new Resources(provider));
    }

    /**
     * Two equal keys find the same content, decoded once and shared; the stream is closed.
     */
    @Test
    void testEqualKeysShare()
    {
        final H first = acquire(key("first"));
        final H again = acquire(key("first"));

        assertTrue(isShared(first, again));
        assertEquals(1, provider.getOpened());
        assertEquals(0, provider.getUnclosed());
        assertEquals(1, getCachedCount());
    }

    /**
     * Different keys find different content.
     */
    @Test
    void testDifferentKeys()
    {
        final H first = acquire(key("first"));
        final H second = acquire(key("second"));

        assertFalse(isShared(first, second));
        assertEquals(2, getCachedCount());
    }

    /**
     * The content stays cached until its last handle is released, then the next acquisition decodes it again.
     */
    @Test
    void testRelease()
    {
        final H first = acquire(key("first"));
        final H again = acquire(key("first"));

        release(first);
        assertEquals(1, getCachedCount());
        release(again);
        assertEquals(0, getCachedCount());

        acquire(key("first"));
        assertEquals(2, provider.getOpened());
        assertEquals(1, getCachedCount());
    }

    /**
     * Releasing a handle twice has no effect and does not throw, so it never releases another acquisition.
     */
    @Test
    void testIdempotentRelease()
    {
        final H first = acquire(key("first"));
        final H again = acquire(key("first"));

        release(first);
        assertDoesNotThrow(() -> release(first));
        assertEquals(1, getCachedCount());
        release(again);
        assertDoesNotThrow(() -> release(again));
        assertEquals(0, getCachedCount());
    }

    /**
     * Nothing is evicted: every content stays cached while it has a handle.
     */
    @Test
    void testNoEviction()
    {
        final List<H> handles = new ArrayList<>();
        for (int i = 0; i < MANY; i++)
        {
            final ResourceKey key = key("many-" + i);
            provider.put(key, getContent(i % 2));
            handles.add(acquire(key));
        }
        for (int i = 0; i < MANY; i++)
        {
            assertTrue(isShared(handles.get(i), acquire(key("many-" + i))));
        }

        assertEquals(MANY, getCachedCount());
        assertEquals(MANY, provider.getOpened());
    }

    /**
     * A missing resource raises the resource exception of its key, and nothing is cached.
     */
    @Test
    void testMissing()
    {
        final ResourceKey key = key("absent");

        final ResourceException exception = assertThrows(ResourceException.class, () -> acquire(key));
        assertEquals(ResourceException.Reason.MISSING, exception.getReason());
        assertEquals(key, exception.getKey());
        assertEquals(0, getCachedCount());
    }

    /**
     * An unreadable resource raises the resource exception of its key, and nothing is cached.
     */
    @Test
    void testInaccessible()
    {
        final ResourceKey key = key("unreadable");
        provider.failOn(key);

        final ResourceException exception = assertThrows(ResourceException.class, () -> acquire(key));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
        assertEquals(key, exception.getKey());
        assertEquals(0, getCachedCount());
    }

    /**
     * Invalid content raises the decoding exception of the module, closes the stream, and caches nothing; the key can
     * be acquired again once its content is fixed.
     */
    @Test
    void testInvalid()
    {
        final ResourceKey key = key("invalid");
        provider.put(key, getInvalidContent());

        assertThrows(getDecodingException(), () -> acquire(key));
        assertEquals(0, provider.getUnclosed());
        assertEquals(0, getCachedCount());

        provider.put(key, getContent(0));
        acquire(key);
        assertEquals(1, getCachedCount());
    }
}
