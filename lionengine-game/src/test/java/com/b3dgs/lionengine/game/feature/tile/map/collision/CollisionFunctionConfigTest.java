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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the collision function configuration class.
 */
public class CollisionFunctionConfigTest
{
    /** Function test. */
    private final CollisionFunction function = new CollisionFunctionLinear(2.0, 3.0);

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(CollisionFunctionConfig.class);
    }

    /**
     * Test the import export.
     */
    @Test
    public void testFunction()
    {
        final Xml root = new Xml("function");
        CollisionFunctionConfig.exports(root, function);
        final CollisionFunction imported = CollisionFunctionConfig.imports(root);

        Assert.assertEquals(function, imported);
    }

    /**
     * Test the export with unknown function.
     */
    @Test
    public void testExportFunctionUnknown()
    {
        final Xml root = new Xml("function");
        CollisionFunctionConfig.exports(root, new CollisionFunction()
        {
            @Override
            public CollisionFunctionType getType()
            {
                return null;
            }

            @Override
            public double compute(double input)
            {
                return 0;
            }
        });
    }

    /**
     * Test the import with invalid function.
     */
    @Test(expected = LionEngineException.class)
    public void testImportFunctionInvalid()
    {
        final Xml root = new Xml("function");
        CollisionFunctionConfig.exports(root, function);
        root.getChild(CollisionFunctionConfig.FUNCTION).writeString(CollisionFunctionConfig.TYPE, "void");
        final CollisionFunction imported = CollisionFunctionConfig.imports(root);

        Assert.assertNull(imported);
        Assert.fail();
    }
}
