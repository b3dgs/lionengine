/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network;

/**
 * Type of network.
 */
public enum NetworkType
{
    /** Server side. */
    SERVER,
    /** Client side. */
    CLIENT,
    /** None. */
    NONE;

    /**
     * Get network type from name.
     * 
     * @param name The name.
     * @return The type.
     */
    public static NetworkType from(String name)
    {
        final NetworkType type;
        if (SERVER.name().equalsIgnoreCase(name))
        {
            type = SERVER;
        }
        else if (CLIENT.name().equalsIgnoreCase(name))
        {
            type = CLIENT;
        }
        else
        {
            type = NONE;
        }
        return type;
    }
}
