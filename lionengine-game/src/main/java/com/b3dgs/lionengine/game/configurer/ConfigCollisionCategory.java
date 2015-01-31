/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.configurer;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Collision tile category.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see XmlNode
 */
public final class ConfigCollisionCategory
{
    /** The category node name. */
    public static final String CATEGORY = Configurer.PREFIX + "category";
    /** The category name attribute. */
    public static final String NAME = "name";
    /** The category axis attribute. */
    public static final String AXIS = "axis";
    /** The horizontal offset attribute. */
    public static final String X = "x";
    /** The vertical offset attribute. */
    public static final String Y = "y";

    /**
     * Create the categories nodes.
     * 
     * @param configurer The configurer reference.
     * @param map The map reference.
     * @return The category collisions instance.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<CollisionCategory> create(Configurer configurer, MapTile<?> map)
            throws LionEngineException
    {
        final Collection<CollisionCategory> categories = new ArrayList<>();
        for (final XmlNode node : configurer.getRoot().getChildren(CATEGORY))
        {
            final CollisionCategory category = create(node, map);
            categories.add(category);
        }
        return categories;
    }

    /**
     * Create the categories node.
     * 
     * @param root The root reference.
     * @param map The map reference.
     * @return The category node instance.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionCategory create(XmlNode root, MapTile<?> map) throws LionEngineException
    {
        final Collection<CollisionFormula> formulas = new ArrayList<>();
        for (final XmlNode groupNode : root.getChildren(ConfigCollisionGroup.GROUP))
        {
            final String groupName = groupNode.getText();
            final CollisionGroup group = map.getCollisionGroup(groupName);
            for (final String name : group.getFormulas())
            {
                formulas.add(map.getCollisionFormula(name));
            }
        }
        return new CollisionCategory(root.readString(NAME), Axis.valueOf(root.readString(AXIS)), root.readInteger(X),
                root.readInteger(Y), formulas);
    }

    /**
     * Constructor.
     */
    private ConfigCollisionCategory()
    {
        // Private constructor
    }
}
