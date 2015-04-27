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
package com.b3dgs.lionengine.editor.properties.animation;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.editor.project.tester.ObjectsFolderTester;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;

/**
 * Test the properties node existence.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesAnimationTester
        extends PropertyTester
{
    /** Can enable animations. */
    private static final String PROPERTY_ANIMATIONS_ENABLE = "enableAnimations";
    /** Can edit animations. */
    private static final String PROPERTY_ANIMATIONS_EDIT = "editAnimations";
    /** Can disable animations. */
    private static final String PROPERTY_ANIMATIONS_DISABLE = "disableAnimations";

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
            if (PROPERTY_ANIMATIONS_ENABLE.equals(property) && model.hasProperty(ConfigFrames.FRAMES))
            {
                return !model.hasProperty(ConfigAnimations.ANIMATION);
            }
            else if (PROPERTY_ANIMATIONS_EDIT.equals(property) && ConfigAnimations.ANIMATION.equals(data))
            {
                return model.hasProperty(ConfigAnimations.ANIMATION);
            }
            else if (PROPERTY_ANIMATIONS_DISABLE.equals(property) && ConfigAnimations.ANIMATION.equals(data))
            {
                return model.hasProperty(ConfigAnimations.ANIMATION);
            }
        }
        return false;
    }
}
