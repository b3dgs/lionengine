/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
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

    /**
     * Check the constraint with the specified tile.
     * 
     * @param mapGroup The map group reference.
     * @param constraints The constraint groups to check.
     * @param tile The tile to check with.
     * @return <code>true</code> if can be ignored, <code>false</code> else.
     */
    private static boolean checkConstraint(MapTileGroup mapGroup, Collection<String> constraints, Tile tile)
    {
        return tile != null && constraints.contains(mapGroup.getGroup(tile));
    }

    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas = new HashMap<>();
    /** Collisions groups list. */
    private final Map<String, CollisionGroup> groups = new HashMap<>();
    /** Formulas per tiles. */
    private final Map<Tile, List<CollisionFormula>> tilesFormulasList = new HashMap<>();
    /** Formulas per tiles. */
    private final Map<Tile, Set<CollisionFormula>> tilesFormulas = new HashMap<>();
    /** Formulas configuration media. */
    private Media formulasConfig;
    /** Groups configuration media. */
    private Media groupsConfig;

    /**
     * Create the map tile collision.
     */
    MapTileCollisionLoader()
    {
        super();
    }

    /**
     * Load map collision from an external file.
     * 
     * @param map The map surface reference.
     * @param mapGroup The map group reference.
     * @param mapCollision The map tile collision owner.
     * @param collisionFormulas The collision formulas descriptor.
     * @param collisionGroups The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    public void loadCollisions(MapTile map,
                               MapTileGroup mapGroup,
                               MapTileCollision mapCollision,
                               Media collisionFormulas,
                               Media collisionGroups)
    {
        if (collisionFormulas.exists())
        {
            loadCollisionFormulas(collisionFormulas);
        }
        if (collisionGroups.exists())
        {
            loadCollisionGroups(mapCollision, collisionGroups);
        }
        loadTilesCollisions(map, mapGroup);
        applyConstraints(map, mapGroup);
    }

    /**
     * Load map collision with default files.
     * 
     * @param map The map surface reference.
     * @param mapGroup The map group reference.
     * @param formulasConfig The collision formulas descriptor.
     * @param groupsConfig The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    public void loadCollisions(MapTile map,
                               MapTileGroup mapGroup,
                               CollisionFormulaConfig formulasConfig,
                               CollisionGroupConfig groupsConfig)
    {
        loadCollisionFormulas(formulasConfig);
        loadCollisionGroups(groupsConfig);
        loadTilesCollisions(map, mapGroup);
        applyConstraints(map, mapGroup);
    }

    /**
     * Update tile collision.
     * 
     * @param map The map reference.
     * @param mapGroup The map group reference.
     * @param tile The tile to update.
     */
    public void update(MapTile map, MapTileGroup mapGroup, Tile tile)
    {
        loadTileCollisions(mapGroup, tile);
        applyConstraints(map, mapGroup, tile);
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
     */
    public Optional<CollisionGroup> getCollisionGroup(String name)
    {
        if (groups.containsKey(name))
        {
            return Optional.of(groups.get(name));
        }
        return Optional.empty();
    }

    /**
     * Get tile formulas.
     * 
     * @param tile The tile reference.
     * @return The associated formulas.
     */
    public List<CollisionFormula> getCollisionFormulasList(Tile tile)
    {
        final List<CollisionFormula> collisionFormulas = tilesFormulasList.get(tile);
        if (collisionFormulas != null)
        {
            return collisionFormulas;
        }
        return Collections.emptyList();
    }

    /**
     * Get tile formulas.
     * 
     * @param tile The tile reference.
     * @return The associated formulas.
     */
    public Set<CollisionFormula> getCollisionFormulas(Tile tile)
    {
        final Set<CollisionFormula> collisionFormulas = tilesFormulas.get(tile);
        if (collisionFormulas != null)
        {
            return collisionFormulas;
        }
        return Collections.emptySet();
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
     * 
     * @param map The map surface reference.
     * @param mapGroup The map group reference.
     */
    private void loadTilesCollisions(MapTile map, MapTileGroup mapGroup)
    {
        for (int v = 0; v < map.getInTileHeight(); v++)
        {
            for (int h = 0; h < map.getInTileWidth(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    loadTileCollisions(mapGroup, tile);
                }
            }
        }
    }

    /**
     * Load the tile collisions.
     * 
     * @param mapGroup The map group reference.
     * @param tile The tile reference.
     */
    private void loadTileCollisions(MapTileGroup mapGroup, Tile tile)
    {
        final List<CollisionFormula> list = tilesFormulasList.get(tile);
        if (list != null)
        {
            list.clear();
        }
        else
        {
            tilesFormulasList.put(tile, new ArrayList<>());
        }

        if (tilesFormulas.containsKey(tile))
        {
            tilesFormulas.get(tile).clear();
        }
        else
        {
            tilesFormulas.put(tile, new HashSet<>());
        }

        addTileCollisions(mapGroup, tile);
    }

    /**
     * Add the tile collisions from loaded configuration.
     * 
     * @param mapGroup The map group reference.
     * @param tile The tile reference.
     */
    private void addTileCollisions(MapTileGroup mapGroup, Tile tile)
    {
        for (final CollisionGroup collision : getCollisionGroups())
        {
            final Set<Integer> group = mapGroup.getGroup(collision.getName());
            if (group.contains(tile.getKey()))
            {
                final List<CollisionFormula> currentList = tilesFormulasList.get(tile);
                final Set<CollisionFormula> current = tilesFormulas.get(tile);
                for (final CollisionFormula formula : collision.getFormulas())
                {
                    if (current.add(formula))
                    {
                        currentList.add(formula);
                    }
                }
            }
        }
    }

    /**
     * Apply tile constraints depending of their adjacent collisions.
     * 
     * @param map The map surface reference.
     * @param mapGroup The map group reference.
     */
    private void applyConstraints(MapTile map, MapTileGroup mapGroup)
    {
        final Map<Tile, List<CollisionFormula>> toRemove = new HashMap<>();
        for (int v = 0; v < map.getInTileHeight(); v++)
        {
            for (int h = 0; h < map.getInTileWidth(); h++)
            {
                final Tile tile = map.getTile(h, v);
                if (tile != null)
                {
                    toRemove.put(tile, checkConstraints(map, mapGroup, tile, h, v));
                }
            }
        }
        for (final Entry<Tile, List<CollisionFormula>> current : toRemove.entrySet())
        {
            final Tile tile = current.getKey();
            final List<CollisionFormula> listRemove = current.getValue();
            final int n = listRemove.size();
            for (int i = 0; i < n; i++)
            {
                final CollisionFormula formula = listRemove.get(i);

                final List<CollisionFormula> list = tilesFormulasList.get(tile);
                if (list != null)
                {
                    list.remove(formula);
                }

                if (tilesFormulas.containsKey(tile))
                {
                    tilesFormulas.get(tile).remove(formula);
                }
            }
        }
    }

    /**
     * Apply tile constraints depending of their adjacent collisions.
     * 
     * @param map The map surface reference.
     * @param mapGroup The map group reference.
     * @param tile The tile reference.
     */
    private void applyConstraints(MapTile map, MapTileGroup mapGroup, Tile tile)
    {
        final List<CollisionFormula> listRemove = checkConstraints(map,
                                                                   mapGroup,
                                                                   tile,
                                                                   tile.getInTileWidth(),
                                                                   tile.getInTileHeight());

        final int n = listRemove.size();
        for (int i = 0; i < n; i++)
        {
            final CollisionFormula formula = listRemove.get(i);

            final List<CollisionFormula> list = tilesFormulasList.get(tile);
            if (list != null)
            {
                list.remove(formula);
            }

            if (tilesFormulas.containsKey(tile))
            {
                tilesFormulas.get(tile).remove(formula);
            }
        }
    }

    /**
     * Check the tile constraints and get the removable formulas.
     * 
     * @param map The map surface reference.
     * @param mapGroup The map group reference.
     * @param tile The current tile to check.
     * @param h The horizontal location.
     * @param v The vertical location.
     * @return The formula to remove.
     */
    private List<CollisionFormula> checkConstraints(MapTile map, MapTileGroup mapGroup, Tile tile, int h, int v)
    {
        final Tile top = map.getTile(h, v + 1);
        final Tile bottom = map.getTile(h, v - 1);
        final Tile left = map.getTile(h - 1, v);
        final Tile right = map.getTile(h + 1, v);

        final List<CollisionFormula> toRemove = new ArrayList<>();
        List<CollisionFormula> list = tilesFormulasList.get(tile);
        if (list == null)
        {
            list = Collections.emptyList();
        }

        final int n = list.size();
        for (int i = 0; i < n; i++)
        {
            final CollisionFormula formula = list.get(i);

            final CollisionConstraint constraint = formula.getConstraint();
            if (checkConstraint(mapGroup, constraint.getConstraints(Orientation.NORTH), top)
                || checkConstraint(mapGroup, constraint.getConstraints(Orientation.SOUTH), bottom)
                || checkConstraint(mapGroup, constraint.getConstraints(Orientation.WEST), left)
                || checkConstraint(mapGroup, constraint.getConstraints(Orientation.EAST), right))
            {
                toRemove.add(formula);
            }
        }
        return toRemove;
    }
}
