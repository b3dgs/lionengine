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
package com.b3dgs.lionengine.core.swt;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Media implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class MediaSwt implements Media
{
    /** No parent. */
    private static final String NO_PARENT = "";

    /** Media path. */
    private final String path;
    /** Media parent path. */
    private final String parent;
    /** File reference. */
    private final File file;

    /**
     * Internal constructor.
     * 
     * @param path The media path.
     * @throws LionEngineException If path in <code>null</code>.
     */
    MediaSwt(String path) throws LionEngineException
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
        file = new File(UtilFile.getPath(UtilityMedia.getRessourcesDir(), path));
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
    public InputStream getInputStream() throws LionEngineException
    {
        return UtilityMedia.getInputStream(this);
    }

    @Override
    public OutputStream getOutputStream() throws LionEngineException
    {
        return UtilityMedia.getOutputStream(this);
    }

    @Override
    public boolean exists()
    {
        return UtilityMedia.exists(this);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        if (path != null)
        {
            result = prime * result + path.hashCode();
        }
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
