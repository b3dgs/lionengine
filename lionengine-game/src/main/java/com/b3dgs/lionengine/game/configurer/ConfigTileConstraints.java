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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.map.TileConstraint;
import com.b3dgs.lionengine.game.map.TileRef;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the tile constraints from a configurer.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.map.ConstraintsExtractor
 */
public final class ConfigTileConstraints
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
     * @param config The constraints configuration.
     * @return The collision constraint data.
     * @throws LionEngineException If error when reading node.
     */
    public static Map<TileRef, Map<Orientation, TileConstraint>> create(Media config)
    {
        final Map<TileRef, Map<Orientation, TileConstraint>> constraints;
        constraints = new HashMap<TileRef, Map<Orientation, TileConstraint>>();

        final XmlNode root = Xml.load(config);

        for (final XmlNode nodeTileRef : root.getChildren(ConfigTile.NODE_TILE))
        {
            final TileRef tileRef = ConfigTile.create(nodeTileRef);
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
    private static Map<Orientation, TileConstraint> getConstraints(XmlNode nodeTileRef)
    {
        final Map<Orientation, TileConstraint> constraints = new HashMap<Orientation, TileConstraint>();
        for (final XmlNode nodeConstraint : nodeTileRef.getChildren(CONSTRAINT))
        {
            final Orientation orientation = Orientation.valueOf(nodeConstraint.readString(ORIENTATION));
            final TileConstraint constraint = new TileConstraint(orientation);

            for (final XmlNode tileRefNode : nodeConstraint.getChildren(ConfigTile.NODE_TILE))
            {
                constraint.add(ConfigTile.create(tileRefNode));
            }
            constraints.put(orientation, constraint);
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
    public static XmlNode export(Map<TileRef, Collection<TileConstraint>> constraints)
    {
        final XmlNode nodeConstraints = Xml.create(CONSTRAINTS);
        for (final Map.Entry<TileRef, Collection<TileConstraint>> entry : constraints.entrySet())
        {
            final XmlNode nodeTileRef = ConfigTile.export(entry.getKey());

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
            final XmlNode nodeAllowed = ConfigTile.export(tileRef);
            nodeOrientation.add(nodeAllowed);
        }
    }

    /**
     * Disabled constructor.
     */
    private ConfigTileConstraints()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
