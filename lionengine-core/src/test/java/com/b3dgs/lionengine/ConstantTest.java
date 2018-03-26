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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link Constant}.
 */
public final class ConstantTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(Constant.class);
    }

    /**
     * Test all constants.
     */
    @Test
    public void testConstants()
    {
        Assert.assertEquals("LionEngine", Constant.ENGINE_NAME);
        Assert.assertEquals("Pierre-Alexandre", Constant.ENGINE_AUTHOR);
        Assert.assertEquals("http://lionengine.b3dgs.com", Constant.ENGINE_WEBSITE);
        Assert.assertEquals(Version.create(9, 0, 0), Constant.ENGINE_VERSION);
        Assert.assertEquals("xmlns:lionengine", Constant.XML_HEADER);
        Assert.assertEquals("lionengine:", Constant.XML_PREFIX);
        Assert.assertEquals("", Constant.EMPTY_STRING);
        Assert.assertEquals("\t", Constant.TAB);
        Assert.assertEquals("\"", Constant.QUOTE);
        Assert.assertEquals("/", Constant.SLASH);
        Assert.assertEquals(".", Constant.DOT);
        Assert.assertEquals(": ", Constant.DOUBLE_DOT);
        Assert.assertEquals(" ", Constant.SPACE);
        Assert.assertEquals("_", Constant.UNDERSCORE);
        Assert.assertEquals("*", Constant.STAR);
        Assert.assertEquals("@", Constant.AT);
        Assert.assertEquals("%", Constant.PERCENT);
        Assert.assertEquals("Hz", Constant.UNIT_RATE);
        Assert.assertEquals(".jar", Constant.TYPE_JAR);
        Assert.assertEquals(1_048_576, Constant.MEGA_BYTE);
        Assert.assertEquals(65_535, Constant.MAX_PORT);
        Assert.assertEquals(360, Constant.MAX_DEGREE);
        Assert.assertEquals(1000, Constant.THOUSAND);
        Assert.assertEquals(100, Constant.HUNDRED);
        Assert.assertEquals(10, Constant.DECADE);
        Assert.assertEquals(256, Constant.UNSIGNED_BYTE);
        Assert.assertEquals(24, Constant.BYTE_4);
        Assert.assertEquals(16, Constant.BYTE_3);
        Assert.assertEquals(8, Constant.BYTE_2);
        Assert.assertEquals(0, Constant.BYTE_1);
        Assert.assertEquals(1.0, Constant.EXTRP, UtilTests.PRECISION);
        Assert.assertEquals(0.5, Constant.HALF, UtilTests.PRECISION);
        Assert.assertEquals(1_000L, Constant.ONE_SECOND_IN_MILLI);
        Assert.assertEquals(1_000_000_000L, Constant.ONE_SECOND_IN_NANO);
        Assert.assertEquals(1_000_000.0, Constant.NANO_TO_MILLI, UtilTests.PRECISION);
        Assert.assertEquals(9.80665, Constant.GRAVITY_EARTH, UtilTests.PRECISION);
        Assert.assertEquals(3.71, Constant.GRAVITY_MARS, UtilTests.PRECISION);
        Assert.assertEquals(1.624, Constant.GRAVITY_MOON, UtilTests.PRECISION);
        Assert.assertEquals("SansSerif", Constant.FONT_SANS_SERIF);
        Assert.assertEquals("Serif", Constant.FONT_SERIF);
        Assert.assertEquals("Dialog", Constant.FONT_DIALOG);

    }

    /**
     * Test system property with default value.
     */
    @Test
    public void testSystemPropertyDefault()
    {
        Assert.assertEquals("default", Constant.getSystemProperty("null", "default"));
    }

    /**
     * Test system property with existing value.
     */
    @Test
    public void testSystemPropertyExists()
    {
        Assert.assertNotEquals("default", Constant.getSystemProperty("java.io.tmpdir", "default"));
    }

    /**
     * Test system property with security not allowing it.
     */
    @Test
    public void testSystemPropertySecurityException()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        System.setSecurityManager(new SecurityManagerMock(false));
        try
        {
            Assert.assertEquals("default", Constant.getSystemProperty("security", "default"));
        }
        finally
        {
            System.setSecurityManager(null);
        }
        Verbose.info("****************************************************************************************");
    }
}
