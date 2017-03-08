/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.core.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Media implementation.
 */
final class MediaAndroid implements Media
{
    /** No parent. */
    private static final String NO_PARENT = Constant.EMPTY_STRING;
    /** Error get stream. */
    private static final String ERROR_GET_STREAM = "Error on getting stream of: ";

    /** Asset manager. */
    private final AssetManager assetManager;
    /** Content resolver. */
    private final ContentResolver contentResolver;
    /** Separator. */
    private final String separator;
    /** Resources directory. */
    private final String resourcesDir;
    /** Media path. */
    private final String path;
    /** Media parent path. */
    private final String parent;
    /** File reference. */
    private final File file;

    /**
     * Internal constructor.
     * 
     * @param assetManager The asset manager.
     * @param contentResolver The content resolver.
     * @param separator The path separator.
     * @param resourcesDir The resources directory.
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaAndroid(AssetManager assetManager,
                 ContentResolver contentResolver,
                 String separator,
                 String resourcesDir,
                 String path)
    {
        Check.notNull(assetManager);
        Check.notNull(contentResolver);
        Check.notNull(separator);
        Check.notNull(resourcesDir);
        Check.notNull(path);

        this.assetManager = assetManager;
        this.contentResolver = contentResolver;
        this.separator = separator;
        this.resourcesDir = resourcesDir;
        this.path = path;
        final int index = path.lastIndexOf(separator);
        if (index > -1)
        {
            parent = path.substring(0, index);
        }
        else
        {
            parent = NO_PARENT;
        }
        file = new File(path);
    }

    /**
     * Get file descriptor.
     * 
     * @return The file descriptor.
     */
    AssetFileDescriptor getDescriptor()
    {
        try
        {
            return assetManager.openFd(getAbsolutePath());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, this);
        }
    }

    /**
     * Get the absolute media path.
     * 
     * @return The absolute media path.
     */
    private String getAbsolutePath()
    {
        return UtilFolder.getPathSeparator(separator, resourcesDir, path);
    }

    /*
     * Media
     */

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public String getParentPath()
    {
        return parent;
    }

    @Override
    public File getFile()
    {
        return file;
    }

    @Override
    public Collection<Media> getMedias()
    {
        try
        {
            final Collection<Media> medias = new ArrayList<Media>();
            final String fullPath = getAbsolutePath();
            final String prefix = fullPath.substring(resourcesDir.length());
            for (final String file : assetManager.list(fullPath))
            {
                medias.add(Medias.create(prefix, file));
            }
            return medias;
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, this);
        }
    }

    @Override
    public InputStream getInputStream()
    {
        Check.notNull(path);

        try
        {
            return assetManager.open(getAbsolutePath());
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, this, ERROR_GET_STREAM, Constant.QUOTE, path, Constant.QUOTE);
        }
    }

    @Override
    public OutputStream getOutputStream()
    {
        Check.notNull(path);

        try
        {
            return contentResolver.openOutputStream(Uri.parse(Uri.encode(UtilFolder.getPathSeparator(separator,
                                                                                                     resourcesDir,
                                                                                                     path))));
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, this, ERROR_GET_STREAM, Constant.QUOTE, path, Constant.QUOTE);
        }
    }

    @Override
    public boolean exists()
    {
        return file.exists();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + path.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Media media = (Media) object;
        return media.getPath().equals(path);
    }

    @Override
    public String toString()
    {
        return path;
    }
}
