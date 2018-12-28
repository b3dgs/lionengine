/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilReflection}.
 */
public final class UtilReflectionTest
{
    /**
     * Test constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilReflection.class);
    }

    /**
     * Create create.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testCreate() throws NoSuchMethodException
    {
        assertEquals("1", UtilReflection.create(String.class, UtilReflection.getParamTypes(String.class), "1"));
    }

    /**
     * Create create with missing method.
     */
    @Test
    public void testCreateNoMethod()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + Object.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(String.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.create(Object.class, UtilReflection.getParamTypes(""), ""),
                     expected);
    }

    /**
     * Create create with invalid arguments.
     */
    @Test
    public void testCreateIllegalArgument()
    {
        final String expected = UtilReflection.ERROR_CONSTRUCTOR
                                + String.class
                                + " "
                                + Arrays.asList(String.class)
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList("test", "test");
        assertThrows(() -> UtilReflection.create(String.class, UtilReflection.getParamTypes("test"), "test", "test"),
                     expected);
    }

    /**
     * Create create with constructor error.
     */
    @Test
    public void testCreateConstructorError()
    {
        assertThrows(() -> UtilReflection.create(String.class, UtilReflection.getParamTypes("test"), (String) null),
                     UtilReflection.ERROR_CONSTRUCTOR + String.class);
    }

    /**
     * Create create with constructor error.
     */
    @Test
    public void testCreateConstructorNotAccessible()
    {
        assertThrows(() -> UtilReflection.create(UtilMath.class, new Class[0]),
                     UtilReflection.ERROR_CONSTRUCTOR + UtilMath.class);
    }

    /**
     * Create create with abstract class.
     */
    @Test
    public void testCreateAbstractClass()
    {
        assertThrows(() -> UtilReflection.create(Engine.class,
                                                 UtilReflection.getParamTypes("", Version.DEFAULT),
                                                 "",
                                                 Version.DEFAULT),
                     UtilReflection.ERROR_CONSTRUCTOR + Engine.class);
    }

    /**
     * Create create reduce.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testCreateReduce() throws NoSuchMethodException
    {
        assertEquals(ViewerMock.class,
                     UtilReflection.createReduce(ViewerMock.class, Integer.valueOf(1), "void").getClass());
    }

    /**
     * Create create reduce with no constructor found.
     */
    @Test
    public void testCreateReduceNoConstructor()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + Config.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(Integer.class, String.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.createReduce(Config.class, Integer.valueOf(1), "void"),
                     expected);
    }

    /**
     * Create create reduce with no constructor found.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testCreateReduceMoreParameters() throws NoSuchMethodException
    {
        UtilReflection.createReduce(Reduce.class, Integer.valueOf(1), "test", Integer.valueOf(3));
    }

    /**
     * Test the accessibility setting.
     */
    @Test
    public void testSetAccessible()
    {
        final AccessibleObject accessible = AccessibleTest.class.getDeclaredFields()[0];

        assertFalse(accessible.isAccessible());

        UtilReflection.setAccessible(accessible, true);

        assertTrue(accessible.isAccessible());

        UtilReflection.setAccessible(accessible, false);

        assertFalse(accessible.isAccessible());

        UtilReflection.setAccessible(accessible, false);

        assertFalse(accessible.isAccessible());
    }

    /**
     * Test get parameters types.
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
        assertEquals(Integer.class, types[0]);
        assertEquals(String.class, types[1]);
        assertEquals(Double.class, types[2]);
        assertEquals(Float.class, types[3]);
    }

    /**
     * Test get method.
     */
    @Test
    public void testGetMethod()
    {
        assertNotNull(UtilReflection.getMethod(new Object(), "toString"));
        assertNull(UtilReflection.getMethod(new Object(), "finalize"));
    }

    /**
     * Test get method not accessible.
     */
    @Test
    public void testGetMethodNotAccessible()
    {
        assertThrows(() -> UtilReflection.getMethod(Verbose.class, "getMessage", "test"),
                     UtilReflection.ERROR_METHOD + "getMessage");
    }

    /**
     * Test get not existing method.
     */
    @Test
    public void testGetMethodNotExists()
    {
        assertThrows(() -> UtilReflection.getMethod(new Object(), "123"), UtilReflection.ERROR_METHOD + "123");
    }

    /**
     * Test get method with call error.
     */
    @Test
    public void testGetMethodCallError()
    {
        assertThrows(() -> UtilReflection.getMethod(Integer.class, "valueOf", ""),
                     UtilReflection.ERROR_METHOD + "valueOf");
    }

    /**
     * Test get method with throws {@link LionEngineException}.
     */
    @Test
    public void testGetMethodThrowsLionEngineException()
    {
        assertThrows(() -> UtilReflection.getMethod(Reduce.class, "throwsLion"), "Lion");
    }

    /**
     * Test get method with invalid parameter.
     */
    @Test
    public void testGetMethodInvalidParameter()
    {
        assertThrows(() -> UtilReflection.getMethod(Integer.class, "valueOf", ""),
                     UtilReflection.ERROR_METHOD + "valueOf");
    }

    /**
     * Test get field.
     */
    @Test
    public void testGetField()
    {
        assertNotNull(UtilReflection.getField(new Config(new Resolution(320, 240, 32), 16, false), "output"));
    }

    /**
     * Test get field accessible.
     */
    @Test
    public void testGetFieldAccessible()
    {
        assertNotNull(UtilReflection.getField(LionEngineException.class, "ERROR_PRIVATE_CONSTRUCTOR"));
    }

    /**
     * Test get field not accessible.
     */
    @Test
    public void testGetFieldNotAccessible()
    {
        assertNotNull(UtilReflection.getField(Verbose.class, "LOGGER"));
    }

    /**
     * Test field accessible from super class.
     */
    @Test
    public void testGetFieldSuperClass()
    {
        final String accessible = UtilReflection.getField(FieldTest2.class, "test");
        assertNotNull(accessible);
    }

    /**
     * Test get field unknown.
     */
    @Test
    public void testGetFieldUnknown()
    {
        assertThrows(() -> UtilReflection.getField(new Object(), "0"), UtilReflection.ERROR_FIELD + "0");
    }

    /**
     * Test get compatible constructor not found.
     */
    @Test
    public void testGetCompatibleConstructorNone()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + String.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(Integer.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.getCompatibleConstructor(String.class,
                                                                   UtilReflection.getParamTypes(new Integer(1))),
                     expected);
    }

    /**
     * Test get compatible constructor parent.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    public void testGetCompatibleConstructorParent() throws NoSuchMethodException
    {
        assertNotNull(UtilReflection.getCompatibleConstructorParent(String.class,
                                                                    UtilReflection.getParamTypes(new Object())));
    }

    /**
     * Test get compatible constructor parent none.
     */
    @Test
    public void testGetCompatibleConstructorParentNone()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + String.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(Integer.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.getCompatibleConstructorParent(String.class,
                                                                         UtilReflection.getParamTypes(new Integer(1))),
                     expected);
    }

    /**
     * Test get interfaces.
     */
    @Test
    public void testGetInterfaces()
    {
        assertEquals(0, UtilReflection.getInterfaces(Interface0.class, Interface0.class).size());
        assertEquals(0, UtilReflection.getInterfaces(Interface1.class, Interface0.class).size());
        assertEquals(Interface2.class,
                     UtilReflection.getInterfaces(Interface3.class, Interface0.class).iterator().next());

        final Collection<Class<?>> interfaces = new ArrayList<>();
        interfaces.add(Interface1.class);

        assertEquals(1, UtilReflection.getInterfaces(ObjectTest1.class, Interface0.class).size());
        assertTrue(UtilReflection.getInterfaces(ObjectTest1.class, Interface0.class).containsAll(interfaces));

        interfaces.add(Interface2.class);
        interfaces.add(Interface3.class);

        assertEquals(3, UtilReflection.getInterfaces(ObjectTest2.class, Interface0.class).size());
        assertTrue(UtilReflection.getInterfaces(ObjectTest2.class, Interface0.class).containsAll(interfaces));
    }

    /**
     * Interface mock
     */
    interface Interface0
    {
        // Mock
    }

    /**
     * Interface mock
     */
    interface Interface1 extends Interface0
    {
        // Mock
    }

    /**
     * Interface mock
     */
    interface Interface2 extends Interface0
    {
        // Mock
    }

    /**
     * Interface mock
     */
    interface Interface3 extends Interface2
    {
        // Mock
    }

    /**
     * Object mock.
     */
    class ObjectTest1 implements Interface1, Serializable
    {
        // Mock
    }

    /**
     * Object mock.
     */
    final class ObjectTest2 extends ObjectTest1 implements Interface3
    {
        // Mock
    }

    /**
     * Reduce class test.
     */
    static final class Reduce
    {
        /**
         * Throws Lion.
         */
        static void throwsLion()
        {
            throw new LionEngineException("Lion");
        }

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
    final class FieldTest2 extends FieldTest
    {
        // No field
    }

    /**
     * Accessible object test.
     */
    final class AccessibleTest
    {
        @SuppressWarnings("unused") private int a;
    }
}
