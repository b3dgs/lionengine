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
package com.b3dgs.lionengine.editor.map.group.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.TileGroup;
import com.b3dgs.lionengine.game.tile.TileGroupType;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the groups list, allowing to add and remove {@link TileGroup}.
 */
public class GroupList extends ObjectList<TileGroup> implements ObjectListListener<TileGroup>
{
    /**
     * Create the group list.
     * 
     * @param properties The properties reference.
     */
    public GroupList(GroupProperties properties)
    {
        super(TileGroup.class, properties);
    }

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
        final Collection<TileGroup> groups = TileGroupsConfig.imports(config);
        loadObjects(groups);
    }

    /*
     * ObjectList
     */

    @Override
    protected TileGroup copyObject(TileGroup group)
    {
        return new TileGroup(group.getName(), group.getType(), group.getTiles());
    }

    @Override
    protected TileGroup createObject(String name)
    {
        return new TileGroup(name, TileGroupType.NONE, new ArrayList<TileRef>());
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(TileGroup group)
    {
        // Nothing to do
    }

    @Override
    public void notifyObjectDeleted(TileGroup group)
    {
        final MapTileGroup mapGroup = WorldModel.INSTANCE.getMap().getFeature(MapTileGroup.class);
        final Media config = mapGroup.getGroupsConfig();
        final XmlNode node = Xml.load(config);
        final Collection<XmlNode> toRemove = new ArrayList<>();
        for (final XmlNode nodeGroup : node.getChildren(TileGroupsConfig.NODE_GROUP))
        {
            if (CollisionGroup.same(nodeGroup.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME), group.getName()))
            {
                toRemove.add(nodeGroup);
            }
        }
        for (final XmlNode remove : toRemove)
        {
            node.removeChild(remove);
        }
        toRemove.clear();
        Xml.save(node, config);
        mapGroup.loadGroups(config);
    }
}
