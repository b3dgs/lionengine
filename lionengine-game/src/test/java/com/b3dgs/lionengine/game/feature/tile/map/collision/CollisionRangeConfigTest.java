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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Test {@link CollisionRangeConfig}.
 */
final class CollisionRangeConfigTest
{
    /**
     * Test constructor.
     */
    @Test
    void testEnum()
    {
        assertPrivateConstructor(CollisionRangeConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Xml root = new Xml("ranges");
        final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
        CollisionRangeConfig.exports(root, range);

        assertEquals(range, CollisionRangeConfig.imports(root.getChild(CollisionRangeConfig.NODE_RANGE)));
    }

    /**
     * Test with invalid axis.
     */
    @Test
    void testInvalidAxis()
    {
        final Xml root = new Xml("ranges");
        final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
        CollisionRangeConfig.exports(root, range);

        root.getChildXml(CollisionRangeConfig.NODE_RANGE).writeString(CollisionRangeConfig.ATT_AXIS, "void");

        assertThrows(() -> CollisionRangeConfig.imports(root.getChild(CollisionRangeConfig.NODE_RANGE)),
                     XmlReader.ERROR_ENUM + "void");
    }
}
