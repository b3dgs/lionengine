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
package com.b3dgs.lionengine.game.tile;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Find all tiles transitions and extract them to an XML file.
 * It will check from an existing map all transitions combination.
 * 
 * @see TileTransitionType
 */
public final class TileTransitionsConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "transitions.xml";
    /** Transitions node. */
    public static final String NODE_TRANSITIONS = Configurer.PREFIX + "transitions";
    /** Transition node. */
    public static final String NODE_TRANSITION = Configurer.PREFIX + "transition";
    /** Attribute transition type. */
    public static final String ATTRIBUTE_TRANSITION_TYPE = "type";
    /** Attribute group in. */
    public static final String ATTRIBUTE_GROUP_IN = "in";
    /** Attribute group out. */
    public static final String ATTRIBUTE_GROUP_OUT = "out";

    /**
     * Import all transitions from configuration.
     * 
     * @param configTransitions The transitions media.
     * @return The transitions imported.
     * @throws LionEngineException If unable to read data.
     */
    public static Map<TileTransition, Collection<TileRef>> imports(Media configTransitions)
    {
        final XmlNode root = Xml.load(configTransitions);
        final Collection<XmlNode> nodesTransition = root.getChildren(NODE_TRANSITION);
        final Map<TileTransition, Collection<TileRef>> transitions;
        transitions = new HashMap<TileTransition, Collection<TileRef>>();

        for (final XmlNode nodeTransition : nodesTransition)
        {
            final String groupIn = nodeTransition.readString(ATTRIBUTE_GROUP_IN);
            final String groupOut = nodeTransition.readString(ATTRIBUTE_GROUP_OUT);
            final String transitionType = nodeTransition.readString(ATTRIBUTE_TRANSITION_TYPE);
            final TileTransitionType type = TileTransitionType.from(transitionType);
            final TileTransition transition = new TileTransition(type, groupIn, groupOut);

            final Collection<XmlNode> nodesTileRef = nodeTransition.getChildren(TileConfig.NODE_TILE);
            final Collection<TileRef> tilesRef = importTiles(nodesTileRef);

            transitions.put(transition, tilesRef);
        }
        return transitions;
    }

    /**
     * Export all transitions to an XML file.
     * 
     * @param media The export output.
     * @param transitions The transitions reference.
     * @throws LionEngineException If error on export.
     */
    public static void exports(Media media, Map<TileTransition, Collection<TileRef>> transitions)
    {
        final XmlNode nodeTransitions = Xml.create(NODE_TRANSITIONS);

        for (final Map.Entry<TileTransition, Collection<TileRef>> entry : transitions.entrySet())
        {
            final TileTransition transition = entry.getKey();
            final XmlNode nodeTransition = nodeTransitions.createChild(NODE_TRANSITION);
            nodeTransition.writeString(ATTRIBUTE_TRANSITION_TYPE, transition.getType().name());
            nodeTransition.writeString(ATTRIBUTE_GROUP_IN, transition.getGroupIn());
            nodeTransition.writeString(ATTRIBUTE_GROUP_OUT, transition.getGroupOut());

            exportTiles(nodeTransition, entry.getValue());
        }

        Xml.save(nodeTransitions, media);
    }

    /**
     * Import all tiles from their nodes.
     * 
     * @param nodesTileRef The tiles nodes.
     * @return The imported tiles ref.
     */
    private static Collection<TileRef> importTiles(Collection<XmlNode> nodesTileRef)
    {
        final Collection<TileRef> tilesRef = new HashSet<TileRef>(nodesTileRef.size());
        for (final XmlNode nodeTileRef : nodesTileRef)
        {
            final TileRef tileRef = TileConfig.create(nodeTileRef);
            tilesRef.add(tileRef);
        }
        return tilesRef;
    }

    /**
     * Export all tiles for the transition.
     * 
     * @param nodeTransition The transition node.
     * @param tilesRef The transition tiles ref.
     */
    private static void exportTiles(XmlNode nodeTransition, Collection<TileRef> tilesRef)
    {
        for (final TileRef tileRef : tilesRef)
        {
            final XmlNode nodeTileRef = TileConfig.export(tileRef);
            nodeTransition.add(nodeTileRef);
        }
    }

    /**
     * Disabled constructor.
     */
    private TileTransitionsConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
