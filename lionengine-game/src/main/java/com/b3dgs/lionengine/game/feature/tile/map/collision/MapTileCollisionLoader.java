/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;

/**
 * Load the map tile collision.
 */
final class MapTileCollisionLoader
{
    /** Error formula not found. */
    static final String ERROR_FORMULA = "Formula not found (may not have been loaded): ";
    /** Info loading formulas. */
    private static final String INFO_LOAD_FORMULAS = "Loading collision formulas from: ";
    /** Info loading groups. */
    private static final String INFO_LOAD_GROUPS = "Loading collision groups from: ";

    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas = new HashMap<>();
    /** Collisions groups list. */
    private final Map<String, CollisionGroup> groups = new HashMap<>();
    /** Formulas per tiles. */
    private final Map<Tile, Collection<CollisionFormula>> tilesFormulas = new HashMap<>();
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
     * @param map The map tile owner.
     * @param mapGroup The map group owner.
     */
    MapTileCollisionLoader(MapTile map, MapTileGroup mapGroup)
    {
        super();

        this.map = map;
        this.mapGroup = mapGroup;
    }

    /**
     * Load map collision from an external file.
     * 
     * @param mapCollision The map tile collision owner.
     * @param collisionFormulas The collision formulas descriptor.
     * @param collisionGroups The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    public void loadCollisions(MapTileCollision mapCollision, Media collisionFormulas, Media collisionGroups)
    {
        if (collisionFormulas.exists())
        {
            loadCollisionFormulas(collisionFormulas);
        }
        if (collisionGroups.exists())
        {
            loadCollisionGroups(mapCollision, collisionGroups);
        }
        loadTilesCollisions();
        applyConstraints();
    }

    /**
     * Load map collision with default files.
     * 
     * @param formulasConfig The collision formulas descriptor.
     * @param groupsConfig The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    public void loadCollisions(CollisionFormulaConfig formulasConfig, CollisionGroupConfig groupsConfig)
    {
        loadCollisionFormulas(formulasConfig);
        loadCollisionGroups(groupsConfig);
        loadTilesCollisions();
        applyConstraints();
    }

    /**
     * Get the collision formula from its name.
     * 
     * @param name The collision formula name.
     * @return The collision formula from name reference.
     * @throws LionEngineException If formula not found.
     */
    public CollisionFormula getCollisionFormula(String name)
    {
        if (formulas.containsKey(name))
        {
            return formulas.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA + name);
    }

    /**
     * Get the collision group from its name.
     * 
     * @param name The collision group name.
     * @return The supported collision group reference.
     * @throws LionEngineException If group not found.
     */
    public CollisionGroup getCollisionGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return groups.get(name);
        }
        throw new LionEngineException(ERROR_FORMULA + name);
    }

    /**
     * Get tile formulas.
     * 
     * @param tile The tile reference.
     * @return The associated formulas.
     */
    public Collection<CollisionFormula> getCollisionFormulas(Tile tile)
    {
        return tilesFormulas.get(tile);
    }

    /**
     * Get the collision formulas list.
     * 
     * @return The collision formulas list.
     */
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return formulas.values();
    }

    /**
     * Get the collision groups list.
     * 
     * @return The collision groups list.
     */
    public Collection<CollisionGroup> getCollisionGroups()
    {
        return groups.values();
    }

    /**
     * Get the formulas config file.
     * 
     * @return The formulas config file.
     */
    public Media getFormulasConfig()
    {
        return formulasConfig;
    }

    /**
     * Get the collisions config file.
     * 
     * @return The collisions config file.
     */
    public Media getCollisionsConfig()
    {
        return groupsConfig;
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
     * @param mapCollision The map tile collision owner.
     * @param groupsConfig The configuration collision groups file.
     */
    private void loadCollisionGroups(MapTileCollision mapCollision, Media groupsConfig)
    {
        Verbose.info(INFO_LOAD_GROUPS, groupsConfig.getFile().getPath());

        this.groupsConfig = groupsConfig;
        final Xml nodeGroups = new Xml(groupsConfig);
        final CollisionGroupConfig config = CollisionGroupConfig.imports(nodeGroups, mapCollision);
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
        if (tilesFormulas.containsKey(tile))
        {
            tilesFormulas.get(tile).clear();
        }
        else
        {
            tilesFormulas.put(tile, new HashSet<>());
        }
        addTileCollisions(tile);
    }

    /**
     * Add the tile collisions from loaded configuration.
     * 
     * @param tile The tile reference.
     */
    private void addTileCollisions(Tile tile)
    {
        for (final CollisionGroup collision : getCollisionGroups())
        {
            final Set<Integer> group = mapGroup.getGroup(collision.getName());
            if (group.contains(tile.getKey()))
            {
                for (final CollisionFormula formula : collision.getFormulas())
                {
                    tilesFormulas.get(tile).add(formula);
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
                    toRemove.put(tile, checkConstraints(tile, h, v));
                }
            }
        }
        for (final Entry<Tile, Collection<CollisionFormula>> current : toRemove.entrySet())
        {
            final Tile tile = current.getKey();
            for (final CollisionFormula formula : current.getValue())
            {
                if (tilesFormulas.containsKey(tile))
                {
                    tilesFormulas.get(tile).remove(formula);
                }
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
    private Collection<CollisionFormula> checkConstraints(Tile tile, int h, int v)
    {
        final Tile top = map.getTile(h, v + 1);
        final Tile bottom = map.getTile(h, v - 1);
        final Tile left = map.getTile(h - 1, v);
        final Tile right = map.getTile(h + 1, v);

        final Collection<CollisionFormula> toRemove = new ArrayList<>();
        for (final CollisionFormula formula : tilesFormulas.computeIfAbsent(tile, t -> Collections.emptyList()))
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
        return tile != null && constraints.contains(mapGroup.getGroup(tile)) && !tilesFormulas.get(tile).isEmpty();
    }
}
