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

import org.junit.Test;

/**
 * Test the check class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class CheckTest
{
    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(Check.class);
    }

    /**
     * Test the check with a null object.
     */
    @Test(expected = LionEngineException.class)
    public void testNotNullFail()
    {
        Check.notNull(null);
    }

    /**
     * Test the check with non null object.
     */
    @Test
    public void testNotNull()
    {
        Check.notNull(new Object());
    }

    /**
     * Test the check superior valid case.
     */
    @Test
    public void testSuperior()
    {
        Check.superiorOrEqual(0, 0);
        Check.superiorOrEqual(1, 0);
        Check.superiorStrict(1, 0);

        Check.superiorOrEqual(0.0, 0.0);
        Check.superiorOrEqual(1.0, 0.0);
        Check.superiorStrict(1.0, 0.0);
    }

    /**
     * Test the check inferior valid case.
     */
    @Test
    public void testInferior()
    {
        Check.inferiorOrEqual(0, 0);
        Check.inferiorOrEqual(0, 1);
        Check.inferiorStrict(0, 1);

        Check.inferiorOrEqual(0.0, 0.0);
        Check.inferiorOrEqual(0.0, 1.0);
        Check.inferiorStrict(0.0, 1.0);
    }

    /**
     * Test the check superior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorFail()
    {
        Check.superiorStrict(-1, 0);
    }

    /**
     * Test the check superior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorEqualFail()
    {
        Check.superiorOrEqual(-1, 0);
    }

    /**
     * Test the check inferior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorFail()
    {
        Check.inferiorStrict(1, 0);
    }

    /**
     * Test the check inferior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testCheckInferiorEqualFail()
    {
        Check.inferiorOrEqual(1, 0);
    }

    /**
     * Test the check superior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorDoubleFail()
    {
        Check.superiorStrict(-1.0, 0.0);
    }

    /**
     * Test the check superior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testSuperiorEqualDoubleFail()
    {
        Check.superiorOrEqual(-1.0, 0.0);
    }

    /**
     * Test the check inferior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorDoubleFail()
    {
        Check.inferiorStrict(1.0, 0.0);
    }

    /**
     * Test the check inferior invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testInferiorEqualDoubleFail()
    {
        Check.inferiorOrEqual(1.0, 0.0);
    }

    /**
     * Test the check different valid case.
     */
    @Test
    public void testDifferent()
    {
        Check.different(1, 0);
    }

    /**
     * Test the check different invalid case.
     */
    @Test(expected = LionEngineException.class)
    public void testDifferentFail()
    {
        Check.different(0, 0);
    }
}
