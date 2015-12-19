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
package com.b3dgs.lionengine.editor.project.dialog.collision;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormula;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroupConfig;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collisions list, allowing to add and remove {@link CollisionGroup}.
 */
public class CollisionList extends ObjectList<CollisionGroup> implements ObjectListListener<CollisionGroup>
{
    /**
     * Remove the collision from configuration.
     * 
     * @param collisionsConfig The collision config file.
     * @param collision The collision to remove.
     */
    private static void removeCollision(Media collisionsConfig, CollisionGroup collision)
    {
        final XmlNode node = Xml.load(collisionsConfig);
        final Collection<XmlNode> toRemove = new ArrayList<>();
        for (final XmlNode nodeFormula : node.getChildren(CollisionGroupConfig.COLLISION))
        {
            if (CollisionGroup.same(nodeFormula.readString(CollisionGroupConfig.GROUP), collision.getName()))
            {
                toRemove.add(nodeFormula);
            }
        }
        for (final XmlNode remove : toRemove)
        {
            node.removeChild(remove);
        }
        toRemove.clear();
        Xml.save(node, collisionsConfig);
    }

    /** Last config used. */
    private Media config;

    /**
     * Create the collision list.
     */
    public CollisionList()
    {
        super(CollisionGroup.class);
    }

    /**
     * Create the collision list.
     * 
     * @param properties The properties reference.
     */
    public CollisionList(CollisionsProperties properties)
    {
        super(CollisionGroup.class, properties);
    }

    /**
     * Load the existing collisions from the object configurer.
     * 
     * @param config The config file.
     */
    public void loadCollisions(Media config)
    {
        this.config = config;
        final Collection<CollisionGroup> groups = CollisionGroupConfig.create(config);
        loadObjects(groups);
    }

    /*
     * ObjectList
     */

    @Override
    protected CollisionGroup copyObject(CollisionGroup collision)
    {
        return new CollisionGroup(collision.getName(), collision.getFormulas());
    }

    @Override
    protected CollisionGroup createObject(String name)
    {
        return new CollisionGroup(name, new ArrayList<CollisionFormula>());
    }

    /*
     * ObjectListListener
     */

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
            final Media collisionsConfig = mapCollision.getFormulasConfig();
            removeCollision(collisionsConfig, collision);
            mapCollision.loadCollisions();
        }
        else if (config != null)
        {
            removeCollision(config, collision);
        }
    }
}
