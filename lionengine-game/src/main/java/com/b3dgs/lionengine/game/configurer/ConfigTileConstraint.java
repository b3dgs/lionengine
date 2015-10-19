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
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.map.TileConstraint;
import com.b3dgs.lionengine.game.map.TileRef;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile constraints from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.map.ConstraintsExtractor
 */
public final class ConfigTileConstraint
{
    /** Configuration file name. */
    public static final String FILENAME = "constraints.xml";
    /** Constraints node. */
    public static final String CONSTRAINTS = Configurer.PREFIX + "constraints";
    /** Constraint node. */
    public static final String CONSTRAINT = Configurer.PREFIX + "constraint";
    /** Orientation attribute. */
    public static final String ORIENTATION = "orientation";

    /**
     * Create the collision constraint data from node.
     * 
     * @param root The root reference.
     * @return The collision constraint data.
     * @throws LionEngineException If error when reading node.
     */
    public static Map<TileRef, Collection<TileConstraint>> create(XmlNode root) throws LionEngineException
    {
        final Map<TileRef, Collection<TileConstraint>> constraints = new HashMap<TileRef, Collection<TileConstraint>>();

        for (final XmlNode nodeTileRef : root.getChildren(ConfigTileRef.TILE_REF))
        {
            final TileRef tileRef = ConfigTileRef.create(nodeTileRef);
            constraints.put(tileRef, getConstraints(nodeTileRef));
        }

        return constraints;
    }

    /**
     * Get the orientation constraints for the current tile ref.
     * 
     * @param nodeTileRef The current tile ref node.
     * @return The constraints read.
     * @throws LionEngineException If error when reading node.
     */
    private static Collection<TileConstraint> getConstraints(XmlNode nodeTileRef) throws LionEngineException
    {
        final Collection<TileConstraint> constraints = new ArrayList<TileConstraint>();
        for (final XmlNode nodeConstraint : nodeTileRef.getChildren(CONSTRAINT))
        {
            final Orientation orientation = Orientation.valueOf(nodeConstraint.readString(ORIENTATION));
            final TileConstraint constraint = new TileConstraint(orientation);

            for (final XmlNode tileRefNode : nodeConstraint.getChildren(ConfigTileRef.TILE_REF))
            {
                constraint.add(ConfigTileRef.create(tileRefNode));
            }
            constraints.add(constraint);
        }
        return constraints;
    }

    /**
     * Export the tile constraints as a node.
     * 
     * @param constraints The tile constraints to export.
     * @return The exported node.
     * @throws LionEngineException If error on writing.
     */
    public static XmlNode export(Map<TileRef, Collection<TileConstraint>> constraints) throws LionEngineException
    {
        final XmlNode nodeConstraints = Stream.createXmlNode(CONSTRAINTS);
        for (final Map.Entry<TileRef, Collection<TileConstraint>> entry : constraints.entrySet())
        {
            final XmlNode nodeTileRef = ConfigTileRef.export(entry.getKey());

            for (final TileConstraint constraint : entry.getValue())
            {
                writeConstraint(nodeTileRef, constraint);
            }

            nodeConstraints.add(nodeTileRef);
        }
        return nodeConstraints;
    }

    /**
     * Write the constraint to the node.
     * 
     * @param nodeTileRef The current tile ref constraints node.
     * @param constraint The tile constraint.
     */
    private static void writeConstraint(XmlNode nodeTileRef, TileConstraint constraint)
    {
        final XmlNode nodeOrientation = nodeTileRef.createChild(CONSTRAINT);
        nodeOrientation.writeString(ORIENTATION, constraint.getOrientation().name());

        for (final TileRef tileRef : constraint.getAllowed())
        {
            final XmlNode nodeAllowed = ConfigTileRef.export(tileRef);
            nodeOrientation.add(nodeAllowed);
        }
    }

    /**
     * Disabled constructor.
     */
    private ConfigTileConstraint()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
