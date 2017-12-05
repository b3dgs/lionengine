/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the collision group data from a configurer.
 * 
 * @see CollisionGroup
 */
public final class CollisionGroupConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "collisions.xml";
    /** Collision group root node. */
    public static final String COLLISIONS = Constant.XML_PREFIX + "collisions";
    /** Collision group node. */
    public static final String COLLISION = Constant.XML_PREFIX + "collision";
    /** Group name attribute. */
    public static final String GROUP = "group";

    /**
     * Create the collision group data from node (should only be used to display names, as real content is
     * <code>null</code>, mainly UI specific to not have dependency on {@link MapTileCollision}).
     * 
     * @param config The tile collision groups descriptor.
     * @return The collisions group data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionGroupConfig imports(Media config)
    {
        final Xml root = new Xml(config);
        final Collection<Xml> childrenCollision = root.getChildren(COLLISION);
        final Map<String, CollisionGroup> groups = new HashMap<String, CollisionGroup>(childrenCollision.size());
        for (final Xml node : childrenCollision)
        {
            final Collection<Xml> childrenFormula = node.getChildren(CollisionFormulaConfig.FORMULA);
            final Collection<CollisionFormula> formulas = new ArrayList<CollisionFormula>(childrenFormula.size());
            for (final Xml formula : childrenFormula)
            {
                final String formulaName = formula.getText();
                formulas.add(new CollisionFormula(formulaName, null, null, null));
            }
            final String groupName = node.readString(GROUP);
            final CollisionGroup collision = new CollisionGroup(groupName, formulas);
            groups.put(groupName, collision);
        }
        return new CollisionGroupConfig(groups);
    }

    /**
     * Create the collision group data from node.
     * 
     * @param root The node root reference.
     * @param map The map reference.
     * @return The collisions group data.
     * @throws LionEngineException If unable to read node.
     */
    public static CollisionGroupConfig imports(Xml root, MapTileCollision map)
    {
        final Collection<Xml> childrenCollision = root.getChildren(COLLISION);
        final Map<String, CollisionGroup> groups = new HashMap<String, CollisionGroup>(childrenCollision.size());
        for (final Xml node : childrenCollision)
        {
            final Collection<Xml> childrenFormula = node.getChildren(CollisionFormulaConfig.FORMULA);
            final Collection<CollisionFormula> formulas = new ArrayList<CollisionFormula>(childrenFormula.size());
            for (final Xml formula : childrenFormula)
            {
                final String formulaName = formula.getText();
                formulas.add(map.getCollisionFormula(formulaName));
            }
            final String groupName = node.readString(GROUP);
            final CollisionGroup collision = new CollisionGroup(groupName, formulas);
            groups.put(groupName, collision);
        }
        return new CollisionGroupConfig(groups);
    }

    /**
     * Export the collision group data as a node.
     * 
     * @param root The node root.
     * @param group The collision group to export.
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, CollisionGroup group)
    {
        final Xml node = root.createChild(COLLISION);
        node.writeString(GROUP, group.getName());

        for (final CollisionFormula formula : group.getFormulas())
        {
            final Xml nodeFormula = node.createChild(CollisionFormulaConfig.FORMULA);
            nodeFormula.setText(formula.getName());
        }
    }

    /**
     * Remove the group node.
     * 
     * @param root The root node.
     * @param group The group name to remove.
     */
    public static void remove(Xml root, String group)
    {
        for (final Xml node : root.getChildren(COLLISION))
        {
            if (node.readString(GROUP).equals(group))
            {
                root.removeChild(node);
            }
        }
    }

    /**
     * Check if node has group node.
     * 
     * @param root The root node.
     * @param group The group name to check.
     * @return <code>true</code> if has group, <code>false</code> else.
     */
    public static boolean has(Xml root, String group)
    {
        for (final Xml node : root.getChildren(COLLISION))
        {
            if (node.readString(GROUP).equals(group))
            {
                return true;
            }
        }
        return false;
    }

    /** Collision groups list. */
    private final Map<String, CollisionGroup> groups;

    /**
     * Create a collision groups config map.
     * 
     * @param groups The collisions groups mapping.
     */
    private CollisionGroupConfig(Map<String, CollisionGroup> groups)
    {
        this.groups = groups;
    }

    /**
     * Get a collision formula data from its name.
     * 
     * @param name The formula name.
     * @return The formula reference.
     * @throws LionEngineException If the formula with the specified name is not found.
     */
    public CollisionGroup getGroup(String name)
    {
        final CollisionGroup group = groups.get(name);
        Check.notNull(group);
        return group;
    }

    /**
     * Get all groups.
     * 
     * @return The groups map, where key is the group name.
     */
    public Map<String, CollisionGroup> getGroups()
    {
        return groups;
    }
}
