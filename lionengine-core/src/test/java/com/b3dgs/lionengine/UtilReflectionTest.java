/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilReflection}.
 */
final class UtilReflectionTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilReflection.class);
    }

    /**
     * Create create.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    void testCreate() throws NoSuchMethodException
    {
        assertEquals("1", UtilReflection.create(String.class, UtilReflection.getParamTypes(String.class), "1"));
    }

    /**
     * Create create with missing method.
     */
    @Test
    void testCreateNoMethod()
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
    void testCreateIllegalArgument()
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
    void testCreateConstructorError()
    {
        assertThrows(() -> UtilReflection.create(String.class, UtilReflection.getParamTypes("test"), (String) null),
                     UtilReflection.ERROR_CONSTRUCTOR + String.class);
    }

    /**
     * Create create with constructor error.
     */
    @Test
    void testCreateConstructorNotAccessible()
    {
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.create(UtilMath.class, new Class[0]),
                     UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                                                                + UtilMath.class.getName()
                                                                                + UtilReflection.ERROR_WITH
                                                                                + "[]");
    }

    /**
     * Create create with abstract class.
     */
    @Test
    void testCreateAbstractClass()
    {
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.create(Engine.class,
                                                 UtilReflection.getParamTypes("", new Version(1, 0, 0)),
                                                 "",
                                                 new Version(1, 0, 0)),
                     UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                                                        + Engine.class.getName()
                                                                        + UtilReflection.ERROR_WITH
                                                                        + Arrays.asList(String.class, Version.class));
    }

    /**
     * Create create reduce.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    void testCreateReduce() throws NoSuchMethodException
    {
        assertEquals(ViewerMock.class,
                     UtilReflection.createReduce(ViewerMock.class, Integer.valueOf(1), "void").getClass());
    }

    /**
     * Create create reduce with no constructor found.
     */
    @Test
    void testCreateReduceNoConstructor()
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
     */
    @Test
    void testCreateReduceMoreParameters()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + Reduce.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(Integer.class, String.class, Integer.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.createReduce(Reduce.class, Integer.valueOf(1), "test", Integer.valueOf(3)),
                     expected);
    }

    /**
     * Test get parameters types.
     */
    @Test
    void testGetParamTypes()
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
    void testGetMethod()
    {
        assertNotNull(UtilTests.getMethod(new Object(), "toString"));
    }

    /**
     * Test get method not accessible.
     */
    @Test
    void testGetMethodNotAccessible()
    {
        assertThrows(() -> UtilTests.getMethod(UtilTests.class, "getPrimitive", "test"),
                     UtilReflection.ERROR_METHOD + "getPrimitive");
    }

    /**
     * Test get not existing method.
     */
    @Test
    void testGetMethodNotExists()
    {
        assertThrows(() -> UtilTests.getMethod(new Object(), "123"), UtilReflection.ERROR_METHOD + "123");
    }

    /**
     * Test get method with call error.
     */
    @Test
    void testGetMethodCallError()
    {
        assertThrows(() -> UtilTests.getMethod(Integer.class, "valueOf", ""), UtilReflection.ERROR_METHOD + "valueOf");
    }

    /**
     * Test get method with throws {@link LionEngineException}.
     */
    @Test
    void testGetMethodThrowsLionEngineException()
    {
        assertThrows(() -> UtilTests.getMethod(Reduce.class, "throwsLion"), "Lion");
    }

    /**
     * Test get method with invalid parameter.
     */
    @Test
    void testGetMethodInvalidParameter()
    {
        assertThrows(() -> UtilTests.getMethod(Integer.class, "valueOf", ""), UtilReflection.ERROR_METHOD + "valueOf");
    }

    /**
     * Test get field.
     */
    @Test
    void testGetField()
    {
        assertNotNull(UtilTests.getField(new Config(new Resolution(320, 240, 32), 16, false), "output"));
    }

    /**
     * Test get field accessible.
     */
    @Test
    void testGetFieldAccessible()
    {
        assertNotNull(UtilTests.getField(LionEngineException.class, "ERROR_PRIVATE_CONSTRUCTOR"));
    }

    /**
     * Test get field not accessible.
     */
    @Test
    void testGetFieldNotAccessible()
    {
        assertNotNull(UtilTests.getField(UtilTests.class, "PRECISION"));
    }

    /**
     * Test field accessible from super class.
     */
    @Test
    void testGetFieldSuperClass()
    {
        final String accessible = UtilTests.getField(FieldTest2.class, "test");
        assertNotNull(accessible);
    }

    /**
     * Test get field unknown.
     */
    @Test
    void testGetFieldUnknown()
    {
        assertThrows(() -> UtilTests.getField(new Object(), "0"), UtilReflection.ERROR_FIELD + "0");
    }

    /**
     * Test get compatible constructor not found.
     */
    @Test
    void testGetCompatibleConstructorNone()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + String.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(Integer.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.getCompatibleConstructor(String.class,
                                                                   UtilReflection.getParamTypes(Integer.valueOf(1))),
                     expected);
    }

    /**
     * Test get compatible constructor parent.
     * 
     * @throws NoSuchMethodException If error.
     */
    @Test
    void testGetCompatibleConstructorParent() throws NoSuchMethodException
    {
        assertNotNull(UtilReflection.getCompatibleConstructorParent(String.class,
                                                                    UtilReflection.getParamTypes(new Object())));
    }

    /**
     * Test get compatible constructor parent none.
     */
    @Test
    void testGetCompatibleConstructorParentNone()
    {
        final String expected = UtilReflection.ERROR_NO_CONSTRUCTOR_COMPATIBLE
                                + String.class.getName()
                                + UtilReflection.ERROR_WITH
                                + Arrays.asList(Integer.class);
        assertThrows(NoSuchMethodException.class,
                     () -> UtilReflection.getCompatibleConstructorParent(String.class,
                                                                         UtilReflection.getParamTypes(Integer.valueOf(1))),
                     expected);
    }

    /**
     * Test get interfaces.
     */
    @Test
    void testGetInterfaces()
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
    static class ObjectTest1 implements Interface1, Serializable
    {
        // Mock
    }

    /**
     * Object mock.
     */
    static final class ObjectTest2 extends ObjectTest1 implements Interface3
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
    static class FieldTest
    {
        /** Test field. */
        static final String test = Constant.EMPTY_STRING;
    }

    /**
     * Test field class without field
     */
    static final class FieldTest2 extends FieldTest
    {
        // No field
    }
}
