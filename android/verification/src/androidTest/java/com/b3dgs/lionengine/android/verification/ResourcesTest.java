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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.ResourceProvider;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;
import com.b3dgs.lionengine.resources.file.FileProvider;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@code resources}, {@code resources-memory} and {@code resources-file} on Android API 21 (ADR-0044): keys, the
 * provider contract on the memory provider and on the file provider over the application cache folder, the
 * application-stream provider and the provider chain. Expected values are the ones of the JVM tests
 * (ResourceKeyTest, ResourceProviderContract, StreamProviderTest, ResourcesTest).
 */
@RunWith(AndroidJUnit4.class)
public final class ResourcesTest
{
    /** Resource at the root, holding every byte value. */
    private static final ResourceKey ROOT = new ResourceKey("root.bin");
    /** Resource two directories deep. */
    private static final ResourceKey NESTED = new ResourceKey("dir/sub/nested.txt");
    /** Empty resource. */
    private static final ResourceKey EMPTY = new ResourceKey("dir/empty.bin");
    /** Resource whose name has an upper case letter. */
    private static final ResourceKey UPPER = new ResourceKey("dir/Case.bin");
    /** Key that no provider has. */
    private static final ResourceKey ABSENT = new ResourceKey("dir/absent.bin");
    /** Size of a copy buffer. */
    private static final int BUFFER = 64;

    /**
     * Returns the content of the provider contract.
     *
     * @return The content by key, in a new map.
     */
    private static Map<ResourceKey, byte[]> content()
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
        content.put(UPPER, new byte[] {1, 2, 3});
        return content;
    }

    /**
     * Reads a stream to its end with the API 21 methods only, then closes it.
     *
     * @param stream The stream.
     * @return The bytes read.
     * @throws IOException If a read fails.
     */
    private static byte[] readAll(InputStream stream) throws IOException
    {
        try (InputStream input = stream; ByteArrayOutputStream output = new ByteArrayOutputStream())
        {
            final byte[] buffer = new byte[BUFFER];
            int read = input.read(buffer);
            while (read >= 0)
            {
                output.write(buffer, 0, read);
                read = input.read(buffer);
            }
            return output.toByteArray();
        }
    }

    /**
     * Writes bytes and closes the stream.
     *
     * @param stream The stream.
     * @param data The bytes.
     * @throws IOException If a write fails.
     */
    private static void writeAll(OutputStream stream, byte[] data) throws IOException
    {
        try (OutputStream output = stream)
        {
            output.write(data);
        }
    }

    /**
     * Deletes a file or a folder with its content.
     *
     * @param file The file or folder.
     */
    private static void delete(File file)
    {
        final File[] children = file.listFiles();
        if (children != null)
        {
            for (final File child : children)
            {
                delete(child);
            }
        }
        assertTrue(file.getPath(), !file.exists() || file.delete());
    }

    /**
     * Checks the read part of the provider contract on a provider holding {@link #content()}.
     *
     * @param provider The provider.
     * @throws IOException If a read fails.
     */
    private static void checkContract(ResourceProvider provider) throws IOException
    {
        for (final Map.Entry<ResourceKey, byte[]> entry : content().entrySet())
        {
            assertTrue(entry.getKey().path(), provider.has(entry.getKey()));
            assertArrayEquals(entry.getKey().path(), entry.getValue(), readAll(provider.open(entry.getKey())));
        }
        assertFalse(provider.has(ABSENT));
        assertFalse(provider.has(new ResourceKey("dir/sub")));
        assertFalse(provider.has(new ResourceKey("dir/case.bin")));
        for (final String path : new String[] {"dir/absent.bin", "dir", "dir/case.bin"})
        {
            final ResourceKey key = new ResourceKey(path);
            final ResourceException exception = assertThrows(ResourceException.class, () -> provider.open(key));
            assertSame(path, ResourceException.Reason.MISSING, exception.getReason());
            assertEquals(key, exception.getKey());
        }
        try (InputStream first = provider.open(ROOT))
        {
            assertEquals(0, first.read());
            assertArrayEquals(content().get(ROOT), readAll(provider.open(ROOT)));
            assertEquals(1, first.read());
        }
    }

    /**
     * Creates the test.
     */
    public ResourcesTest()
    {
        super();
    }

    /**
     * Valid paths are kept as given, invalid ones are rejected, and the case matters.
     */
    @Test
    public void testKeys()
    {
        for (final String path : new String[] {"hero.png", "sprites/hero.png", "Sprites/Hero.PNG", ".hidden", "a/..b"})
        {
            assertEquals(path, new ResourceKey(path).path());
        }
        for (final String path : new String[] {"", "/a", "a/", "a//b", ".", "..", "a/../b", "a\\b", "/"})
        {
            assertThrows(path, IllegalArgumentException.class, () -> new ResourceKey(path));
        }
        assertEquals(new ResourceKey("sprites/hero.png"), new ResourceKey("sprites/hero.png"));
        assertFalse(new ResourceKey("sprites/hero.png").equals(new ResourceKey("sprites/Hero.png")));
    }

    /**
     * The memory provider fulfills the provider contract, its content written through its write streams.
     *
     * @throws IOException If a stream fails.
     */
    @Test
    public void testMemoryProvider() throws IOException
    {
        final MemoryProvider provider = new MemoryProvider();
        assertTrue(provider.isWritable());
        for (final Map.Entry<ResourceKey, byte[]> entry : content().entrySet())
        {
            writeAll(provider.openWrite(entry.getKey()), entry.getValue());
        }
        checkContract(provider);
        writeAll(provider.openWrite(NESTED), new byte[] {4});
        assertArrayEquals(new byte[] {4}, readAll(provider.open(NESTED)));
    }

    /**
     * The file provider fulfills the provider contract on the file system of the device, read only.
     *
     * @throws IOException If a file cannot be written.
     */
    @Test
    public void testFileProvider() throws IOException
    {
        final File cache = InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir();
        final File folder = new File(cache, "file-provider");
        delete(folder);
        final File root = new File(folder, "root");
        for (final Map.Entry<ResourceKey, byte[]> entry : content().entrySet())
        {
            final File file = new File(root, entry.getKey().path());
            assertTrue(file.getParentFile().isDirectory() || file.getParentFile().mkdirs());
            writeAll(new FileOutputStream(file), entry.getValue());
        }
        try
        {
            final FileProvider provider = new FileProvider(root);
            assertEquals(root.getCanonicalFile(), provider.getRoot());
            assertFalse(provider.isWritable());
            checkContract(provider);
            assertThrows(IllegalStateException.class, () -> provider.openWrite(ABSENT));
            assertThrows(IllegalArgumentException.class, () -> new FileProvider(new File(folder, "absent")));
        }
        finally
        {
            delete(folder);
        }
    }

    /**
     * The first provider holding a key wins; the application-stream provider opens a new stream at each call.
     *
     * @throws IOException If a stream fails.
     */
    @Test
    public void testChain() throws IOException
    {
        final MemoryProvider mods = new MemoryProvider();
        writeAll(mods.openWrite(NESTED), "mod".getBytes(StandardCharsets.UTF_8));
        final StreamProvider application = new StreamProvider();
        final int[] calls = new int[1];
        application.register(NESTED, () -> new ByteArrayInputStream("base".getBytes(StandardCharsets.UTF_8)));
        application.register(ROOT, () ->
        {
            calls[0]++;
            return new ByteArrayInputStream(new byte[] {1});
        });
        final Resources resources = new Resources(mods, application);

        assertArrayEquals("mod".getBytes(StandardCharsets.UTF_8), readAll(resources.open(NESTED)));
        assertArrayEquals(new byte[] {1}, readAll(resources.open(ROOT)));
        assertArrayEquals(new byte[] {1}, readAll(resources.open(ROOT)));
        assertEquals(2, calls[0]);
        assertFalse(resources.exists(ABSENT));
        final ResourceException exception = assertThrows(ResourceException.class, () -> resources.open(ABSENT));
        assertSame(ResourceException.Reason.MISSING, exception.getReason());
    }
}
