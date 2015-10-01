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
package com.b3dgs.lionengine.test.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.test.SecurityManagerMock;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the verbose class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class VerboseTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        UtilReflection.getMethod(Verbose.class, "prepareLogger");
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Verbose.set(Verbose.INFORMATION, Verbose.WARNING, Verbose.CRITICAL);
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test the verbose level.
     * 
     * @param level The verbose level.
     */
    private static void testVerbose(Verbose level)
    {
        Verbose.set(level);
        Verbose.info("info");
        Verbose.warning(VerboseTest.class, "testVerbose", "warning");
        Verbose.critical(VerboseTest.class, "testVerbose", "critical");
        Verbose.exception(VerboseTest.class, "testVerbose", new LionEngineException("exception"), "exception");
    }

    /**
     * Prepare the test.
     */
    @Before
    public void prepareTest()
    {
        System.setSecurityManager(null);
        UtilReflection.getMethod(Verbose.class, "prepareLogger");
    }

    /**
     * Clean test.
     */
    @After
    public void afterTest()
    {
        System.setSecurityManager(null);
    }

    /**
     * Test the enum.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    public void testEnum() throws ReflectiveOperationException
    {
        UtilTests.testEnum(Verbose.class);
    }

    /**
     * Test the verbose class.
     */
    @Test
    public void testVerbose()
    {
        testVerbose(Verbose.INFORMATION);
        testVerbose(Verbose.WARNING);
        testVerbose(Verbose.CRITICAL);
    }

    /**
     * Test the verbose security.
     */
    @Test
    public void testVerboseSecurity()
    {
        System.setSecurityManager(new SecurityManagerMock(false));
        UtilReflection.getMethod(Verbose.class, "prepareLogger");
    }
}
