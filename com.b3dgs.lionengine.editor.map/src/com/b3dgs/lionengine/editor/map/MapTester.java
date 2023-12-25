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
package com.b3dgs.lionengine.editor.map;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.helper.MapTileHelper;

/**
 * Test if the map has been defined.
 */
public final class MapTester extends PropertyTester
{
    /** Test if map defined. */
    private static final String PROPERTY_TEST = "test";

    /**
     * Create tester.
     */
    public MapTester()
    {
        super();
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final MapTileHelper map = WorldModel.INSTANCE.getMap();
        final boolean result;
        if (PROPERTY_TEST.equals(property))
        {
            result = map.isCreated();
        }
        else
        {
            result = false;
        }
        return result;
    }
}
