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

import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;

/**
 * Map tile collision model implementation.
 */
public class MapTileCollisionModel extends FeatureAbstract implements MapTileCollision
{
    /** Map collision loader. */
    private final MapTileCollisionLoader loader = new MapTileCollisionLoader();
    /** Map collision computer. */
    private final MapTileCollisionComputer computer = new MapTileCollisionComputer();

    /** Map tile surface. */
    private MapTileSurface map;
    /** Map tile group. */
    private MapTileGroup mapGroup;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * <li>{@link MapTileGroup}</li>
     * </ul>
     */
    public MapTileCollisionModel()
    {
        super();
    }

    /*
     * MapTileCollision
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
        mapGroup = provider.getFeature(MapTileGroup.class);
    }

    @Override
    public void loadCollisions(Media collisionFormulas, Media collisionGroups)
    {
        loader.loadCollisions(map, mapGroup, this, collisionFormulas, collisionGroups);
    }

    @Override
    public void loadCollisions(CollisionFormulaConfig formulasConfig, CollisionGroupConfig groupsConfig)
    {
        loader.loadCollisions(map, mapGroup, formulasConfig, groupsConfig);
    }

    @Override
    public void saveCollisions()
    {
        final Media formulasConfig = loader.getFormulasConfig();
        if (formulasConfig != null)
        {
            final Xml formulasRoot = new Xml(CollisionFormulaConfig.NODE_FORMULAS);
            for (final CollisionFormula formula : getCollisionFormulas())
            {
                CollisionFormulaConfig.exports(formulasRoot, formula);
            }
            formulasRoot.save(formulasConfig);
        }

        final Media groupsConfig = loader.getCollisionsConfig();
        if (groupsConfig != null)
        {
            final Xml groupsNode = new Xml(CollisionGroupConfig.NODE_COLLISIONS);
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
        return computer.computeCollision(map, this::getCollisionFormulas, transformable, category);
    }

    @Override
    public CollisionFormula getCollisionFormula(String name)
    {
        return loader.getCollisionFormula(name);
    }

    @Override
    public CollisionGroup getCollisionGroup(String name)
    {
        return loader.getCollisionGroup(name);
    }

    @Override
    public Collection<CollisionFormula> getCollisionFormulas(Tile tile)
    {
        return loader.getCollisionFormulas(tile);
    }

    @Override
    public Collection<CollisionFormula> getCollisionFormulas()
    {
        return loader.getCollisionFormulas();
    }

    @Override
    public Collection<CollisionGroup> getCollisionGroups()
    {
        return loader.getCollisionGroups();
    }

    @Override
    public Media getFormulasConfig()
    {
        return loader.getFormulasConfig();
    }

    @Override
    public Media getCollisionsConfig()
    {
        return loader.getCollisionsConfig();
    }
}
