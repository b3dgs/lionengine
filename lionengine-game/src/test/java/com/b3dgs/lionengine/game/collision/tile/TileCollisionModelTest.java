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
package com.b3dgs.lionengine.game.collision.tile;

import static com.b3dgs.lionengine.test.UtilTests.PRECISION;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.map.UtilMap;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGame;

/**
 * Test the tile collision model class.
 */
public class TileCollisionModelTest
{
    /** Formula vertical test. */
    private final CollisionFormula formulaV = new CollisionFormula("y",
                                                                   new CollisionRange(Axis.Y, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    /** Formula horizontal test. */
    private final CollisionFormula formulaH = new CollisionFormula("x",
                                                                   new CollisionRange(Axis.X, 0, 1, 0, 1),
                                                                   new CollisionFunctionLinear(0.0, 0.0),
                                                                   new CollisionConstraint());
    /** Group test. */
    private final CollisionGroup group = new CollisionGroup(UtilMap.GROUND, Arrays.asList(formulaV, formulaH));
    /** Category vertical test. */
    private final CollisionCategory categoryY = new CollisionCategory("y", Axis.Y, 0, 0, Arrays.asList(group));
    /** Category horizontal test. */
    private final CollisionCategory categoryX = new CollisionCategory("x", Axis.X, 0, 0, Arrays.asList(group));
    /** Tile test. */
    private final Tile tile = new TileGame(Integer.valueOf(0), 0, 0.0, 0.0, 1, 1);
    /** Model test. */
    private final TileCollisionModel model = new TileCollisionModel(tile);

    /**
     * Test the tile collision model.
     */
    @Test
    public void testModel()
    {
        model.addCollisionFormula(formulaV);
        model.addCollisionFormula(formulaH);

        Assert.assertTrue(model.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));

        Assert.assertNull(model.getCollisionX(categoryY, 0.0, 1.0, 0.0, 0.0));
        Assert.assertNull(model.getCollisionX(categoryY, 0.0, -1.0, 0.0, 1.0));
        Assert.assertNull(model.getCollisionY(categoryX, -1.0, 0.0, 0.0, 1.0));
        Assert.assertNull(model.getCollisionY(categoryX, 1.0, 0.0, 0.0, 0.0));
    }

    /**
     * Test the tile collision hit.
     */
    @Test
    public void testCollisionInside()
    {
        model.addCollisionFormula(formulaV);
        model.addCollisionFormula(formulaH);

        Assert.assertEquals(1.0, model.getCollisionY(categoryY, 0.0, 1.0, 0.0, 0.0).doubleValue(), PRECISION);
        Assert.assertEquals(-1.0, model.getCollisionY(categoryY, 0.0, -1.0, 0.0, 1.0).doubleValue(), PRECISION);
        Assert.assertEquals(-1.0, model.getCollisionX(categoryX, -1.0, 0.0, 1.0, 0.0).doubleValue(), PRECISION);
        Assert.assertEquals(1.0, model.getCollisionX(categoryX, 1.0, 0.0, 0.0, 0.0).doubleValue(), PRECISION);
    }

    /**
     * Test the tile collision hit.
     */
    @Test
    public void testCollisionInsideWithRemovedCollision()
    {
        model.addCollisionFormula(formulaV);
        model.addCollisionFormula(formulaH);
        model.removeCollisionFormula(formulaV);

        Assert.assertNull(model.getCollisionY(categoryY, 0.0, 1.0, 0.0, 0.0));
        Assert.assertNull(model.getCollisionY(categoryY, 0.0, -1.0, 0.0, 1.0));
        Assert.assertEquals(-1.0, model.getCollisionX(categoryX, -1.0, 0.0, 1.0, 0.0).doubleValue(), PRECISION);
        Assert.assertEquals(1.0, model.getCollisionX(categoryX, 1.0, 0.0, 0.0, 0.0).doubleValue(), PRECISION);

        model.removeCollisionFormulas();

        Assert.assertNull(model.getCollisionY(categoryY, 0.0, 1.0, 0.0, 0.0));
        Assert.assertNull(model.getCollisionY(categoryY, 0.0, -1.0, 0.0, 1.0));
        Assert.assertNull(model.getCollisionX(categoryX, -1.0, 0.0, 1.0, 0.0));
        Assert.assertNull(model.getCollisionX(categoryX, 1.0, 0.0, 0.0, 0.0));
    }

    /**
     * Test the tile collision outside.
     */
    @Test
    public void testCollisionOutside()
    {
        model.addCollisionFormula(formulaV);
        model.addCollisionFormula(formulaH);

        Assert.assertNull(model.getCollisionY(categoryY, 2.0, 1.0, 2.0, 0.0));
        Assert.assertNull(model.getCollisionY(categoryY, 2.0, -1.0, 2.0, 1.0));
        Assert.assertNull(model.getCollisionX(categoryX, -1.0, 2.0, 1.0, 2.0));
        Assert.assertNull(model.getCollisionX(categoryX, 1.0, 2.0, 0.0, 2.0));
    }
}
