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
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.map.TileRef;
import com.b3dgs.lionengine.game.map.TileTransition;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Find all tiles transitions and extract them to an XML file.
 * It will check from an existing map all transitions combination.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileTransition
 */
public final class ConfigTileTransitions
{
    /** Configuration file name. */
    public static final String FILENAME = "transitions.xml";
    /** Transitions node. */
    public static final String NODE_TRANSITIONS = Configurer.PREFIX + "transitions";
    /** Transition node. */
    public static final String NODE_TRANSITION = Configurer.PREFIX + "transition";
    /** Attribute transition name. */
    public static final String ATTRIBUTE_TRANSITION_NAME = "name";

    /**
     * Import all transitions from configuration.
     * 
     * @param configTransitions The transitions media.
     * @return The transitions imported.
     * @throws LionEngineException If unable to read data.
     */
    public static Map<TileTransition, Collection<TileRef>> imports(Media configTransitions) throws LionEngineException
    {
        final XmlNode root = Stream.loadXml(configTransitions);
        final Collection<XmlNode> nodesTransition = root.getChildren(NODE_TRANSITION);
        final Map<TileTransition, Collection<TileRef>> transitions;
        transitions = new HashMap<TileTransition, Collection<TileRef>>(nodesTransition.size());

        for (final XmlNode nodeTransition : nodesTransition)
        {
            final String transitionName = nodeTransition.readString(ATTRIBUTE_TRANSITION_NAME);
            final TileTransition transition = TileTransition.from(transitionName);

            final Collection<XmlNode> nodesTileRef = nodeTransition.getChildren(ConfigTile.NODE_TILE);
            final Collection<TileRef> tilesRef = importTiles(nodesTileRef);

            transitions.put(transition, tilesRef);
        }
        return transitions;
    }

    /**
     * Export all transitions to an XML file.
     * 
     * @param media The export output.
     * @param map The map reference.
     * @throws LionEngineException If error on export.
     */
    public static void exports(Media media, MapTile map) throws LionEngineException
    {
        final Map<TileTransition, Collection<TileRef>> transitions = getTransitions(map);
        final XmlNode nodeTransitions = Stream.createXmlNode(NODE_TRANSITIONS);

        for (final Map.Entry<TileTransition, Collection<TileRef>> entry : transitions.entrySet())
        {
            final TileTransition transition = entry.getKey();
            final XmlNode nodeTransition = nodeTransitions.createChild(NODE_TRANSITION);
            nodeTransition.writeString(ATTRIBUTE_TRANSITION_NAME, transition.name());

            exportTiles(nodeTransition, entry.getValue());
        }

        Stream.saveXml(nodeTransitions, media);
    }

    /**
     * Check map tile transitions.
     *
     * @param map The map reference.
     * @return The transitions found.
     */
    public static Map<TileTransition, Collection<TileRef>> getTransitions(MapTile map)
    {
        final Map<TileTransition, Collection<TileRef>> transitions = new HashMap<TileTransition, Collection<TileRef>>();
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    final TileTransition transition = getTransition(map, tile);
                    final Collection<TileRef> tiles = getTiles(transitions, transition);
                    tiles.add(new TileRef(tile));
                }
            }
        }
        return transitions;
    }

    /**
     * Get the tile transition.
     * 
     * @param map The map reference.
     * @param tile The current tile.
     * @return The tile transition.
     */
    public static TileTransition getTransition(MapTile map, Tile tile)
    {
        final Boolean[] bits = new Boolean[TileTransition.BITS];
        final int tx = tile.getX() / tile.getWidth();
        final int ty = tile.getY() / tile.getHeight();
        int i = 0;
        for (int v = ty + 1; v >= ty - 1; v--)
        {
            for (int h = tx - 1; h <= tx + 1; h++)
            {
                final Tile neighbor = map.getTile(h, v);
                if (neighbor != null)
                {
                    bits[i] = Boolean.valueOf(neighbor.getGroup().equals(tile.getGroup()));
                }
                i++;
            }
        }
        return TileTransition.from(bits);
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
            final TileRef tileRef = ConfigTile.create(nodeTileRef);
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
            final XmlNode nodeTileRef = ConfigTile.export(tileRef);
            nodeTransition.add(nodeTileRef);
        }
    }

    /**
     * Get the tiles for the transition. Create empty list if new transition.
     * 
     * @param transitions The transitions data.
     * @param transition The transition type.
     * @return The associated tiles.
     */
    private static Collection<TileRef> getTiles(Map<TileTransition, Collection<TileRef>> transitions,
                                                TileTransition transition)
    {
        if (!transitions.containsKey(transition))
        {
            transitions.put(transition, new HashSet<TileRef>());
        }
        return transitions.get(transition);
    }

    /**
     * Disabled constructor.
     */
    private ConfigTileTransitions()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
