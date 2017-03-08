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

import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;

/**
 * Test the collision result class.
 */
public class CollisionResultTest
{
    /**
     * Test the collision result.
     */
    @Test
    public void testResult()
    {
        final Double x = Double.valueOf(1.0);
        final Double y = Double.valueOf(2.0);
        final Tile tile = new TileGame(Integer.valueOf(0), 1, 3.0, 4.0, 1, 1);
        final CollisionResult result = new CollisionResult(x, y, tile);

        Assert.assertEquals(x, result.getX());
        Assert.assertEquals(y, result.getY());
        Assert.assertEquals(tile, result.getTile());
    }
}
