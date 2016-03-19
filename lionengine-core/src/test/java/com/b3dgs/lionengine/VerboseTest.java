/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Test;

import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the verbose class.
 */
public class VerboseTest
{
    /**
     * Test the verbose level.
     * 
     * @param level The verbose level.
     */
    private static void testVerbose(Verbose level)
    {
        Verbose.set(level);
        Verbose.info("info");
        Verbose.warning("warning");
        Verbose.warning(VerboseTest.class, "testVerbose", "warning");
        Verbose.critical(VerboseTest.class, "testVerbose", "critical");
        Verbose.exception(new LionEngineException("exception"), "exception");
    }

    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(Verbose.class);
    }

    /**
     * Test the verbose class.
     */
    @Test
    public void testVerbose()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        testVerbose(Verbose.INFORMATION);
        testVerbose(Verbose.WARNING);
        testVerbose(Verbose.CRITICAL);
        Verbose.set(Verbose.INFORMATION, Verbose.WARNING, Verbose.CRITICAL);
        Verbose.info("****************************************************************************************");
    }
}
