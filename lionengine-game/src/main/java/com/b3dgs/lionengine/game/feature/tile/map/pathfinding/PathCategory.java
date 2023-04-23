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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.NameableAbstract;

/**
 * Represents a pathfinding category, with its group list.
 */
public class PathCategory extends NameableAbstract
{
    /** Associated groups. */
    private final Collection<String> groups;

    /**
     * Create the category.
     * 
     * @param name The category name.
     * @param groups The associated groups.
     * @throws LionEngineException If invalid arguments.
     */
    public PathCategory(String name, Collection<String> groups)
    {
        super(name);

        this.groups = new ArrayList<>(groups);
    }

    /**
     * Get the associated groups name as read only.
     * 
     * @return The groups name.
     */
    public Collection<String> getGroups()
    {
        return Collections.unmodifiableCollection(groups);
    }
}
