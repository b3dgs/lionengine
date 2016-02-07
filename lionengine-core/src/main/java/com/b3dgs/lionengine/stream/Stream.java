/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.stream;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Stream factory. Can create the following elements:
 * <ul>
 * <li>{@link FileReading}</li>
 * <li>{@link FileWriting}</li>
 * </ul>
 */
public final class Stream
{
    /**
     * Open a binary file as read only.
     * 
     * @param media The media file.
     * @return The created reader.
     * @throws LionEngineException If error when opening the media.
     */
    public static FileReading createFileReading(Media media)
    {
        return new FileReadingImpl(media);
    }

    /**
     * Open a binary file as write only.
     * 
     * @param media The media file.
     * @return The created writer.
     * @throws LionEngineException If error when opening the media.
     */
    public static FileWriting createFileWriting(Media media)
    {
        return new FileWritingImpl(media);
    }

    /**
     * Private constructor.
     */
    private Stream()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
