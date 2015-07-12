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
package com.b3dgs.lionengine;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utility class related to java reflection.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class UtilReflection
{
    /**
     * Get the parameter types as array.
     * 
     * @param arguments The arguments list.
     * @return The arguments type array.
     */
    public static Class<?>[] getParamTypes(Object... arguments)
    {
        final Collection<Object> types = new ArrayList<>();
        for (final Object argument : arguments)
        {
            types.add(argument.getClass());
        }
        final Class<?>[] typesArray = new Class<?>[types.size()];
        return types.toArray(typesArray);
    }

    /**
     * Private constructor.
     */
    private UtilReflection()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
