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
package com.b3dgs.lionengine.test;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Architecture;
import com.b3dgs.lionengine.test.util.UtilTests;

/**
 * Test architecture class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ArchitectureTest
{
    /**
     * Test the enum.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testEnum() throws ReflectiveOperationException
    {
        UtilTests.testEnum(Architecture.class);
    }

    /**
     * Test the architecture getter .
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testArchitectureGet() throws ReflectiveOperationException
    {
        Assert.assertNotNull(Architecture.getArchitecture());
    }

    /**
     * Test the find architecture.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testArchitectureFind() throws ReflectiveOperationException
    {
        Assert.assertEquals(Architecture.UNKNOWN, Architecture.find(null));
        Assert.assertEquals(Architecture.UNKNOWN, Architecture.find("0"));
        Assert.assertEquals(Architecture.X86, Architecture.find("86"));
        Assert.assertEquals(Architecture.X86, Architecture.find("32"));
        Assert.assertEquals(Architecture.X64, Architecture.find("64"));
    }
}
