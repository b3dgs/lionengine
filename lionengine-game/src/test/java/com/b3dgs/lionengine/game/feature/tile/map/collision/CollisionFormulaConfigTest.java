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

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.io.Xml;

/**
 * Test the collision formula configuration.
 */
public class CollisionFormulaConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /** Range test. */
    private final CollisionRange range = new CollisionRange(Axis.X, 0, 1, 2, 3);
    /** Function test. */
    private final CollisionFunction function = new CollisionFunctionLinear(1.0, 2.0);
    /** Constraint test. */
    private final CollisionConstraint constaint = new CollisionConstraint();
    /** Formula test. */
    private final CollisionFormula formula = new CollisionFormula("formula", range, function, constaint);

    /**
     * Test the import export.
     */
    @Test
    public void testFormula()
    {
        final Xml root = new Xml("formula");
        CollisionFormulaConfig.exports(root, formula);
        final Media config = Medias.create("formulas.xml");
        root.save(config);

        final CollisionFormulaConfig imported = CollisionFormulaConfig.imports(config);

        Assert.assertEquals("formula", imported.getFormulas().keySet().iterator().next());
        Assert.assertEquals(formula, imported.getFormula("formula"));

        imported.clear();

        Assert.assertTrue(imported.getFormulas().isEmpty());
        Assert.assertTrue(config.getFile().delete());
    }

    /**
     * Test the has and remove functions.
     */
    @Test
    public void testHasRemove()
    {
        final Xml root = new Xml("formula");
        CollisionFormulaConfig.exports(root, formula);

        Assert.assertFalse(CollisionFormulaConfig.has(root, "void"));
        Assert.assertTrue(CollisionFormulaConfig.has(root, "formula"));

        CollisionFormulaConfig.remove(root, "void");
        CollisionFormulaConfig.remove(root, "formula");

        Assert.assertFalse(CollisionFormulaConfig.has(root, "formula"));
    }
}
