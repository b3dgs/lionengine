/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;

/**
 * Represents the collision category configuration.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see CollisionCategory
 */
public final class CollisionCategoryConfig
{
    /** Categories node name. */
    public static final String NODE_CATEGORIES = Constant.XML_PREFIX + "categories";
    /** Category node name. */
    public static final String NODE_CATEGORY = Constant.XML_PREFIX + "category";
    /** Category attribute name. */
    public static final String ATT_NAME = "name";
    /** Category attribute axis. */
    public static final String ATT_AXIS = "axis";
    /** Category attribute horizontal offset. */
    public static final String ATT_X = "x";
    /** Category attribute vertical offset. */
    public static final String ATT_Y = "y";
    /** Category attribute glue flag. */
    public static final String ATT_GLUE = "glue";
    /** Unknown axis error. */
    private static final String ERROR_AXIS = "Unknown axis: ";

    /**
     * Create the collision category data from node (should only be used to display names, as real content is
     * <code>null</code>, mainly UI specific to not have dependency on {@link MapTileCollision}).
     * 
     * @param root The node root reference (must not be <code>null</code>).
     * @return The collisions category data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<CollisionCategory> imports(XmlReader root)
    {
        Check.notNull(root);

        final Collection<CollisionCategory> categories = new ArrayList<>();
        if (root.hasNode(NODE_CATEGORIES))
        {
            final Collection<XmlReader> childrenCategory = root.getChild(NODE_CATEGORIES).getChildren(NODE_CATEGORY);
            for (final XmlReader node : childrenCategory)
            {
                final Collection<XmlReader> childrenGroup = node.getChildren(TileGroupsConfig.NODE_GROUP);
                final Collection<CollisionGroup> groups = new ArrayList<>(childrenGroup.size());

                for (final XmlReader group : childrenGroup)
                {
                    final String name = group.getText();
                    groups.add(new CollisionGroup(name, new ArrayList<>(0)));
                }
                childrenGroup.clear();

                final String name = node.getString(ATT_NAME);
                final Axis axis = node.getEnum(Axis.class, ATT_AXIS);
                final int x = node.getInteger(ATT_X);
                final int y = node.getInteger(ATT_Y);
                final boolean glue = node.getBoolean(true, ATT_GLUE);

                final CollisionCategory category = new CollisionCategory(name, axis, x, y, glue, groups);
                categories.add(category);
            }
            childrenCategory.clear();
        }

        return categories;
    }

    /**
     * Create the categories data from nodes.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @param map The map reference (must not be <code>null</code>).
     * @return The category collisions data.
     * @throws LionEngineException If unable to read node.
     */
    public static Collection<CollisionCategory> imports(Configurer configurer, MapTileCollision map)
    {
        Check.notNull(configurer);
        Check.notNull(map);

        final Collection<XmlReader> children = configurer.getRoot()
                                                         .getChild(NODE_CATEGORIES)
                                                         .getChildren(NODE_CATEGORY);
        final Collection<CollisionCategory> categories = new ArrayList<>(children.size());

        for (final XmlReader node : children)
        {
            final CollisionCategory category = imports(node, map);
            categories.add(category);
        }
        children.clear();

        return categories;
    }

    /**
     * Create the category data from node.
     * 
     * @param root The root reference (must not be <code>null</code>).
     * @param map The map reference (must not be <code>null</code>).
     * @return The category node instance.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionCategory imports(XmlReader root, MapTileCollision map)
    {
        Check.notNull(root);
        Check.notNull(map);

        final Collection<XmlReader> children = root.getChildren(TileGroupsConfig.NODE_GROUP);
        final Collection<CollisionGroup> groups = new ArrayList<>(children.size());

        for (final XmlReader groupNode : children)
        {
            final String groupName = groupNode.getText();
            map.getCollisionGroup(groupName).ifPresent(groups::add);
        }
        children.clear();

        final String axisName = root.getString(ATT_AXIS);
        final Axis axis;
        try
        {
            axis = Axis.valueOf(axisName);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new LionEngineException(exception, ERROR_AXIS + axisName);
        }

        final int x = root.getInteger(ATT_X);
        final int y = root.getInteger(ATT_Y);
        final boolean glue = root.getBoolean(true, ATT_GLUE);
        final String name = root.getString(ATT_NAME);

        return new CollisionCategory(name, axis, x, y, glue, groups);
    }

    /**
     * Export the collision category data as a node.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param category The collision category to export (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, CollisionCategory category)
    {
        Check.notNull(root);
        Check.notNull(category);

        final Xml categories;
        if (root.hasNode(NODE_CATEGORIES))
        {
            categories = root.getChildXml(NODE_CATEGORIES);
        }
        else
        {
            categories = root.createChild(NODE_CATEGORIES);
        }

        final Xml node = categories.createChild(NODE_CATEGORY);
        node.writeString(ATT_NAME, category.getName());
        node.writeEnum(ATT_AXIS, category.getAxis());
        node.writeInteger(ATT_X, category.getOffsetX());
        node.writeInteger(ATT_Y, category.getOffsetY());
        node.writeBoolean(ATT_GLUE, category.isGlue());

        for (final CollisionGroup group : category.getGroups())
        {
            final Xml groupNode = node.createChild(TileGroupsConfig.NODE_GROUP);
            groupNode.setText(group.getName());
        }
    }

    /**
     * Disabled constructor.
     */
    private CollisionCategoryConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
