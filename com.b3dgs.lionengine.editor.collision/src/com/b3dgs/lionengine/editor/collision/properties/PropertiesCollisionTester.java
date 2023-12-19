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
package com.b3dgs.lionengine.editor.collision.properties;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.object.ObjectsTester;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.feature.collidable.CollisionConfig;

/**
 * Test the properties node existence.
 */
public final class PropertiesCollisionTester extends PropertyTester
{
    /** Can enable collisions. */
    private static final String PROPERTY_COLLISIONS_ENABLE = "enableCollisions";
    /** Can edit collisions. */
    private static final String PROPERTY_COLLISIONS_EDIT = "editCollisions";
    /** Can disable collisions. */
    private static final String PROPERTY_COLLISIONS_DISABLE = "disableCollisions";

    /**
     * Check result depending of selection.
     * 
     * @param model The properties model.
     * @param data The selection reference.
     * @param property The property to check.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(PropertiesModel model, Object data, String property)
    {
        final boolean result;
        if (PROPERTY_COLLISIONS_ENABLE.equals(property))
        {
            result = !model.hasProperty(CollisionConfig.NODE_COLLISION);
        }
        else if (CollisionConfig.NODE_COLLISION.equals(data)
                 && (PROPERTY_COLLISIONS_EDIT.equals(property) || PROPERTY_COLLISIONS_DISABLE.equals(property)))
        {
            result = model.hasProperty(CollisionConfig.NODE_COLLISION);
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
    public PropertiesCollisionTester()
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
        if (!model.isEmpty() && ObjectsTester.isObjectFile(ProjectModel.INSTANCE.getSelection()))
        {
            final Object data = model.getSelectedData();
            return check(model, data, property);
        }
        return false;
    }
}
