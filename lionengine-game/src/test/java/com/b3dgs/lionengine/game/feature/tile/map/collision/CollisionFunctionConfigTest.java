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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilEnum;
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
    @BeforeAll
    public static void beforeTests()
    {
        HACK.addByValue(HACK.make("FAIL"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
        HACK.restore();
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(CollisionFunctionConfig.class);
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

        assertEquals(function, imported);
    }

    /**
     * Test exports imports with unknown enum.
     */
    @Test
    public void testFunctionUnknown()
    {
        final Xml root = new Xml("function");
        root.createChild(CollisionFunctionConfig.FUNCTION).writeString(CollisionFunctionConfig.TYPE,
                                                                       CollisionFunctionType.values()[1].name());

        assertThrows(() -> CollisionFunctionConfig.imports(root), "Unknown type: FAIL");
    }

    /**
     * Test export with <code>null</code> function.
     */
    @Test
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

            @Override
            public int getRenderX(double input)
            {
                return 0;
            }

            @Override
            public int getRenderY(double input)
            {
                return 0;
            }
        });

        assertThrows(() -> CollisionFunctionConfig.imports(root), CollisionFunctionConfig.ERROR_TYPE + "null");
    }

    /**
     * Test export with unknown function.
     */
    @Test
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

            @Override
            public int getRenderX(double input)
            {
                return 0;
            }

            @Override
            public int getRenderY(double input)
            {
                return 0;
            }
        });

        assertThrows(() -> CollisionFunctionConfig.imports(root), CollisionFunctionConfig.ERROR_TYPE + "FAIL");
    }

    /**
     * Test import with invalid function.
     */
    @Test
    public void testImportFunctionInvalid()
    {
        final Xml root = new Xml("function");
        final CollisionFunction function = new CollisionFunctionLinear(2.0, 3.0);
        CollisionFunctionConfig.exports(root, function);
        root.getChild(CollisionFunctionConfig.FUNCTION).writeString(CollisionFunctionConfig.TYPE, "void");

        assertThrows(() -> CollisionFunctionConfig.imports(root), CollisionFunctionConfig.ERROR_TYPE + "void");
    }
}
