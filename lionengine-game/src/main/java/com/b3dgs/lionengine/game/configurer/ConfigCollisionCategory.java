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
package com.b3dgs.lionengine.game.configurer;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the collision category configuration from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see CollisionCategory
 */
public final class ConfigCollisionCategory
{
    /** Category node name. */
    public static final String CATEGORY = Configurer.PREFIX + "category";
    /** Category attribute name. */
    public static final String NAME = "name";
    /** Category attribute axis. */
    public static final String AXIS = "axis";
    /** Category attribute horizontal offset. */
    public static final String X = "x";
    /** Category attribute vertical offset. */
    public static final String Y = "y";
    /** Unknown axis error. */
    private static final String ERROR_AXIS = "Unknown axis: ";

    /**
     * Create the collision category data from node (should only be used to display names, as real content is
     * <code>null</code>, mainly UI specific to not have dependency on {@link MapTileCollision}).
     * 
     * @param root The node root reference.
     * @return The collisions category data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<CollisionCategory> create(XmlNode root) throws LionEngineException
    {
        final Collection<CollisionCategory> categories = new ArrayList<>();
        for (final XmlNode node : root.getChildren(CATEGORY))
        {
            final Collection<CollisionGroup> groups = new ArrayList<>();
            for (final XmlNode group : node.getChildren(ConfigTileGroup.GROUP))
            {
                final String name = group.getText();
                groups.add(new CollisionGroup(name, new ArrayList<CollisionFormula>(0)));
            }

            final String name = node.readString(NAME);
            final Axis axis = Axis.valueOf(node.readString(AXIS));
            final int x = node.readInteger(X);
            final int y = node.readInteger(Y);

            final CollisionCategory category = new CollisionCategory(name, axis, x, y, groups);
            categories.add(category);
        }
        return categories;
    }

    /**
     * Create the categories data from nodes.
     * 
     * @param configurer The configurer reference.
     * @param map The map reference.
     * @return The category collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<CollisionCategory> create(Configurer configurer, MapTileCollision map)
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
     * Create the category data from node.
     * 
     * @param root The root reference.
     * @param map The map reference.
     * @return The category node instance.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionCategory create(XmlNode root, MapTileCollision map) throws LionEngineException
    {
        final Collection<CollisionGroup> groups = new ArrayList<>();
        for (final XmlNode groupNode : root.getChildren(ConfigTileGroup.GROUP))
        {
            final String groupName = groupNode.getText();
            final CollisionGroup group = map.getCollisionGroup(groupName);
            groups.add(group);
        }

        final String name = root.readString(NAME);
        final String axisName = root.readString(AXIS);
        final Axis axis;
        try
        {
            axis = Axis.valueOf(axisName);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_AXIS, axisName);
        }
        final int x = root.readInteger(X);
        final int y = root.readInteger(Y);

        return new CollisionCategory(name, axis, x, y, groups);
    }

    /**
     * Export the collision category data as a node.
     * 
     * @param root The node root.
     * @param category The collision category to export.
     * @throws LionEngineException If error on writing.
     */
    public static void export(XmlNode root, CollisionCategory category) throws LionEngineException
    {
        final XmlNode node = root.createChild(CATEGORY);
        node.writeString(NAME, category.getName());
        node.writeString(AXIS, category.getAxis().name());
        node.writeInteger(X, category.getOffsetX());
        node.writeInteger(Y, category.getOffsetY());

        for (final CollisionGroup group : category.getGroups())
        {
            final XmlNode groupNode = node.createChild(ConfigTileGroup.GROUP);
            groupNode.setText(group.getName());
        }
    }

    /**
     * Disabled constructor.
     */
    private ConfigCollisionCategory()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
