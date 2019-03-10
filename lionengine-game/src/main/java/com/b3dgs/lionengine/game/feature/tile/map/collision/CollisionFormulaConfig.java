/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;

/**
 * Represents the collisions formula.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see CollisionFormula
 */
public final class CollisionFormulaConfig
{
    /** Configuration file name. */
    public static final String FILENAME = "formulas.xml";
    /** Collision formula root node. */
    public static final String NODE_FORMULAS = Constant.XML_PREFIX + "formulas";
    /** Collision formula node. */
    public static final String NODE_FORMULA = Constant.XML_PREFIX + "formula";
    /** The formula name attribute. */
    public static final String ATT_NAME = "name";
    /** The formula glue attribute. */
    public static final String ATT_GLUE = "glue";

    /**
     * Create the formula data from node.
     * 
     * @param config The collision formulas descriptor (must not be <code>null</code>).
     * @return The collision formula data.
     * @throws LionEngineException If error when reading data.
     */
    public static CollisionFormulaConfig imports(Media config)
    {
        final Xml root = new Xml(config);
        final Map<String, CollisionFormula> collisions = new HashMap<>(0);

        for (final Xml node : root.getChildren(NODE_FORMULA))
        {
            final String name = node.readString(ATT_NAME);
            final CollisionFormula collision = createCollision(node);
            collisions.put(name, collision);
        }

        return new CollisionFormulaConfig(collisions);
    }

    /**
     * Export the current formula data to the formula node.
     * 
     * @param root The root node (must not be <code>null</code>).
     * @param formula The formula reference (must not be <code>null</code>).
     * @throws LionEngineException If error on writing.
     */
    public static void exports(Xml root, CollisionFormula formula)
    {
        Check.notNull(root);
        Check.notNull(formula);

        final Xml node = root.createChild(NODE_FORMULA);
        node.writeString(ATT_NAME, formula.getName());
        node.writeBoolean(ATT_GLUE, formula.isGlue());

        CollisionRangeConfig.exports(node, formula.getRange());
        CollisionFunctionConfig.exports(node, formula.getFunction());
        CollisionConstraintConfig.exports(node, formula.getConstraint());
    }

    /**
     * Create a collision formula from its node.
     * 
     * @param node The collision formula node (must not be <code>null</code>).
     * @return The tile collision formula instance.
     * @throws LionEngineException If error when reading data.
     */
    public static CollisionFormula createCollision(Xml node)
    {
        Check.notNull(node);

        final String name = node.readString(ATT_NAME);
        final CollisionRange range = CollisionRangeConfig.imports(node.getChild(CollisionRangeConfig.NODE_RANGE));
        final CollisionFunction function = CollisionFunctionConfig.imports(node);
        final CollisionConstraint constraint = CollisionConstraintConfig.imports(node);
        final boolean glue = node.readBoolean(false, ATT_GLUE);

        return new CollisionFormula(name, range, function, constraint, glue);
    }

    /**
     * Remove the formula node.
     * 
     * @param root The root node (must not be <code>null</code>).
     * @param formula The formula name to remove (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public static void remove(Xml root, String formula)
    {
        Check.notNull(root);
        Check.notNull(formula);

        for (final Xml node : root.getChildren(NODE_FORMULA))
        {
            if (node.readString(ATT_NAME).equals(formula))
            {
                root.removeChild(node);
            }
        }
    }

    /**
     * Check if node has formula node.
     * 
     * @param root The root node (must not be <code>null</code>).
     * @param formula The formula name to check (must not be <code>null</code>).
     * @return <code>true</code> if has formula, <code>false</code> else.
     * @throws LionEngineException If invalid argument.
     */
    public static boolean has(Xml root, String formula)
    {
        Check.notNull(root);
        Check.notNull(formula);

        for (final Xml node : root.getChildren(NODE_FORMULA))
        {
            if (node.readString(ATT_NAME).equals(formula))
            {
                return true;
            }
        }
        return false;
    }

    /** Collision formulas list. */
    private final Map<String, CollisionFormula> formulas;

    /**
     * Create a collision formula config map.
     * 
     * @param formulas The collisions formula mapping (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public CollisionFormulaConfig(Map<String, CollisionFormula> formulas)
    {
        super();

        Check.notNull(formulas);

        this.formulas = new HashMap<>(formulas);
    }

    /**
     * Get a collision formula data from its name.
     * 
     * @param name The formula name (must not be <code>null</code>).
     * @return The formula reference.
     * @throws LionEngineException If the formula with the specified name is not found.
     */
    public CollisionFormula getFormula(String name)
    {
        Check.notNull(name);

        final CollisionFormula collision = formulas.get(name);
        Check.notNull(collision);

        return collision;
    }

    /**
     * Get all formulas as read only.
     * 
     * @return The formulas map, where key is the formula name.
     */
    public Map<String, CollisionFormula> getFormulas()
    {
        return Collections.unmodifiableMap(formulas);
    }
}
