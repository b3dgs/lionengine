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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Version}.
 */
final class VersionTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final Version version = Version.create(3, 2, 1);

        assertEquals(3, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(1, version.getMicro());
    }

    /**
     * Test equality.
     */
    @Test
    void testEquals()
    {
        final Version version = Version.create(3, 2, 1);

        assertEquals(version, version);
        assertEquals(version, Version.create(3, 2, 1));

        assertNotEquals(version, null);
        assertNotEquals(version, new Object());
        assertNotEquals(version, Version.create(3, 2, 0));
        assertNotEquals(version, Version.create(3, 0, 1));
        assertNotEquals(version, Version.create(0, 2, 1));
        assertNotEquals(version, Version.create(3, 0, 0));
        assertNotEquals(version, Version.create(0, 0, 1));
        assertNotEquals(version, Version.create(0, 0, 0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final Version version = Version.create(3, 2, 1);

        assertHashEquals(version, Version.create(3, 2, 1));

        assertHashNotEquals(version, new Object());
        assertHashNotEquals(version, Version.create(3, 2, 0));
        assertHashNotEquals(version, Version.create(3, 0, 1));
        assertHashNotEquals(version, Version.create(0, 2, 1));
        assertHashNotEquals(version, Version.create(3, 0, 0));
        assertHashNotEquals(version, Version.create(0, 0, 1));
        assertHashNotEquals(version, Version.create(0, 0, 0));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        assertEquals("3.2.1", Version.create(3, 2, 1).toString());
    }
}
