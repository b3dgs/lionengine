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

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the operating system class.
 */
public class OperatingSystemTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(OperatingSystem.class);
    }

    /**
     * Test the operating system getters.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testOperatingSystem() throws Exception
    {
        Assert.assertNotNull(OperatingSystem.getOperatingSystem());
    }

    /**
     * Test the find operating system.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testFindOs() throws Exception
    {
        Assert.assertEquals(OperatingSystem.UNKNOWN, OperatingSystem.find(null));
        Assert.assertEquals(OperatingSystem.UNKNOWN, OperatingSystem.find("azerty"));
        Assert.assertEquals(OperatingSystem.WINDOWS, OperatingSystem.find("win"));
        Assert.assertEquals(OperatingSystem.MAC, OperatingSystem.find("mac"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.find("nix"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.find("nux"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.find("bsd"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.find("aix"));
        Assert.assertEquals(OperatingSystem.SOLARIS, OperatingSystem.find("sunos"));
    }
}
