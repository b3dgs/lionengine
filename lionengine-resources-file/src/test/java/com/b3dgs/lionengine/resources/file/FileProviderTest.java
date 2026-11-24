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
package com.b3dgs.lionengine.resources.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.ResourceProvider;
import com.b3dgs.lionengine.resources.ResourceProviderContract;

/**
 * Test {@link FileProvider} against the {@link ResourceProvider} contract of the {@code resources} test JAR, and its
 * own rules: root, no escape, refused writing (REQ-RES-002, REQ-RES-003, REQ-RES-006).
 */
final class FileProviderTest extends ResourceProviderContract
{
    /**
     * Writes a file, creating its directories.
     *
     * @param file The file.
     * @param data The content.
     */
    private static void write(File file, byte[] data)
    {
        assertTrue(file.getParentFile().isDirectory() || file.getParentFile().mkdirs());
        try (OutputStream output = new FileOutputStream(file))
        {
            output.write(data);
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(exception);
        }
    }

    /** Folder of each test, holding the root and files outside it. */
    @TempDir
    File folder;

    @Override
    protected ResourceProvider createProvider(Map<ResourceKey, byte[]> content)
    {
        final File root = new File(folder, "root");
        assertTrue(root.mkdirs());
        content.forEach((key, data) -> write(new File(root, key.path()), data));
        return new FileProvider(root);
    }

    /**
     * The root must be an existing directory; it is kept canonical.
     *
     * @throws IOException If the canonical path cannot be computed.
     */
    @Test
    void testRoot() throws IOException
    {
        final File file = new File(folder, "file.bin");
        write(file, new byte[1]);

        assertThrows(IllegalArgumentException.class, () -> new FileProvider(null));
        final File absent = new File(folder, "absent");
        assertThrows(IllegalArgumentException.class, () -> new FileProvider(absent));
        assertThrows(IllegalArgumentException.class, () -> new FileProvider(file));
        assertEquals(folder.getCanonicalFile(), new FileProvider(new File(folder, ".")).getRoot());
    }

    /**
     * A file that resolves outside the root through a symbolic link is present but refused.
     *
     * @throws IOException If the link cannot be created.
     */
    @Test
    void testLinkOutsideRootRefused() throws IOException
    {
        final File outside = new File(folder, "secret.bin");
        write(outside, new byte[]
        {
            4
        });
        final ResourceProvider provider = createProvider(content());
        final File link = new File(((FileProvider) provider).getRoot(), "dir/link.bin");
        assumeTrue(createLink(link, outside), "Symbolic links not available on this system");
        final ResourceKey key = new ResourceKey("dir/link.bin");

        assertTrue(provider.has(key));
        final ResourceException exception = assertThrows(ResourceException.class, () -> provider.open(key));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
        assertEquals(key, exception.getKey());
    }

    /**
     * A link that stays under the root is followed.
     *
     * @throws IOException If the link cannot be created.
     */
    @Test
    void testLinkInsideRootFollowed() throws IOException
    {
        final ResourceProvider provider = createProvider(content());
        final File root = ((FileProvider) provider).getRoot();
        assumeTrue(createLink(new File(root, "alias.bin"), new File(root, ROOT.path())), "No symbolic links");

        assertArrayEquals(content().get(ROOT), readAll(provider.open(new ResourceKey("alias.bin"))));
    }

    /**
     * A file present but unreadable is inaccessible. Not checkable with full file permissions, as for the root user.
     */
    @Test
    void testUnreadable()
    {
        final ResourceProvider provider = createProvider(content());
        final File file = new File(((FileProvider) provider).getRoot(), NESTED.path());
        assumeTrue(file.setReadable(false, false), "Permissions not supported");
        assumeFalse(file.canRead(), "Full file permissions: an unreadable file cannot be made");

        assertTrue(provider.has(NESTED));
        final ResourceException exception = assertThrows(ResourceException.class, () -> provider.open(NESTED));
        assertEquals(ResourceException.Reason.INACCESSIBLE, exception.getReason());
    }

    /**
     * The provider is read-only and rejects {@code null} keys.
     */
    @Test
    void testReadOnlyAndArguments()
    {
        final ResourceProvider provider = createProvider(content());

        assertFalse(provider.isWritable());
        assertThrows(IllegalStateException.class, () -> provider.openWrite(ROOT));
        assertThrows(IllegalArgumentException.class, () -> provider.has(null));
        assertThrows(IllegalArgumentException.class, () -> provider.open(null));
    }

    /**
     * Creates a symbolic link.
     *
     * @param link The link to create.
     * @param target The file it points to.
     * @return {@code true} if created, {@code false} if the system refuses symbolic links.
     */
    private static boolean createLink(File link, File target)
    {
        try
        {
            Files.createSymbolicLink(link.toPath(), target.toPath());
            return true;
        }
        catch (final IOException | UnsupportedOperationException exception)
        {
            return false;
        }
    }
}
