/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.core.Engine;

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
     * Create the create with constructor error.
     * 
     * @throws NoSuchMethodException If error.
     * @throws NoSuchFieldException If error.
     * @throws IllegalAccessException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateConstructorNotAccessible() throws NoSuchMethodException,
            NoSuchFieldException,
            IllegalAccessException
    {
        final AtomicBoolean accessible = UtilReflection.getField(UtilReflection.class, "ACCESSIBLE");
        try
        {
            accessible.set(false);
            Assert.assertNotNull(UtilReflection.create(UtilMath.class, new Class[0]));
        }
        finally
        {
            accessible.set(true);
        }
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
     * Create the create reduce.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testCreateReduce() throws NoSuchMethodException
    {
        Assert.assertEquals(ViewerMock.class,
                            UtilReflection.createReduce(ViewerMock.class, Integer.valueOf(1), "void").getClass());
    }

    /**
     * Create the create reduce with no constructor found.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = NoSuchMethodException.class)
    public void testCreateReduceNoConstructor() throws NoSuchMethodException
    {
        Assert.assertNull(UtilReflection.createReduce(Config.class, Integer.valueOf(1), "void"));
    }

    /**
     * Create the create reduce with no constructor found.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testCreateReduceMoreParameters() throws NoSuchMethodException
    {
        UtilReflection.createReduce(Reduce.class, Integer.valueOf(1), "test", Integer.valueOf(3));

    }

    /**
     * Test the get parameters types.
     */
    @Test
    public void testGetParamTypes()
    {
        final Collection<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(1));
        params.add("test");
        params.add(Double.valueOf(5.2));
        params.add(Float.class);

        final Class<?>[] types = UtilReflection.getParamTypes(params.toArray());
        Assert.assertEquals(Integer.class, types[0]);
        Assert.assertEquals(String.class, types[1]);
        Assert.assertEquals(Double.class, types[2]);
        Assert.assertEquals(Float.class, types[3]);
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
     * Test the get method.
     */
    @Test(expected = LionEngineException.class)
    public void testGetMethodNotAccessible()
    {
        final AtomicBoolean accessible = UtilReflection.getField(UtilReflection.class, "ACCESSIBLE");
        try
        {
            accessible.set(false);
            Assert.assertNotNull(UtilReflection.getMethod(UtilProjectStats.class, "countLineTypes", "test"));
        }
        finally
        {
            accessible.set(true);
        }
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
        Assert.assertNotNull(UtilReflection.getField(new Config(new Resolution(320, 240, 32), 16, false), "output"));
    }

    /**
     * Test the get field accessible.
     */
    @Test
    public void testGetFieldAccessible()
    {
        Assert.assertNotNull(UtilReflection.getField(LionEngineException.class, "ERROR_PRIVATE_CONSTRUCTOR"));
    }

    /**
     * Test the get field not accessible.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFieldNotAccessible()
    {
        final AtomicBoolean accessible = UtilReflection.getField(UtilReflection.class, "ACCESSIBLE");
        try
        {
            accessible.set(false);
            Assert.assertNotNull(UtilReflection.getField(LionEngineException.class, "ERROR_UNKNOWN_ENUM"));
        }
        finally
        {
            accessible.set(true);
        }
    }

    /**
     * Test field accessible from super class.
     */
    @Test
    public void testGetFieldSuperClass()
    {
        final String accessible = UtilReflection.getField(FieldTest2.class, "test");
        Assert.assertNotNull(accessible);
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

    /**
     * Test the get compatible constructor parent.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testGetCompatibleConstructorParent() throws NoSuchMethodException
    {
        Assert.assertNotNull(UtilReflection.getCompatibleConstructorParent(String.class,
                                                                           UtilReflection.getParamTypes(new Object())));
    }

    /**
     * Test the get compatible constructor parent none.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test(expected = NoSuchMethodException.class)
    public void testGetCompatibleConstructorParentNone() throws NoSuchMethodException
    {
        Assert.assertNotNull(UtilReflection.getCompatibleConstructorParent(String.class,
                                                                           UtilReflection.getParamTypes(new Integer(1))));
    }

    /**
     * Test the get interfaces.
     */
    @Test
    public void testGetInterfaces()
    {
        Assert.assertEquals(0, UtilReflection.getInterfaces(Interface0.class, Interface0.class).size());
        Assert.assertEquals(0, UtilReflection.getInterfaces(Interface1.class, Interface0.class).size());
        Assert.assertEquals(Interface2.class,
                            UtilReflection.getInterfaces(Interface3.class, Interface0.class).iterator().next());

        final Collection<Class<?>> interfaces = new ArrayList<>();
        interfaces.add(Interface1.class);

        Assert.assertEquals(1, UtilReflection.getInterfaces(ObjectTest1.class, Interface0.class).size());
        Assert.assertTrue(UtilReflection.getInterfaces(ObjectTest1.class, Interface0.class).containsAll(interfaces));

        interfaces.add(Interface2.class);
        interfaces.add(Interface3.class);

        Assert.assertEquals(3, UtilReflection.getInterfaces(ObjectTest2.class, Interface0.class).size());
        Assert.assertTrue(UtilReflection.getInterfaces(ObjectTest2.class, Interface0.class).containsAll(interfaces));
    }

    /**
     * Reduce class test.
     */
    static class Reduce
    {
        /**
         * Create.
         * 
         * @param a First choice.
         */
        Reduce(Integer a)
        {
            super();
        }

        /**
         * Create.
         * 
         * @param a First choice.
         * @param b Second choice.
         */
        Reduce(Integer a, String b)
        {
            super();
        }
    }

    /**
     * Test field class without field
     */
    class FieldTest
    {
        /** Test field. */
        static final String test = Constant.EMPTY_STRING;
    }

    /**
     * Test field class without field
     */
    class FieldTest2 extends FieldTest
    {
        // No field
    }
}
