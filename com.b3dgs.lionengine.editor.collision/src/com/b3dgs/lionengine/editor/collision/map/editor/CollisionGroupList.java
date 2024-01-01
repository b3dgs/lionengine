/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision.map.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.ObjectListAbstract;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroupConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;

/**
 * Represents the collisions list, allowing to add and remove {@link CollisionGroup}.
 */
public class CollisionGroupList extends ObjectListAbstract<CollisionGroup> implements ObjectListListener<CollisionGroup>
{
    /**
     * Remove the collision from configuration.
     * 
     * @param collisionsConfig The collision config file.
     * @param collision The collision to remove.
     */
    private static void removeCollision(Media collisionsConfig, CollisionGroup collision)
    {
        final Collection<Xml> toRemove = new ArrayList<>();

        final Xml node = new Xml(collisionsConfig);
        final Collection<Xml> children = node.getChildrenXml(CollisionGroupConfig.NODE_COLLISION);
        for (final Xml nodeFormula : children)
        {
            if (CollisionGroup.same(nodeFormula.getString(CollisionGroupConfig.ATT_GROUP), collision.getName()))
            {
                toRemove.add(nodeFormula);
            }
        }
        children.clear();

        for (final Xml remove : toRemove)
        {
            node.removeChild(remove);
        }
        toRemove.clear();

        node.save(collisionsConfig);
    }

    /** Last config used. */
    private Media groupsConfig;

    /**
     * Create the collision list.
     */
    public CollisionGroupList()
    {
        super(CollisionGroup.class);
    }

    /**
     * Create the collision list.
     * 
     * @param properties The properties reference.
     */
    public CollisionGroupList(CollisionGroupProperties properties)
    {
        super(CollisionGroup.class, properties);
    }

    /**
     * Load the existing collisions from the object configurer.
     * 
     * @param groupsConfig The groups config file.
     */
    public void loadCollisions(Media groupsConfig)
    {
        this.groupsConfig = groupsConfig;
        final CollisionGroupConfig groups = CollisionGroupConfig.imports(groupsConfig);
        loadObjects(groups.getGroups().values());
    }

    @Override
    protected CollisionGroup copyObject(CollisionGroup collision)
    {
        return new CollisionGroup(collision.getName(), collision.getFormulas());
    }

    @Override
    protected CollisionGroup createObject(String name)
    {
        return new CollisionGroup(name, new ArrayList<>());
    }

    @Override
    public void notifyObjectSelected(CollisionGroup collision)
    {
        // Nothing to do
    }

    @Override
    public void notifyObjectDeleted(CollisionGroup collision)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            final Media formulasConfig = mapCollision.getFormulasConfig();
            if (formulasConfig != null)
            {
                removeCollision(formulasConfig, collision);
                mapCollision.loadCollisions(formulasConfig, groupsConfig);
            }
        }
        else if (groupsConfig != null)
        {
            removeCollision(groupsConfig, collision);
        }
    }
}
