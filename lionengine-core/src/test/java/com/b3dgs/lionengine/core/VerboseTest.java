/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Version;

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
        Verbose.info("********************************** EXPECTED EXCEPTIONS **********************************");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        EngineCore.terminate();
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
     * Test the verbose class.
     */
    @Test
    public void testVerbose()
    {
        EngineCore.start("VerboseTest", Version.create(1, 0, 0), Verbose.CRITICAL, null, null);
        VerboseTest.testVerbose(Verbose.NONE);
        VerboseTest.testVerbose(Verbose.INFORMATION);
        VerboseTest.testVerbose(Verbose.WARNING);
        VerboseTest.testVerbose(Verbose.CRITICAL);
        Assert.assertNotNull(Verbose.values());
        Assert.assertEquals(Verbose.NONE, Verbose.valueOf(Verbose.NONE.name()));
    }
}
