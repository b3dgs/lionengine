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
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test {@link JsonValue}, {@link JsonObject} and {@link JsonArray} (REQ-DEF-001, REQ-DEF-002).
 */
final class JsonValueTest
{
    /** Source name of the test documents. */
    private static final String SOURCE = "values.json";
    /** Document with one value of each kind. */
    private static final JsonObject VALUES = new JsonReader().read("""
        {
          "text": "hello",
          "integer": 42,
          "big": 3000000000,
          "real": 0.5,
          "flag": true,
          "nothing": null,
          "list": [1, 2],
          "map": {"a": 1}
        }""", SOURCE).asObject();

    /**
     * Wrong-kind accessors with the expected message start.
     *
     * @return The test cases.
     */
    static Stream<Arguments> typeErrors()
    {
        return Stream.of(Arguments.of((Executable) () -> VALUES.get("integer").asString(),
                                      "Expected a string, found an integer",
                                      "$.integer"),
                         Arguments.of((Executable) () -> VALUES.get("real").asLong(),
                                      "Expected an integer, found a real number",
                                      "$.real"),
                         Arguments.of((Executable) () -> VALUES.get("real").asInt(),
                                      "Expected an integer, found a real number",
                                      "$.real"),
                         Arguments.of((Executable) () -> VALUES.get("text").asInt(),
                                      "Expected an integer, found a string",
                                      "$.text"),
                         Arguments.of((Executable) () -> VALUES.get("text").asLong(),
                                      "Expected an integer, found a string",
                                      "$.text"),
                         Arguments.of((Executable) () -> VALUES.get("flag").asDouble(),
                                      "Expected a number, found a boolean",
                                      "$.flag"),
                         Arguments.of((Executable) () -> VALUES.get("nothing").asBoolean(),
                                      "Expected a boolean, found null",
                                      "$.nothing"),
                         Arguments.of((Executable) () -> VALUES.get("list").asObject(),
                                      "Expected an object, found an array",
                                      "$.list"),
                         Arguments.of((Executable) () -> VALUES.get("map").asArray(),
                                      "Expected an array, found an object",
                                      "$.map"),
                         Arguments.of((Executable) () -> VALUES.get("list").asArray().get(1).asString(),
                                      "Expected a string, found an integer",
                                      "$.list[1]"),
                         Arguments.of((Executable) () -> VALUES.get("big").asInt(),
                                      "Integer outside the int range",
                                      "$.big"));
    }

    /**
     * A wrong-kind accessor fails with a message naming both kinds, located at the value.
     *
     * @param access The accessor call.
     * @param message The expected message start.
     * @param path The expected path.
     */
    @ParameterizedTest
    @MethodSource("typeErrors")
    void testTypeError(Executable access, String message, String path)
    {
        final DefinitionException error = assertThrows(DefinitionException.class, access);

        assertEquals(path, error.getPath());
        assertEquals(SOURCE, error.getSource());
        assertTrue(error.getMessage().startsWith(message + " (" + SOURCE + ":"), error.getMessage());
    }

    /**
     * Accessors of the matching kind return the value; an integer converts to a {@code double}.
     */
    @Test
    void testAccessors()
    {
        assertAll(() -> assertEquals("hello", VALUES.get("text").asString()),
                  () -> assertEquals(42, VALUES.get("integer").asInt()),
                  () -> assertEquals(42L, VALUES.get("integer").asLong()),
                  () -> assertEquals(42.0, VALUES.get("integer").asDouble()),
                  () -> assertEquals(3_000_000_000L, VALUES.get("big").asLong()),
                  () -> assertEquals(0.5, VALUES.get("real").asDouble()),
                  () -> assertTrue(VALUES.get("flag").asBoolean()),
                  () -> assertTrue(VALUES.get("nothing").isNull()),
                  () -> assertFalse(VALUES.get("flag").isNull()),
                  () -> assertSame(VALUES, VALUES.asObject()),
                  () -> assertSame(VALUES.get("list"), VALUES.get("list").asArray()));
    }

    /**
     * Kinds of the values.
     */
    @Test
    void testKinds()
    {
        assertAll(() -> assertEquals(JsonValue.Kind.OBJECT, VALUES.getKind()),
                  () -> assertEquals(JsonValue.Kind.STRING, VALUES.get("text").getKind()),
                  () -> assertEquals(JsonValue.Kind.LONG, VALUES.get("integer").getKind()),
                  () -> assertEquals(JsonValue.Kind.DOUBLE, VALUES.get("real").getKind()),
                  () -> assertEquals(JsonValue.Kind.BOOLEAN, VALUES.get("flag").getKind()),
                  () -> assertEquals(JsonValue.Kind.NULL, VALUES.get("nothing").getKind()),
                  () -> assertEquals(JsonValue.Kind.ARRAY, VALUES.get("list").getKind()));
    }

    /**
     * Object members are read by name or by index in document order.
     */
    @Test
    void testObject()
    {
        assertAll(() -> assertEquals(8, VALUES.size()),
                  () -> assertEquals("text", VALUES.getKey(0)),
                  () -> assertEquals("map", VALUES.getKey(7)),
                  () -> assertSame(VALUES.get("map"), VALUES.getValue(7)),
                  () -> assertTrue(VALUES.has("nothing")),
                  () -> assertFalse(VALUES.has("missing")),
                  () -> assertNull(VALUES.get("missing")),
                  () -> assertEquals(-1, VALUES.indexOf("missing")),
                  () -> assertEquals(2, VALUES.get("list").asArray().size()));
    }

    /**
     * Paths name members with a dot when they are identifiers, with quotes otherwise.
     */
    @Test
    void testPaths()
    {
        final JsonObject root = new JsonReader().read("""
            {"a": {"b_2": [0, {"lionengine:movable": {"it's": 1, "back\\\\slash": 2, "1st": 3, "": 4}}]}}""",
                                                      SOURCE)
                                                .asObject();
        final JsonObject traits = root.get("a").asObject().get("b_2").asArray().get(1).asObject();
        final JsonObject movable = traits.get("lionengine:movable").asObject();

        assertAll(() -> assertEquals("$", root.getPath()),
                  () -> assertEquals("$.a.b_2[1]", traits.getPath()),
                  () -> assertEquals("$.a.b_2[1]['lionengine:movable']", movable.getPath()),
                  () -> assertEquals("$.a.b_2[1]['lionengine:movable']['it\\'s']", movable.get("it's").getPath()),
                  () -> assertEquals("$.a.b_2[1]['lionengine:movable']['back\\\\slash']",
                                     movable.get("back\\slash").getPath()),
                  () -> assertEquals("$.a.b_2[1]['lionengine:movable']['1st']", movable.get("1st").getPath()),
                  () -> assertEquals("$.a.b_2[1]['lionengine:movable']['']", movable.get("").getPath()));
    }

    /**
     * Values know where they start.
     */
    @Test
    void testLocation()
    {
        final JsonValue list = VALUES.get("list");

        assertEquals(SOURCE, list.getSource());
        assertEquals(8, list.getLine());
        assertEquals(11, list.getColumn());
        assertEquals(15, list.asArray().get(1).getColumn());
    }
}
