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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Resources}: ordered lookup, override, errors, writing and stream ownership (REQ-RES-001, REQ-RES-003,
 * REQ-RES-006).
 */
final class ResourcesTest
{
    /** Key of the tests. */
    private static final ResourceKey KEY = new ResourceKey("data/level.json");
    /** Other key. */
    private static final ResourceKey OTHER = new ResourceKey("data/other.json");

    /**
     * Encodes a text.
     *
     * @param text The text.
     * @return The UTF-8 bytes.
     */
    private static byte[] bytes(String text)
    {
        return text.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Reads a resource as text, then closes its stream.
     *
     * @param resources The resources.
     * @param key The key.
     * @return The text.
     */
    private static String read(Resources resources, ResourceKey key)
    {
        return new String(ResourceProviderContract.readAll(resources.open(key)), StandardCharsets.UTF_8);
    }

    /**
     * The first provider that has a key wins; a key only in a later provider is found there.
     */
    @Test
    void testFirstProviderWins()
    {
        final FakeResourceProvider mods = new FakeResourceProvider(false);
        final FakeResourceProvider base = new FakeResourceProvider(false);
        mods.put(KEY, bytes("mod"));
        base.put(KEY, bytes("base"));
        base.put(OTHER, bytes("other"));
        final Resources resources = new Resources(mods, base);

        assertEquals("mod", read(resources, KEY));
        assertEquals("other", read(resources, OTHER));
        assertEquals(1, mods.getOpened());
        assertEquals(1, base.getOpened());
    }

    /**
     * Existence never throws for a missing key; opening it fails with reason {@code MISSING} and the key.
     */
    @Test
    void testMissing()
    {
        final FakeResourceProvider base = new FakeResourceProvider(false);
        base.put(OTHER, bytes("other"));
        final Resources resources = new Resources(base);

        assertTrue(resources.exists(OTHER));
        assertFalse(resources.exists(KEY));

        final ResourceException exception = assertThrows(ResourceException.class, () -> resources.open(KEY));
        assertEquals(ResourceException.Reason.MISSING, exception.getReason());
        assertEquals(KEY, exception.getKey());
        assertNull(exception.getCause());
    }

    /**
     * When the provider that has a key cannot read it, the error is reported: no fallback to the next provider.
     */
    @Test
    void testInaccessibleWithoutFallback()
    {
        final FakeResourceProvider mods = new FakeResourceProvider(false);
        final FakeResourceProvider base = new FakeResourceProvider(false);
        mods.failOn(KEY);
        base.put(KEY, bytes("base"));
        final Resources resources = new Resources(mods, base);

        assertTrue(resources.exists(KEY));
        final ResourceException exception = assertThrows(ResourceException.class, () -> resources.open(KEY));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
        assertEquals(0, base.getOpened());
    }

    /**
     * Writing goes to the first writable provider; a provider placed before it still wins for reading.
     *
     * @throws IOException If a write fails.
     */
    @Test
    void testWriteToFirstWritable() throws IOException
    {
        final FakeResourceProvider base = new FakeResourceProvider(false);
        final FakeResourceProvider saves = new FakeResourceProvider(true);
        final FakeResourceProvider spare = new FakeResourceProvider(true);
        base.put(OTHER, bytes("base"));
        final Resources resources = new Resources(base, saves, spare);

        assertTrue(resources.isWritable());
        try (OutputStream output = resources.openWrite(KEY))
        {
            output.write(bytes("saved"));
        }
        try (OutputStream output = resources.openWrite(OTHER))
        {
            output.write(bytes("hidden"));
        }

        assertEquals("saved", read(resources, KEY));
        assertTrue(saves.has(KEY));
        assertFalse(spare.has(KEY));
        assertEquals("base", read(resources, OTHER));
    }

    /**
     * Without a writable provider, writing is refused.
     */
    @Test
    void testReadOnly()
    {
        final Resources resources = new Resources(new FakeResourceProvider(false));

        assertFalse(resources.isWritable());
        assertThrows(IllegalStateException.class, () -> resources.openWrite(KEY));
    }

    /**
     * The stream belongs to the caller: {@link Resources} neither wraps nor closes it (REQ-RES-003).
     *
     * @throws IOException If a read fails.
     */
    @Test
    void testCallerOwnsStream() throws IOException
    {
        final FakeResourceProvider base = new FakeResourceProvider(false);
        base.put(KEY, bytes("abc"));
        final Resources resources = new Resources(base);

        final InputStream stream = resources.open(KEY);
        assertEquals(1, base.getUnclosed());
        assertArrayEquals(bytes("abc"), stream.readAllBytes());
        assertEquals(1, base.getUnclosed());

        stream.close();
        stream.close();
        assertEquals(0, base.getUnclosed());
    }

    /**
     * The providers are copied; {@code null} providers and keys are rejected; without provider every key is missing.
     */
    @Test
    void testArguments()
    {
        final FakeResourceProvider base = new FakeResourceProvider(false);
        base.put(KEY, bytes("base"));
        final ResourceProvider[] providers =
        {
            base
        };
        final Resources resources = new Resources(providers);
        providers[0] = new FakeResourceProvider(false);

        assertTrue(resources.exists(KEY));
        assertThrows(IllegalArgumentException.class, () -> new Resources((ResourceProvider[]) null));
        assertThrows(IllegalArgumentException.class, () -> new Resources(base, null));
        assertThrows(IllegalArgumentException.class, () -> resources.exists(null));
        assertThrows(IllegalArgumentException.class, () -> resources.open(null));
        assertThrows(IllegalArgumentException.class, () -> resources.openWrite(null));

        final Resources empty = new Resources();
        assertFalse(empty.exists(KEY));
        assertThrows(ResourceException.class, () -> empty.open(KEY));
    }
}
