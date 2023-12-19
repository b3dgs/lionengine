/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.formula.properties;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;

/**
 * Test the properties node existence.
 */
public final class PropertiesFormulaTester extends PropertyTester
{
    /** Can add formula. */
    private static final String PROPERTY_FORMULA_ADD = "addFormula";
    /** Can remove formula. */
    private static final String PROPERTY_FORMULA_REMOVE = "removeFormula";
    /** Can edit formula. */
    private static final String PROPERTY_FORMULA_EDIT = "editFormula";

    /**
     * Check result depending of selection.
     * 
     * @param data The selection reference.
     * @param property The property to check.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(Object data, String property)
    {
        final boolean result;
        if (PROPERTY_FORMULA_ADD.equals(property))
        {
            result = CollisionFormulaConfig.NODE_FORMULAS.equals(data);
        }
        else if (PROPERTY_FORMULA_REMOVE.equals(property) || PROPERTY_FORMULA_EDIT.equals(property))
        {
            result = CollisionFormulaConfig.NODE_FORMULA.equals(data);
        }
        else
        {
            result = false;
        }
        return result;
    }

    /**
     * Create tester.
     */
    public PropertiesFormulaTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final PropertiesModel model = PropertiesModel.INSTANCE;
        if (!model.isEmpty())
        {
            final Object data = model.getSelectedData();
            return check(data, property);
        }
        return false;
    }
}
