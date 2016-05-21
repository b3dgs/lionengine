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
package com.b3dgs.lionengine.game;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.test.UtilTests;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Test the configurer class.
 */
public class ConfigurerTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = createConfig("configurer.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Create a test configuration.
     * 
     * @param name The file name.
     * @return The test configuration media.
     */
    private static Media createConfig(String name)
    {
        final Media media = Medias.create(name);

        final XmlNode root = Xml.create("root");
        root.writeString("attStr", "string");
        root.writeString("attInt", "1");

        final XmlNode accessible = root.createChild(Accessible.class.getSimpleName());
        accessible.setText(Accessible.class.getName());

        final XmlNode notAccessible = root.createChild(NotAccessible.class.getSimpleName());
        notAccessible.setText(NotAccessible.class.getName());

        final XmlNode custom = root.createChild(Custom.class.getSimpleName());
        custom.setText(Custom.class.getName());

        final XmlNode abstractClass = root.createChild(Abstract.class.getSimpleName());
        abstractClass.setText(Abstract.class.getName());

        final XmlNode unknown = root.createChild("unknown");
        unknown.setText("unknown");

        final XmlNode throwsClass = root.createChild(Throws.class.getSimpleName());
        throwsClass.setText(Throws.class.getName());

        Xml.save(root, media);

        return media;
    }

    /** Test configurer. */
    private final Configurer configurer = new Configurer(config);

    /**
     * Test the root getter.
     */
    @Test
    public void testGetRoot()
    {
        final XmlNode root = configurer.getRoot();
        Assert.assertNotNull(root);
        Assert.assertEquals("root", root.getNodeName());
    }

    /**
     * Test the path getter.
     */
    @Test
    public void testGetPath()
    {
        Assert.assertEquals(config.getFile().getParent(), configurer.getPath());
    }

    /**
     * Test the media getter.
     */
    @Test
    public void testGetMedia()
    {
        Assert.assertEquals(config, configurer.getMedia());
    }

    /**
     * Test the string getter.
     */
    @Test
    public void testGetString()
    {
        Assert.assertEquals("string", configurer.getString("attStr"));
    }

    /**
     * Test the boolean getter.
     */
    @Test
    public void testGetBoolean()
    {
        Assert.assertFalse(configurer.getBoolean("attStr"));
    }

    /**
     * Test the integer getter.
     */
    @Test
    public void testGetInteger()
    {
        Assert.assertEquals(1, configurer.getInteger("attInt"));
    }

    /**
     * Test the integer getter invalid value.
     */
    @Test(expected = LionEngineException.class)
    public void testGetIntegerInvalid()
    {
        Assert.assertEquals(0, configurer.getInteger("attStr"));
    }

    /**
     * Test the integer getter.
     */
    @Test
    public void testGetDouble()
    {
        Assert.assertEquals(1.0, configurer.getDouble("attInt"), UtilTests.PRECISION);
    }

    /**
     * Test the double getter invalid value.
     */
    @Test(expected = LionEngineException.class)
    public void testGetDoubleInvalid()
    {
        Assert.assertEquals(0.0, configurer.getDouble("attStr"), UtilTests.PRECISION);
    }

    /**
     * Test the text getter.
     */
    @Test
    public void testGetText()
    {
        Assert.assertEquals(Accessible.class.getName(), configurer.getText(Accessible.class.getSimpleName()));
    }

    /**
     * Test get node not found.
     */
    @Test(expected = LionEngineException.class)
    public void testNodeNotFound()
    {
        Assert.assertNull(configurer.getText("void", "null"));
    }

    /**
     * Test the get implementation with default constructor.
     */
    @Test
    public void testGetImplementationDefault()
    {
        final Accessible impl = configurer.getImplementation(Accessible.class, Accessible.class.getSimpleName());
        Assert.assertTrue(impl.created);
    }

    /**
     * Test the get implementation with not accessible constructor.
     */
    @Test
    public void testGetImplementationNotAccessible()
    {
        final NotAccessible impl = configurer.getImplementation(NotAccessible.class,
                                                                NotAccessible.class.getSimpleName());
        Assert.assertTrue(impl.created);
    }

    /**
     * Test the get implementation with custom constructor.
     */
    @Test
    public void testGetImplementationCustom()
    {
        final Custom impl = configurer.getImplementation(Custom.class,
                                                         Boolean.class,
                                                         Boolean.TRUE,
                                                         Custom.class.getSimpleName());
        Assert.assertTrue(impl.created);
    }

    /**
     * Test the get implementation with custom constructor using invalid argument.
     */
    @Test
    public void testGetImplementationCustomInvalidArgument()
    {
        try
        {
            Assert.assertNull(configurer.getImplementation(Custom.class,
                                                           Boolean.class,
                                                           Constant.EMPTY_STRING,
                                                           Custom.class.getSimpleName()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(),
                                IllegalArgumentException.class,
                                exception.getCause().getClass());
        }
    }

    /**
     * Test the get implementation using abstract class.
     */
    @Test
    public void testGetImplementationCustomAbstract()
    {
        try
        {
            Assert.assertNull(configurer.getImplementation(Abstract.class,
                                                           UtilReflection.getParamTypes(Boolean.TRUE),
                                                           Arrays.asList(Boolean.TRUE),
                                                           Abstract.class.getSimpleName()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(), InstantiationException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the get implementation using unknown class.
     */
    @Test
    public void testGetImplementationUnknown()
    {
        try
        {
            Assert.assertNull(configurer.getImplementation(Object.class, "unknown"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(), ClassNotFoundException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the get implementation using no compatible constructor.
     */
    @Test
    public void testGetImplementationNoCompatibleConstructor()
    {
        try
        {
            Assert.assertNull(configurer.getImplementation(Accessible.class,
                                                           UtilReflection.getParamTypes(Boolean.TRUE),
                                                           Arrays.asList(Boolean.TRUE),
                                                           Accessible.class.getSimpleName()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(), NoSuchMethodException.class, exception.getCause().getClass());
        }
    }

    /**
     * Test the get implementation using constructor throwing an exception.
     */
    @Test
    public void testGetImplementationConstructorThrows()
    {
        try
        {
            Assert.assertNull(configurer.getImplementation(Throws.class, Throws.class.getSimpleName()));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertEquals(exception.getMessage(),
                                InvocationTargetException.class,
                                exception.getCause().getClass());
        }
    }

    /**
     * Test the save.
     */
    @Test
    public void testSave()
    {
        final Media media = createConfig("save.xml");
        final Configurer configurer = new Configurer(media);
        final XmlNode root = configurer.getRoot();
        root.setText("save");
        configurer.save();

        Assert.assertEquals("save", new Configurer(media).getText());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Mock class.
     */
    static class Accessible
    {
        private final boolean created;

        /**
         * Create mock.
         */
        public Accessible()
        {
            created = true;
        }
    }

    /**
     * Mock class.
     */
    static class NotAccessible
    {
        private final boolean created;

        private NotAccessible()
        {
            created = true;
        }
    }

    /**
     * Mock class.
     */
    static class Custom
    {
        private final boolean created;

        /**
         * Create mock.
         * 
         * @param value Parameter value.
         */
        public Custom(Boolean value)
        {
            created = value.booleanValue();
        }
    }

    /**
     * Mock class.
     */
    abstract static class Abstract
    {
        /**
         * Create mock.
         * 
         * @param value Parameter value.
         */
        public Abstract(Boolean value)
        {
            Assert.assertNotNull(value);
        }
    }

    /**
     * Mock class.
     */
    static class Throws
    {
        /**
         * Create mock.
         */
        public Throws()
        {
            throw new IllegalStateException();
        }
    }
}
