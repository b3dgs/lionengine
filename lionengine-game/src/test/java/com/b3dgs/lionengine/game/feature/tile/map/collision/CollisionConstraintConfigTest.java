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
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Orientation;

/**
 * Test {@link CollisionConstraintConfig}.
 */
public final class CollisionConstraintConfigTest
{
    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(CollisionConstraintConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final Xml root = new Xml("constraint");

        final CollisionConstraint constraint = new CollisionConstraint();
        constraint.add(Orientation.EAST, "group");
        CollisionConstraintConfig.exports(root, constraint);

        final CollisionConstraint imported = CollisionConstraintConfig.imports(root);

        assertEquals(constraint, imported);
    }

    /**
     * Test with empty constraint.
     */
    @Test
    public void testExportsImportsEmptyConstraints()
    {
        final Xml root = new Xml("constraint");

        final CollisionConstraint constraint = new CollisionConstraint();
        CollisionConstraintConfig.exports(root, constraint);

        assertEquals(constraint, CollisionConstraintConfig.imports(root));
    }
}
