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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test {@link JsonReader} (REQ-DEF-001, REQ-DEF-002).
 */
final class JsonReaderTest
{
    /** Source name of the test documents. */
    private static final String SOURCE = "test.json";

    /**
     * Invalid documents with the expected error message, line, column and path.
     *
     * @return The test cases.
     */
    static Stream<Arguments> errors()
    {
        return Stream.of(Arguments.of("", JsonParser.ERROR_EMPTY, 1, 1, "$"),
                         Arguments.of("  \n ", JsonParser.ERROR_EMPTY, 2, 2, "$"),
                         Arguments.of("{} x", JsonParser.ERROR_AFTER_ROOT, 1, 4, "$"),
                         Arguments.of("{\"a\":1 // note\n}", JsonParser.ERROR_COMMENT, 1, 8, "$"),
                         Arguments.of("/* note */ {}", JsonParser.ERROR_COMMENT, 1, 1, "$"),
                         Arguments.of("[1,]", JsonParser.ERROR_TRAILING_COMMA, 1, 4, "$"),
                         Arguments.of("{\"a\":1, }", JsonParser.ERROR_TRAILING_COMMA, 1, 9, "$"),
                         Arguments.of("{\"a\":1,\"a\":2}", JsonParser.ERROR_DUPLICATE_KEY, 1, 8, "$.a"),
                         Arguments.of("{\n  \"a\": 1,\n  \"a\": 2\n}", JsonParser.ERROR_DUPLICATE_KEY, 3, 3, "$.a"),
                         Arguments.of("[NaN]", JsonParser.ERROR_NOT_FINITE, 1, 2, "$[0]"),
                         Arguments.of("[Infinity]", JsonParser.ERROR_NOT_FINITE, 1, 2, "$[0]"),
                         Arguments.of("[-Infinity]", JsonParser.ERROR_NOT_FINITE, 1, 2, "$[0]"),
                         Arguments.of("[\"\\x\"]", JsonParser.ERROR_ESCAPE, 1, 3, "$[0]"),
                         Arguments.of("[\"\\u12G4\"]", JsonParser.ERROR_UNICODE_ESCAPE, 1, 3, "$[0]"),
                         Arguments.of("[\"\\u12\"]", JsonParser.ERROR_UNICODE_ESCAPE, 1, 3, "$[0]"),
                         Arguments.of("[\"\\u12", JsonParser.ERROR_UNICODE_ESCAPE, 1, 3, "$[0]"),
                         Arguments.of("[\"\\uD800\"]", JsonParser.ERROR_SURROGATE, 1, 3, "$[0]"),
                         Arguments.of("[\"\\uDC00\"]", JsonParser.ERROR_SURROGATE, 1, 3, "$[0]"),
                         Arguments.of("[\"\\uD800\\u0041\"]", JsonParser.ERROR_SURROGATE, 1, 3, "$[0]"),
                         Arguments.of("[\"a\tb\"]", JsonParser.ERROR_CONTROL, 1, 4, "$[0]"),
                         Arguments.of("[\"abc", JsonParser.ERROR_UNTERMINATED_STRING, 1, 6, "$[0]"),
                         Arguments.of("[\"abc\\", JsonParser.ERROR_UNTERMINATED_STRING, 1, 7, "$[0]"),
                         Arguments.of("[1", JsonParser.ERROR_UNTERMINATED_ARRAY, 1, 3, "$"),
                         Arguments.of("[1,", JsonParser.ERROR_UNTERMINATED_ARRAY, 1, 4, "$"),
                         Arguments.of("{\"a\":1", JsonParser.ERROR_UNTERMINATED_OBJECT, 1, 7, "$"),
                         Arguments.of("{\"a\"", JsonParser.ERROR_UNTERMINATED_OBJECT, 1, 5, "$.a"),
                         Arguments.of("{\"a\":", JsonParser.ERROR_UNTERMINATED_OBJECT, 1, 6, "$.a"),
                         Arguments.of("{", JsonParser.ERROR_UNTERMINATED_OBJECT, 1, 2, "$"),
                         Arguments.of("{1:2}", JsonParser.ERROR_KEY, 1, 2, "$"),
                         Arguments.of("{\"a\" 1}", JsonParser.ERROR_COLON, 1, 6, "$.a"),
                         Arguments.of("[1 2]", JsonParser.ERROR_ARRAY_SEPARATOR, 1, 4, "$"),
                         Arguments.of("{\"a\":1 \"b\":2}", JsonParser.ERROR_OBJECT_SEPARATOR, 1, 8, "$"),
                         Arguments.of("[01]", JsonParser.ERROR_LEADING_ZERO, 1, 2, "$[0]"),
                         Arguments.of("[-01]", JsonParser.ERROR_LEADING_ZERO, 1, 2, "$[0]"),
                         Arguments.of("[1.]", JsonParser.ERROR_NUMBER, 1, 2, "$[0]"),
                         Arguments.of("[1.e3]", JsonParser.ERROR_NUMBER, 1, 2, "$[0]"),
                         Arguments.of("[-]", JsonParser.ERROR_NUMBER, 1, 2, "$[0]"),
                         Arguments.of("-", JsonParser.ERROR_NUMBER, 1, 1, "$"),
                         Arguments.of("[1e]", JsonParser.ERROR_NUMBER, 1, 2, "$[0]"),
                         Arguments.of("[1E+]", JsonParser.ERROR_NUMBER, 1, 2, "$[0]"),
                         Arguments.of("[1e400]", JsonParser.ERROR_NUMBER_RANGE, 1, 2, "$[0]"),
                         Arguments.of("[.5]", JsonParser.ERROR_CHARACTER + "'.'", 1, 2, "$[0]"),
                         Arguments.of("[+1]", JsonParser.ERROR_CHARACTER + "'+'", 1, 2, "$[0]"),
                         Arguments.of("[\u00e9]", JsonParser.ERROR_CHARACTER + "U+00E9", 1, 2, "$[0]"),
                         Arguments.of("['a']", JsonParser.ERROR_CHARACTER + "'''", 1, 2, "$[0]"),
                         Arguments.of("[tru]", JsonParser.ERROR_LITERAL, 1, 2, "$[0]"),
                         Arguments.of("[True]", JsonParser.ERROR_LITERAL, 1, 2, "$[0]"),
                         Arguments.of("{\"a\":{\"b\":[1,{\"c\":x}]}}", JsonParser.ERROR_LITERAL, 1, 19, "$.a.b[1].c"),
                         Arguments.of("{\"lionengine:movable\":{\"maxSpeed\":x}}",
                                      JsonParser.ERROR_LITERAL,
                                      1,
                                      35,
                                      "$['lionengine:movable'].maxSpeed"),
                         Arguments.of("[\r\n1,\r\n]", JsonParser.ERROR_TRAILING_COMMA, 3, 1, "$"),
                         Arguments.of("[\r1,\r]", JsonParser.ERROR_TRAILING_COMMA, 3, 1, "$"),
                         Arguments.of("\uFEFF[1,]", JsonParser.ERROR_TRAILING_COMMA, 1, 4, "$"));
    }

    /**
     * Valid numbers with their expected kind and value.
     *
     * @return The test cases.
     */
    static Stream<Arguments> numbers()
    {
        return Stream.of(Arguments.of("0", JsonValue.Kind.LONG, Double.valueOf(0.0)),
                         Arguments.of("-0", JsonValue.Kind.LONG, Double.valueOf(0.0)),
                         Arguments.of("42", JsonValue.Kind.LONG, Double.valueOf(42.0)),
                         Arguments.of("-7", JsonValue.Kind.LONG, Double.valueOf(-7.0)),
                         Arguments.of("1.5", JsonValue.Kind.DOUBLE, Double.valueOf(1.5)),
                         Arguments.of("-0.25", JsonValue.Kind.DOUBLE, Double.valueOf(-0.25)),
                         Arguments.of("1e3", JsonValue.Kind.DOUBLE, Double.valueOf(1000.0)),
                         Arguments.of("2E-2", JsonValue.Kind.DOUBLE, Double.valueOf(0.02)),
                         Arguments.of("1.0e+2", JsonValue.Kind.DOUBLE, Double.valueOf(100.0)),
                         Arguments.of("9223372036854775808", JsonValue.Kind.DOUBLE, Double.valueOf(9.223372036854775808E18)),
                         Arguments.of("-9223372036854775809",
                                      JsonValue.Kind.DOUBLE,
                                      Double.valueOf(-9.223372036854775809E18)),
                         Arguments.of("1e-400", JsonValue.Kind.DOUBLE, Double.valueOf(0.0)));
    }

    /**
     * Reads a document from characters.
     *
     * @param json The document.
     * @return The root value.
     */
    private static JsonValue read(String json)
    {
        return new JsonReader().read(json, SOURCE);
    }

    /**
     * Each invalid document fails with its message, line, column and JSON path.
     *
     * @param json The document.
     * @param message The expected description.
     * @param line The expected line.
     * @param column The expected column.
     * @param path The expected path.
     */
    @ParameterizedTest
    @MethodSource("errors")
    void testError(String json, String message, int line, int column, String path)
    {
        final DefinitionException error = assertThrows(DefinitionException.class, () -> read(json));

        assertAll(() -> assertEquals(SOURCE, error.getSource()),
                  () -> assertEquals(line, error.getLine()),
                  () -> assertEquals(column, error.getColumn()),
                  () -> assertEquals(path, error.getPath()),
                  () -> assertEquals(message + " (" + SOURCE + ":" + line + ":" + column + ", " + path + ")",
                                     error.getMessage()));
    }

    /**
     * Numbers: an integer literal that fits a {@code long} is a {@code long}, any other number a {@code double}.
     *
     * @param json The number.
     * @param kind The expected kind.
     * @param value The expected value as a {@code double}.
     */
    @ParameterizedTest
    @MethodSource("numbers")
    void testNumber(String json, JsonValue.Kind kind, Double value)
    {
        final JsonValue number = read(json);

        assertEquals(kind, number.getKind());
        assertEquals(value.doubleValue(), number.asDouble());
    }

    /**
     * The {@code long} bounds are read exactly.
     */
    @Test
    void testLongBounds()
    {
        final JsonArray array = read("[9223372036854775807, -9223372036854775808]").asArray();

        assertEquals(Long.MAX_VALUE, array.get(0).asLong());
        assertEquals(Long.MIN_VALUE, array.get(1).asLong());
    }

    /**
     * Every escape is decoded, including a surrogate pair.
     */
    @Test
    void testEscapes()
    {
        final JsonValue text = read("\"\\\"\\\\\\/\\b\\f\\n\\r\\t\\u00e9\\u00C9\\uD83D\\uDE00 \"");

        assertEquals("\"\\/\b\f\n\r\t\u00e9\u00c9\ud83d\ude00 ", text.asString());
    }

    /**
     * Characters outside ASCII are read as is.
     */
    @Test
    void testUnicodeCharacters()
    {
        assertEquals("caf\u00e9 \ud83d\ude00", read("\"caf\u00e9 \ud83d\ude00\"").asString());
    }

    /**
     * A complete document with every kind of value, read from UTF-8 bytes with a byte-order mark.
     */
    @Test
    void testDocument()
    {
        final String json = "\uFEFF{\n"
                            + "  \"format\": \"lionengine.sheet\",\n"
                            + "  \"version\": 1,\n"
                            + "  \"scale\": 1.5,\n"
                            + "  \"visible\": true,\n"
                            + "  \"hidden\": false,\n"
                            + "  \"parent\": null,\n"
                            + "  \"frames\": [0, 1, [], {}],\n"
                            + "  \"empty\": {}\n"
                            + "}\n";
        final JsonObject root = new JsonReader().read(json.getBytes(StandardCharsets.UTF_8), SOURCE).asObject();

        assertAll(() -> assertEquals(8, root.size()),
                  () -> assertEquals("format", root.getKey(0)),
                  () -> assertEquals("empty", root.getKey(7)),
                  () -> assertEquals("lionengine.sheet", root.get("format").asString()),
                  () -> assertEquals(1, root.get("version").asInt()),
                  () -> assertEquals(1.5, root.getValue(2).asDouble()),
                  () -> assertTrue(root.get("visible").asBoolean()),
                  () -> assertFalse(root.get("hidden").asBoolean()),
                  () -> assertTrue(root.get("parent").isNull()),
                  () -> assertEquals(4, root.get("frames").asArray().size()),
                  () -> assertEquals(0, root.get("frames").asArray().get(2).asArray().size()),
                  () -> assertEquals(0, root.get("empty").asObject().size()),
                  () -> assertEquals(1, root.getLine()),
                  () -> assertEquals(1, root.getColumn()),
                  () -> assertEquals(3, root.get("version").getLine()),
                  () -> assertEquals(14, root.get("version").getColumn()));
    }

    /**
     * Malformed UTF-8 fails at the position of the first bad byte.
     */
    @Test
    void testInvalidUtf8()
    {
        final byte[] bytes = {'[', '\n', '"', 'a', (byte) 0xC3, '"', ']'};
        final JsonReader reader = new JsonReader();
        final DefinitionException error = assertThrows(DefinitionException.class, () -> reader.read(bytes, SOURCE));

        assertEquals(JsonReader.ERROR_ENCODING, error.getMessage().substring(0, JsonReader.ERROR_ENCODING.length()));
        assertEquals(2, error.getLine());
        assertEquals(3, error.getColumn());
    }

    /**
     * Malformed UTF-8 at the end of the document fails too.
     */
    @Test
    void testTruncatedUtf8()
    {
        final byte[] bytes = {'"', 'a', (byte) 0xE2, (byte) 0x82};
        final JsonReader reader = new JsonReader();
        final DefinitionException error = assertThrows(DefinitionException.class, () -> reader.read(bytes, SOURCE));

        assertEquals(1, error.getLine());
        assertEquals(3, error.getColumn());
    }

    /**
     * The nesting limit counts objects and arrays inside one another.
     */
    @Test
    void testDepthLimit()
    {
        final JsonReader reader = new JsonReader(JsonReader.DEFAULT_MAX_DOCUMENT_SIZE,
                                                 2,
                                                 JsonReader.DEFAULT_MAX_STRING_LENGTH);
        assertEquals(1, reader.read("[{\"a\":1}]", SOURCE).asArray().size());

        final DefinitionException error = assertThrows(DefinitionException.class,
                                                       () -> reader.read("[{\"a\":[]}]", SOURCE));
        assertEquals(JsonParser.ERROR_DEPTH + "2 (test.json:1:7, $[0].a)", error.getMessage());
    }

    /**
     * The default nesting limit is 64.
     */
    @Test
    void testDefaultDepthLimit()
    {
        final String inside = "[".repeat(JsonReader.DEFAULT_MAX_DEPTH) + "]".repeat(JsonReader.DEFAULT_MAX_DEPTH);
        assertEquals(1, read("[" + inside.substring(1, inside.length() - 1) + "]").asArray().size());

        assertThrows(DefinitionException.class, () -> read("[" + inside + "]"));
    }

    /**
     * The string length limit applies to values and member names, after decoding.
     */
    @Test
    void testStringLimit()
    {
        final JsonReader reader = new JsonReader(JsonReader.DEFAULT_MAX_DOCUMENT_SIZE,
                                                 JsonReader.DEFAULT_MAX_DEPTH,
                                                 3);
        assertEquals("a\u00e9c", reader.read("\"a\\u00e9c\"", SOURCE).asString());

        final DefinitionException value = assertThrows(DefinitionException.class,
                                                       () -> reader.read("[\"abcd\"]", SOURCE));
        assertEquals(JsonParser.ERROR_STRING_LENGTH + "3 characters (test.json:1:2, $[0])", value.getMessage());

        final DefinitionException key = assertThrows(DefinitionException.class,
                                                     () -> reader.read("{\"abcd\":1}", SOURCE));
        assertEquals(JsonParser.ERROR_STRING_LENGTH + "3 characters (test.json:1:2, $)", key.getMessage());
    }

    /**
     * The document size limit is in bytes for byte input and in characters for character input.
     */
    @Test
    void testDocumentLimit()
    {
        final JsonReader reader = new JsonReader(4, JsonReader.DEFAULT_MAX_DEPTH, JsonReader.DEFAULT_MAX_STRING_LENGTH);
        assertEquals(12, reader.read("[12]", SOURCE).asArray().get(0).asInt());

        final DefinitionException chars = assertThrows(DefinitionException.class, () -> reader.read("[1, 2]", SOURCE));
        assertEquals(JsonReader.ERROR_DOCUMENT_SIZE + "4 characters (test.json:0:0)", chars.getMessage());

        final byte[] bytes = "\"\u00e9\u00e9\"".getBytes(StandardCharsets.UTF_8);
        final DefinitionException utf8 = assertThrows(DefinitionException.class, () -> reader.read(bytes, SOURCE));
        assertEquals(JsonReader.ERROR_DOCUMENT_SIZE + "4 bytes (test.json:0:0)", utf8.getMessage());
    }

    /**
     * The default limits are those of ADR-0032.
     */
    @Test
    void testDefaultLimits()
    {
        final JsonReader reader = new JsonReader();

        assertEquals(16 * 1024 * 1024, reader.getMaxDocumentSize());
        assertEquals(64, reader.getMaxDepth());
        assertEquals(1024 * 1024, reader.getMaxStringLength());
    }

    /**
     * Limits must be positive.
     */
    @Test
    void testInvalidLimits()
    {
        assertThrows(IllegalArgumentException.class, () -> new JsonReader(0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new JsonReader(1, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new JsonReader(1, 1, -1));
    }

    /**
     * Input and source must not be {@code null}.
     */
    @Test
    void testNullArguments()
    {
        final JsonReader reader = new JsonReader();

        assertThrows(IllegalArgumentException.class, () -> reader.read((byte[]) null, SOURCE));
        assertThrows(IllegalArgumentException.class, () -> reader.read((CharSequence) null, SOURCE));
        assertThrows(IllegalArgumentException.class, () -> reader.read("{}", null));
    }
}
