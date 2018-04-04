/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link CollisionRangeConfig}.
 */
public final class CollisionRangeConfigTest
{
    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testEnum() throws Exception
    {
        UtilTests.testPrivateConstructor(CollisionRangeConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final Xml root = new Xml("ranges");
        final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
        CollisionRangeConfig.exports(root, range);

        Assert.assertEquals(range, CollisionRangeConfig.imports(root.getChild(CollisionRangeConfig.NODE_RANGE)));
    }

    /**
     * Test with invalid axis.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidAxis()
    {
        final Xml root = new Xml("ranges");
        final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
        CollisionRangeConfig.exports(root, range);

        root.getChild(CollisionRangeConfig.NODE_RANGE).writeString(CollisionRangeConfig.ATT_AXIS, "void");
        Assert.assertNull(CollisionRangeConfig.imports(root.getChild(CollisionRangeConfig.NODE_RANGE)));
    }
}
