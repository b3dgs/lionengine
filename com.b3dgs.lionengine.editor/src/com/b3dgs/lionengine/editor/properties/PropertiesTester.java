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
package com.b3dgs.lionengine.editor.properties;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;

/**
 * Test the properties node existence.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesTester
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
    /** Can set frames. */
    private static final String PROPERTY_FRAMES_SET = "setFrames";
    /** Can remove frames. */
    private static final String PROPERTY_FRAMES_REMOVE = "removeFrames";
    /** Can enable animations. */
    private static final String PROPERTY_ANIMATIONS_ENABLE = "enableAnimations";
    /** Can edit animations. */
    private static final String PROPERTY_ANIMATIONS_EDIT = "editAnimations";
    /** Can disable animations. */
    private static final String PROPERTY_ANIMATIONS_DISABLE = "disableAnimations";
    /** Can enable collisions. */
    private static final String PROPERTY_COLLISIONS_ENABLE = "enableCollisions";
    /** Can edit collisions. */
    private static final String PROPERTY_COLLISIONS_EDIT = "editCollisions";
    /** Can disable collisions. */
    private static final String PROPERTY_COLLISIONS_DISABLE = "disableCollisions";

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final PropertiesModel model = PropertiesModel.INSTANCE;
        if (!model.isEmpty())
        {
            if (PropertiesTester.PROPERTY_SURFACE_SET.equals(property))
            {
                return !model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PropertiesTester.PROPERTY_SURFACE_REMOVE.equals(property))
            {
                return model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PropertiesTester.PROPERTY_ICON_SET.equals(property))
            {
                return !model.hasProperty(ConfigSurface.SURFACE_ICON) && model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PropertiesTester.PROPERTY_ICON_REMOVE.equals(property))
            {
                return model.hasProperty(ConfigSurface.SURFACE_ICON);
            }
            else if (PropertiesTester.PROPERTY_FRAMES_SET.equals(property))
            {
                return !model.hasProperty(ConfigFrames.FRAMES) && model.hasProperty(ConfigSurface.SURFACE_IMAGE);
            }
            else if (PropertiesTester.PROPERTY_FRAMES_REMOVE.equals(property))
            {
                return model.hasProperty(ConfigFrames.FRAMES);
            }
            else if (PropertiesTester.PROPERTY_ANIMATIONS_ENABLE.equals(property)
                    && model.hasProperty(ConfigFrames.FRAMES))
            {
                return !model.hasProperty(ConfigAnimations.ANIMATION);
            }
            else if (PropertiesTester.PROPERTY_ANIMATIONS_EDIT.equals(property))
            {
                return model.hasProperty(ConfigAnimations.ANIMATION);
            }
            else if (PropertiesTester.PROPERTY_ANIMATIONS_DISABLE.equals(property))
            {
                return model.hasProperty(ConfigAnimations.ANIMATION);
            }
            else if (PropertiesTester.PROPERTY_COLLISIONS_ENABLE.equals(property))
            {
                return !model.hasProperty(ConfigCollisions.COLLISION);
            }
            else if (PropertiesTester.PROPERTY_COLLISIONS_EDIT.equals(property))
            {
                return model.hasProperty(ConfigCollisions.COLLISION);
            }
            else if (PropertiesTester.PROPERTY_COLLISIONS_DISABLE.equals(property))
            {
                return model.hasProperty(ConfigCollisions.COLLISION);
            }
        }
        return false;
    }
}
