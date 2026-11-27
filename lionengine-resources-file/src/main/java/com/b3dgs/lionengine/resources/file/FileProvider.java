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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.ResourceProvider;

/**
 * Read-only provider of the files under a root directory, for desktop games (ADR-0007, ADR-0032, REQ-RES-002,
 * REQ-RES-006).
 *
 * <p>
 * The key {@code sprites/hero.png} is the file {@code sprites/hero.png} under the root, whatever the separator of the
 * system. Keys are case-sensitive on every system: on a case-insensitive filesystem, a key matches a file only when
 * every segment has the exact case of the file name. Directories are not resources.
 * </p>
 *
 * <p>
 * The provider never reads outside its root: keys cannot contain {@code .} or {@code ..} segments, and a file that
 * resolves outside the root through a symbolic link is present but refused, with reason
 * {@link ResourceException.Reason#INACCESSIBLE}. Thread-safe; it keeps no state but its root. Android games use the
 * Android asset provider instead.
 * </p>
 *
 * @since 11.0.0
 */
public final class FileProvider implements ResourceProvider
{
    /** Key separator. */
    private static final String SEPARATOR = "/";

    /**
     * Checks that a key is not {@code null}.
     *
     * @param key The key.
     * @throws IllegalArgumentException If the key is {@code null}.
     */
    private static void checkKey(ResourceKey key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key must not be null");
        }
    }

    /**
     * Tells whether a directory has an entry of this exact name.
     *
     * @param directory The directory.
     * @param name The entry name.
     * @return {@code true} if the directory lists the name with the same case.
     */
    private static boolean contains(File directory, String name)
    {
        final String[] names = directory.list();
        if (names != null)
        {
            for (final String entry : names)
            {
                if (entry.equals(name))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /** Root directory, canonical. */
    private final File root;
    /** Canonical path of the root followed by the system separator: every readable file starts with it. */
    private final String prefix;

    /**
     * Creates a provider of the files under a root directory.
     *
     * @param root The root directory; it must exist.
     * @throws IllegalArgumentException If {@code root} is {@code null}, is not a directory or has no canonical path.
     */
    public FileProvider(File root)
    {
        if (root == null || !root.isDirectory())
        {
            throw new IllegalArgumentException("Not a directory: " + root);
        }
        try
        {
            this.root = root.getCanonicalFile();
        }
        catch (final IOException exception)
        {
            throw new IllegalArgumentException("Root without canonical path: " + root, exception);
        }
        prefix = this.root.getPath() + File.separator;
    }

    /**
     * Returns the root directory.
     *
     * @return The canonical root directory.
     */
    public File getRoot()
    {
        return root;
    }

    /**
     * Returns the file of a key when it exists with the exact case of the key.
     *
     * @param key The key.
     * @return The file, {@code null} if there is no regular file with this exact path.
     */
    private File find(ResourceKey key)
    {
        final String[] segments = key.path().split(SEPARATOR);
        File file = root;
        int found = 0;
        while (found < segments.length && contains(file, segments[found]))
        {
            file = new File(file, segments[found]);
            found++;
        }
        if (found == segments.length && file.isFile())
        {
            return file;
        }
        return null;
    }

    /**
     * Tells whether a file resolves under the root.
     *
     * @param file The file.
     * @return {@code true} if its canonical path is under the root.
     * @throws IOException If the canonical path cannot be computed.
     */
    private boolean isUnderRoot(File file) throws IOException
    {
        return file.getCanonicalPath().startsWith(prefix);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     */
    @Override
    public boolean has(ResourceKey key)
    {
        checkKey(key);
        return find(key) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     * @throws ResourceException With reason {@link ResourceException.Reason#INACCESSIBLE} also when the file resolves
     *             outside the root.
     */
    @Override
    public InputStream open(ResourceKey key)
    {
        checkKey(key);
        final File file = find(key);
        if (file == null)
        {
            throw new ResourceException(key, ResourceException.Reason.MISSING, null);
        }
        try
        {
            if (!isUnderRoot(file))
            {
                throw new ResourceException(key,
                                            ResourceException.Reason.INACCESSIBLE,
                                            new IOException("Outside the root: " + file));
            }
            return new FileInputStream(file);
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false}: the files are game data.
     */
    @Override
    public boolean isWritable()
    {
        return false;
    }

    /**
     * Refuses writing: the provider is read-only.
     *
     * @param key The resource key.
     * @return Never returns.
     * @throws IllegalStateException Always.
     */
    @Override
    public OutputStream openWrite(ResourceKey key)
    {
        throw new IllegalStateException("Read-only provider of files: " + root);
    }
}
