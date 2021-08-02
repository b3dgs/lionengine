/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsNpe;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Test {@link CollisionFunctionConfig}.
 */
final class CollisionFunctionConfigTest
{
    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        assertPrivateConstructor(CollisionFunctionConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    void testFunction()
    {
        final Xml root = new Xml("function");
        final CollisionFunction function = new CollisionFunctionLinear(2.0, 3.0);
        CollisionFunctionConfig.exports(root, function);

        final CollisionFunction imported = CollisionFunctionConfig.imports(root);

        assertEquals(function, imported);
    }

    /**
     * Test export with <code>null</code> function.
     */
    @Test
    void testExportFunctionNull()
    {
        final Xml root = new Xml("null");
        assertThrowsNpe(() -> CollisionFunctionConfig.exports(root, new CollisionFunction()
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
        }));
    }

    /**
     * Test import with invalid function.
     */
    @Test
    void testImportFunctionInvalid()
    {
        final Xml root = new Xml("function");
        final CollisionFunction function = new CollisionFunctionLinear(2.0, 3.0);
        CollisionFunctionConfig.exports(root, function);
        root.getChildXml(CollisionFunctionConfig.FUNCTION).writeString(CollisionFunctionConfig.TYPE, "void");

        assertThrows(() -> CollisionFunctionConfig.imports(root), XmlReader.ERROR_ENUM + "void");
    }

    /**
     * Test exports imports with unknown enum.
     */
    @Test
    void testFunctionUnknown()
    {
        final Xml root = new Xml("function");
        root.createChild(CollisionFunctionConfig.FUNCTION)
            .writeString(CollisionFunctionConfig.TYPE, CollisionFunctionType.values()[1].name());

        assertThrows(() -> CollisionFunctionConfig.imports(root), "Unknown enum: FAIL");
    }

    /**
     * Test export with unknown function.
     */
    @Test
    void testExportFunctionUnknown()
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

        assertThrows(() -> CollisionFunctionConfig.imports(root), "Unknown enum: FAIL");
    }
}
