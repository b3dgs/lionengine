/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.properties.surface;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.editor.project.tester.ObjectsFolderTester;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;

/**
 * Test the properties node existence.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesSurfaceTester
        extends PropertyTester
{
    /** Can set surface. */
    private static final String PROPERTY_SURFACE_SET = "setSurface";
    /** Can remove surface. */
    private static final String PROPERTY_SURFACE_REMOVE = "removeSurface";
    /** Can set icon. */
    private static final String PROPERTY_ICON_SET = "setIcon";
    /** Can remove icon. */
    private static final String PROPERTY_ICON_REMOVE = "removeIcon";

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final PropertiesModel model = PropertiesModel.INSTANCE;
        if (!model.isEmpty() && ObjectsFolderTester.isObjectFile(ProjectsModel.INSTANCE.getSelection()))
        {
            final Object data = model.getSelectedData();
            if (PROPERTY_SURFACE_SET.equals(property))
            {
                return !model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PROPERTY_SURFACE_REMOVE.equals(property) && ConfigSurface.SURFACE_IMAGE.equals(data))
            {
                return model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PROPERTY_ICON_SET.equals(property))
            {
                return !model.hasProperty(ConfigSurface.SURFACE_ICON) && model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PROPERTY_ICON_REMOVE.equals(property) && ConfigSurface.SURFACE_ICON.equals(data))
            {
                return model.hasProperty(ConfigSurface.SURFACE_ICON);
            }
        }
        return false;
    }
}
