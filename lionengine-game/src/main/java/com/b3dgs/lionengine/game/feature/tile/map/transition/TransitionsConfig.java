/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.feature.tile.TileConfig;

/**
 * Find all tiles transitions and extract them to an XML file.
 * It will check from an existing map all transitions combination.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see TransitionType
 */
public final class TransitionsConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "transitions.xml";
    /** Transitions node. */
    public static final String NODE_TRANSITIONS = Constant.XML_PREFIX + "transitions";
    /** Transition node. */
    public static final String NODE_TRANSITION = Constant.XML_PREFIX + "transition";
    /** Attribute transition type. */
    public static final String ATTRIBUTE_TRANSITION_TYPE = "type";
    /** Attribute group in. */
    public static final String ATTRIBUTE_GROUP_IN = "in";
    /** Attribute group out. */
    public static final String ATTRIBUTE_GROUP_OUT = "out";

    /**
     * Import all transitions from configuration.
     * 
     * @param config The transitions media (must not be <code>null</code>).
     * @return The transitions imported with associated tiles.
     * @throws LionEngineException If unable to read data.
     */
    public static Map<Transition, Collection<Integer>> imports(Media config)
    {
        final XmlReader root = new XmlReader(config);
        final Collection<XmlReader> nodesTransition = root.getChildren(NODE_TRANSITION);
        final Map<Transition, Collection<Integer>> transitions = new HashMap<>(nodesTransition.size());

        for (final XmlReader nodeTransition : nodesTransition)
        {
            final String groupIn = nodeTransition.getString(ATTRIBUTE_GROUP_IN);
            final String groupOut = nodeTransition.getString(ATTRIBUTE_GROUP_OUT);
            final String transitionType = nodeTransition.getString(ATTRIBUTE_TRANSITION_TYPE);
            final TransitionType type = TransitionType.from(transitionType);
            final Transition transition = new Transition(type, groupIn, groupOut);

            final Collection<XmlReader> nodesTile = nodeTransition.getChildren(TileConfig.NODE_TILE);
            final Collection<Integer> tiles = importTiles(nodesTile);
            nodesTile.clear();

            transitions.put(transition, tiles);
        }
        nodesTransition.clear();

        return transitions;
    }

    /**
     * Export all transitions to media.
     *
     * @param media The export media output (must not be <code>null</code>).
     * @param levels The level rips used (must not be <code>null</code>).
     * @param sheetsMedia The sheets media (must not be <code>null</code>).
     * @param groupsMedia The groups media (must not be <code>null</code>).
     * @throws LionEngineException If error on export.
     */
    public static void exports(Media media, Collection<Media> levels, Media sheetsMedia, Media groupsMedia)
    {
        Check.notNull(media);
        Check.notNull(levels);
        Check.notNull(sheetsMedia);
        Check.notNull(groupsMedia);

        final TransitionsExtractor extractor = new TransitionsExtractorImpl();
        final Map<Transition, Collection<Integer>> transitions = extractor.getTransitions(levels,
                                                                                          sheetsMedia,
                                                                                          groupsMedia);
        exports(media, transitions);
    }

    /**
     * Export all transitions to media.
     * 
     * @param media The export media output (must not be <code>null</code>).
     * @param transitions The transitions reference (must not be <code>null</code>).
     * @throws LionEngineException If error on export.
     */
    public static void exports(Media media, Map<Transition, Collection<Integer>> transitions)
    {
        Check.notNull(media);
        Check.notNull(transitions);

        final Xml nodeTransitions = new Xml(NODE_TRANSITIONS);

        for (final Map.Entry<Transition, Collection<Integer>> entry : transitions.entrySet())
        {
            final Transition transition = entry.getKey();

            final Xml nodeTransition = nodeTransitions.createChild(NODE_TRANSITION);
            nodeTransition.writeEnum(ATTRIBUTE_TRANSITION_TYPE, transition.getType());
            nodeTransition.writeString(ATTRIBUTE_GROUP_IN, transition.getIn());
            nodeTransition.writeString(ATTRIBUTE_GROUP_OUT, transition.getOut());

            exportTiles(nodeTransition, entry.getValue());
        }

        nodeTransitions.save(media);
    }

    /**
     * Import all tiles from their nodes.
     * 
     * @param nodesTile The tiles nodes (must not be <code>null</code>).
     * @return The imported tiles.
     */
    private static Collection<Integer> importTiles(Collection<XmlReader> nodesTile)
    {
        final Collection<Integer> tiles = new HashSet<>(nodesTile.size());

        for (final XmlReader nodeTile : nodesTile)
        {
            final Integer tile = Integer.valueOf(TileConfig.imports(nodeTile));
            tiles.add(tile);
        }

        return tiles;
    }

    /**
     * Export all tiles for the transition.
     * 
     * @param nodeTransition The transition node (must not be <code>null</code>).
     * @param tiles The transition tiles (must not be <code>null</code>).
     */
    private static void exportTiles(Xml nodeTransition, Collection<Integer> tiles)
    {
        for (final Integer tile : tiles)
        {
            final Xml nodeTile = TileConfig.exports(tile.intValue());
            nodeTransition.add(nodeTile);
        }
    }

    /**
     * Disabled constructor.
     */
    private TransitionsConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
