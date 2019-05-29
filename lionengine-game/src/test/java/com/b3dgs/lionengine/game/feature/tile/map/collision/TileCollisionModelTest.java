/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilEnum;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.map.UtilMap;

/**
 * Test {@link TileCollisionModel}.
 */
public class TileCollisionModelTest
{
    /** Hack enum. */
    private static final UtilEnum<Axis> HACK = new UtilEnum<>(Axis.class, TileCollisionModel.class);

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
        HACK.restore();
    }

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
    private final CollisionCategory categoryY = new CollisionCategory("y", Axis.Y, 0, 0, true, Arrays.asList(group));
    /** Category horizontal test. */
    private final CollisionCategory categoryX = new CollisionCategory("x", Axis.X, 0, 0, true, Arrays.asList(group));
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

        assertTrue(model.getCollisionFormulas().containsAll(Arrays.asList(formulaV, formulaH)));
    }

    /**
     * Test the tile collision hit.
     */
    @Test
    public void testCollisionInside()
    {
        model.addCollisionFormula(formulaV);
        model.addCollisionFormula(formulaH);

        assertEquals(0.0, model.getCollisionY(categoryY, formulaV, 0.0, 0.0).doubleValue());
        assertEquals(0.0, model.getCollisionY(categoryY, formulaV, 0.0, 1.0).doubleValue());
        assertEquals(0.0, model.getCollisionX(categoryX, formulaH, 1.0, 0.0).doubleValue());
        assertEquals(0.0, model.getCollisionX(categoryX, formulaH, 0.0, 0.0).doubleValue());
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

        assertNull(model.getCollisionY(categoryY, formulaH, 0.0, 0.0));
        assertNull(model.getCollisionY(categoryY, formulaH, 0.0, 1.0));
        assertEquals(0.0, model.getCollisionX(categoryX, formulaH, 1.0, 0.0).doubleValue());
        assertEquals(0.0, model.getCollisionX(categoryX, formulaH, 0.0, 0.0).doubleValue());

        model.removeCollisionFormulas();

        assertNull(model.getCollisionY(categoryY, formulaH, 0.0, 0.0));
        assertNull(model.getCollisionY(categoryY, formulaH, 0.0, 1.0));
        assertNull(model.getCollisionX(categoryX, formulaV, 1.0, 0.0));
        assertNull(model.getCollisionX(categoryX, formulaV, 0.0, 0.0));
    }

    /**
     * Test the tile collision outside.
     */
    @Test
    public void testCollisionOutside()
    {
        model.addCollisionFormula(formulaV);
        model.addCollisionFormula(formulaH);

        assertNull(model.getCollisionY(categoryY, formulaV, 0.0, 3.0));
        assertNull(model.getCollisionY(categoryY, formulaV, 3.0, 0.0));
        assertNull(model.getCollisionX(categoryX, formulaH, 0.0, 3.0));
        assertNull(model.getCollisionX(categoryX, formulaH, 3.0, 0.0));
    }

    /**
     * Test with unknown axis.
     */
    @Test
    public void testUnknownAxis()
    {
        final Axis unknown = Axis.values()[2];
        assertThrows(InvocationTargetException.class, () ->
        {
            final Method method = model.getClass()
                                       .getDeclaredMethod("getInputValue", Axis.class, Double.TYPE, Double.TYPE);
            UtilReflection.setAccessible(method, true);
            method.invoke(model, unknown, Double.valueOf(0.0), Double.valueOf(1.0));
        }, null);
    }
}
