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
package com.b3dgs.lionengine.editor.project.dialog.group;

import java.util.Collection;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.game.collision.TileGroup;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.stream.Stream;

/**
 * Represents the groups list, allowing to add and remove {@link TileGroup}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GroupList
        extends ObjectList<TileGroup>
{
    /** Default group name. */
    private static final String DEFAULT_NAME = "default;";

    /**
     * Create the group list.
     */
    public GroupList()
    {
        super(TileGroup.class);
    }

    /**
     * Load the existing groups from the object configurer.
     * 
     * @param config The config file.
     */
    public void loadGroups(Media config)
    {
        final Collection<TileGroup> groups = ConfigTileGroup.create(Stream.loadXml(config));
        loadObjects(groups);
    }

    /*
     * ObjectList
     */

    @Override
    protected TileGroup copyObject(TileGroup object)
    {
        return new TileGroup(object.getName(), object.getSheet(), object.getStart(), object.getEnd());
    }

    @Override
    protected TileGroup createDefaultObject()
    {
        return new TileGroup(DEFAULT_NAME, 0, 0, 0);
    }
}
