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
package com.b3dgs.lionengine.audio.midi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Media mock.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class MediaMock implements Media
{
    /** Media path. */
    private final String path;

    /**
     * Constructor.
     * 
     * @param name The media name.
     */
    public MediaMock(String name)
    {
        path = MediaMock.class.getResource(name).getFile();
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
        return path.substring(0, path.lastIndexOf(Medias.getSeparator()));
    }

    @Override
    public File getFile()
    {
        return new File(path);
    }

    @Override
    public InputStream getInputStream()
    {
        try
        {
            return new FileInputStream(path);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    @Override
    public OutputStream getOutputStream()
    {
        return null;
    }

    @Override
    public boolean exists()
    {
        return UtilFile.exists(path);
    }
}
