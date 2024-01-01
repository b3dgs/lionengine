/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link CollisionFormulaConfig}.
 */
final class CollisionFormulaConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
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
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Xml root = new Xml("formula");
        CollisionFormulaConfig.exports(root, formula);

        final Media config = Medias.create("formulas.xml");
        root.save(config);

        final CollisionFormulaConfig imported = CollisionFormulaConfig.imports(config);

        assertEquals("formula", imported.getFormulas().keySet().iterator().next());
        assertEquals(formula, imported.getFormula("formula"));

        assertTrue(config.getFile().delete());
    }

    /**
     * Test has and remove functions.
     */
    @Test
    void testHasRemove()
    {
        final Xml root = new Xml("formula");
        CollisionFormulaConfig.exports(root, formula);

        assertFalse(CollisionFormulaConfig.has(root, "void"));
        assertTrue(CollisionFormulaConfig.has(root, "formula"));

        CollisionFormulaConfig.remove(root, "void");
        CollisionFormulaConfig.remove(root, "formula");

        assertFalse(CollisionFormulaConfig.has(root, "formula"));
    }
}
