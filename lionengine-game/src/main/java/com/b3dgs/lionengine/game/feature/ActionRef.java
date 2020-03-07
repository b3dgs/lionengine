/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import java.util.Collection;
import java.util.function.Function;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;

/**
 * Action reference structure.
 */
public class ActionRef
{
    /** Action path media. */
    private final String path;
    /** Cancel flag. */
    private final boolean cancel;
    /** Unique flag. */
    private final boolean unique;
    /** Associated actions. */
    private final Collection<ActionRef> refs;
    /** Id supplier. */
    private final Function<Class<? extends Feature>, Feature> id;

    /**
     * Create action reference.
     * 
     * @param path The action media path (must not be <code>null</code>).
     * @param cancel The cancel flag (must not be <code>null</code>).
     * @param refs The associated actions (must not be <code>null</code>).
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public ActionRef(String path, boolean cancel, Collection<ActionRef> refs)
    {
        this(path, cancel, refs, null);
    }

    /**
     * Create action reference.
     * 
     * @param path The action media path (must not be <code>null</code>).
     * @param cancel The cancel flag (must not be <code>null</code>).
     * @param refs The associated actions (must not be <code>null</code>).
     * @param id The id supplier to handle unique instance if needed (can be <code>null</code> if non unique).
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public ActionRef(String path,
                     boolean cancel,
                     Collection<ActionRef> refs,
                     Function<Class<? extends Feature>, Feature> id)
    {
        super();

        Check.notNull(path);
        Check.notNull(refs);

        this.path = path;
        this.cancel = cancel;
        this.refs = refs;
        this.id = id;
        unique = id != null;
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
    public boolean hasCancel()
    {
        return cancel;
    }

    /**
     * Get the unique flag.
     * 
     * @return The unique flag.
     */
    public boolean isUnique()
    {
        return unique;
    }

    /**
     * Get the associated actions.
     * 
     * @return The associated actions.
     */
    public Collection<ActionRef> getRefs()
    {
        return refs;
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
        if (unique)
        {
            result = prime * result + id.apply(Identifiable.class).hashCode();
        }
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
        return other.cancel == cancel
               && other.path.equals(path)
               && other.refs.equals(refs)
               && (!unique || other.id.apply(Identifiable.class).equals(id.apply(Identifiable.class)));
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
