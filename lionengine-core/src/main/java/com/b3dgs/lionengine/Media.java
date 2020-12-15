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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Represents a path to a resource located outside. This abstraction allows to load a resource from any kind of
 * location, such as external storage, <code>JAR</code>... Can point to a file or a directory.
 */
public interface Media extends Nameable
{
    /**
     * Get the relative media path.
     * 
     * @return The relative media path.
     */
    String getPath();

    /**
     * Get the relative media parent path.
     * 
     * @return The relative media parent path.
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
     * @return The media content (empty if nothing or invalid directory).
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

    /*
     * Nameable
     */

    /**
     * Get the media name (excluding its path).
     * 
     * @return The media name without its path.
     */
    @Override
    String getName();
}
