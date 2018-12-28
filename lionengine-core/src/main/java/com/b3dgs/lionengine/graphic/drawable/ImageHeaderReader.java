/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Image header reader interface.
 */
interface ImageHeaderReader
{
    /**
     * Check if image is format.
     * 
     * @param media The image to check (must not be <code>null</code>).
     * @return <code>true</code> if format, <code>false</code> else.
     * @throws LionEngineException If invalid argument.
     */
    boolean is(Media media);

    /**
     * Read image header.
     * 
     * @param input The input to read (must not be <code>null</code>).
     * @return The header read.
     * @throws IOException If header cannot be read.
     * @throws LionEngineException If invalid argument.
     */
    ImageHeader readHeader(InputStream input) throws IOException;
}
