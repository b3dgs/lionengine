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
 * Test the operating system class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class OperatingSystemTest
{
    /**
     * Test the enum.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testEnum() throws ReflectiveOperationException
    {
        UtilTests.testEnum(OperatingSystem.class);
    }

    /**
     * Test the operating system class.
     */
    @Test
    public void testOperatingSystem()
    {
        Assert.assertNotNull(OperatingSystem.getArchitecture());
        Assert.assertNotNull(OperatingSystem.getOperatingSystem());
        Assert.assertNotNull(OperatingSystem.MAC);
        Assert.assertNotNull(OperatingSystem.WINDOWS);
        Assert.assertNotNull(OperatingSystem.UNIX);
        Assert.assertNotNull(OperatingSystem.UNKNOWN);
        Assert.assertNotNull(OperatingSystem.SOLARIS);

        Assert.assertNotNull(OperatingSystem.values());
        Assert.assertEquals(OperatingSystem.WINDOWS, OperatingSystem.valueOf(OperatingSystem.WINDOWS.name()));

        Assert.assertEquals(OperatingSystem.UNKNOWN, OperatingSystem.findOs(null));
        Assert.assertEquals(OperatingSystem.UNKNOWN, OperatingSystem.findOs("azerty"));
        Assert.assertEquals(OperatingSystem.WINDOWS, OperatingSystem.findOs("win"));
        Assert.assertEquals(OperatingSystem.MAC, OperatingSystem.findOs("mac"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.findOs("nix"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.findOs("nux"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.findOs("bsd"));
        Assert.assertEquals(OperatingSystem.UNIX, OperatingSystem.findOs("aix"));
        Assert.assertEquals(OperatingSystem.SOLARIS, OperatingSystem.findOs("sunos"));

        Assert.assertEquals(Architecture.UNKNOWN, OperatingSystem.findArchitecture(null));
        Assert.assertEquals(Architecture.UNKNOWN, OperatingSystem.findArchitecture("0"));
        Assert.assertEquals(Architecture.X86, OperatingSystem.findArchitecture("86"));
        Assert.assertEquals(Architecture.X86, OperatingSystem.findArchitecture("32"));
        Assert.assertEquals(Architecture.X64, OperatingSystem.findArchitecture("64"));
    }
}
