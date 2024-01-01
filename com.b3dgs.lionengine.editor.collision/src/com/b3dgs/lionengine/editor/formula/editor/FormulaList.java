/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.formula.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.ObjectListAbstract;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.Axis;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormula;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroup;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionRange;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;

/**
 * Represents the formulas list, allowing to add and remove {@link CollisionFormula}.
 */
public class FormulaList extends ObjectListAbstract<CollisionFormula> implements ObjectListListener<CollisionFormula>
{
    /**
     * Remove the formula from configuration.
     * 
     * @param formulasConfig The formula config file.
     * @param formula The formula to remove.
     */
    private static void removeFormula(Media formulasConfig, CollisionFormula formula)
    {
        final Collection<Xml> toRemove = new ArrayList<>();

        final Xml node = new Xml(formulasConfig);
        final Collection<Xml> children = node.getChildrenXml(CollisionFormulaConfig.NODE_FORMULA);
        for (final Xml nodeFormula : children)
        {
            if (CollisionGroup.same(nodeFormula.getString(TileGroupsConfig.ATT_GROUP_NAME), formula.getName()))
            {
                toRemove.add(nodeFormula);
            }
        }
        children.clear();

        for (final Xml remove : toRemove)
        {
            node.removeChild(remove);
        }
        toRemove.clear();

        node.save(formulasConfig);
    }

    /** Last config used. */
    private Media config;

    /**
     * Create the group list.
     */
    public FormulaList()
    {
        super(CollisionFormula.class);
    }

    /**
     * Create the group list.
     * 
     * @param properties The properties reference.
     */
    public FormulaList(FormulaProperties properties)
    {
        super(CollisionFormula.class, properties);
    }

    /**
     * Load the existing formulas from the object configurer.
     * 
     * @param config The config file.
     */
    public void loadFormulas(Media config)
    {
        this.config = config;
        final CollisionFormulaConfig configCollisionFormula = CollisionFormulaConfig.imports(config);
        final Collection<CollisionFormula> formulas = configCollisionFormula.getFormulas().values();
        loadObjects(formulas);
    }

    @Override
    protected CollisionFormula copyObject(CollisionFormula formula)
    {
        return new CollisionFormula(formula.getName(),
                                    formula.getRange(),
                                    formula.getFunction(),
                                    formula.getConstraint());
    }

    @Override
    protected CollisionFormula createObject(String name)
    {
        return new CollisionFormula(name,
                                    new CollisionRange(Axis.Y, 0, 0, 0, 0),
                                    new CollisionFunctionLinear(0, 0),
                                    new CollisionConstraint());
    }

    @Override
    public void notifyObjectSelected(CollisionFormula formula)
    {
        // Nothing to do
    }

    @Override
    public void notifyObjectDeleted(CollisionFormula formula)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            final Media formulasConfig = mapCollision.getFormulasConfig();
            if (formulasConfig != null)
            {
                removeFormula(formulasConfig, formula);
                mapCollision.loadCollisions(formulasConfig, mapCollision.getCollisionsConfig());
            }
        }
        else if (config != null)
        {
            removeFormula(config, formula);
        }
    }
}
