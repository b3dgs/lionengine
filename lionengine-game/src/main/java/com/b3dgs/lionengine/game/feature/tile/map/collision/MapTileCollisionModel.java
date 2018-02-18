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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Map tile collision model implementation.
 * <p>
 * The {@link Services} must provide:
 * </p>
 * <ul>
 * <li>{@link MapTile}</li>
 * <li>{@link MapTileGroup}</li>
 * </ul>
 */
public class MapTileCollisionModel extends FeatureModel implements MapTileCollision
{
    /** Info loading formulas. */
    private static final String INFO_LOAD_FORMULAS = "Loading collision formulas from: ";
    /** Info loading groups. */
    private static final String INFO_LOAD_GROUPS = "Loading collision groups from: ";
    /** Error formula not found. */
    private static final String ERROR_FORMULA = "Formula not found (may not have been loaded): ";

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
    private final Map<String, CollisionFormula> formulas = new HashMap<>();
    /** Collisions groups list. */
    private final Map<String, CollisionGroup> groups = new HashMap<>();
    /** Map reference. */
    private final MapTile map;
    /** Map tile group. */
    private final MapTileGroup mapGroup;
    /** Formulas configuration media. */
    private Media formulasConfig;
    /** Groups configuration media. */
    private Media groupsConfig;

    /**
     * Create the map tile collision.
     * 
     * @param services The services reference.
     */
    public MapTileCollisionModel(Services services)
    {
        super();

        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
    }

    /**
     * Load the collision formula. All previous collisions will be cleared.
     * 
     * @param formulasConfig The configuration collision formulas file.
     */
    private void loadCollisionFormulas(Media formulasConfig)
    {
        Verbose.info(INFO_LOAD_FORMULAS, formulasConfig.getFile().getPath());
        this.formulasConfig = formulasConfig;
        final CollisionFormulaConfig config = CollisionFormulaConfig.imports(formulasConfig);
        loadCollisionFormulas(config);
    }

    /**
     * Load the collision formula. All previous collisions will be cleared.
     * 
     * @param config The configuration collision formulas.
     */
    private void loadCollisionFormulas(CollisionFormulaConfig config)
    {
        formulas.clear();
        formulas.putAll(config.getFormulas());
    }

    /**
     * Load the collision groups. All previous groups will be cleared.
     * 
     * @param groupsConfig The configuration collision groups file.
     */
    private void loadCollisionGroups(Media groupsConfig)
    {
        Verbose.info(INFO_LOAD_GROUPS, groupsConfig.getFile().getPath());

        this.groupsConfig = groupsConfig;
        final Xml nodeGroups = new Xml(groupsConfig);
        final CollisionGroupConfig config = CollisionGroupConfig.imports(nodeGroups, this);
        loadCollisionGroups(config);
    }

    /**
     * Load the collision groups. All previous groups will be cleared.
     * 
     * @param config The configuration collision groups.
     */
    private void loadCollisionGroups(CollisionGroupConfig config)
    {
        groups.clear();
        groups.putAll(config.getGroups());
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
        final Map<Tile, Collection<CollisionFormula>> toRemove = new HashMap<>();
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

        final Collection<CollisionFormula> toRemove = new ArrayList<>();
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
        result = computeCollision(category, oh, ov, UtilMath.getRound(sx, h + sx), UtilMath.getRound(sy, v));
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
    public void loadCollisions(CollisionFormulaConfig formulasConfig, CollisionGroupConfig groupsConfig)
    {
        loadCollisionFormulas(formulasConfig);
        loadCollisionGroups(groupsConfig);
        loadTilesCollisions();
        applyConstraints();
    }

    @Override
    public void saveCollisions()
    {
        if (formulasConfig != null)
        {
            final Xml formulasRoot = new Xml(CollisionFormulaConfig.FORMULAS);
            for (final CollisionFormula formula : getCollisionFormulas())
            {
                CollisionFormulaConfig.exports(formulasRoot, formula);
            }
            formulasRoot.save(formulasConfig);
        }
        if (groupsConfig != null)
        {
            final Xml groupsNode = new Xml(CollisionGroupConfig.COLLISIONS);
            for (final CollisionGroup group : getCollisionGroups())
            {
                CollisionGroupConfig.exports(groupsNode, group);
            }
            groupsNode.save(groupsConfig);
        }
    }

    /*
     * MapTileCollision
     */

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
        final double sx;
        final double sy;
        if (norm < 0 || norm > 0)
        {
            sx = dh / norm;
            sy = dv / norm;
        }
        else
        {
            sx = 0;
            sy = 0;
        }

        double h = sh;
        double v = sv;

        CollisionResult found = null;
        for (int count = 0; count < norm; count++)
        {
            final CollisionResult res = getResult(category, sx, sy, h, v);
            if (res != null)
            {
                found = res;
                if (res.getX() != null)
                {
                    h = res.getX().doubleValue();
                }
                if (res.getY() != null)
                {
                    v = res.getY().doubleValue();
                }
            }
            v += sy;
            h += sx;
        }
        return found;
    }

    @Override
    public CollisionFormula getCollisionFormula(String name)
    {
        if (formulas.containsKey(name))
        {
            return formulas.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA + name);
    }

    @Override
    public CollisionGroup getCollisionGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return groups.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA + name);
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
