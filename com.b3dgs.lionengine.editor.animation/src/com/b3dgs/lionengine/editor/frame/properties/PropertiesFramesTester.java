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
package com.b3dgs.lionengine.editor.frame.properties;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;

/**
 * Test the properties node existence.
 */
public final class PropertiesFramesTester extends PropertyTester
{
    /** Can set frames. */
    private static final String PROPERTY_FRAMES_SET = "setFrames";
    /** Can remove frames. */
    private static final String PROPERTY_FRAMES_REMOVE = "removeFrames";

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
        if (PROPERTY_FRAMES_SET.equals(property))
        {
            result = !model.hasProperty(FramesConfig.NODE_FRAMES) && model.hasProperty(SurfaceConfig.ATT_IMAGE);
        }
        else if (PROPERTY_FRAMES_REMOVE.equals(property) && FramesConfig.NODE_FRAMES.equals(data))
        {
            result = model.hasProperty(FramesConfig.NODE_FRAMES);
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
    public PropertiesFramesTester()
    {
        super();
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final PropertiesModel model = PropertiesModel.INSTANCE;
        if (!model.isEmpty())
        {
            final Object data = model.getSelectedData();
            return check(model, data, property);
        }
        return false;
    }
}
