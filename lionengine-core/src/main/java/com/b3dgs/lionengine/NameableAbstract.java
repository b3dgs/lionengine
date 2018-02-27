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
package com.b3dgs.lionengine;

/**
 * Base implementation of a nameable object, identified with its name considered as unique.
 */
public abstract class NameableAbstract implements Nameable
{
    /** Object name. */
    private final String name;

    /**
     * Create nameable.
     * 
     * @param name The object name (must not be <code>null</code>).
     * @throws LionEngineException If name is <code>null</code>.
     */
    public NameableAbstract(String name)
    {
        super();

        Check.notNull(name);

        this.name = name;
    }

    /*
     * Nameable
     */

    @Override
    public final String getName()
    {
        return name;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Nameable other = (Nameable) object;
        return getName().equals(other.getName());
    }
}
