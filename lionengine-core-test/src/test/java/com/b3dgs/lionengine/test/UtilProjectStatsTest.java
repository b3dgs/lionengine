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

import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilProjectStats;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Test the utility project stats class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilProjectStatsTest
{
    /**
     * Test the constructor.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        UtilTests.testPrivateConstructor(UtilProjectStats.class);
    }

    /**
     * Test project stats.
     */
    @Test
    public void testProjectStats()
    {
        UtilProjectStats.start("src");

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        UtilProjectStats.countFileLines("null");
        Verbose.info("****************************************************************************************");
    }
}
