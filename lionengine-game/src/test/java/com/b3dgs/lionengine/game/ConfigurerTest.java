/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;

/**
 * Test {@link Configurer}.
 */
final class ConfigurerTest
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
        XmlReader.clearCache();
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
        root.writeByte("attByte", (byte) 2);
        root.writeChar("attChar", 'c');
        root.writeShort("attShort", (short) 2);
        root.writeInteger("attInt", 1);
        root.writeLong("attLong", 1L);
        root.writeFloat("attFloat", 1.0F);
        root.writeDouble("attDouble", 1.0);

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
    void testGetRoot()
    {
        final Xml root = configurer.getRoot();

        assertNotNull(root);
        assertEquals("root", root.getNodeName());
    }

    /**
     * Test the path getter.
     */
    @Test
    void testGetPath()
    {
        assertEquals(config.getFile().getParent(), configurer.getPath());
    }

    /**
     * Test the media getter.
     */
    @Test
    void testGetMedia()
    {
        assertEquals(config, configurer.getMedia());
    }

    /**
     * Test the string getter.
     */
    @Test
    void testGetString()
    {
        assertEquals("string", configurer.getString("attStr"));
    }

    /**
     * Test the string getter with default value.
     */
    @Test
    void testGetStringDefault()
    {
        assertEquals("string", configurer.getStringDefault("default", "attStr"));
        assertEquals("default", configurer.getStringDefault("default", "void"));
        assertEquals("default", configurer.getStringDefault("default", "void", "none"));
    }

    /**
     * Test the string optional getter.
     */
    @Test
    void testGetStringOptional()
    {
        assertEquals(Optional.of("string"), configurer.getStringOptional("attStr"));
        assertEquals(Optional.empty(), configurer.getStringOptional("void"));
        assertEquals(Optional.empty(), configurer.getStringOptional("void", "none"));
    }

    /**
     * Test the boolean getter.
     */
    @Test
    void testGetBoolean()
    {
        assertFalse(configurer.getBoolean("attStr"));
    }

    /**
     * Test the boolean getter with default value.
     */
    @Test
    void testGetBooleanDefault()
    {
        assertFalse(configurer.getBoolean(true, "attStr"));
        assertTrue(configurer.getBoolean(true, "void"));
    }

    /**
     * Test the boolean getter with optional.
     */
    @Test
    void testGetBooleanOptional()
    {
        assertEquals(Optional.of(Boolean.FALSE), configurer.getBooleanOptional("attStr"));
        assertEquals(Optional.empty(), configurer.getBooleanOptional("void"));
        assertEquals(Optional.empty(), configurer.getBooleanOptional("void", "none"));
    }

    /**
     * Test the byte getter.
     */
    @Test
    void testGetByte()
    {
        assertEquals(2, configurer.getByte("attByte"));
    }

    /**
     * Test the byte getter with default value.
     */
    @Test
    void testGetByteDefault()
    {
        assertEquals(2, configurer.getByte((byte) 2, "attByte"));
        assertEquals(3, configurer.getByte((byte) 3, "void"));
    }

    /**
     * Test the byte getter invalid value.
     */
    @Test
    void testGetByteInvalid()
    {
        assertThrows(() -> configurer.getByte("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the byte getter invalid value.
     */
    @Test
    void testGetByteDefaultInvalid()
    {
        assertThrows(() -> configurer.getByte((byte) 1, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the char getter.
     */
    @Test
    void testGetChar()
    {
        assertEquals('c', configurer.getChar("attChar"));
    }

    /**
     * Test the char getter with default value.
     */
    @Test
    void testGetCharDefault()
    {
        assertEquals('c', configurer.getChar('c', "attChar"));
        assertEquals('d', configurer.getChar('d', "void"));
    }

    /**
     * Test the char getter invalid value.
     */
    @Test
    void testGetCharInvalid()
    {
        assertThrows(() -> configurer.getInteger("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the char getter invalid value.
     */
    @Test
    void testGetCharDefaultInvalid()
    {
        assertThrows(() -> configurer.getInteger(1, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the short getter.
     */
    @Test
    void testGetShort()
    {
        assertEquals(2, configurer.getShort("attShort"));
    }

    /**
     * Test the short getter with default value.
     */
    @Test
    void testGetShortDefault()
    {
        assertEquals(2, configurer.getShort((short) 2, "attShort"));
        assertEquals(3, configurer.getShort((short) 3, "void"));
    }

    /**
     * Test the short getter invalid value.
     */
    @Test
    void testGetShortInvalid()
    {
        assertThrows(() -> configurer.getShort("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the short getter invalid value.
     */
    @Test
    void testGetShortDefaultInvalid()
    {
        assertThrows(() -> configurer.getShort((short) 1, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the integer getter.
     */
    @Test
    void testGetInteger()
    {
        assertEquals(1, configurer.getInteger("attInt"));
    }

    /**
     * Test the integer getter with default value.
     */
    @Test
    void testGetIntegerDefault()
    {
        assertEquals(1, configurer.getInteger(2, "attInt"));
        assertEquals(2, configurer.getInteger(2, "void"));
    }

    /**
     * Test the integer getter invalid value.
     */
    @Test
    void testGetIntegerInvalid()
    {
        assertThrows(() -> configurer.getInteger("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the integer getter invalid value.
     */
    @Test
    void testGetIntegerDefaultInvalid()
    {
        assertThrows(() -> configurer.getInteger(1, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the integer getter with optional.
     */
    @Test
    void testGetIntegerOptional()
    {
        assertEquals(OptionalInt.of(1), configurer.getIntegerOptional("attInt"));
        assertEquals(OptionalInt.empty(), configurer.getIntegerOptional("void"));
        assertEquals(OptionalInt.empty(), configurer.getIntegerOptional("void", "none"));
    }

    /**
     * Test the long getter.
     */
    @Test
    void testGetLong()
    {
        assertEquals(1, configurer.getLong("attLong"));
    }

    /**
     * Test the long getter with default value.
     */
    @Test
    void testGetLongDefault()
    {
        assertEquals(1, configurer.getLong(2, "attLong"));
        assertEquals(2, configurer.getLong(2, "void"));
    }

    /**
     * Test the long getter invalid value.
     */
    @Test
    void testGetLongInvalid()
    {
        assertThrows(() -> configurer.getLong("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the long getter invalid value.
     */
    @Test
    void testGetLongDefaultInvalid()
    {
        assertThrows(() -> configurer.getLong(1, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the long getter with optional.
     */
    @Test
    void testGetLongOptional()
    {
        assertEquals(OptionalLong.of(1), configurer.getLongOptional("attLong"));
        assertEquals(OptionalLong.empty(), configurer.getLongOptional("void"));
        assertEquals(OptionalLong.empty(), configurer.getLongOptional("void", "none"));
    }

    /**
     * Test the float getter.
     */
    @Test
    void testGetFloat()
    {
        assertEquals(1.0F, configurer.getFloat("attFloat"));
    }

    /**
     * Test the float getter with default value.
     */
    @Test
    void testGetFloatDefault()
    {
        assertEquals(1, configurer.getFloat(2.0F, "attFloat"));
        assertEquals(2, configurer.getFloat(2.0F, "void"));
    }

    /**
     * Test the float getter invalid value.
     */
    @Test
    void testGetFloatInvalid()
    {
        assertThrows(() -> configurer.getFloat("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the float getter invalid value.
     */
    @Test
    void testGetFloatDefaultInvalid()
    {
        assertThrows(() -> configurer.getFloat(1.0F, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the integer getter.
     */
    @Test
    void testGetDouble()
    {
        assertEquals(1.0, configurer.getDouble("attDouble"));
    }

    /**
     * Test the integer getter with default value.
     */
    @Test
    void testGetDoubleDefault()
    {
        assertEquals(1.0, configurer.getDouble(2.0, "attDouble"));
        assertEquals(2.0, configurer.getDouble(2.0, "void"));
    }

    /**
     * Test the double getter invalid value.
     */
    @Test
    void testGetDoubleInvalid()
    {
        assertThrows(() -> configurer.getDouble("attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the double getter invalid value.
     */
    @Test
    void testGetDoubleDefaultInvalid()
    {
        assertThrows(() -> configurer.getDouble(1.0, "attStr"), XmlReader.ERROR_ATTRIBUTE + "attStr");
    }

    /**
     * Test the double getter with optional.
     */
    @Test
    void testGetDoubleOptional()
    {
        assertEquals(OptionalDouble.of(1.0), configurer.getDoubleOptional("attDouble"));
        assertEquals(OptionalDouble.empty(), configurer.getDoubleOptional("void"));
        assertEquals(OptionalDouble.empty(), configurer.getDoubleOptional("void", "none"));
    }

    /**
     * Test the text getter.
     */
    @Test
    void testGetText()
    {
        assertEquals(Accessible.class.getName(), configurer.getText(Accessible.class.getSimpleName()));
    }

    /**
     * Test the text getter with default value.
     */
    @Test
    void testGetTextDefault()
    {
        assertEquals(Accessible.class.getName(),
                     configurer.getTextDefault("default", Accessible.class.getSimpleName()));

        assertEquals("default", configurer.getTextDefault("default", "void"));
    }

    /**
     * Test get node not found.
     */
    @Test
    void testNodeNotFound()
    {
        assertThrows(() -> configurer.getText("void", "null"), XmlReader.ERROR_NODE + "[void, null]");
    }

    /**
     * Test the get implementation with default constructor.
     */
    @Test
    void testGetImplementationDefault()
    {
        final Accessible impl = configurer.getImplementation(Accessible.class, Accessible.class.getSimpleName());

        assertTrue(impl.created);
    }

    /**
     * Test the get implementation with not accessible constructor.
     */
    @Test
    void testGetImplementationNotAccessible()
    {
        assertThrows(() -> configurer.getImplementation(NotAccessible.class, NotAccessible.class.getSimpleName()),
                     "Class constructor error: " + NotAccessible.class.getName());
    }

    /**
     * Test the get implementation with custom constructor.
     */
    @Test
    void testGetImplementationCustom()
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
    void testGetImplementationCustomInvalidArgument()
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
    void testGetImplementationCustomAbstract()
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
    void testGetImplementationUnknown()
    {
        assertCause(() -> configurer.getImplementation(Object.class, "unknown"), ClassNotFoundException.class);
    }

    /**
     * Test the get implementation using no compatible constructor.
     */
    @Test
    void testGetImplementationNoCompatibleConstructor()
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
    void testGetImplementationConstructorThrows()
    {
        assertCause(() -> configurer.getImplementation(Throws.class, Throws.class.getSimpleName()),
                    InvocationTargetException.class);
    }

    /**
     * Test has node.
     */
    @Test
    void testHasNode()
    {
        assertFalse(configurer.hasNode("void"));
        assertTrue(configurer.hasNode("unknown"));
        assertFalse(configurer.hasNode("void", "unknown"));
    }

    /**
     * Test the save.
     */
    @Test
    void testSave()
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
    public static final class Accessible
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
    public static final class NotAccessible
    {
        private NotAccessible()
        {
            super();
        }
    }

    /**
     * Mock class.
     */
    public static final class Custom
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
    public abstract static class Abstract
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
    public static final class Throws
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
