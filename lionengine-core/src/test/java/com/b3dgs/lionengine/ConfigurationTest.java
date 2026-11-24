/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test {@link Configuration} (REQ-DEF-002, REQ-DEF-003).
 */
final class ConfigurationTest
{
    /** Source name of the test documents. */
    private static final String SOURCE = "config.json";
    /** Parameters with one member of each kind. */
    private static final String PARAMETERS = """
        {
          "count": 3,
          "big": 5000000000,
          "speed": 2.5,
          "steps": 4,
          "solid": true,
          "name": "hero",
          "size": {"width": 16, "height": 32},
          "frames": [0, 1, 2],
          "nothing": null,
          "comment": "Not read, never reported."
        }""";

    /**
     * Creates a configuration of a document.
     *
     * @param json The document, an object.
     * @return The configuration.
     */
    private static Configuration configuration(String json)
    {
        return new Configuration(new JsonReader().read(json, SOURCE).asObject());
    }

    /**
     * Getters of a wrong type with the expected message.
     *
     * @return The test cases.
     */
    static Stream<Arguments> typeErrors()
    {
        return Stream.of(Arguments.of("count", "speed", "Expected an integer, found a real number"),
                         Arguments.of("long", "speed", "Expected an integer, found a real number"),
                         Arguments.of("double", "name", "Expected a number, found a string"),
                         Arguments.of("boolean", "count", "Expected a boolean, found an integer"),
                         Arguments.of("string", "nothing", "Expected a string, found null"),
                         Arguments.of("object", "frames", "Expected an object, found an array"),
                         Arguments.of("array", "size", "Expected an array, found an object"),
                         Arguments.of("count", "big", JsonNumber.ERROR_INT_RANGE));
    }

    /**
     * Calls the getter of a type on a member, without and with a default.
     *
     * @param configuration The configuration.
     * @param type The getter type.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void get(Configuration configuration, String type, String name, boolean withDefault)
    {
        switch (type)
        {
            case "count" -> callInt(configuration, name, withDefault);
            case "long" -> callLong(configuration, name, withDefault);
            case "double" -> callDouble(configuration, name, withDefault);
            case "boolean" -> callBoolean(configuration, name, withDefault);
            case "string" -> callString(configuration, name, withDefault);
            case "object" -> callObject(configuration, name, withDefault);
            default -> callArray(configuration, name, withDefault);
        }
    }

    /**
     * Calls an {@code int} getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callInt(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getInt(name, 0);
        }
        else
        {
            configuration.getInt(name);
        }
    }

    /**
     * Calls a {@code long} getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callLong(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getLong(name, 0L);
        }
        else
        {
            configuration.getLong(name);
        }
    }

    /**
     * Calls a {@code double} getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callDouble(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getDouble(name, 0.0);
        }
        else
        {
            configuration.getDouble(name);
        }
    }

    /**
     * Calls a {@code boolean} getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callBoolean(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getBoolean(name, false);
        }
        else
        {
            configuration.getBoolean(name);
        }
    }

    /**
     * Calls a string getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callString(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getString(name, null);
        }
        else
        {
            configuration.getString(name);
        }
    }

    /**
     * Calls an object getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callObject(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getObject(name, null);
        }
        else
        {
            configuration.getObject(name);
        }
    }

    /**
     * Calls an array getter.
     *
     * @param configuration The configuration.
     * @param name The member name.
     * @param withDefault {@code true} to call the getter with a default.
     */
    private static void callArray(Configuration configuration, String name, boolean withDefault)
    {
        if (withDefault)
        {
            configuration.getArray(name, null);
        }
        else
        {
            configuration.getArray(name);
        }
    }

    /**
     * Required getters return present values; an integer converts to a {@code double}.
     */
    @Test
    void testRequired()
    {
        final Configuration configuration = configuration(PARAMETERS);

        assertAll(() -> assertEquals(3, configuration.getInt("count")),
                  () -> assertEquals(5_000_000_000L, configuration.getLong("big")),
                  () -> assertEquals(2.5, configuration.getDouble("speed")),
                  () -> assertEquals(4.0, configuration.getDouble("steps")),
                  () -> assertTrue(configuration.getBoolean("solid")),
                  () -> assertEquals("hero", configuration.getString("name")),
                  () -> assertEquals(32, configuration.getObject("size").getInt("height")),
                  () -> assertEquals(3, configuration.getArray("frames").size()));
    }

    /**
     * Getters with a default return the present value, or the default when the member is missing.
     */
    @Test
    void testDefaults()
    {
        final Configuration configuration = configuration(PARAMETERS);
        final Configuration fallback = configuration("{}");
        final JsonArray empty = new JsonReader().read("[]", SOURCE).asArray();

        assertAll(() -> assertEquals(3, configuration.getInt("count", 7)),
                  () -> assertEquals(7, configuration.getInt("missing", 7)),
                  () -> assertEquals(5_000_000_000L, configuration.getLong("big", 1L)),
                  () -> assertEquals(1L, configuration.getLong("missing", 1L)),
                  () -> assertEquals(2.5, configuration.getDouble("speed", 1.0)),
                  () -> assertEquals(1.0, configuration.getDouble("missing", 1.0)),
                  () -> assertTrue(configuration.getBoolean("solid", false)),
                  () -> assertTrue(configuration.getBoolean("missing", true)),
                  () -> assertEquals("hero", configuration.getString("name", "none")),
                  () -> assertNull(configuration.getString("missing", null)),
                  () -> assertEquals(16, configuration.getObject("size", null).getInt("width")),
                  () -> assertSame(fallback, configuration.getObject("missing", fallback)),
                  () -> assertEquals(3, configuration.getArray("frames", null).size()),
                  () -> assertSame(empty, configuration.getArray("missing", empty)));
    }

    /**
     * A getter of the wrong type fails at the value, naming the parameter, with and without a default.
     *
     * @param type The getter type.
     * @param name The member name.
     * @param message The expected description.
     */
    @ParameterizedTest
    @MethodSource("typeErrors")
    void testTypeError(String type, String name, String message)
    {
        final Configuration configuration = configuration(PARAMETERS);
        final DefinitionException required = assertThrows(DefinitionException.class,
                                                          () -> get(configuration, type, name, false));
        final DefinitionException optional = assertThrows(DefinitionException.class,
                                                          () -> get(configuration, type, name, true));

        assertAll(() -> assertEquals(name, required.getParameter()),
                  () -> assertNull(required.getId()),
                  () -> assertEquals("$." + name, required.getPath()),
                  () -> assertTrue(required.getMessage().startsWith(message + " (config.json:"), required.getMessage()),
                  () -> assertEquals(required.getMessage(), optional.getMessage()));
    }

    /**
     * A missing required member fails at the object, naming the parameter.
     */
    @Test
    void testMissing()
    {
        final Configuration configuration = configuration("{\"size\": {\"width\": 16}}").getObject("size");
        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> configuration.getInt("height"));

        assertEquals("Missing value (config.json:1:10, $.size, height)", error.getMessage());
    }

    /**
     * Every member must be read, except the comment; nested objects are checked with their parent.
     */
    @Test
    void testStrictFields()
    {
        final Configuration configuration = configuration("""
            {"count": 3, "size": {"width": 16, "height": 32}, "comment": "x"}""");
        configuration.getInt("count");
        configuration.getObject("size").getInt("width");

        final DefinitionException nested = assertThrows(DefinitionException.class, configuration::checkAllRead);
        assertEquals("Unknown field (config.json:1:46, $.size.height, height)", nested.getMessage());

        assertEquals(32, configuration.getObject("size").getInt("height"));
        configuration.checkAllRead();
    }

    /**
     * A member never read is reported first, in document order, at its value.
     */
    @Test
    void testUnreadMember()
    {
        final Configuration configuration = configuration("{\"comment\": \"x\", \"speed\": 1, \"spped\": 2}");
        configuration.getInt("speed");

        final DefinitionException error = assertThrows(DefinitionException.class, configuration::checkAllRead);

        assertEquals("Unknown field (config.json:1:39, $.spped, spped)", error.getMessage());
    }

    /**
     * When everything is read, the check passes.
     */
    @Test
    void testAllRead()
    {
        final Configuration configuration = configuration("{\"comment\": \"x\", \"a\": {\"b\": 1}}");
        assertEquals(1, configuration.getObject("a").getInt("b"));
        assertSame(configuration.getObject("a"), configuration.getObject("a", null));

        configuration.checkAllRead();
        assertFalse(configuration.getJson().has("missing"));
    }

    /**
     * The header of an engine definition file: format and version.
     */
    @Test
    void testCheckFormat()
    {
        final String header = "{\"format\": \"lionengine.sheet\", \"version\": 2}";

        assertEquals(2, configuration(header).checkFormat("lionengine.sheet", 2));
        assertEquals(2, configuration(header).checkFormat("lionengine.sheet", 3));

        final Configuration otherKind = configuration(header);
        final DefinitionException format = assertThrows(DefinitionException.class,
                                                        () -> otherKind.checkFormat("lionengine.map", 1));
        assertEquals("Unknown format lionengine.sheet, expected lionengine.map (config.json:1:12, $.format, format)",
                     format.getMessage());

        final Configuration newer = configuration(header);
        final DefinitionException version = assertThrows(DefinitionException.class,
                                                         () -> newer.checkFormat("lionengine.sheet", 1));
        assertEquals("Unsupported version 2, supported from 1 to 1 (config.json:1:43, $.version, version)",
                     version.getMessage());
    }

    /**
     * A version below 1 and a missing header fail.
     */
    @Test
    void testInvalidHeader()
    {
        final Configuration zero = configuration("{\"format\": \"lionengine.sheet\", \"version\": 0}");
        assertThrows(DefinitionException.class, () -> zero.checkFormat("lionengine.sheet", 1));

        final Configuration missing = configuration("{\"format\": \"lionengine.sheet\"}");
        assertThrows(DefinitionException.class, () -> missing.checkFormat("lionengine.sheet", 1));

        final Configuration none = configuration("{}");
        assertThrows(DefinitionException.class, () -> none.checkFormat("lionengine.sheet", 1));
    }

    /**
     * Invalid format checks are rejected.
     */
    @Test
    void testInvalidFormatCheck()
    {
        final Configuration configuration = configuration("{}");

        assertThrows(IllegalArgumentException.class, () -> configuration.checkFormat(null, 1));
        assertThrows(IllegalArgumentException.class, () -> configuration.checkFormat("lionengine.sheet", 0));
    }

    /**
     * A factory-specific error is located at the member, or at the object when the member is missing.
     */
    @Test
    void testCreateError()
    {
        final Configuration configuration = configuration("{\"speed\": -1}");

        assertEquals("Negative speed (config.json:1:11, $.speed, speed)",
                     configuration.createError("speed", "Negative speed").getMessage());
        assertEquals("Missing gravity (config.json:1:1, $, gravity)",
                     configuration.createError("gravity", "Missing gravity").getMessage());
    }

    /**
     * A {@code null} object is rejected.
     */
    @Test
    void testNullObject()
    {
        assertThrows(IllegalArgumentException.class, () -> new Configuration(null));
    }
}
