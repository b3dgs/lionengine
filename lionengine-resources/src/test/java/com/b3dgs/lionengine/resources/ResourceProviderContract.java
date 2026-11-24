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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

/**
 * Contract suite of {@link ResourceProvider} (ADR-0007, ADR-0032, ADR-0045, REQ-RES-001, REQ-RES-003, REQ-RES-005,
 * REQ-RES-006).
 *
 * <p>
 * Published in the {@code resources} test JAR: every provider binds it by extending this class and creating a provider
 * that holds the given content, so that all providers behave the same for the code that reads through them. Writing is
 * checked according to {@link ResourceProvider#isWritable()}.
 * </p>
 */
public abstract class ResourceProviderContract
{
    /** Resource at the root, holding every byte value. */
    protected static final ResourceKey ROOT = new ResourceKey("root.bin");
    /** Resource two directories deep. */
    protected static final ResourceKey NESTED = new ResourceKey("dir/sub/nested.txt");
    /** Empty resource. */
    protected static final ResourceKey EMPTY = new ResourceKey("dir/empty.bin");
    /** Resource whose name has an upper case letter. */
    protected static final ResourceKey UPPER = new ResourceKey("dir/Case.bin");
    /** Key that no provider has. */
    protected static final ResourceKey ABSENT = new ResourceKey("dir/absent.bin");

    /** Number of reads per thread in the concurrency test. */
    private static final int READS = 200;
    /** Time limit of the concurrency test, in seconds. */
    private static final long TIMEOUT_SECONDS = 30L;

    /**
     * Returns the content every bound provider holds: {@link #ROOT}, {@link #NESTED}, {@link #EMPTY} and
     * {@link #UPPER}.
     *
     * @return The content by key, in a new map.
     */
    protected static Map<ResourceKey, byte[]> content()
    {
        final byte[] all = new byte[256];
        for (int i = 0; i < all.length; i++)
        {
            all[i] = (byte) i;
        }
        final Map<ResourceKey, byte[]> content = new LinkedHashMap<>();
        content.put(ROOT, all);
        content.put(NESTED, "Nested text, UTF-8: é".getBytes(StandardCharsets.UTF_8));
        content.put(EMPTY, new byte[0]);
        content.put(UPPER, new byte[]
        {
            1, 2, 3
        });
        return content;
    }

    /**
     * Reads a stream to its end, then closes it.
     *
     * @param stream The stream.
     * @return The bytes read.
     */
    protected static byte[] readAll(InputStream stream)
    {
        try (InputStream input = stream)
        {
            return input.readAllBytes();
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Creates a provider that holds the given content and nothing else.
     *
     * @param content The content by key.
     * @return The provider.
     */
    protected abstract ResourceProvider createProvider(Map<ResourceKey, byte[]> content);

    /**
     * The provider has its keys only: not a missing key, not a directory, not a key that differs by case.
     */
    @Test
    void testHas()
    {
        final ResourceProvider provider = createProvider(content());

        for (final ResourceKey key : content().keySet())
        {
            assertTrue(provider.has(key), key::path);
        }
        assertFalse(provider.has(ABSENT));
        assertFalse(provider.has(new ResourceKey("dir")));
        assertFalse(provider.has(new ResourceKey("dir/sub")));
        assertFalse(provider.has(new ResourceKey("dir/case.bin")));
        assertFalse(provider.has(new ResourceKey("ROOT.BIN")));
    }

    /**
     * Each key opens on its exact content.
     */
    @Test
    void testOpen()
    {
        final ResourceProvider provider = createProvider(content());

        for (final Map.Entry<ResourceKey, byte[]> entry : content().entrySet())
        {
            assertArrayEquals(entry.getValue(), readAll(provider.open(entry.getKey())), entry.getKey()::path);
        }
    }

    /**
     * Each open returns a new stream, independent from the others.
     *
     * @throws IOException If a read fails.
     */
    @Test
    void testOpenNewStream() throws IOException
    {
        final ResourceProvider provider = createProvider(content());

        try (InputStream first = provider.open(ROOT))
        {
            assertEquals(0, first.read());
            assertEquals(1, first.read());
            assertArrayEquals(content().get(ROOT), readAll(provider.open(ROOT)));
            assertEquals(2, first.read());
        }
    }

    /**
     * A missing key, a directory and a key that differs by case are missing, with the key in the exception.
     */
    @Test
    void testOpenMissing()
    {
        final ResourceProvider provider = createProvider(content());

        for (final String path : List.of("dir/absent.bin", "dir", "dir/case.bin"))
        {
            final ResourceKey key = new ResourceKey(path);
            final ResourceException exception = assertThrows(ResourceException.class, () -> provider.open(key));

            assertEquals(ResourceException.Reason.MISSING, exception.getReason(), path);
            assertEquals(key, exception.getKey());
        }
    }

    /**
     * Closing a stream twice is harmless (REQ-RES-003).
     *
     * @throws IOException If a close fails.
     */
    @Test
    void testCloseTwice() throws IOException
    {
        final ResourceProvider provider = createProvider(content());
        final InputStream stream = provider.open(NESTED);

        stream.close();
        assertDoesNotThrow(stream::close);
    }

    /**
     * A writable provider creates and replaces content, readable once the stream is closed; a read-only provider
     * refuses writing.
     *
     * @throws IOException If a write fails.
     */
    @Test
    void testWrite() throws IOException
    {
        final ResourceProvider provider = createProvider(content());
        if (!provider.isWritable())
        {
            assertThrows(IllegalStateException.class, () -> provider.openWrite(ABSENT));
            assertFalse(provider.has(ABSENT));
            return;
        }
        final byte[] created = "created".getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = provider.openWrite(ABSENT))
        {
            output.write(created);
        }
        final byte[] replaced = "replaced".getBytes(StandardCharsets.UTF_8);
        try (OutputStream output = provider.openWrite(ROOT))
        {
            output.write(replaced);
        }

        assertTrue(provider.has(ABSENT));
        assertArrayEquals(created, readAll(provider.open(ABSENT)));
        assertArrayEquals(replaced, readAll(provider.open(ROOT)));
    }

    /**
     * Two threads read in parallel and always get the exact content (REQ-RES-006).
     *
     * @throws InterruptedException If interrupted while waiting for the threads.
     * @throws ExecutionException If a read fails or gives other content.
     * @throws TimeoutException If the reads take too long.
     */
    @Test
    void testConcurrentReads() throws InterruptedException, ExecutionException, TimeoutException
    {
        final ResourceProvider provider = createProvider(content());
        final Map<ResourceKey, byte[]> expected = content();
        final CountDownLatch start = new CountDownLatch(1);
        final Callable<Integer> reader = () ->
        {
            start.await();
            int reads = 0;
            for (int i = 0; i < READS; i++)
            {
                for (final Map.Entry<ResourceKey, byte[]> entry : expected.entrySet())
                {
                    assertTrue(provider.has(entry.getKey()));
                    assertArrayEquals(entry.getValue(), readAll(provider.open(entry.getKey())));
                    reads++;
                }
            }
            return Integer.valueOf(reads);
        };
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        try
        {
            final Future<Integer> first = executor.submit(reader);
            final Future<Integer> second = executor.submit(reader);
            start.countDown();

            assertEquals(READS * expected.size(), first.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).intValue());
            assertEquals(READS * expected.size(), second.get(TIMEOUT_SECONDS, TimeUnit.SECONDS).intValue());
        }
        finally
        {
            executor.shutdownNow();
        }
    }
}
