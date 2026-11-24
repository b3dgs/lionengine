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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Test {@link StreamProvider} against the {@link ResourceProvider} contract, and its own rules (REQ-RES-002).
 */
final class StreamProviderTest extends ResourceProviderContract
{
    @Override
    protected ResourceProvider createProvider(Map<ResourceKey, byte[]> content)
    {
        final StreamProvider provider = new StreamProvider();
        content.forEach((key, data) -> provider.register(key, () -> new ByteArrayInputStream(data)));
        return provider;
    }

    /**
     * The supplier is called once per open, and a new registration replaces the previous one.
     */
    @Test
    void testRegister()
    {
        final StreamProvider provider = new StreamProvider();
        final AtomicInteger calls = new AtomicInteger();
        provider.register(NESTED, () ->
        {
            calls.incrementAndGet();
            return new ByteArrayInputStream(new byte[]
            {
                1
            });
        });

        readAll(provider.open(NESTED));
        readAll(provider.open(NESTED));
        assertEquals(2, calls.get());

        provider.register(NESTED, () -> new ByteArrayInputStream(new byte[]
        {
            2
        }));
        assertArrayEquals(new byte[]
        {
            2
        }, readAll(provider.open(NESTED)));
        assertEquals(2, calls.get());
    }

    /**
     * A supplier that fails or gives no stream makes the key inaccessible, with the cause.
     */
    @Test
    void testInaccessible()
    {
        final StreamProvider provider = new StreamProvider();
        final IOException cause = new IOException("Download lost");
        provider.register(ROOT, () ->
        {
            throw cause;
        });
        provider.register(NESTED, () -> null);

        final ResourceException failed = assertThrows(ResourceException.class, () -> provider.open(ROOT));
        assertEquals(ResourceException.Reason.INACCESSIBLE, failed.getReason());
        assertEquals(ROOT, failed.getKey());
        assertInstanceOf(IOException.class, failed.getCause());

        final ResourceException empty = assertThrows(ResourceException.class, () -> provider.open(NESTED));
        assertEquals(ResourceException.Reason.INACCESSIBLE, empty.getReason());
        assertNull(empty.getCause());
        assertTrue(provider.has(ROOT));
    }

    /**
     * Arguments must not be {@code null}; the provider is read-only.
     */
    @Test
    void testArguments()
    {
        final StreamProvider provider = new StreamProvider();

        assertThrows(IllegalArgumentException.class, () -> provider.register(null, InputStream::nullInputStream));
        assertThrows(IllegalArgumentException.class, () -> provider.register(ROOT, null));
        assertThrows(IllegalArgumentException.class, () -> provider.has(null));
        assertThrows(IllegalArgumentException.class, () -> provider.open(null));
        assertFalse(provider.isWritable());
    }
}
