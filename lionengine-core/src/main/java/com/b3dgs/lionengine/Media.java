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
package com.b3dgs.lionengine;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * A media represents a path to a resources located outside. This abstraction allows to load a resource from any kind of
 * location, such as <code>HDD</code>, <code>JAR</code>... It could point to a file or a directory.
 */
public interface Media
{
    /**
     * Get the media path.
     * 
     * @return The media path.
     */
    String getPath();

    /**
     * Get the media parent path.
     * 
     * @return The media parent path.
     */
    String getParentPath();

    /**
     * Get the file descriptor.
     * 
     * @return The file descriptor.
     * @throws LionEngineException If descriptor is not accessible.
     */
    File getFile();

    /**
     * Get the medias in the media path.
     * 
     * @return The media content.
     * @throws LionEngineException If not a valid directory.
     */
    Collection<Media> getMedias();

    /**
     * Get the media input stream.
     * 
     * @return The media input stream.
     * @throws LionEngineException If error when getting the stream.
     */
    InputStream getInputStream();

    /**
     * Get the media output stream.
     * 
     * @return The media output stream.
     * @throws LionEngineException If error when getting the stream.
     */
    OutputStream getOutputStream();

    /**
     * Check if the following media point to an existing target (could be file of directory).
     * 
     * @return <code>true</code> if media is an existing file or directory, <code>false</code> else.
     */
    boolean exists();
}
