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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link CollisionFunctionConfig}.
 */
public final class CollisionFunctionConfigTest
{
    /** Hack enum. */
    private static final UtilEnum<CollisionFunctionType> HACK = new UtilEnum<>(CollisionFunctionType.class,
                                                                               CollisionFunctionConfig.class);

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
        HACK.restore();
    }

    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(CollisionFunctionConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testFunction()
    {
        final Xml root = new Xml("function");
        final CollisionFunction function = new CollisionFunctionLinear(2.0, 3.0);
        CollisionFunctionConfig.exports(root, function);

        final CollisionFunction imported = CollisionFunctionConfig.imports(root);

        Assert.assertEquals(function, imported);
    }

    /**
     * Test export with <code>null</code> function.
     */
    @Test(expected = LionEngineException.class)
    public void testExportFunctionNull()
    {

        final Xml root = new Xml("null");
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

        Assert.assertNull(CollisionFunctionConfig.imports(root));
    }

    /**
     * Test export with unknown function.
     */
    @Test(expected = LionEngineException.class)
    public void testExportFunctionUnknown()
    {
        final Xml root = new Xml("FAIL");
        CollisionFunctionConfig.exports(root, new CollisionFunction()
        {
            @Override
            public CollisionFunctionType getType()
            {
                return CollisionFunctionType.values()[1];
            }

            @Override
            public double compute(double input)
            {
                return 0;
            }
        });

        Assert.assertNull(CollisionFunctionConfig.imports(root));
    }

    /**
     * Test import with invalid function.
     */
    @Test(expected = LionEngineException.class)
    public void testImportFunctionInvalid()
    {
        final Xml root = new Xml("function");
        final CollisionFunction function = new CollisionFunctionLinear(2.0, 3.0);
        CollisionFunctionConfig.exports(root, function);

        root.getChild(CollisionFunctionConfig.FUNCTION).writeString(CollisionFunctionConfig.TYPE, "void");
        Assert.assertNull(CollisionFunctionConfig.imports(root));
    }
}
