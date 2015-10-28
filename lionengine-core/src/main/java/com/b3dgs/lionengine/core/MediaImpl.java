/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;

/**
 * Media implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class MediaImpl implements Media
{
    /** No parent. */
    private static final String NO_PARENT = Constant.EMPTY_STRING;
    /** Error open media. */
    private static final String ERROR_OPEN_MEDIA = "Cannot open the media";
    /** Error open media in JAR. */
    private static final String ERROR_OPEN_MEDIA_JAR = "Resource in JAR not found";

    /** Media path. */
    private final String path;
    /** Media parent path. */
    private final String parent;

    /**
     * Internal constructor.
     * 
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaImpl(String path)
    {
        Check.notNull(path);
        this.path = path;
        final int index = path.lastIndexOf(Medias.getSeparator());
        if (index > -1)
        {
            parent = path.substring(0, index);
        }
        else
        {
            parent = NO_PARENT;
        }
    }

    /**
     * Check if media exists from jar.
     * 
     * @return <code>true</code> if exists, <code>false</code> else.
     */
    private boolean existsFromJar()
    {
        try
        {
            final InputStream input = getInputStream();
            try
            {
                input.close();
            }
            catch (final IOException exception)
            {
                Verbose.exception(MediaImpl.class, "existsFromJar", exception);
            }
            return true;
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
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
        final File file;
        if (Medias.getClassResources() != null)
        {
            final URL url = Medias.getClassResources().getResource(path);
            if (url == null)
            {
                file = new File(path);
            }
            else
            {
                file = new File(url.getFile());
            }
        }
        else
        {
            file = new File(UtilFile.getPath(Medias.getResourcesDir(), path));
        }
        return file;
    }

    @Override
    public InputStream getInputStream()
    {
        final String path = UtilFile.getPath(Medias.getResourcesDir(), getPath());
        try
        {
            final Class<?> loader = Medias.getClassResources();
            if (loader != null)
            {
                final InputStream input = loader.getResourceAsStream(path);
                if (input == null)
                {
                    throw new LionEngineException(this, ERROR_OPEN_MEDIA_JAR);
                }
                return input;
            }
            return new FileInputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    @Override
    public OutputStream getOutputStream()
    {
        final String path = UtilFile.getPath(Medias.getResourcesDir(), getPath());
        try
        {
            return new FileOutputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, this, ERROR_OPEN_MEDIA);
        }
    }

    @Override
    public boolean exists()
    {
        if (Medias.getClassResources() != null)
        {
            return existsFromJar();
        }
        return getFile().exists();
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
    public boolean equals(Object obj)
    {
        if (obj instanceof Media)
        {
            final Media media = (Media) obj;
            return media.getPath().equals(path);
        }
        return false;
    }
}
