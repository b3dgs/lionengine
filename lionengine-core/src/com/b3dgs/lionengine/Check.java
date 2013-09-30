/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Utility class check.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * Check.argument(value &gt; 0, &quot;Value was not positive:&quot;, String.valueOf(value));
 * final Object object = null;
 * Check.notNull(object, &quot;Object is null !&quot;);
 * </pre>
 */
public final class Check
{
    /**
     * Check if the condition if <code>true</code>. Throws a {@link LionEngineException} if <code>false</code>.
     * 
     * @param condition The condition to check.
     * @param messages The messages to put in the exception.
     */
    public static void argument(boolean condition, String... messages)
    {
        if (!condition)
        {
            throw new LionEngineException(messages);
        }
    }

    /**
     * Check if the object is not <code>null</code>. Throws a {@link LionEngineException} if <code>null</code>.
     * 
     * @param object The object to check.
     * @param messages The messages to put in the exception.
     */
    public static void notNull(Object object, String... messages)
    {
        if (object == null)
        {
            throw new LionEngineException(messages);
        }
    }

    /**
     * Private constructor.
     */
    private Check()
    {
        throw new RuntimeException();
    }
}
