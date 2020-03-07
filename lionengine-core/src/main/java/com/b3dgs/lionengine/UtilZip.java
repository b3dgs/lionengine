/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Tools related to ZIP handling.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class UtilZip
{
    /** Error opening ZIP. */
    static final String ERROR_OPEN_ZIP = "Unable to open ZIP : ";

    /**
     * Get all entries existing in the path considering the extension.
     * 
     * @param jar The JAR to check (must not be <code>null</code>).
     * @param path The path to check (must not be <code>null</code>).
     * @param extension The extension without dot; eg: xml (must not be <code>null</code>).
     * @return The entries list.
     * @throws LionEngineException If invalid arguments or unable to open ZIP.
     */
    public static Collection<ZipEntry> getEntriesByExtension(File jar, String path, String extension)
    {
        Check.notNull(jar);
        Check.notNull(path);
        Check.notNull(extension);

        try (ZipFile zip = new ZipFile(jar))
        {
            return checkEntries(zip, path, extension);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_OPEN_ZIP + jar.getAbsolutePath());
        }
    }

    /**
     * Check all entries existing in the ZIP considering the extension.
     * 
     * @param zip The ZIP to check.
     * @param path The path to check.
     * @param extension The extension (without dot; eg: xml).
     * @return The entries list.
     */
    private static Collection<ZipEntry> checkEntries(ZipFile zip, String path, String extension)
    {
        final Collection<ZipEntry> entries = new ArrayList<>();
        final Enumeration<? extends ZipEntry> zipEntries = zip.entries();
        while (zipEntries.hasMoreElements())
        {
            final ZipEntry entry = zipEntries.nextElement();
            final String name = entry.getName();
            if (name.startsWith(path) && UtilFile.getExtension(name).equals(extension))
            {
                entries.add(entry);
            }
        }
        return entries;
    }

    /**
     * Private constructor.
     */
    private UtilZip()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
