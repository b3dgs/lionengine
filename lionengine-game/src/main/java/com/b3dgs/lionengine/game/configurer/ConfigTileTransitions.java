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
    public static final String ATT_TRANSITION_NAME = "name";

    /**
     * Import all transitions from an XML file.
     * 
     * @param media The transitions media.
     * @return The transitions imported.
     * @throws LionEngineException If error on reading transitions.
     */
    public static Map<TileTransition, Collection<TileRef>> imports(Media media) throws LionEngineException
    {
        final Map<TileTransition, Collection<TileRef>> transitions = new HashMap<TileTransition, Collection<TileRef>>();
        final XmlNode root = Stream.loadXml(media);
        for (final XmlNode nodeTransition : root.getChildren(NODE_TRANSITION))
        {
            final String name = nodeTransition.readString(ATT_TRANSITION_NAME);
            final TileTransition transition = TileTransition.from(name);

            final Collection<TileRef> tiles = new HashSet<TileRef>();
            for (final XmlNode nodeTileRef : nodeTransition.getChildren(ConfigTileRef.TILE_REF))
            {
                tiles.add(ConfigTileRef.create(nodeTileRef));
            }

            transitions.put(transition, tiles);
        }
        return transitions;
    }

    /**
     * Export all transitions to an XML file.
     * 
     * @param media The export output.
     * @param map The map reference.
     */
    public static void exports(Media media, MapTile map)
    {
        final Map<TileTransition, Collection<TileRef>> transitions = getTransitions(map);
        final XmlNode root = Stream.createXmlNode(NODE_TRANSITIONS);
        for (final Map.Entry<TileTransition, Collection<TileRef>> entry : transitions.entrySet())
        {
            final TileTransition transition = entry.getKey();
            final XmlNode nodeTransition = root.createChild(NODE_TRANSITION);
            nodeTransition.writeString(ATT_TRANSITION_NAME, transition.name());
            for (final TileRef tile : entry.getValue())
            {
                nodeTransition.add(ConfigTileRef.export(tile));
            }
        }
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
