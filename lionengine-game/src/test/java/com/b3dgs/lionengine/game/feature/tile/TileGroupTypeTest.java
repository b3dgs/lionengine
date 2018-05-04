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
package com.b3dgs.lionengine.game.feature.tile;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link TileGroupType}.
 */
public final class TileGroupTypeTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(TileGroupType.class);
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    public void testFromString()
    {
        for (final TileGroupType type : TileGroupType.values())
        {
            assertEquals(type, TileGroupType.from(type.name()));
        }
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    public void testFromStringInvalid()
    {
        assertThrows(() -> TileGroupType.from("null"), TileGroupType.ERROR_TYPE_NAME + "null");
    }
}
