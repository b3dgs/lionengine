/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

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
import com.b3dgs.lionengine.graphic.ColorRgba;

/**
 * Represents the minimap configuration.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Minimap
 */
public final class MinimapConfig
{
    /** Default filename. */
    public static final String FILENAME = "minimap.xml";
    /** Minimap root node. */
    public static final String NODE_MINIMAP = Constant.XML_PREFIX + "minimap";
    /** Color node. */
    public static final String NODE_COLOR = Constant.XML_PREFIX + "color";
    /** Red name attribute. */
    public static final String ATT_COLOR_RED = "r";
    /** Green name attribute. */
    public static final String ATT_COLOR_GREEN = "g";
    /** Blue name attribute. */
    public static final String ATT_COLOR_BLUE = "b";

    /**
     * Create the minimap data from node.
     * 
     * @param configMinimap The minimap configuration media (must not be <code>null</code>).
     * @return The minimap data.
     * @throws LionEngineException If unable to read data.
     */
    public static Map<Integer, ColorRgba> imports(Media configMinimap)
    {
        Check.notNull(configMinimap);

        final Map<Integer, ColorRgba> colors = new HashMap<>();
        final XmlReader nodeMinimap = new XmlReader(configMinimap);

        final Collection<XmlReader> children = nodeMinimap.getChildren(NODE_COLOR);
        for (final XmlReader nodeColor : children)
        {
            final ColorRgba color = new ColorRgba(nodeColor.getInteger(ATT_COLOR_RED),
                                                  nodeColor.getInteger(ATT_COLOR_GREEN),
                                                  nodeColor.getInteger(ATT_COLOR_BLUE));

            for (final XmlReader nodeTile : nodeColor.getChildren(TileConfig.NODE_TILE))
            {
                final Integer tile = Integer.valueOf(TileConfig.imports(nodeTile));
                colors.put(tile, color);
            }
        }
        children.clear();

        return colors;
    }

    /**
     * Export tiles colors data to configuration media.
     * 
     * @param configMinimap The configuration media output (must not be <code>null</code>).
     * @param tiles The tiles data (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Media configMinimap, Map<Integer, ColorRgba> tiles)
    {
        Check.notNull(configMinimap);
        Check.notNull(tiles);

        final Map<ColorRgba, Collection<Integer>> colors = convertToColorKey(tiles);
        final Xml nodeMinimap = new Xml(NODE_MINIMAP);

        for (final Map.Entry<ColorRgba, Collection<Integer>> entry : colors.entrySet())
        {
            final ColorRgba color = entry.getKey();
            final Xml nodeColor = nodeMinimap.createChild(NODE_COLOR);
            nodeColor.writeInteger(ATT_COLOR_RED, color.getRed());
            nodeColor.writeInteger(ATT_COLOR_GREEN, color.getGreen());
            nodeColor.writeInteger(ATT_COLOR_BLUE, color.getBlue());

            for (final Integer number : entry.getValue())
            {
                final Xml nodeTile = TileConfig.exports(number.intValue());
                nodeColor.add(nodeTile);
            }
        }

        nodeMinimap.save(configMinimap);
    }

    /**
     * Convert map associating color per tile to tiles per color.
     * 
     * @param tiles The tiles data (must not be <code>null</code>).
     * @return The map with color as key.
     */
    private static Map<ColorRgba, Collection<Integer>> convertToColorKey(Map<Integer, ColorRgba> tiles)
    {
        final Map<ColorRgba, Collection<Integer>> colors = new HashMap<>();

        for (final Map.Entry<Integer, ColorRgba> entry : tiles.entrySet())
        {
            final Collection<Integer> tilesNumber = getTiles(colors, entry.getValue());
            tilesNumber.add(entry.getKey());
        }

        return colors;
    }

    /**
     * Get the tiles corresponding to the color. Create empty list if new color.
     * 
     * @param colors The colors data (must not be <code>null</code>).
     * @param color The color to check (must not be <code>null</code>).
     * @return The associated tiles.
     */
    private static Collection<Integer> getTiles(Map<ColorRgba, Collection<Integer>> colors, ColorRgba color)
    {
        return colors.computeIfAbsent(color, k -> new HashSet<>());
    }

    /**
     * Disabled constructor.
     */
    private MinimapConfig()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
