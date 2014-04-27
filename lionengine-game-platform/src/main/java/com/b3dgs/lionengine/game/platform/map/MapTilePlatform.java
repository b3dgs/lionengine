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
package com.b3dgs.lionengine.game.platform.map;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlParser;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.platform.CollisionFunction;
import com.b3dgs.lionengine.game.platform.CollisionRefential;
import com.b3dgs.lionengine.game.platform.CollisionTile;
import com.b3dgs.lionengine.game.platform.entity.EntityPlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Default platform map implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <C> Collision type used.
 * @param <T> Tile type used.
 */
public abstract class MapTilePlatform<C extends Enum<C> & CollisionTile, T extends TilePlatform<C>>
        extends MapTileGame<C, T>
{
    /** Collision draw cache. */
    private EnumMap<C, ImageBuffer> collisionCache;

    /**
     * Get the tile search speed value.
     * 
     * @param d The distance value.
     * @return The speed value.
     */
    private static double getTileSearchSpeed(int d)
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
    private static double getTileSearchSpeed(int dsup, int dinf)
    {
        if (0 == dsup)
        {
            return MapTilePlatform.getTileSearchSpeed(dinf);
        }
        return dinf / (double) dsup;
    }

    /**
     * Constructor.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTilePlatform(int tileWidth, int tileHeight)
    {
        super(tileWidth, tileHeight);
    }

    /**
     * Create the collision draw surface. Must be called after map creation to enable collision rendering.
     * 
     * @param collisionClass The collisionClass enumeration class.
     */
    public void createCollisionDraw(Class<C> collisionClass)
    {
        if (collisionCache == null)
        {
            collisionCache = new EnumMap<>(collisionClass);
        }
        else
        {
            collisionCache.clear();
        }

        for (final C collision : collisionClass.getEnumConstants())
        {
            final Set<CollisionFunction> functions = collision.getCollisionFunctions();
            if (functions != null)
            {
                final ImageBuffer buffer = Core.GRAPHIC.createImageBuffer(getTileWidth(), getTileHeight(),
                        Transparency.TRANSLUCENT);
                final Graphic g = buffer.createGraphic();
                g.setColor(new ColorRgba(0, 0, 0, 0));
                g.drawRect(0, 0, buffer.getWidth(), buffer.getHeight(), true);
                g.setColor(ColorRgba.PURPLE);

                for (final CollisionFunction function : functions)
                {
                    createFunctionDraw(g, function);
                }
                g.dispose();
                collisionCache.put(collision, buffer);
            }
        }
    }

    /**
     * Assign the collision function to all tiles with the same collision.
     * 
     * @param collision The current collision enum.
     * @param function The function reference.
     */
    public void assignCollisionFunction(C collision, CollisionFunction function)
    {
        collision.addCollisionFunction(function);
    }

    /**
     * Remove a collision function.
     * 
     * @param collisionClass The collision enum type.
     * @param function The function to remove.
     */
    public void removeCollisionFunction(Class<C> collisionClass, CollisionFunction function)
    {
        for (final C collision : collisionClass.getEnumConstants())
        {
            collision.removeCollisionFunction(function);
        }
    }

    /**
     * Get the tile at the entity location.
     * 
     * @param entity The entity.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the entity.
     */
    public T getTile(EntityPlatform entity, int offsetX, int offsetY)
    {
        final int tx = (entity.getLocationIntX() + offsetX) / getTileWidth();
        final int ty = (entity.getLocationIntY() + offsetY) / getTileHeight();
        return getTile(tx, ty);
    }

    /**
     * Get the first tile hit by the localizable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the localizable can not pass through a collidable tile.
     * 
     * @param localizable The localizable reference.
     * @param collisions Collisions list to search for.
     * @param applyRayCast <code>true</code> to apply collision to each tile crossed, <code>false</code> to ignore and
     *            just return the first tile hit.
     * @return The first tile hit, <code>null</code> if none found.
     */
    public T getFirstTileHit(Localizable localizable, EnumSet<C> collisions, boolean applyRayCast)
    {
        // Starting location
        final int sv = (int) Math.floor(localizable.getLocationOldY());
        final int sh = (int) Math.floor(localizable.getLocationOldX());

        // Ending location
        final int ev = (int) Math.floor(localizable.getLocationY());
        final int eh = (int) Math.floor(localizable.getLocationX());

        // Distance calculation
        final int dv = ev - sv;
        final int dh = eh - sh;

        // Search vector and number of search steps
        final double sx, sy;
        final int stepMax;
        if (Math.abs(dv) >= Math.abs(dh))
        {
            sy = MapTilePlatform.getTileSearchSpeed(dv);
            sx = MapTilePlatform.getTileSearchSpeed(Math.abs(dv), dh);
            stepMax = Math.abs(dv);
        }
        else
        {
            sx = MapTilePlatform.getTileSearchSpeed(dh);
            sy = MapTilePlatform.getTileSearchSpeed(Math.abs(dh), dv);
            stepMax = Math.abs(dh);
        }

        T t = null;
        int step = 0;
        for (double v = sv, h = sh; step <= stepMax;)
        {
            T tile = checkCollision(localizable, collisions, h, (int) Math.floor(v / getTileHeight()), applyRayCast);
            if (t == null)
            {
                t = tile;
            }
            h += sx;

            tile = checkCollision(localizable, collisions, h, (int) Math.ceil(v / getTileHeight()), applyRayCast);
            if (t == null)
            {
                t = tile;
            }
            v += sy;

            step++;
            if (!applyRayCast && t != null)
            {
                return t;
            }
        }
        return t;
    }

    /**
     * Get location x relative to map referential as tile.
     * 
     * @param localizable The localizable reference.
     * @return The location x relative to map referential as tile.
     */
    public int getInTileX(Localizable localizable)
    {
        return (int) Math.floor(localizable.getLocationX() / getTileWidth());
    }

    /**
     * Get location y relative to map referential as tile.
     * 
     * @param localizable The localizable reference.
     * @return The location y relative to map referential as tile.
     */
    public int getInTileY(Localizable localizable)
    {
        return (int) Math.floor(localizable.getLocationY() / getTileHeight());
    }

    /**
     * Create the function draw to buffer.
     * 
     * @param g The graphic buffer.
     * @param function The function to draw.
     */
    private void createFunctionDraw(Graphic g, CollisionFunction function)
    {
        final int min = function.getRange().getMin();
        final int max = function.getRange().getMax();
        switch (function.getAxis())
        {
            case X:
                switch (function.getInput())
                {
                    case X:
                        for (int x = min; x <= max; x++)
                        {
                            final int fx = (int) function.computeCollision(x);
                            g.drawRect(fx, getTileHeight() - x, 0, 0, false);
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
                break;
            case Y:
                switch (function.getInput())
                {
                    case X:
                        for (int x = min; x <= max; x++)
                        {
                            final int fx = (int) function.computeCollision(x);
                            g.drawRect(x, getTileHeight() - 1 - fx, 0, 0, false);
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
                break;
            default:
                throw new RuntimeException("Unknown type: " + function.getAxis());
        }
    }

    /**
     * Load the collision function from the node.
     * 
     * @param collision The current collision enum.
     * @param functionNode The function node reference.
     */
    private void loadCollisionFunction(C collision, XmlNode functionNode)
    {
        final CollisionFunction function = new CollisionFunction();
        function.setName(functionNode.readString("name"));
        function.setAxis(CollisionRefential.valueOf(functionNode.readString("axis")));
        function.setInput(CollisionRefential.valueOf(functionNode.readString("input")));
        function.setValue(functionNode.readDouble("value"));
        function.setOffset(functionNode.readInteger("offset"));
        function.setRange(functionNode.readInteger("min"), functionNode.readInteger("max"));

        assignCollisionFunction(collision, function);
    }

    /**
     * Check the collision at the specified location.
     * 
     * @param localizable The localizable reference.
     * @param collisions Collisions list to search for.
     * @param h The horizontal index.
     * @param ty The vertical tile.
     * @param applyRayCast <code>true</code> to apply collision to each tile crossed, <code>false</code> to ignore and
     *            just return the first tile hit.
     * @return The first tile hit, <code>null</code> if none found.
     */
    private T checkCollision(Localizable localizable, EnumSet<C> collisions, double h, int ty, boolean applyRayCast)
    {
        final T tile = getTile((int) Math.floor(h / getTileWidth()), ty);
        if (tile != null && collisions.contains(tile.getCollision()))
        {
            final Double coll = tile.getCollisionY(localizable);
            if (applyRayCast && coll != null)
            {
                localizable.teleportY(coll.doubleValue());
            }
            return tile;
        }
        return null;
    }

    /**
     * Render the collision function.
     * 
     * @param g The graphic output.
     * @param tile The tile reference.
     * @param x The horizontal render location.
     * @param y The vertical render location.
     */
    private void renderCollision(Graphic g, T tile, int x, int y)
    {
        final ImageBuffer buffer = collisionCache.get(tile.getCollision());
        if (buffer != null)
        {
            g.drawImage(buffer, x, y);
        }
    }

    /*
     * MapTileGame
     */

    @Override
    public void loadCollisions(Media media)
    {
        super.loadCollisions(media);

        final XmlParser xml = File.createXmlParser();
        final XmlNode root = xml.load(media);
        final List<XmlNode> collisions = root.getChildren("collision");
        for (final XmlNode node : collisions)
        {
            final C collision = getCollisionFrom(node.readString("name"));
            for (final XmlNode functionNode : node.getChildren("function"))
            {
                loadCollisionFunction(collision, functionNode);
            }
        }
    }

    @Override
    protected void renderTile(Graphic g, T tile, int x, int y)
    {
        super.renderTile(g, tile, x, y);
        if (collisionCache != null)
        {
            renderCollision(g, tile, x, y);
        }
    }
}
