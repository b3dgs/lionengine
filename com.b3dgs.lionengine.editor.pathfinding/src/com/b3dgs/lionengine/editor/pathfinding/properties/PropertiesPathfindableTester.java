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
package com.b3dgs.lionengine.editor.pathfinding.properties;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.object.ObjectsTester;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableConfig;

/**
 * Test the properties node existence.
 */
public final class PropertiesPathfindableTester extends PropertyTester
{
    /** Can enable pathfinding. */
    private static final String PROPERTY_PATHFINDABLE_ENABLE = "enablePathfindable";
    /** Can edit pathfinding. */
    private static final String PROPERTY_PATHFINDABLE_EDIT = "editPathfindable";
    /** Can disable pathfinding. */
    private static final String PROPERTY_PATHFINDABLE_DISABLE = "disablePathfindable";

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
        if (PROPERTY_PATHFINDABLE_ENABLE.equals(property))
        {
            result = !PropertiesModel.INSTANCE.hasProperty(PathfindableConfig.PATHFINDABLE);
        }
        else if (PathfindableConfig.PATHFINDABLE.equals(data)
                 && (PROPERTY_PATHFINDABLE_EDIT.equals(property) || PROPERTY_PATHFINDABLE_DISABLE.equals(property)))
        {
            result = PropertiesModel.INSTANCE.hasProperty(PathfindableConfig.PATHFINDABLE);
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
    public PropertiesPathfindableTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (!PropertiesModel.INSTANCE.isEmpty() && ObjectsTester.isObjectFile(ProjectModel.INSTANCE.getSelection()))
        {
            final Object data = PropertiesModel.INSTANCE.getSelectedData();
            return check(data, property);
        }
        return false;
    }
}
