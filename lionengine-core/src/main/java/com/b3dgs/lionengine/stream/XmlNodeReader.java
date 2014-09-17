/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * The reading capability of an {@link XmlNode}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface XmlNodeReader
{
    /**
     * Read a boolean.
     * 
     * @param attribute The boolean name.
     * @return The boolean value.
     * @throws LionEngineException If error when reading.
     */
    boolean readBoolean(String attribute) throws LionEngineException;

    /**
     * Read a byte.
     * 
     * @param attribute The integer name.
     * @return The byte value.
     * @throws LionEngineException If error when reading.
     */
    byte readByte(String attribute) throws LionEngineException;

    /**
     * Read a short.
     * 
     * @param attribute The integer name.
     * @return The short value.
     * @throws LionEngineException If error when reading.
     */
    short readShort(String attribute) throws LionEngineException;

    /**
     * Read an integer.
     * 
     * @param attribute The integer name.
     * @return The integer value.
     * @throws LionEngineException If error when reading.
     */
    int readInteger(String attribute) throws LionEngineException;

    /**
     * Read a long.
     * 
     * @param attribute The float name.
     * @return The long value.
     * @throws LionEngineException If error when reading.
     */
    long readLong(String attribute) throws LionEngineException;

    /**
     * Read a float.
     * 
     * @param attribute The float name.
     * @return The float value.
     * @throws LionEngineException If error when reading.
     */
    float readFloat(String attribute) throws LionEngineException;

    /**
     * Read a double.
     * 
     * @param attribute The double name.
     * @return The double value.
     * @throws LionEngineException If error when reading.
     */
    double readDouble(String attribute) throws LionEngineException;

    /**
     * Read a string. If the read string is equal to {@link XmlNode#NULL}, <code>null</code> will be returned instead.
     * 
     * @param attribute The string name.
     * @return The string value.
     * @throws LionEngineException If error when reading.
     */
    String readString(String attribute) throws LionEngineException;
}
