/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.utility;

import com.b3dgs.lionengine.Check;

/**
 * Represents an extension.
 * 
 * @param <T> The extension type.
 */
final class Extension<T> implements Comparable<Extension<T>>
{
    /** Extension instance. */
    private final T instance;
    /** Extension priority. */
    private final int priority;

    /**
     * Create extension.
     * 
     * @param instance The extension instance.
     * @param priority The extension priority.
     */
    Extension(T instance, int priority)
    {
        super();

        Check.notNull(instance);

        this.instance = instance;
        this.priority = priority;
    }

    /**
     * Get the extension instance.
     * 
     * @return The extension instance.
     */
    T getExtension()
    {
        return instance;
    }

    @Override
    public int compareTo(Extension<T> other)
    {
        final int value;
        if (priority < other.priority)
        {
            value = 1;
        }
        else if (priority > other.priority)
        {
            value = -1;
        }
        else
        {
            value = 0;
        }
        return value;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + instance.hashCode();
        return prime * result + priority;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof Extension))
        {
            return false;
        }
        final Extension<?> other = (Extension<?>) obj;
        return priority == other.priority && getExtension().equals(other.getExtension());
    }
}
