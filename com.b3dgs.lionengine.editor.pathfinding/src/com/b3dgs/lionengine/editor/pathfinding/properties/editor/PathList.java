/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.pathfinding.properties.editor;

import java.util.Collections;
import java.util.Map;

import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.pathfinding.PathData;
import com.b3dgs.lionengine.game.pathfinding.PathfindableConfig;

/**
 * Represents the path list, allowing to add and remove {@link PathData}.
 */
public class PathList extends ObjectList<PathData>
{
    /**
     * Create path list and associate its properties.
     * 
     * @param properties The properties reference.
     */
    public PathList(PathProperties properties)
    {
        super(PathData.class, properties);
    }

    /**
     * Load the existing paths from the object configurer.
     * 
     * @param configurer The configurer reference.
     */
    public void loadPaths(Configurer configurer)
    {
        final Map<String, PathData> config = PathfindableConfig.create(configurer);
        loadObjects(config.values());
    }

    /*
     * ObjectList
     */

    @Override
    protected PathData copyObject(PathData path)
    {
        return new PathData(path.getName(), path.getCost(), path.isBlocking(), path.getAllowedMovements());
    }

    @Override
    protected PathData createObject(String name)
    {
        return new PathData(name, 1.0, true, Collections.emptyList());
    }
}
