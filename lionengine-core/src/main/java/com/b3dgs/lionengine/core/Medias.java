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

import com.b3dgs.lionengine.LionEngineException;

/**
 * Implementation provider for the {@link FactoryMedia}.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Medias
{
    /** Factory media implementation. */
    private static FactoryMedia factoryMedia;

    /**
     * Set the media factory used.
     * 
     * @param factoryMedia The media factory used.
     */
    public static synchronized void setFactoryMedia(FactoryMedia factoryMedia)
    {
        Medias.factoryMedia = factoryMedia;
    }

    /**
     * Create a media.
     * 
     * @param path The media path.
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String path) throws LionEngineException
    {
        return factoryMedia.create(path);
    }

    /**
     * Create a media from an abstract path.
     * 
     * @param path The media path.
     * @return The media instance.
     * @throws LionEngineException If path is <code>null</code>.
     */
    public static synchronized Media create(String... path) throws LionEngineException
    {
        return factoryMedia.create(path);
    }

    /**
     * Get the path separator.
     * 
     * @return The path separator.
     */
    public static synchronized String getSeparator()
    {
        return factoryMedia.getSeparator();
    }

    /**
     * Set the path separator.
     * 
     * @param separator The path separator.
     */
    public static synchronized void setSeparator(String separator)
    {
        factoryMedia.setSeparator(separator);
    }

    /**
     * Private constructor.
     */
    private Medias()
    {
        // Utility class
    }
}
