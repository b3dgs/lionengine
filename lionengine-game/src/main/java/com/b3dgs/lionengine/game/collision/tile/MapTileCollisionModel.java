/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.collision.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileRef;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Map tile collision model implementation.
 * 
 * <p>
 * The map must have the {@link MapTileGroup} feature.
 * </p>
 */
public class MapTileCollisionModel implements MapTileCollision
{
    /** Info loading formulas. */
    private static final String INFO_LOAD_FORMULAS = "Loading collision formulas from: ";
    /** Info loading groups. */
    private static final String INFO_LOAD_GROUPS = "Loading collision groups from: ";
    /** Error formula not found. */
    private static final String ERROR_FORMULA = "Formula not found (may not have been loaded): ";

    /**
     * Create the function draw to buffer.
     * 
     * @param collision The collision reference.
     * @param tw The tile width.
     * @param th The tile height.
     * @return The created collision representation buffer.
     */
    public static ImageBuffer createFunctionDraw(CollisionFormula collision, int tw, int th)
    {
        final ImageBuffer buffer = Graphics.createImageBuffer(tw, th, Transparency.TRANSLUCENT);
        final Graphic g = buffer.createGraphic();
        g.setColor(ColorRgba.PURPLE);

        createFunctionDraw(g, collision, tw, th);

        g.dispose();
        return buffer;
    }

    /**
     * Create the function draw to buffer by computing all possible locations.
     * 
     * @param g The graphic buffer.
     * @param formula The collision formula.
     * @param tw The tile width.
     * @param th The tile height.
     */
    private static void createFunctionDraw(Graphic g, CollisionFormula formula, int tw, int th)
    {
        for (int x = 0; x < tw; x++)
        {
            for (int y = 0; y < th; y++)
            {
                renderCollision(g, formula, th, x, y);
            }
        }
    }

    /**
     * Render collision from current vector.
     * 
     * @param g The graphic buffer.
     * @param formula The collision formula.
     * @param th The tile height.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     */
    private static void renderCollision(Graphic g, CollisionFormula formula, int th, int x, int y)
    {
        final CollisionFunction function = formula.getFunction();
        final CollisionRange range = formula.getRange();
        switch (range.getOutput())
        {
            case X:
                renderX(g, function, range, th, x, y);
                break;
            case Y:
                renderY(g, function, range, th, x, y);
                break;
            default:
                throw new LionEngineException(range.getOutput());
        }
    }

    /**
     * Render horizontal collision from current vector.
     * 
     * @param g The graphic buffer.
     * @param function The collision function.
     * @param range The collision range.
     * @param th The tile height.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     */
    private static void renderX(Graphic g, CollisionFunction function, CollisionRange range, int th, int x, int y)
    {
        final double fx = function.compute(y);
        if (UtilMath.isBetween(x, range.getMinX(), range.getMaxX())
            && UtilMath.isBetween(y, range.getMinY(), range.getMaxY()))
        {
            g.drawRect((int) fx, th - y - 1, 0, 0, false);
        }
    }

    /**
     * Render vertical collision from current vector.
     * 
     * @param g The graphic buffer.
     * @param function The collision function.
     * @param range The collision range.
     * @param th The tile height.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     */
    private static void renderY(Graphic g, CollisionFunction function, CollisionRange range, int th, int x, int y)
    {
        final double fy = function.compute(x);
        if (UtilMath.isBetween(y, range.getMinY(), range.getMaxY())
            && UtilMath.isBetween(x, range.getMinX(), range.getMaxX()))
        {
            g.drawRect(x, th - (int) fy - 1, 0, 0, false);
        }
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

    /**
     * Get the horizontal collision from current location.
     * 
     * @param category The collision category.
     * @param tileCollision The current tile collision.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed horizontal collision.
     */
    private static Double getCollisionX(CollisionCategory category,
                                        TileCollision tileCollision,
                                        double ox,
                                        double oy,
                                        double x,
                                        double y)
    {
        if (category.getAxis() == Axis.X)
        {
            return tileCollision.getCollisionX(category, ox, oy, x, y);
        }
        return null;
    }

    /**
     * Get the vertical collision from current location.
     * 
     * @param category The collision category.
     * @param tileCollision The current tile collision.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed vertical collision.
     */
    private static Double getCollisionY(CollisionCategory category,
                                        TileCollision tileCollision,
                                        double ox,
                                        double oy,
                                        double x,
                                        double y)
    {
        if (category.getAxis() == Axis.Y)
        {
            return tileCollision.getCollisionY(category, ox, oy, x, y);
        }
        return null;
    }

    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas = new HashMap<String, CollisionFormula>();
    /** Collisions groups list. */
    private final Map<String, CollisionGroup> groups = new HashMap<String, CollisionGroup>();
    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Collision draw cache. */
    private Map<CollisionFormula, ImageBuffer> collisionCache;
    /** Formulas configuration media. */
    private Media formulasConfig;
    /** Groups configuration media. */
    private Media groupsConfig;

    /**
     * Create the map tile collision.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link MapTileGroup}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileCollisionModel(Services services)
    {
        map = services.get(MapTile.class);
        mapGroup = services.get(MapTileGroup.class);
        viewer = services.get(Viewer.class);
    }

    /**
     * Load the collision formula. All previous collisions will be cleared.
     * 
     * @param formulasConfig The configuration collision formulas file.
     */
    private void loadCollisionFormulas(Media formulasConfig)
    {
        Verbose.info(INFO_LOAD_FORMULAS, formulasConfig.getFile().getPath());
        formulas.clear();
        this.formulasConfig = formulasConfig;
        final CollisionFormulaConfig config = CollisionFormulaConfig.create(formulasConfig);
        for (final CollisionFormula formula : config.getFormulas().values())
        {
            formulas.put(formula.getName(), formula);
        }
        config.clear();
    }

    /**
     * Load the collision groups. All previous groups will be cleared.
     * 
     * @param groupsConfig The configuration collision groups file.
     */
    private void loadCollisionGroups(Media groupsConfig)
    {
        Verbose.info(INFO_LOAD_GROUPS, groupsConfig.getFile().getPath());
        groups.clear();
        this.groupsConfig = groupsConfig;
        final XmlNode nodeGroups = Xml.load(groupsConfig);
        final Collection<CollisionGroup> groups = CollisionGroupConfig.create(nodeGroups, this);
        for (final CollisionGroup group : groups)
        {
            this.groups.put(group.getName(), group);
        }
        groups.clear();
    }

    /**
     * Load collisions for each tile. Previous collisions will be removed.
     */
    private void loadTilesCollisions()
    {
        for (int v = 0; v < map.getInTileHeight(); v++)
        {
            for (int h = 0; h < map.getInTileWidth(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    loadTileCollisions(tile);
                }
            }
        }
    }

    /**
     * Load the tile collisions.
     * 
     * @param tile The tile reference.
     */
    private void loadTileCollisions(Tile tile)
    {
        final TileCollision tileCollision;
        if (!tile.hasFeature(TileCollision.class))
        {
            tileCollision = new TileCollisionModel(tile);
            tile.addFeature(tileCollision);
        }
        else
        {
            tileCollision = tile.getFeature(TileCollision.class);
        }
        tileCollision.removeCollisionFormulas();
        addTileCollisions(tileCollision, tile);
    }

    /**
     * Add the tile collisions from loaded configuration.
     * 
     * @param tileCollision The tile reference.
     * @param tile The tile reference.
     */
    private void addTileCollisions(TileCollision tileCollision, Tile tile)
    {
        final TileRef ref = new TileRef(tile);
        for (final CollisionGroup collision : getCollisionGroups())
        {
            final Collection<TileRef> group = mapGroup.getGroup(collision.getName());
            if (group.contains(ref))
            {
                for (final CollisionFormula formula : collision.getFormulas())
                {
                    tileCollision.addCollisionFormula(formula);
                }
            }
        }
    }

    /**
     * Apply tile constraints depending of their adjacent collisions.
     */
    private void applyConstraints()
    {
        final Map<Tile, Collection<CollisionFormula>> toRemove = new HashMap<Tile, Collection<CollisionFormula>>();
        for (int v = 0; v < map.getInTileHeight(); v++)
        {
            for (int h = 0; h < map.getInTileWidth(); h++)
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

        final Collection<CollisionFormula> toRemove = new ArrayList<CollisionFormula>();
        for (final CollisionFormula formula : tile.getCollisionFormulas())
        {
            final CollisionConstraint constraint = formula.getConstraint();
            if (checkConstraint(constraint.getConstraints(Orientation.NORTH), top)
                || checkConstraint(constraint.getConstraints(Orientation.SOUTH), bottom)
                || checkConstraint(constraint.getConstraints(Orientation.WEST), left)
                || checkConstraint(constraint.getConstraints(Orientation.EAST), right))
            {
                toRemove.add(formula);
            }
        }
        return toRemove;
    }

    /**
     * Check the constraint with the specified tile.
     * 
     * @param constraints The constraint groups to check.
     * @param tile The tile to check with.
     * @return <code>true</code> if can be ignored, <code>false</code> else.
     */
    private boolean checkConstraint(Collection<String> constraints, Tile tile)
    {
        return tile != null
               && constraints.contains(mapGroup.getGroup(tile))
               && !tile.getFeature(TileCollision.class).getCollisionFormulas().isEmpty();
    }

    /**
     * Compute the collision from current location.
     * 
     * @param category The collision category.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The computed collision result, <code>null</code> if none.
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
                final Double cx = getCollisionX(category, tileCollision, ox, oy, x, y);
                final Double cy = getCollisionY(category, tileCollision, ox, oy, x, y);
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

    /**
     * Get the collision result from current sub location.
     * 
     * @param category The collision category.
     * @param sx The horizontal speed.
     * @param sy The vertical speed.
     * @param h The current horizontal location.
     * @param v The current vertical location.
     * @return The collision found, <code>null</code> if none.
     */
    private CollisionResult getResult(CollisionCategory category, double sx, double sy, double h, double v)
    {
        final double oh = UtilMath.getRound(sx, h);
        final double ov = UtilMath.getRound(sy, v);

        final CollisionResult result;
        result = computeCollision(category, oh, ov, UtilMath.getRound(sx, h), UtilMath.getRound(sy, v + sy));
        if (result == null)
        {
            return computeCollision(category, oh, ov, UtilMath.getRound(sx, h + sx), UtilMath.getRound(sy, v + sy));
        }
        return result;
    }

    /*
     * MapTileCollision
     */

    @Override
    public void loadCollisions()
    {
        final String parent = map.getSheetsConfig().getParentPath();
        loadCollisions(Medias.create(parent, CollisionFormulaConfig.FILENAME),
                       Medias.create(parent, CollisionGroupConfig.FILENAME));
    }

    @Override
    public void loadCollisions(Media collisionFormulas, Media collisionGroups)
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
    public void saveCollisions()
    {
        if (formulasConfig != null)
        {
            final XmlNode formulasRoot = Xml.create(CollisionFormulaConfig.FORMULAS);
            for (final CollisionFormula formula : getCollisionFormulas())
            {
                CollisionFormulaConfig.export(formulasRoot, formula);
            }
            Xml.save(formulasRoot, formulasConfig);
        }
        if (groupsConfig != null)
        {
            final XmlNode groupsNode = Xml.create(CollisionGroupConfig.COLLISIONS);
            for (final CollisionGroup group : getCollisionGroups())
            {
                CollisionGroupConfig.export(groupsNode, group);
            }
            Xml.save(groupsNode, groupsConfig);
        }
    }

    /*
     * MapTileCollision
     */

    @Override
    public void createCollisionDraw()
    {
        clearCollisionDraw();
        collisionCache = new HashMap<CollisionFormula, ImageBuffer>(formulas.size());

        for (final CollisionFormula collision : formulas.values())
        {
            final ImageBuffer buffer = createFunctionDraw(collision, map.getTileWidth(), map.getTileHeight());
            collisionCache.put(collision, buffer);
        }
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

        double h = sh;
        double v = sv;

        for (int count = 0; count < norm; count++)
        {
            final CollisionResult found = getResult(category, sx, sy, h, v);
            if (found != null)
            {
                return found;
            }
            v += sy;
            h += sx;
        }
        return null;
    }

    @Override
    public void render(Graphic g)
    {
        for (int v = 0; v < map.getInTileHeight(); v++)
        {
            for (int h = 0; h < map.getInTileWidth(); h++)
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
    public Media getFormulasConfig()
    {
        return formulasConfig;
    }

    @Override
    public Media getCollisionsConfig()
    {
        return groupsConfig;
    }
}
