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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.feature.tile.TileConfig;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.io.Xml;

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
    public static Map<Transition, Collection<TileRef>> imports(Media config)
    {
        final Xml root = new Xml(config);
        final Collection<Xml> nodesTransition = root.getChildren(NODE_TRANSITION);
        final Map<Transition, Collection<TileRef>> transitions = new HashMap<>(nodesTransition.size());

        for (final Xml nodeTransition : nodesTransition)
        {
            final String groupIn = nodeTransition.readString(ATTRIBUTE_GROUP_IN);
            final String groupOut = nodeTransition.readString(ATTRIBUTE_GROUP_OUT);
            final String transitionType = nodeTransition.readString(ATTRIBUTE_TRANSITION_TYPE);
            final TransitionType type = TransitionType.from(transitionType);
            final Transition transition = new Transition(type, groupIn, groupOut);

            final Collection<Xml> nodesTileRef = nodeTransition.getChildren(TileConfig.NODE_TILE);
            final Collection<TileRef> tilesRef = importTiles(nodesTileRef);

            transitions.put(transition, tilesRef);
        }

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
        final Map<Transition, Collection<TileRef>> transitions = extractor.getTransitions(levels,
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
    public static void exports(Media media, Map<Transition, Collection<TileRef>> transitions)
    {
        Check.notNull(media);
        Check.notNull(transitions);

        final Xml nodeTransitions = new Xml(NODE_TRANSITIONS);

        for (final Map.Entry<Transition, Collection<TileRef>> entry : transitions.entrySet())
        {
            final Transition transition = entry.getKey();

            final Xml nodeTransition = nodeTransitions.createChild(NODE_TRANSITION);
            nodeTransition.writeString(ATTRIBUTE_TRANSITION_TYPE, transition.getType().name());
            nodeTransition.writeString(ATTRIBUTE_GROUP_IN, transition.getIn());
            nodeTransition.writeString(ATTRIBUTE_GROUP_OUT, transition.getOut());

            exportTiles(nodeTransition, entry.getValue());
        }

        nodeTransitions.save(media);
    }

    /**
     * Import all tiles from their nodes.
     * 
     * @param nodesTileRef The tiles nodes (must not be <code>null</code>).
     * @return The imported tiles ref.
     */
    private static Collection<TileRef> importTiles(Collection<Xml> nodesTileRef)
    {
        final Collection<TileRef> tilesRef = new HashSet<>(nodesTileRef.size());

        for (final Xml nodeTileRef : nodesTileRef)
        {
            final TileRef tileRef = TileConfig.imports(nodeTileRef);
            tilesRef.add(tileRef);
        }

        return tilesRef;
    }

    /**
     * Export all tiles for the transition.
     * 
     * @param nodeTransition The transition node (must not be <code>null</code>).
     * @param tilesRef The transition tiles ref (must not be <code>null</code>).
     */
    private static void exportTiles(Xml nodeTransition, Collection<TileRef> tilesRef)
    {
        for (final TileRef tileRef : tilesRef)
        {
            final Xml nodeTileRef = TileConfig.exports(tileRef);
            nodeTransition.add(nodeTileRef);
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
