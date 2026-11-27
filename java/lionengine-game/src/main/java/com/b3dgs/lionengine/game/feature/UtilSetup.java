/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Tools related to featurable.
 */
public final class UtilSetup
{
    /** Class attribute name. */
    public static final String ATT_CLASS = Constant.XML_PREFIX + "class";
    /** Default class name. */
    private static final String DEFAULT_CLASS_NAME = "com.b3dgs.lionengine.game.feature.FeaturableModel";

    /**
     * Get class attribute.
     * 
     * @param root The root node.
     * @return The class name.
     */
    public static String getClass(AttributesReader root)
    {
        if (root.hasNode(ATT_CLASS))
        {
            return root.getChild(ATT_CLASS).getText();
        }
        return DEFAULT_CLASS_NAME;
    }

    /**
     * Private constructor.
     */
    private UtilSetup()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
