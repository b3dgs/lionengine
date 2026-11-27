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
package com.b3dgs.lionengine.resources.memory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.ResourceProvider;
import com.b3dgs.lionengine.resources.ResourceProviderContract;

/**
 * Test {@link MemoryProvider} against the {@link ResourceProvider} contract of the {@code resources} test JAR, and its
 * own rules (REQ-RES-002, REQ-RES-006).
 */
final class MemoryProviderTest extends ResourceProviderContract
{
    /**
     * Writes a resource and closes the stream.
     *
     * @param provider The provider.
     * @param key The key.
     * @param data The content.
     */
    private static void write(ResourceProvider provider, ResourceKey key, byte[] data)
    {
        try (OutputStream output = provider.openWrite(key))
        {
            output.write(data);
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    protected ResourceProvider createProvider(Map<ResourceKey, byte[]> content)
    {
        final MemoryProvider provider = new MemoryProvider();
        content.forEach((key, data) -> write(provider, key, data));
        return provider;
    }

    /**
     * Content is readable once the stream is closed; a stream already opened keeps its content; bytes written after
     * the first close are ignored.
     *
     * @throws IOException If a stream fails.
     */
    @Test
    void testWriteVisibleAtClose() throws IOException
    {
        final MemoryProvider provider = new MemoryProvider();
        write(provider, ROOT, new byte[]
        {
            1
        });
        final InputStream before = provider.open(ROOT);

        final OutputStream output = provider.openWrite(ROOT);
        output.write(2);
        assertArrayEquals(new byte[]
        {
            1
        }, readAll(provider.open(ROOT)));

        output.close();
        output.write(3);
        output.close();

        assertArrayEquals(new byte[]
        {
            2
        }, readAll(provider.open(ROOT)));
        assertArrayEquals(new byte[]
        {
            1
        }, readAll(before));
    }

    /**
     * An unclosed write stores nothing; {@code null} keys are rejected.
     */
    @Test
    void testUnclosedAndArguments()
    {
        final MemoryProvider provider = new MemoryProvider();
        provider.openWrite(NESTED);

        assertFalse(provider.has(NESTED));
        assertTrue(provider.isWritable());
        assertThrows(IllegalArgumentException.class, () -> provider.has(null));
        assertThrows(IllegalArgumentException.class, () -> provider.open(null));
        assertThrows(IllegalArgumentException.class, () -> provider.openWrite(null));
    }
}
