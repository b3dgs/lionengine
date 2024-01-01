/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Utilities related to bundle manipulation and file extraction.
 */
public final class UtilBundle
{
    /** Path not found error. */
    private static final String ERROR_PATH_NOT_FOUND = "Path not found: ";
    /** Path invalid error. */
    private static final String ERROR_PATH_INVALID = "Path invalid: ";

    /**
     * Get the URL relative to bundle.
     * 
     * @param path The original path.
     * @return The bundle URL.
     * @throws LionEngineException If file not found in any bundle.
     */
    public static URL getUrl(String path)
    {
        for (final Bundle bundle : Platform.getProduct().getDefiningBundle().getBundleContext().getBundles())
        {
            final URL url = bundle.getEntry(path);
            if (url != null)
            {
                try
                {
                    return FileLocator.toFileURL(url);
                }
                catch (final IOException exception)
                {
                    throw new LionEngineException(exception, ERROR_PATH_INVALID + url.getPath());
                }
            }
        }
        throw new LionEngineException(ERROR_PATH_NOT_FOUND + path);
    }

    /**
     * Get the file from its name, relative to the plugin path.
     * 
     * @param path The file path.
     * @return The file instance.
     * @throws LionEngineException If path error.
     */
    public static File getFile(String path)
    {
        try
        {
            for (final Bundle bundle : Platform.getProduct().getDefiningBundle().getBundleContext().getBundles())
            {
                final File root = FileLocator.getBundleFile(bundle);
                final File file = new File(root, path);
                if (file.exists())
                {
                    return file;
                }
            }
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_PATH_INVALID + path);
        }
        throw new LionEngineException(ERROR_PATH_NOT_FOUND + path);
    }

    /**
     * Get the bundle absolute location.
     * 
     * @return The bundle absolute location.
     */
    public static File getLocation()
    {
        final String location = Platform.getProduct().getDefiningBundle().getLocation();
        final String path = location.substring(location.lastIndexOf(':') + 1);
        return new File(path).getAbsoluteFile();
    }

    /**
     * Private constructor.
     */
    private UtilBundle()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
