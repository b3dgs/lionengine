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
package com.b3dgs.lionengine.core.android;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Media implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class MediaAndroid
        implements Media
{
    /** Media path. */
    private final String path;
    /** File reference. */
    private final File file;

    /**
     * Internal constructor.
     * 
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaAndroid(String path) throws LionEngineException
    {
        Check.notNull(path);
        this.path = path;
        file = new File(path);
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
    public File getFile()
    {
        return file;
    }

    @Override
    public InputStream getInputStream() throws LionEngineException
    {
        return UtilityMedia.getStream(this, "MediaImpl", false);
    }

    @Override
    public OutputStream getOutputStream() throws LionEngineException
    {
        return UtilityMedia.getOutputStream(this, "MediaImpl", false);
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
        result = prime * result + (path == null ? 0 : path.hashCode());
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
