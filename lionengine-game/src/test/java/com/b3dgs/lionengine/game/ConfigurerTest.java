/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Xml;

/**
 * Test {@link Configurer}.
 */
public final class ConfigurerTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = createConfig("configurer.xml");
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
        Configurer.clearCache();
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

        final Xml root = new Xml("root");
        root.writeString("attStr", "string");
        root.writeString("attInt", "1");

        final Xml accessible = root.createChild(Accessible.class.getSimpleName());
        accessible.setText(Accessible.class.getName());

        final Xml notAccessible = root.createChild(NotAccessible.class.getSimpleName());
        notAccessible.setText(NotAccessible.class.getName());

        final Xml custom = root.createChild(Custom.class.getSimpleName());
        custom.setText(Custom.class.getName());

        final Xml abstractClass = root.createChild(Abstract.class.getSimpleName());
        abstractClass.setText(Abstract.class.getName());

        final Xml unknown = root.createChild("unknown");
        unknown.setText("unknown");

        final Xml throwsClass = root.createChild(Throws.class.getSimpleName());
        throwsClass.setText(Throws.class.getName());

        root.save(media);

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
        final Xml root = configurer.getRoot();

        assertNotNull(root);
        assertEquals("root", root.getNodeName());
    }

    /**
     * Test the path getter.
     */
    @Test
    public void testGetPath()
    {
        assertEquals(config.getFile().getParent(), configurer.getPath());
    }

    /**
     * Test the media getter.
     */
    @Test
    public void testGetMedia()
    {
        assertEquals(config, configurer.getMedia());
    }

    /**
     * Test the string getter.
     */
    @Test
    public void testGetString()
    {
        assertEquals("string", configurer.getString("attStr"));
    }

    /**
     * Test the string getter with default value.
     */
    @Test
    public void testGetStringDefault()
    {
        assertEquals("string", configurer.getStringDefault("default", "attStr"));
        assertEquals("default", configurer.getStringDefault("default", "void"));
        assertEquals("default", configurer.getStringDefault("default", "void", "none"));
    }

    /**
     * Test the boolean getter.
     */
    @Test
    public void testGetBoolean()
    {
        assertFalse(configurer.getBoolean("attStr"));
    }

    /**
     * Test the boolean getter with default value.
     */
    @Test
    public void testGetBooleanDefault()
    {
        assertFalse(configurer.getBooleanDefault(true, "attStr"));
        assertTrue(configurer.getBooleanDefault(true, "void"));
    }

    /**
     * Test the integer getter.
     */
    @Test
    public void testGetInteger()
    {
        assertEquals(1, configurer.getInteger("attInt"));
    }

    /**
     * Test the integer getter with default value.
     */
    @Test
    public void testGetIntegerDefault()
    {
        assertEquals(1, configurer.getIntegerDefault(2, "attInt"));
        assertEquals(2, configurer.getIntegerDefault(2, "void"));
    }

    /**
     * Test the integer getter invalid value.
     */
    @Test
    public void testGetIntegerInvalid()
    {
        assertThrows(() -> configurer.getInteger("attStr"), "[configurer.xml] ");
    }

    /**
     * Test the integer getter invalid value.
     */
    @Test
    public void testGetIntegerDefaultInvalid()
    {
        assertThrows(() -> configurer.getIntegerDefault(1, "attStr"), "[configurer.xml] ");
    }

    /**
     * Test the integer getter.
     */
    @Test
    public void testGetDouble()
    {
        assertEquals(1.0, configurer.getDouble("attInt"));
    }

    /**
     * Test the integer getter with default value.
     */
    @Test
    public void testGetDoubleDefault()
    {
        assertEquals(1.0, configurer.getDoubleDefault(2.0, "attInt"));

        assertEquals(2.0, configurer.getDoubleDefault(2.0, "void"));
    }

    /**
     * Test the double getter invalid value.
     */
    @Test
    public void testGetDoubleInvalid()
    {
        assertThrows(() -> configurer.getDouble("attStr"), "[configurer.xml] ");
    }

    /**
     * Test the double getter invalid value.
     */
    @Test
    public void testGetDoubleDefaultInvalid()
    {
        assertThrows(() -> configurer.getDoubleDefault(1.0, "attStr"), "[configurer.xml] ");
    }

    /**
     * Test the text getter.
     */
    @Test
    public void testGetText()
    {
        assertEquals(Accessible.class.getName(), configurer.getText(Accessible.class.getSimpleName()));
    }

    /**
     * Test the text getter with default value.
     */
    @Test
    public void testGetTextDefault()
    {
        assertEquals(Accessible.class.getName(),
                     configurer.getTextDefault("default", Accessible.class.getSimpleName()));

        assertEquals("default", configurer.getTextDefault("default", "void"));
    }

    /**
     * Test get node not found.
     */
    @Test
    public void testNodeNotFound()
    {
        assertThrows(() -> configurer.getText("void", "null"), "[configurer.xml] ");
    }

    /**
     * Test the get implementation with default constructor.
     */
    @Test
    public void testGetImplementationDefault()
    {
        final Accessible impl = configurer.getImplementation(Accessible.class, Accessible.class.getSimpleName());

        assertTrue(impl.created);
    }

    /**
     * Test the get implementation with not accessible constructor.
     */
    @Test
    public void testGetImplementationNotAccessible()
    {
        final NotAccessible impl = configurer.getImplementation(NotAccessible.class,
                                                                NotAccessible.class.getSimpleName());
        assertTrue(impl.created);
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
        assertTrue(impl.created);
    }

    /**
     * Test the get implementation with custom constructor using invalid argument.
     */
    @Test
    public void testGetImplementationCustomInvalidArgument()
    {
        assertCause(() -> configurer.getImplementation(Custom.class,
                                                       Boolean.class,
                                                       Constant.EMPTY_STRING,
                                                       Custom.class.getSimpleName()),
                    IllegalArgumentException.class);
    }

    /**
     * Test the get implementation using abstract class.
     */
    @Test
    public void testGetImplementationCustomAbstract()
    {
        assertCause(() -> configurer.getImplementation(Abstract.class,
                                                       UtilReflection.getParamTypes(Boolean.TRUE),
                                                       Arrays.asList(Boolean.TRUE),
                                                       Abstract.class.getSimpleName()),
                    InstantiationException.class);
    }

    /**
     * Test the get implementation using unknown class.
     */
    @Test
    public void testGetImplementationUnknown()
    {
        assertCause(() -> configurer.getImplementation(Object.class, "unknown"), ClassNotFoundException.class);
    }

    /**
     * Test the get implementation using no compatible constructor.
     */
    @Test
    public void testGetImplementationNoCompatibleConstructor()
    {
        assertCause(() -> configurer.getImplementation(Accessible.class,
                                                       UtilReflection.getParamTypes(Boolean.TRUE),
                                                       Arrays.asList(Boolean.TRUE),
                                                       Accessible.class.getSimpleName()),
                    NoSuchMethodException.class);
    }

    /**
     * Test the get implementation using constructor throwing an exception.
     */
    @Test
    public void testGetImplementationConstructorThrows()
    {
        assertCause(() -> configurer.getImplementation(Throws.class, Throws.class.getSimpleName()),
                    InvocationTargetException.class);
    }

    /**
     * Test has node.
     */
    @Test
    public void testHasNode()
    {
        assertFalse(configurer.hasNode("void"));
        assertTrue(configurer.hasNode("unknown"));
        assertFalse(configurer.hasNode("void", "unknown"));
    }

    /**
     * Test the save.
     */
    @Test
    public void testSave()
    {
        final Media media = createConfig("save.xml");
        final Configurer configurer = new Configurer(media);
        final Xml root = configurer.getRoot();
        root.setText("save");
        configurer.save();

        assertEquals("save", new Configurer(media).getText());
        assertTrue(media.getFile().delete());
    }

    /**
     * Mock class.
     */
    static final class Accessible
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
    static final class NotAccessible
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
    static final class Custom
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
            assertNotNull(value);
        }
    }

    /**
     * Mock class.
     */
    static final class Throws
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
