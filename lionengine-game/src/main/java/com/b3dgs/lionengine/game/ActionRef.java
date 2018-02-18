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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Action reference structure.
 */
public class ActionRef
{
    /** Action path media. */
    private final String path;
    /** Cancel flag. */
    private final boolean cancel;
    /** Associated actions. */
    private final Collection<ActionRef> refs;

    /**
     * Create action reference.
     * 
     * @param path The action media path.
     * @param cancel The cancel flag.
     * @param refs The associated actions.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public ActionRef(String path, boolean cancel, Collection<ActionRef> refs)
    {
        super();

        Check.notNull(path);
        Check.notNull(refs);

        this.path = path;
        this.cancel = cancel;
        this.refs = new ArrayList<>(refs);
    }

    /**
     * Get media path.
     * 
     * @return The media path.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Get the cancel flag.
     * 
     * @return The cancel flag.
     */
    public boolean getCancel()
    {
        return cancel;
    }

    /**
     * Get the associated actions as read only.
     * 
     * @return The associated actions.
     */
    public Collection<ActionRef> getRefs()
    {
        return Collections.unmodifiableCollection(refs);
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (cancel ? 1231 : 1237);
        result = prime * result + path.hashCode();
        result = prime * result + refs.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        final ActionRef other = (ActionRef) obj;
        return other.cancel == cancel && other.path.equals(path) && other.refs.equals(refs);
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [path=")
                                  .append(path)
                                  .append(", cancel=")
                                  .append(cancel)
                                  .append(", refs=")
                                  .append(refs)
                                  .append("]")
                                  .toString();
    }
}
