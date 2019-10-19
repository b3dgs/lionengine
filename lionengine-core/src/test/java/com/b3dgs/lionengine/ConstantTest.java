/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Constant}.
 */
public final class ConstantTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(Constant.class);
    }

    /**
     * Test all constants.
     */
    @Test
    public void testConstants()
    {
        assertEquals("LionEngine", Constant.ENGINE_NAME);
        assertEquals("Pierre-Alexandre", Constant.ENGINE_AUTHOR);
        assertEquals("http://lionengine.b3dgs.com", Constant.ENGINE_WEBSITE);
        assertEquals(Version.create(9, 0, 3), Constant.ENGINE_VERSION);
        assertEquals("xmlns:lionengine", Constant.XML_HEADER);
        assertEquals("lionengine:", Constant.XML_PREFIX);
        assertEquals("", Constant.EMPTY_STRING);
        assertEquals("\t", Constant.TAB);
        assertEquals("\"", Constant.QUOTE);
        assertEquals("/", Constant.SLASH);
        assertEquals(".", Constant.DOT);
        assertEquals(": ", Constant.DOUBLE_DOT);
        assertEquals(" ", Constant.SPACE);
        assertEquals("_", Constant.UNDERSCORE);
        assertEquals("*", Constant.STAR);
        assertEquals("@", Constant.AT);
        assertEquals("%", Constant.PERCENT);
        assertEquals("Hz", Constant.UNIT_RATE);
        assertEquals(".jar", Constant.TYPE_JAR);
        assertEquals(1_048_576, Constant.MEGA_BYTE);
        assertEquals(65_535, Constant.MAX_PORT);
        assertEquals(360, Constant.MAX_DEGREE);
        assertEquals(1000, Constant.THOUSAND);
        assertEquals(100, Constant.HUNDRED);
        assertEquals(10, Constant.DECADE);
        assertEquals(256, Constant.UNSIGNED_BYTE);
        assertEquals(24, Constant.BYTE_4);
        assertEquals(16, Constant.BYTE_3);
        assertEquals(8, Constant.BYTE_2);
        assertEquals(0, Constant.BYTE_1);
        assertEquals(1.0, Constant.EXTRP);
        assertEquals(0.5, Constant.HALF);
        assertEquals(1_000L, Constant.ONE_SECOND_IN_MILLI);
        assertEquals(1_000_000_000L, Constant.ONE_SECOND_IN_NANO);
        assertEquals(1_000_000.0, Constant.NANO_TO_MILLI);
        assertEquals(9.80665, Constant.GRAVITY_EARTH);
        assertEquals(3.71, Constant.GRAVITY_MARS);
        assertEquals(1.624, Constant.GRAVITY_MOON);
        assertEquals("SansSerif", Constant.FONT_SANS_SERIF);
        assertEquals("Serif", Constant.FONT_SERIF);
        assertEquals("Dialog", Constant.FONT_DIALOG);

    }

    /**
     * Test system property with default value.
     */
    @Test
    public void testSystemPropertyDefault()
    {
        assertEquals("default", Constant.getSystemProperty("null", "default"));
    }

    /**
     * Test system property with existing value.
     */
    @Test
    public void testSystemPropertyExists()
    {
        assertNotEquals("default", Constant.getSystemProperty("java.io.tmpdir", "default"));
    }

    /**
     * Test system property with security not allowing it.
     */
    @Test
    public void testSystemPropertySecurityException()
    {
        final SecurityManager old = System.getSecurityManager();
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        System.setSecurityManager(new SecurityManagerMock(false));
        try
        {
            assertEquals("default", Constant.getSystemProperty("security", "default"));
        }
        finally
        {
            System.setSecurityManager(old);
        }
        Verbose.info("****************************************************************************************");
    }
}
