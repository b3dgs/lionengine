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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link TileConfig}.
 */
public final class TileConfigTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(TileConfig.class);
    }

    /**
     * Test imports.
     */
    @Test
    public void testImports()
    {
        final Xml nodeTile = new Xml(TileConfig.NODE_TILE);
        nodeTile.writeInteger(TileConfig.ATT_TILE_SHEET, 0);
        nodeTile.writeInteger(TileConfig.ATT_TILE_NUMBER, 1);

        Assert.assertEquals(new TileRef(0, 1), TileConfig.imports(nodeTile));
    }

    /**
     * Test exports.
     */
    @Test
    public void testExports()
    {
        final int sheet = 0;
        final int number = 1;
        final Xml nodeTile = TileConfig.exports(new TileRef(sheet, number));

        Assert.assertEquals(sheet, nodeTile.readInteger(TileConfig.ATT_TILE_SHEET));
        Assert.assertEquals(number, nodeTile.readInteger(TileConfig.ATT_TILE_NUMBER));
    }
}
