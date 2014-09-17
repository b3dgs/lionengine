/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.CollisionRefential;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Utility related to map tile operations.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilMapTile
{
    /** Tag prefix. */
    public static final String TAG_PREFIX = "lionengine:";
    /** Tag tile collisions. */
    public static final String TAG_TILE_COLLISIONS = UtilMapTile.TAG_PREFIX + "tileCollisions";
    /** Tag tile collision. */
    public static final String TAG_TILE_COLLISION = UtilMapTile.TAG_PREFIX + "collision";
    /** Attribute tile collision name. */
    public static final String ATT_TILE_COLLISION_NAME = "name";
    /** Tag function. */
    public static final String TAG_FUNCTION = UtilMapTile.TAG_PREFIX + "function";
    /** Tag tiles. */
    public static final String TAG_TILES = UtilMapTile.TAG_PREFIX + "tiles";
    /** Tag tile. */
    public static final String TAG_TILE = UtilMapTile.TAG_PREFIX + "tile";
    /** Attribute tile pattern. */
    public static final String ATT_TILE_PATTERN = "pattern";
    /** Attribute tile number. */
    public static final String ATT_TILE_NUMBER = "number";
    /** Attribute tile start. */
    public static final String ATT_TILE_START = "start";
    /** Attribute tile end. */
    public static final String ATT_TILE_END = "end";
    /** Attribute name. */
    public static final String ATT_FUNCTION_NAME = "name";
    /** Attribute axis. */
    public static final String ATT_FUNCTION_AXIS = "axis";
    /** Attribute input. */
    public static final String ATT_FUNCTION_INPUT = "input";
    /** Attribute value. */
    public static final String ATT_FUNCTION_VALUE = "value";
    /** Attribute offset. */
    public static final String ATT_FUNCTION_OFFSET = "offset";
    /** Attribute min. */
    public static final String ATT_FUNCTION_MIN = "min";
    /** Attribute max. */
    public static final String ATT_FUNCTION_MAX = "max";

    /**
     * Create the function draw to buffer.
     * 
     * @param functions The functions list.
     * @param map The map reference.
     * @return The created collision representation buffer.
     */
    public static ImageBuffer createFunctionDraw(Collection<CollisionFunction> functions, MapTile<?> map)
    {
        final ImageBuffer buffer = Core.GRAPHIC.createImageBuffer(map.getTileWidth(), map.getTileHeight(),
                Transparency.TRANSLUCENT);
        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.PURPLE);

        for (final CollisionFunction function : functions)
        {
            UtilMapTile.createFunctionDraw(g, function, map.getTileHeight());
        }
        g.dispose();
        return buffer;
    }

    /**
     * Save the current map collisions to the collision file.
     * 
     * @param map The map reference.
     * @param media The collision file to store the collisions.
     * @throws LionEngineException If error when saving collisions.
     */
    public static void saveCollisions(MapTile<?> map, Media media) throws LionEngineException
    {
        final XmlNode root = Stream.createXmlNode(UtilMapTile.TAG_TILE_COLLISIONS);
        for (final CollisionTile collision : map.getCollisions())
        {
            final XmlNode node = Stream.createXmlNode(UtilMapTile.TAG_TILE_COLLISION);
            node.writeString(UtilMapTile.ATT_TILE_COLLISION_NAME, collision.getValue().name());
            if (UtilMapTile.saveTilesCollisions(map, node, collision))
            {
                root.add(node);
            }
            final Set<CollisionFunction> functions = collision.getCollisionFunctions();
            if (functions != null)
            {
                for (final CollisionFunction function : functions)
                {
                    final XmlNode functionNode = Stream.createXmlNode(UtilMapTile.TAG_FUNCTION);
                    functionNode.writeString(UtilMapTile.ATT_FUNCTION_NAME, function.getName());
                    functionNode.writeString(UtilMapTile.ATT_FUNCTION_AXIS, function.getAxis().name());
                    functionNode.writeString(UtilMapTile.ATT_FUNCTION_INPUT, function.getInput().name());
                    functionNode.writeDouble(UtilMapTile.ATT_FUNCTION_VALUE, function.getValue());
                    functionNode.writeInteger(UtilMapTile.ATT_FUNCTION_OFFSET, function.getOffset());
                    functionNode.writeInteger(UtilMapTile.ATT_FUNCTION_MIN, function.getRange().getMin());
                    functionNode.writeInteger(UtilMapTile.ATT_FUNCTION_MAX, function.getRange().getMax());
                    node.add(functionNode);
                }
            }
        }
        Stream.saveXml(root, media);
    }

    /**
     * Save the tile node depending of their consecutiveness.
     * 
     * @param node The XML node.
     * @param pattern The pattern number.
     * @param numbers The numbers list.
     * @return <code>true</code> if stored, <code>false</code> else.
     * @throws LionEngineException If error when saving tile node.
     */
    public static boolean saveTileNode(XmlNode node, Integer pattern, List<Integer> numbers) throws LionEngineException
    {
        final boolean added;
        if (numbers.size() == 1)
        {
            final XmlNode tile = Stream.createXmlNode(UtilMapTile.TAG_TILE);
            node.add(tile);
            tile.writeInteger(UtilMapTile.ATT_TILE_PATTERN, pattern.intValue());
            tile.writeInteger(UtilMapTile.ATT_TILE_NUMBER, numbers.get(0).intValue());
            added = true;
        }
        else if (numbers.size() > 1)
        {
            final XmlNode tile = Stream.createXmlNode(UtilMapTile.TAG_TILES);
            node.add(tile);
            tile.writeInteger(UtilMapTile.ATT_TILE_PATTERN, pattern.intValue());
            tile.writeInteger(UtilMapTile.ATT_TILE_START, numbers.get(0).intValue());
            tile.writeInteger(UtilMapTile.ATT_TILE_END, numbers.get(numbers.size() - 1).intValue());
            added = true;
        }
        else
        {
            added = false;
        }
        return added;
    }

    /**
     * Split non consecutive numbers per pattern into multiple list of numbers.
     * 
     * @param patterns The pattern set.
     * @param pattern The current pattern.
     * @return The splited numbers list.
     */
    public static List<List<Integer>> splitNonConsecutiveNumbers(Map<Integer, SortedSet<Integer>> patterns,
            Integer pattern)
    {
        final SortedSet<Integer> numbers = patterns.get(pattern);
        final List<List<Integer>> series = new ArrayList<>(8);

        int lastValue = -2;
        List<Integer> currentSerie = null;
        for (final Integer number : numbers)
        {
            final int newValue = number.intValue();
            if (newValue - lastValue != 1)
            {
                currentSerie = new ArrayList<>(8);
                series.add(currentSerie);
            }
            lastValue = newValue;
            if (currentSerie != null)
            {
                currentSerie.add(number);
            }
        }
        return series;
    }

    /**
     * Get the tile search speed value.
     * 
     * @param d The distance value.
     * @return The speed value.
     */
    public static double getTileSearchSpeed(int d)
    {
        if (d < 0)
        {
            return -1;
        }
        else if (d > 0)
        {
            return 1;
        }
        return 0.0;
    }

    /**
     * Get the tile search speed value.
     * 
     * @param dsup The distance superior value.
     * @param dinf The distance inferior value.
     * @return The speed value.
     */
    public static double getTileSearchSpeed(int dsup, int dinf)
    {
        if (0 == dsup)
        {
            return UtilMapTile.getTileSearchSpeed(dinf);
        }
        return dinf / (double) dsup;
    }

    /**
     * Read collisions from external file.
     * 
     * @param collisions The collision nodes.
     * @param tilePattern The tile pattern number.
     * @param tileNumber The tile number.
     * @return The collision found.
     * @throws LionEngineException If error when reading.
     */
    public static String getCollision(List<XmlNode> collisions, int tilePattern, int tileNumber)
            throws LionEngineException
    {
        for (final XmlNode collision : collisions)
        {
            final String name = collision.readString(UtilMapTile.ATT_TILE_COLLISION_NAME);

            String found = UtilMapTile.searchCollision(collision, name, UtilMapTile.TAG_TILES, tilePattern, tileNumber);
            if (found != null)
            {
                return found;
            }

            found = UtilMapTile.searchCollision(collision, name, UtilMapTile.TAG_TILE, tilePattern, tileNumber);
            if (found != null)
            {
                return found;
            }
        }
        return null;
    }

    /**
     * Get the collision function from the node.
     * 
     * @param collision The current collision enum.
     * @param functionNode The function node reference.
     * @return The created collision function from the node data.
     * @throws LionEngineException If error when reading.
     */
    public static CollisionFunction getCollisionFunction(CollisionTile collision, XmlNode functionNode)
            throws LionEngineException
    {
        final CollisionFunction function = new CollisionFunction();
        function.setName(functionNode.readString(UtilMapTile.ATT_FUNCTION_NAME));
        function.setAxis(CollisionRefential.valueOf(functionNode.readString(UtilMapTile.ATT_FUNCTION_AXIS)));
        function.setInput(CollisionRefential.valueOf(functionNode.readString(UtilMapTile.ATT_FUNCTION_INPUT)));
        function.setValue(functionNode.readDouble(UtilMapTile.ATT_FUNCTION_VALUE));
        function.setOffset(functionNode.readInteger(UtilMapTile.ATT_FUNCTION_OFFSET));
        function.setRange(functionNode.readInteger(UtilMapTile.ATT_FUNCTION_MIN),
                functionNode.readInteger(UtilMapTile.ATT_FUNCTION_MAX));
        return function;
    }

    /**
     * Create the function draw to buffer.
     * 
     * @param g The graphic buffer.
     * @param function The function to draw.
     * @param tileHeight The tile height value.
     */
    private static void createFunctionDraw(Graphic g, CollisionFunction function, int tileHeight)
    {
        final int min = function.getRange().getMin();
        final int max = function.getRange().getMax();
        switch (function.getAxis())
        {
            case X:
                UtilMapTile.createFunctionDrawX(g, function, min, max, tileHeight);
                break;
            case Y:
                UtilMapTile.createFunctionDrawY(g, function, min, max, tileHeight);
                break;
            default:
                throw new RuntimeException("Unknown type: " + function.getAxis());
        }
    }

    /**
     * Create the function draw to buffer for the horizontal axis.
     * 
     * @param g The graphic buffer.
     * @param function The function to draw.
     * @param min The minimum value.
     * @param max The maximum value.
     * @param tileHeight The tile height value.
     */
    private static void createFunctionDrawX(Graphic g, CollisionFunction function, int min, int max, int tileHeight)
    {
        switch (function.getInput())
        {
            case X:
                for (int x = min; x <= max; x++)
                {
                    final int fx = (int) function.computeCollision(x);
                    g.drawRect(fx, tileHeight - x, 0, 0, false);
                }
                break;
            case Y:
                for (int y = min; y <= max; y++)
                {
                    final int fy = (int) function.computeCollision(y);
                    g.drawRect(fy, y, 0, 0, false);
                }
                break;
            default:
                throw new RuntimeException("Unknown type: " + function.getInput());
        }
    }

    /**
     * Create the function draw to buffer for the vertical axis.
     * 
     * @param g The graphic buffer.
     * @param function The function to draw.
     * @param min The minimum value.
     * @param max The maximum value.
     * @param tileHeight The tile height value.
     */
    private static void createFunctionDrawY(Graphic g, CollisionFunction function, int min, int max, int tileHeight)
    {
        switch (function.getInput())
        {
            case X:
                for (int x = min; x <= max; x++)
                {
                    final int fx = (int) function.computeCollision(x);
                    g.drawRect(x, tileHeight - 1 - fx, 0, 0, false);
                }
                break;
            case Y:
                for (int y = min; y <= max; y++)
                {
                    final int fy = (int) function.computeCollision(y);
                    g.drawRect(fy, y, 0, 0, false);
                }
                break;
            default:
                throw new RuntimeException("Unknown type: " + function.getInput());
        }
    }

    /**
     * Search the collision correspondence depending of the category.
     * 
     * @param collision The collision node.
     * @param name The collision name.
     * @param category The collision search category.
     * @param tilePattern The tile pattern number.
     * @param tileNumber The tile number.
     * @return The collision found.
     * @throws LionEngineException If error when reading.
     */
    private static String searchCollision(XmlNode collision, String name, String category, int tilePattern,
            int tileNumber) throws LionEngineException
    {
        final List<XmlNode> tilesCollisions = collision.getChildren(category);

        for (final XmlNode tile : tilesCollisions)
        {
            final int pattern = tile.readInteger(UtilMapTile.ATT_TILE_PATTERN);
            int start = -1;
            int end = -1;
            if (UtilMapTile.TAG_TILES.equals(category))
            {
                start = tile.readInteger(UtilMapTile.ATT_TILE_START);
                end = tile.readInteger(UtilMapTile.ATT_TILE_END);
            }
            else if (UtilMapTile.TAG_TILE.equals(category))
            {
                start = tile.readInteger(UtilMapTile.ATT_TILE_NUMBER);
                end = start;
            }
            if (tilePattern == pattern)
            {
                if (tileNumber + 1 >= start && tileNumber + 1 <= end)
                {
                    tilesCollisions.clear();
                    return name;
                }
            }
        }

        tilesCollisions.clear();
        return null;
    }

    /**
     * Save tiles collisions for all of the map tile.
     * 
     * @param map The map reference.
     * @param node The XML node.
     * @param collision The current collision.
     * @return <code>true</code> if at least on tile stored, <code>false</code> else.
     * @throws LionEngineException If error when saving tile node.
     */
    private static boolean saveTilesCollisions(MapTile<?> map, XmlNode node, CollisionTile collision)
            throws LionEngineException
    {
        final Map<Integer, SortedSet<Integer>> patterns = UtilMapTile.getCollisionsPattern(map, node, collision);
        boolean added = false;
        for (final Integer pattern : patterns.keySet())
        {
            final List<List<Integer>> elements = UtilMapTile.splitNonConsecutiveNumbers(patterns, pattern);
            for (final List<Integer> numbers : elements)
            {
                added = UtilMapTile.saveTileNode(node, pattern, numbers);
            }
        }
        return added;
    }

    /**
     * Sort each tile number for each pattern for each collision in a map.
     * 
     * @param map The map reference.
     * @param node The current node.
     * @param collision The current collision.
     * @return The values.
     */
    private static Map<Integer, SortedSet<Integer>> getCollisionsPattern(MapTile<?> map, XmlNode node,
            CollisionTile collision)
    {
        final Map<Integer, SortedSet<Integer>> patterns = new HashMap<>(8);
        for (int ty = 0; ty < map.getHeightInTile(); ty++)
        {
            for (int tx = 0; tx < map.getWidthInTile(); tx++)
            {
                final TileGame tile = map.getTile(tx, ty);
                if (tile != null && tile.getCollision() == collision)
                {
                    final Integer pattern = tile.getPattern();
                    final SortedSet<Integer> numbers;

                    if (!patterns.containsKey(pattern))
                    {
                        numbers = new TreeSet<>();
                        patterns.put(pattern, numbers);
                    }
                    else
                    {
                        numbers = patterns.get(pattern);
                    }
                    numbers.add(Integer.valueOf(tile.getNumber() + 1));
                }
            }
        }
        return patterns;
    }
}
