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
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the collision range configuration class.
 */
public class CollisionRangeConfigTest
{
    /** Range test. */
    private final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testEnum() throws Exception
    {
        UtilTests.testPrivateConstructor(CollisionRangeConfig.class);
    }

    /**
     * Test the import export.
     */
    @Test
    public void testRange()
    {
        final Xml root = new Xml("ranges");
        CollisionRangeConfig.exports(root, range);
        final CollisionRange imported = CollisionRangeConfig.imports(root.getChild(CollisionRangeConfig.RANGE));

        Assert.assertEquals(range, imported);
    }

    /**
     * Test the import with invalid axis.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidAxis()
    {
        final Xml root = new Xml("ranges");
        CollisionRangeConfig.exports(root, range);
        root.getChild(CollisionRangeConfig.RANGE).writeString(CollisionRangeConfig.AXIS, "void");
        final CollisionRange imported = CollisionRangeConfig.imports(root.getChild(CollisionRangeConfig.RANGE));

        Assert.assertNull(imported);
        Assert.fail();
    }
}
