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
package com.b3dgs.lionengine.core.sequence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Utilities related to sequence.
 */
public final class UtilSequence
{
    /**
     * Create a sequence from its class.
     * 
     * @param nextSequence The next sequence class.
     * @param context The context reference.
     * @param arguments The arguments list.
     * @return The sequence instance.
     * @throws LionEngineException If not able to create the sequence for any reason.
     */
    public static Sequence create(Class<? extends Sequencable> nextSequence, Context context, Object... arguments)
    {
        Check.notNull(nextSequence);
        Check.notNull(context);

        try
        {
            final Class<?>[] params = getParamTypes(context, arguments);
            return UtilReflection.create(nextSequence, params, getParams(context, arguments));
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /**
     * Pause time.
     * 
     * @param delay The delay in millisecond.
     */
    public static void pause(long delay)
    {
        try
        {
            Thread.sleep(delay);
        }
        catch (@SuppressWarnings("unused") final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Get the parameter types as array.
     * 
     * @param context The context reference.
     * @param arguments The arguments list.
     * @return The arguments array.
     */
    private static Class<?>[] getParamTypes(Context context, Object... arguments)
    {
        final Collection<Object> params = new ArrayList<>();
        params.add(context);
        params.addAll(Arrays.asList(arguments));
        return UtilReflection.getParamTypes(params.toArray());
    }

    /**
     * Get the parameter as array.
     * 
     * @param context The context reference.
     * @param arguments The arguments list.
     * @return The arguments array.
     */
    private static Object[] getParams(Context context, Object... arguments)
    {
        final Collection<Object> params = new ArrayList<>(1);
        params.add(context);
        params.addAll(Arrays.asList(arguments));
        return params.toArray();
    }

    /**
     * Private constructor.
     */
    private UtilSequence()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
