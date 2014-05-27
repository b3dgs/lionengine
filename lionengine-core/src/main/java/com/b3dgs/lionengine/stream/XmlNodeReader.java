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
     */
    boolean readBoolean(String attribute);

    /**
     * Read a byte.
     * 
     * @param attribute The integer name.
     * @return The byte value.
     */
    byte readByte(String attribute);

    /**
     * Read a short.
     * 
     * @param attribute The integer name.
     * @return The short value.
     */
    short readShort(String attribute);

    /**
     * Read an integer.
     * 
     * @param attribute The integer name.
     * @return The integer value.
     */
    int readInteger(String attribute);

    /**
     * Read a long.
     * 
     * @param attribute The float name.
     * @return The long value.
     */
    long readLong(String attribute);

    /**
     * Read a float.
     * 
     * @param attribute The float name.
     * @return The float value.
     */
    float readFloat(String attribute);

    /**
     * Read a double.
     * 
     * @param attribute The double name.
     * @return The double value.
     */
    double readDouble(String attribute);

    /**
     * Read a string. If the read string is equal to {@link XmlNode#NULL}, <code>null</code> will be returned instead.
     * 
     * @param attribute The string name.
     * @return The string value.
     */
    String readString(String attribute);
}
