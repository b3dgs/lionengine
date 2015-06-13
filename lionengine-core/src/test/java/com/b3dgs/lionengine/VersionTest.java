/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test version class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class VersionTest
{
    /**
     * Test the version class.
     */
    @Test
    public void testVersion()
    {
        final Version version = Version.create(3, 2, 1);
        Assert.assertEquals(3, version.getMajor());
        Assert.assertEquals(2, version.getMinor());
        Assert.assertEquals(1, version.getMicro());

        Assert.assertNotNull(version.toString());

        final Version version2 = Version.create(1, 3, 2);
        Assert.assertFalse(version.equals(version2));
    }
}
