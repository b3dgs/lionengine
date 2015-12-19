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

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Version;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the utility reflection class.
 */
public class UtilReflectionTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilReflection.class);
    }

    /**
     * Create the create with missing method.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = NoSuchMethodException.class)
    public void testCreateNoMethod() throws NoSuchMethodException
    {
        Assert.assertNull(UtilReflection.create(Object.class, UtilReflection.getParamTypes(""), ""));
    }

    /**
     * Create the create with invalid arguments.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateIllegalArgument() throws NoSuchMethodException
    {
        Assert.assertNull(UtilReflection.create(String.class, UtilReflection.getParamTypes("test"), "test", "test"));
    }

    /**
     * Create the create with constructor error.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateConstructorError() throws NoSuchMethodException
    {
        Assert.assertNull(UtilReflection.create(String.class, UtilReflection.getParamTypes("test"), (String) null));
    }

    /**
     * Create the create with abstract class.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateAbstractClass() throws NoSuchMethodException
    {
        Assert.assertNull(UtilReflection.create(Engine.class,
                                                UtilReflection.getParamTypes("", Version.DEFAULT),
                                                "",
                                                Version.DEFAULT));
    }

    /**
     * Test the get parameters types.
     */
    @Test
    public void testGetParamTypes()
    {
        final Collection<Object> params = new ArrayList<Object>();
        params.add(Integer.valueOf(1));
        params.add("test");
        params.add(Double.valueOf(5.2));

        final Class<?>[] types = UtilReflection.getParamTypes(params.toArray());
        Assert.assertEquals(Integer.class, types[0]);
        Assert.assertEquals(String.class, types[1]);
        Assert.assertEquals(Double.class, types[2]);
    }

    /**
     * Test the get method.
     */
    @Test
    public void testGetMethod()
    {
        Assert.assertNotNull(UtilReflection.getMethod(new Object(), "toString"));
        Assert.assertNull(UtilReflection.getMethod(new Object(), "finalize"));
    }

    /**
     * Test the get not existing method.
     */
    @Test(expected = LionEngineException.class)
    public void testGetMethodNotExists()
    {
        Assert.assertNotNull(UtilReflection.getMethod(new Object(), "123"));
    }

    /**
     * Test the get method with call error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetMethodCallError()
    {
        Assert.assertNotNull(UtilReflection.getMethod(Integer.class, "valueOf", ""));
    }

    /**
     * Test the get method with invalid parameter.
     */
    @Test(expected = LionEngineException.class)
    public void testGetMethodInvalidParameter()
    {
        Assert.assertNotNull(UtilReflection.getMethod(Integer.class, "valueOf", ""));
    }

    /**
     * Test the get field.
     */
    @Test
    public void testGetField()
    {
        Assert.assertNotNull(UtilReflection.getField(new Resolution(320, 240, 32), "lock"));
    }

    /**
     * Test the get field unknown.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFieldUnknown()
    {
        Assert.assertNotNull(UtilReflection.getField(new Object(), "0"));
    }

    /**
     * Test the get compatible constructor not found.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = NoSuchMethodException.class)
    public void testGetCompatibleConstructorNone() throws NoSuchMethodException
    {
        Assert.assertNotNull(UtilReflection.getCompatibleConstructor(String.class,
                                                                     UtilReflection.getParamTypes(new Integer(1))));
    }
}
