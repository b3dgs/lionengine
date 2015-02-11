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
package com.b3dgs.lionengine.game.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.game.collision.CollisionResult;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionFormula;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionGroup;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Map tile collision model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapTileCollisionModel
        implements MapTileCollision
{
    /** Info loading formulas. */
    private static final String INFO_LOAD_FORMULAS = "Loading collision formulas from: ";
    /** Info loading groups. */
    private static final String INFO_LOAD_GROUPS = "Loading collision groups from: ";
    /** Error formula not found. */
    private static final String ERROR_FORMULA = "Formula not found (may not have been loaded): ";

    /**
     * Check the constraint with the specified tile.
     * 
     * @param constraint The constraint name to check.
     * @param tile The tile to check with.
     * @return <code>true</code> if can be ignored, <code>false</code> else.
     */
    private static boolean checkConstraint(String constraint, Tile tile)
    {
        return constraint != null && tile != null
                && !tile.getFeature(TileCollision.class).getCollisionFormulas().isEmpty();
    }

    /**
     * Check if tile contains at least one collision from the category.
     * 
     * @param tile The tile reference.
     * @param category The category reference.
     * @return <code>true</code> if there is a formula in common between tile and category.
     */
    private static boolean containsCollisionFormula(TileCollision tile, CollisionCategory category)
    {
        final Collection<CollisionFormula> formulas = tile.getCollisionFormulas();
        for (final CollisionFormula formula : category.getFormulas())
        {
            if (formulas.contains(formula))
            {
                return true;
            }
        }
        return false;
    }

    /** Map reference. */
    private final MapTile map;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas;
    /** Collisions groups list. */
    private final Map<String, CollisionGroup> groups;
    /** Collision draw cache. */
    private HashMap<CollisionFormula, ImageBuffer> collisionCache;

    /**
     * Create the map tile collision.
     * 
     * @param map The map reference.
     * @param viewer The viewer reference.
     */
    public MapTileCollisionModel(MapTile map, Viewer viewer)
    {
        this.map = map;
        this.viewer = viewer;
        formulas = new HashMap<>();
        groups = new HashMap<>();
    }

    /**
     * Create the function draw to buffer.
     * 
     * @param collision The collision reference.
     * @return The created collision representation buffer.
     */
    private ImageBuffer createFunctionDraw(CollisionFormula collision)
    {
        final ImageBuffer buffer = Core.GRAPHIC.createImageBuffer(map.getTileWidth() + 2, map.getTileHeight() + 2,
                Transparency.TRANSLUCENT);
        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.PURPLE);

        createFunctionDraw(g, collision);

        g.dispose();
        return buffer;
    }

    /**
     * Create the function draw to buffer.
     * 
     * @param g The graphic buffer.
     * @param formula The collision formula to draw.
     */
    private void createFunctionDraw(Graphic g, CollisionFormula formula)
    {
        final CollisionFunction function = formula.getFunction();
        final CollisionRange range = formula.getRange();

        for (int x = 0; x < map.getTileWidth(); x++)
        {
            for (int y = 0; y < map.getTileHeight(); y++)
            {
                switch (range.getOutput())
                {
                    case X:
                        final double fx = function.compute(x);
                        if (UtilMath.isBetween(x, range.getMinX(), range.getMaxX()))
                        {
                            g.drawRect((int) fx + 1, map.getTileHeight() - y, 0, 0, false);
                        }
                        break;
                    case Y:
                        final double fy = function.compute(y);
                        if (UtilMath.isBetween(y, range.getMinY(), range.getMaxY()))
                        {
                            g.drawRect(x + 1, map.getTileHeight() - (int) fy, 0, 0, false);
                        }
                        break;
                    default:
                        throw new RuntimeException("Unknown type: " + range.getOutput());
                }
            }
        }
    }

    /**
     * Load the collision formula. All previous collisions will be cleared.
     * 
     * @param collisionFormulas The configuration collision formulas file.
     */
    private void loadCollisionFormulas(Media collisionFormulas)
    {
        Verbose.info(INFO_LOAD_FORMULAS, collisionFormulas.getFile().getPath());
        removeCollisionFormulas();
        final XmlNode nodeFormulas = Stream.loadXml(collisionFormulas);
        final ConfigCollisionFormula config = ConfigCollisionFormula.create(nodeFormulas);
        for (final CollisionFormula formula : config.getFormulas().values())
        {
            addCollisionFormula(formula);
        }
        config.clear();
    }

    /**
     * Load the collision groups. All previous groups will be cleared.
     * 
     * @param collisionGroups The configuration collision groups file.
     */
    private void loadCollisionGroups(Media collisionGroups)
    {
        Verbose.info(INFO_LOAD_GROUPS, collisionGroups.getFile().getPath());
        removeCollisionGroups();
        final XmlNode nodeGroups = Stream.loadXml(collisionGroups);
        final Collection<CollisionGroup> groups = ConfigCollisionGroup.create(nodeGroups, this);
        for (final CollisionGroup group : groups)
        {
            addCollisionGroup(group);
        }
        groups.clear();
    }

    /**
     * Load collisions for each tile. Previous collisions will be removed.
     */
    private void loadTilesCollisions()
    {
        for (int v = 0; v < map.getHeightInTile(); v++)
        {
            for (int h = 0; h < map.getWidthInTile(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    final TileCollision tileCollision = new TileCollisionModel(tile);
                    tile.addFeature(tileCollision);
                    tileCollision.removeCollisionFormulas();
                    addTileCollisions(tileCollision, tile.getSheet().intValue(), tile.getNumber());
                }
            }
        }
    }

    /**
     * Add the tile collisions from loaded configuration.
     * 
     * @param tile The tile reference.
     * @param sheet The tile sheet value.
     * @param number The tile number value.
     */
    private void addTileCollisions(TileCollision tile, int sheet, int number)
    {
        for (final CollisionGroup group : getCollisionGroups())
        {
            if (group.getSheet() == sheet && UtilMath.isBetween(number, group.getStart(), group.getEnd()))
            {
                tile.setGroup(group.getName());
                for (final CollisionFormula formula : group.getFormulas())
                {
                    tile.addCollisionFormula(formula);
                }
            }
        }
    }

    /**
     * Apply tile constraints depending of their adjacent collisions.
     */
    private void applyConstraints()
    {
        final Map<Tile, Collection<CollisionFormula>> toRemove = new HashMap<>();
        for (int v = 0; v < map.getHeightInTile(); v++)
        {
            for (int h = 0; h < map.getWidthInTile(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    final TileCollision tileCollision = tile.getFeature(TileCollision.class);
                    toRemove.put(tile, checkConstraints(tileCollision, h, v));
                }
            }
        }
        for (final Entry<Tile, Collection<CollisionFormula>> current : toRemove.entrySet())
        {
            final Tile tile = current.getKey();
            final TileCollision tileCollision = tile.getFeature(TileCollision.class);
            for (final CollisionFormula formula : current.getValue())
            {
                tileCollision.removeCollisionFormula(formula);
            }
        }
    }

    /**
     * Check the tile constraints and get the removable formulas.
     * 
     * @param tile The current tile to check.
     * @param h The horizontal location.
     * @param v The vertical location.
     * @return The formula to remove.
     */
    private Collection<CollisionFormula> checkConstraints(TileCollision tile, int h, int v)
    {
        final Tile top = map.getTile(h, v + 1);
        final Tile bottom = map.getTile(h, v - 1);
        final Tile left = map.getTile(h - 1, v);
        final Tile right = map.getTile(h + 1, v);

        final Collection<CollisionFormula> toRemove = new ArrayList<>();
        for (final CollisionFormula formula : tile.getCollisionFormulas())
        {
            final CollisionConstraint constraint = formula.getConstraint();
            if (checkConstraint(constraint.getTop(), top) || checkConstraint(constraint.getBottom(), bottom)
                    || checkConstraint(constraint.getLeft(), left) || checkConstraint(constraint.getRight(), right))
            {
                toRemove.add(formula);
            }
        }
        return toRemove;
    }

    /**
     * Compute the collision from current location.
     * 
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed collision result.
     */
    private CollisionResult computeCollision(CollisionCategory category, double ox, double oy, double x, double y)
    {
        final Tile tile = map.getTile((int) Math.floor(x / map.getTileWidth()),
                (int) Math.floor(y / map.getTileHeight()));
        if (tile != null)
        {
            final TileCollision tileCollision = tile.getFeature(TileCollision.class);
            if (containsCollisionFormula(tileCollision, category))
            {
                final Double cx = category.getAxis() == Axis.X ? tileCollision.getCollisionX(category, ox, oy, x, y)
                        : null;
                final Double cy = category.getAxis() == Axis.Y ? tileCollision.getCollisionY(category, ox, oy, x, y)
                        : null;

                return new CollisionResult(cx, cy, tile);
            }
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
    private void renderCollision(Graphic g, TileCollision tile, int x, int y)
    {
        for (final CollisionFormula collision : tile.getCollisionFormulas())
        {
            final ImageBuffer buffer = collisionCache.get(collision);
            if (buffer != null)
            {
                // x - 1 because collision result is outside tile area
                g.drawImage(buffer, x - 1, y);
            }
        }
    }

    /*
     * MapTileCollision
     */

    @Override
    public void createCollisionDraw()
    {
        clearCollisionDraw();
        collisionCache = new HashMap<>(formulas.size());

        for (final CollisionFormula collision : formulas.values())
        {
            final ImageBuffer buffer = createFunctionDraw(collision);
            collisionCache.put(collision, buffer);
        }
    }

    @Override
    public void loadCollisions(Media collisionFormulas, Media collisionGroups) throws LionEngineException
    {
        if (collisionFormulas.exists())
        {
            loadCollisionFormulas(collisionFormulas);
        }
        if (collisionGroups.exists())
        {
            loadCollisionGroups(collisionGroups);
        }
        loadTilesCollisions();
        applyConstraints();
    }

    @Override
    public void saveCollisions() throws LionEngineException
    {
        final Media formulas = Core.MEDIA.create(map.getSheetsDirectory().getPath(), FORMULAS_FILE_NAME);
        final XmlNode formulasRoot = Stream.createXmlNode(ConfigCollisionFormula.FORMULAS);
        for (final CollisionFormula formula : getCollisionFormulas())
        {
            formulasRoot.add(ConfigCollisionFormula.export(formula));
        }
        Stream.saveXml(formulasRoot, formulas);

        final Media groups = Core.MEDIA.create(map.getSheetsDirectory().getPath(), GROUPS_FILE_NAME);
        final XmlNode groupsNode = Stream.createXmlNode(ConfigCollisionGroup.GROUPS);
        for (final CollisionGroup group : getCollisionGroups())
        {
            groupsNode.add(ConfigCollisionGroup.export(group));
        }
        Stream.saveXml(groupsNode, groups);
    }

    @Override
    public void clearCollisionDraw()
    {
        if (collisionCache != null)
        {
            for (final ImageBuffer buffer : collisionCache.values())
            {
                buffer.dispose();
            }
            collisionCache.clear();
            collisionCache = null;
        }
    }

    @Override
    public void addCollisionFormula(CollisionFormula formula)
    {
        formulas.put(formula.getName(), formula);
    }

    @Override
    public void addCollisionGroup(CollisionGroup group)
    {
        groups.put(group.getName(), group);
    }

    @Override
    public void removeCollisionFormula(CollisionFormula formula)
    {
        formulas.remove(formula.getName());
    }

    @Override
    public void removeCollisionGroup(CollisionGroup group)
    {
        groups.remove(group.getName());
    }

    @Override
    public void removeCollisionFormulas()
    {
        formulas.clear();
    }

    @Override
    public void removeCollisionGroups()
    {
        groups.clear();
    }

    @Override
    public CollisionResult computeCollision(Transformable transformable, CollisionCategory category)
    {
        // Distance calculation
        final double sh = transformable.getOldX() + category.getOffsetX();
        final double sv = transformable.getOldY() + category.getOffsetY();

        final double dh = transformable.getX() + category.getOffsetX() - sh;
        final double dv = transformable.getY() + category.getOffsetY() - sv;

        // Search vector and number of search steps
        final double norm = Math.sqrt(dh * dh + dv * dv);
        final double sx = dh / norm;
        final double sy = dv / norm;

        double oh;
        double ov;
        int count = 0;
        for (double h = sh, v = sv; count < norm; count++)
        {
            oh = Math.floor(h);
            ov = Math.floor(v);
            h += sx;
            CollisionResult result = computeCollision(category, oh, ov, h, v);
            if (result != null)
            {
                return result;
            }

            v += sy;
            result = computeCollision(category, oh, ov, h, v);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    @Override
    public void render(Graphic g)
    {
        for (int v = 0; v < map.getHeightInTile(); v++)
        {
            for (int h = 0; h < map.getWidthInTile(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    final int x = (int) Math.floor(viewer.getViewpointX(tile.getX()));
                    final int y = (int) Math.floor(viewer.getViewpointY(tile.getY() + tile.getHeight()));
                    renderCollision(g, tile.getFeature(TileCollision.class), x, y);
                }
            }
        }
    }

    @Override
    public CollisionFormula getCollisionFormula(String name)
    {
        if (formulas.containsKey(name))
        {
            return formulas.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA, name);
    }

    @Override
    public CollisionGroup getCollisionGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return groups.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA, name);
    }

    @Override
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return formulas.values();
    }

    @Override
    public Collection<CollisionGroup> getCollisionGroups()
    {
        return groups.values();
    }

    @Override
    public MapTile getMap()
    {
        return map;
    }
}
