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
package com.b3dgs.lionengine.editor.formula.editor;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.ObjectList;
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
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the formulas list, allowing to add and remove {@link CollisionFormula}.
 */
public class FormulaList extends ObjectList<CollisionFormula> implements ObjectListListener<CollisionFormula>
{
    /**
     * Remove the formula from configuration.
     * 
     * @param formulasConfig The formula config file.
     * @param formula The formula to remove.
     */
    private static void removeFormula(Media formulasConfig, CollisionFormula formula)
    {
        final Xml node = new Xml(formulasConfig);
        final Collection<Xml> toRemove = new ArrayList<>();
        for (final Xml nodeFormula : node.getChildren(CollisionFormulaConfig.FORMULA))
        {
            if (CollisionGroup.same(nodeFormula.readString(TileGroupsConfig.ATTRIBUTE_GROUP_NAME), formula.getName()))
            {
                toRemove.add(nodeFormula);
            }
        }
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

    /*
     * ObjectList
     */

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

    /*
     * ObjectListListener
     */

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
            removeFormula(formulasConfig, formula);
            mapCollision.loadCollisions(formulasConfig, mapCollision.getCollisionsConfig());
        }
        else if (config != null)
        {
            removeFormula(config, formula);
        }
    }
}
