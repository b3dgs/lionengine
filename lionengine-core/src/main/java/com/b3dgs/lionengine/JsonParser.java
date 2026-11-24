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

import java.util.Locale;

/**
 * Strict RFC 8259 parser of one document (ADR-0028, ADR-0032, REQ-DEF-001, REQ-DEF-002).
 *
 * <p>
 * Created by {@link JsonReader} for one read. Recursive descent over the decoded characters; the nesting limit
 * bounds the recursion. Line and column are tracked while skipping whitespace, the only place where a line break is
 * valid. The JSON path of the value being read is kept in a buffer for error messages.
 * </p>
 */
final class JsonParser
{
    /** Error: nothing but whitespace. */
    static final String ERROR_EMPTY = "Empty document";
    /** Error: something follows the root value. */
    static final String ERROR_AFTER_ROOT = "Content after the root value";
    /** Error: a comment. */
    static final String ERROR_COMMENT = "Comments are not allowed";
    /** Error: a comma before a closing bracket. */
    static final String ERROR_TRAILING_COMMA = "Trailing comma";
    /** Error: a member name used twice in one object. */
    static final String ERROR_DUPLICATE_KEY = "Duplicate key";
    /** Error: {@code NaN} or {@code Infinity}. */
    static final String ERROR_NOT_FINITE = "NaN and Infinity are not allowed";
    /** Error: an unknown escape sequence. */
    static final String ERROR_ESCAPE = "Invalid escape sequence";
    /** Error: a malformed {@code \}{@code u} escape. */
    static final String ERROR_UNICODE_ESCAPE = "Invalid Unicode escape";
    /** Error: a surrogate escape without its pair. */
    static final String ERROR_SURROGATE = "Unpaired surrogate in a Unicode escape";
    /** Error: a control character not escaped in a string. */
    static final String ERROR_CONTROL = "Unescaped control character in a string";
    /** Error: a string without closing quote. */
    static final String ERROR_UNTERMINATED_STRING = "Unterminated string";
    /** Error: an object without closing brace. */
    static final String ERROR_UNTERMINATED_OBJECT = "Unterminated object";
    /** Error: an array without closing bracket. */
    static final String ERROR_UNTERMINATED_ARRAY = "Unterminated array";
    /** Error: an object member that does not start with a name. */
    static final String ERROR_KEY = "Expected a member name";
    /** Error: a member name not followed by a colon. */
    static final String ERROR_COLON = "Expected ':'";
    /** Error: an object member not followed by a comma or a closing brace. */
    static final String ERROR_OBJECT_SEPARATOR = "Expected ',' or '}'";
    /** Error: an array element not followed by a comma or a closing bracket. */
    static final String ERROR_ARRAY_SEPARATOR = "Expected ',' or ']'";
    /** Error: a malformed number. */
    static final String ERROR_NUMBER = "Invalid number";
    /** Error: an integer part with a leading zero. */
    static final String ERROR_LEADING_ZERO = "Invalid number: leading zero";
    /** Error: a number beyond the {@code double} range. */
    static final String ERROR_NUMBER_RANGE = "Number outside the double range";
    /** Error: a word other than {@code true}, {@code false} and {@code null}. */
    static final String ERROR_LITERAL = "Unknown literal";
    /** Error start: a character that cannot start a value. */
    static final String ERROR_CHARACTER = "Unexpected character ";
    /** Error start: nesting beyond the limit. */
    static final String ERROR_DEPTH = "Nesting deeper than the limit of ";
    /** Error start: a string beyond the limit. */
    static final String ERROR_STRING_LENGTH = "String longer than the limit of ";

    /** Byte-order mark, ignored at the start of the document. */
    private static final char BYTE_ORDER_MARK = 0xFEFF;
    /** First character allowed unescaped in a string. */
    private static final char FIRST_PRINTABLE = 0x20;
    /** Last printable ASCII character, shown as is in error messages. */
    private static final char LAST_PRINTABLE = 0x7E;
    /** Radix of decimal numbers. */
    private static final int DECIMAL_RADIX = 10;
    /** Radix of Unicode escapes. */
    private static final int HEX_RADIX = 16;
    /** Value of the hexadecimal digit {@code a}. */
    private static final int HEX_LETTER_VALUE = 10;
    /** Number of digits of a Unicode escape. */
    private static final int UNICODE_ESCAPE_DIGITS = 4;
    /** Escape letters, in the order of {@link #UNESCAPED}. */
    private static final String ESCAPED = "\"\\/bfnrt";
    /** Characters of the escape letters of {@link #ESCAPED}. */
    private static final String UNESCAPED = "\"\\/\b\f\n\r\t";
    /** Literal {@code true}. */
    private static final String LITERAL_TRUE = "true";
    /** Literal {@code false}. */
    private static final String LITERAL_FALSE = "false";
    /** Literal {@code null}. */
    private static final String LITERAL_NULL = "null";
    /** Rejected word {@code NaN}. */
    private static final String LITERAL_NAN = "NaN";
    /** Rejected word {@code Infinity}. */
    private static final String LITERAL_INFINITY = "Infinity";
    /** Quote around a character in error messages. */
    private static final char QUOTE = '\'';
    /** Format of a non printable character in error messages. */
    private static final String CODE_POINT = "U+%04X";
    /** End of the string length error. */
    private static final String CHARACTERS = " characters";

    /**
     * Describes a character for an error message.
     *
     * @param c The character.
     * @return The character between quotes if printable, its code otherwise.
     */
    private static String describe(char c)
    {
        final String description;
        if (c >= FIRST_PRINTABLE && c <= LAST_PRINTABLE)
        {
            description = QUOTE + String.valueOf(c) + QUOTE;
        }
        else
        {
            description = String.format(Locale.ROOT, CODE_POINT, Integer.valueOf(c));
        }
        return description;
    }

    /**
     * Returns the value of a hexadecimal digit.
     *
     * @param c The character.
     * @return The value in {@code [0, 15]}, {@code -1} if the character is not an ASCII hexadecimal digit.
     */
    private static int hexValue(char c)
    {
        int value = -1;
        if (JsonPath.isDigit(c))
        {
            value = c - '0';
        }
        else if (c >= 'a' && c <= 'f')
        {
            value = c - 'a' + HEX_LETTER_VALUE;
        }
        else if (c >= 'A' && c <= 'F')
        {
            value = c - 'A' + HEX_LETTER_VALUE;
        }
        return value;
    }

    /** Source of the document. */
    private final String source;
    /** Decoded characters. */
    private final char[] chars;
    /** Number of decoded characters. */
    private final int length;
    /** Nesting limit. */
    private final int maxDepth;
    /** String length limit, in characters. */
    private final int maxStringLength;
    /** JSON path of the value being read. */
    private final StringBuilder path = new StringBuilder(JsonPath.ROOT);
    /** Decoded text of the string being read. */
    private final StringBuilder text = new StringBuilder();
    /** Index of the current character. */
    private int position;
    /** Current line, from 1. */
    private int line = 1;
    /** Index of the first character of the current line. */
    private int lineStart;
    /** Number of objects and arrays being read. */
    private int depth;

    /**
     * Creates a parser.
     *
     * @param reader The reader giving the limits.
     * @param source The source of the document.
     * @param chars The decoded characters.
     * @param length The number of decoded characters.
     */
    JsonParser(JsonReader reader, String source, char[] chars, int length)
    {
        this.source = source;
        this.chars = chars;
        this.length = length;
        maxDepth = reader.getMaxDepth();
        maxStringLength = reader.getMaxStringLength();
    }

    /**
     * Reads the document.
     *
     * @return The root value.
     * @throws DefinitionException If the document is not valid JSON or exceeds a limit.
     */
    JsonValue parse()
    {
        if (length > 0 && chars[0] == BYTE_ORDER_MARK)
        {
            position = 1;
            lineStart = 1;
        }
        skipWhitespace();
        if (position == length)
        {
            throw error(ERROR_EMPTY);
        }
        return expectEnd(parseValue(null));
    }

    /**
     * Checks that only whitespace follows the root value.
     *
     * @param root The root value.
     * @return The root value.
     */
    private JsonValue expectEnd(JsonValue root)
    {
        skipWhitespace();
        if (position < length)
        {
            throw error(ERROR_AFTER_ROOT);
        }
        return root;
    }

    /**
     * Skips whitespace and counts lines. A slash starts a comment, which is an error.
     */
    private void skipWhitespace()
    {
        boolean whitespace = true;
        while (whitespace && position < length)
        {
            final char c = chars[position];
            if (c == '/')
            {
                throw error(ERROR_COMMENT);
            }
            whitespace = c == ' ' || c == '\t' || c == '\n' || c == '\r';
            if (whitespace)
            {
                position++;
                newLine(c);
            }
        }
    }

    /**
     * Starts a new line after a line feed, or after a carriage return not followed by a line feed.
     *
     * @param c The character just skipped.
     */
    private void newLine(char c)
    {
        if (c == '\n' || c == '\r' && (position == length || chars[position] != '\n'))
        {
            line++;
            lineStart = position;
        }
    }

    /**
     * Returns the column of the current character.
     *
     * @return The column, from 1.
     */
    private int column()
    {
        return position - lineStart + 1;
    }

    /**
     * Reads a value starting at the current character, which exists and is not whitespace.
     *
     * @param parent The enclosing object or array, {@code null} for the root.
     * @return The value.
     */
    private JsonValue parseValue(JsonValue parent)
    {
        final char c = chars[position];
        final JsonValue value;
        if (c == '{')
        {
            value = parseObject(parent);
        }
        else if (c == '[')
        {
            value = parseArray(parent);
        }
        else if (c == '"')
        {
            final int startColumn = column();
            value = new JsonString(source, line, startColumn, parent, readString());
        }
        else if (c == '-' || JsonPath.isDigit(c))
        {
            value = parseNumber(parent);
        }
        else if (JsonPath.isLetter(c))
        {
            value = parseWord(parent);
        }
        else
        {
            throw error(ERROR_CHARACTER + describe(c));
        }
        return value;
    }

    /**
     * Reads an object.
     *
     * @param parent The enclosing object or array, {@code null} for the root.
     * @return The object.
     */
    private JsonObject parseObject(JsonValue parent)
    {
        final JsonObject object = new JsonObject(source, line, column(), parent);
        enter();
        skipWhitespace();
        if (position < length && chars[position] == '}')
        {
            position++;
        }
        else
        {
            boolean more = true;
            while (more)
            {
                parseMember(object);
                skipWhitespace();
                more = parseSeparator('}', ERROR_UNTERMINATED_OBJECT, ERROR_OBJECT_SEPARATOR);
            }
        }
        depth--;
        return object;
    }

    /**
     * Reads one member of an object: name, colon and value.
     *
     * @param object The object to fill.
     */
    private void parseMember(JsonObject object)
    {
        expectMore(ERROR_UNTERMINATED_OBJECT);
        if (chars[position] != '"')
        {
            throw error(ERROR_KEY);
        }
        final int keyColumn = column();
        final String key = readString();
        final int mark = path.length();
        JsonPath.appendKey(path, key);
        object.add(key, parseMemberValue(object, key, keyColumn));
        path.setLength(mark);
    }

    /**
     * Reads the colon and the value of a member whose name was just read.
     *
     * @param object The object being filled.
     * @param key The member name.
     * @param keyColumn The column of the member name, for the duplicate error.
     * @return The member value.
     */
    private JsonValue parseMemberValue(JsonObject object, String key, int keyColumn)
    {
        if (object.has(key))
        {
            throw errorAt(ERROR_DUPLICATE_KEY, keyColumn);
        }
        skipWhitespace();
        expectMore(ERROR_UNTERMINATED_OBJECT);
        if (chars[position] != ':')
        {
            throw error(ERROR_COLON);
        }
        position++;
        skipWhitespace();
        expectMore(ERROR_UNTERMINATED_OBJECT);
        return parseValue(object);
    }

    /**
     * Reads an array.
     *
     * @param parent The enclosing object or array, {@code null} for the root.
     * @return The array.
     */
    private JsonArray parseArray(JsonValue parent)
    {
        final JsonArray array = new JsonArray(source, line, column(), parent);
        enter();
        skipWhitespace();
        if (position < length && chars[position] == ']')
        {
            position++;
        }
        else
        {
            boolean more = true;
            while (more)
            {
                expectMore(ERROR_UNTERMINATED_ARRAY);
                final int mark = path.length();
                JsonPath.appendIndex(path, array.size());
                array.add(parseValue(array));
                path.setLength(mark);
                skipWhitespace();
                more = parseSeparator(']', ERROR_UNTERMINATED_ARRAY, ERROR_ARRAY_SEPARATOR);
            }
        }
        depth--;
        return array;
    }

    /**
     * Reads the separator after a member or an element.
     *
     * @param close The closing character of the container.
     * @param unterminated The error message when the document ends.
     * @param expected The error message for any other character.
     * @return {@code true} after a comma (another member or element follows), {@code false} after the closing
     *         character.
     */
    private boolean parseSeparator(char close, String unterminated, String expected)
    {
        expectMore(unterminated);
        final char c = chars[position];
        if (c != ',' && c != close)
        {
            throw error(expected);
        }
        position++;
        final boolean more = c == ',';
        if (more)
        {
            skipWhitespace();
            if (position < length && chars[position] == close)
            {
                throw error(ERROR_TRAILING_COMMA);
            }
        }
        return more;
    }

    /**
     * Enters an object or an array: checks the nesting limit and skips the opening character.
     */
    private void enter()
    {
        if (depth == maxDepth)
        {
            throw error(ERROR_DEPTH + maxDepth);
        }
        depth++;
        position++;
    }

    /**
     * Checks that the document does not end here.
     *
     * @param message The error message if it ends.
     */
    private void expectMore(String message)
    {
        if (position == length)
        {
            throw error(message);
        }
    }

    /**
     * Reads a string starting at its opening quote. A length error is located at the opening quote.
     *
     * @return The decoded string.
     */
    private String readString()
    {
        final int startColumn = column();
        position++;
        text.setLength(0);
        boolean open = true;
        while (open)
        {
            expectMore(ERROR_UNTERMINATED_STRING);
            final char c = chars[position];
            if (c == '"')
            {
                position++;
                open = false;
            }
            else if (c == '\\')
            {
                readEscape();
            }
            else if (c < FIRST_PRINTABLE)
            {
                throw error(ERROR_CONTROL);
            }
            else
            {
                text.append(c);
                position++;
            }
            if (text.length() > maxStringLength)
            {
                throw errorAt(ERROR_STRING_LENGTH + maxStringLength + CHARACTERS, startColumn);
            }
        }
        return text.toString();
    }

    /**
     * Reads an escape sequence starting at its backslash.
     */
    private void readEscape()
    {
        final int escapeColumn = column();
        position++;
        expectMore(ERROR_UNTERMINATED_STRING);
        final char c = chars[position];
        final int index = ESCAPED.indexOf(c);
        if (index >= 0)
        {
            text.append(UNESCAPED.charAt(index));
            position++;
        }
        else if (c == 'u')
        {
            readUnicode(escapeColumn);
        }
        else
        {
            throw errorAt(ERROR_ESCAPE, escapeColumn);
        }
    }

    /**
     * Reads a Unicode escape starting at its {@code u}, with the second escape of a surrogate pair.
     *
     * @param escapeColumn The column of the backslash, for errors.
     */
    private void readUnicode(int escapeColumn)
    {
        final char first = readHex(escapeColumn);
        if (Character.isHighSurrogate(first))
        {
            if (position + 1 >= length || chars[position] != '\\' || chars[position + 1] != 'u')
            {
                throw errorAt(ERROR_SURROGATE, escapeColumn);
            }
            position++;
            final char second = readHex(escapeColumn);
            if (!Character.isLowSurrogate(second))
            {
                throw errorAt(ERROR_SURROGATE, escapeColumn);
            }
            text.append(first).append(second);
        }
        else if (Character.isLowSurrogate(first))
        {
            throw errorAt(ERROR_SURROGATE, escapeColumn);
        }
        else
        {
            text.append(first);
        }
    }

    /**
     * Reads the four hexadecimal digits of a Unicode escape starting at its {@code u}.
     *
     * @param escapeColumn The column of the backslash, for errors.
     * @return The escaped character.
     */
    private char readHex(int escapeColumn)
    {
        position++;
        if (position + UNICODE_ESCAPE_DIGITS > length)
        {
            throw errorAt(ERROR_UNICODE_ESCAPE, escapeColumn);
        }
        int value = 0;
        for (int i = 0; i < UNICODE_ESCAPE_DIGITS; i++)
        {
            final int digit = hexValue(chars[position]);
            if (digit < 0)
            {
                throw errorAt(ERROR_UNICODE_ESCAPE, escapeColumn);
            }
            value = value * HEX_RADIX + digit;
            position++;
        }
        return (char) value;
    }

    /**
     * Reads a word: {@code true}, {@code false} or {@code null}.
     *
     * @param parent The enclosing object or array, {@code null} for the root.
     * @return The value.
     */
    private JsonValue parseWord(JsonValue parent)
    {
        final int start = position;
        final int startColumn = column();
        while (position < length && JsonPath.isLetter(chars[position]))
        {
            position++;
        }
        final String word = new String(chars, start, position - start);
        final JsonValue value;
        if (LITERAL_TRUE.equals(word))
        {
            value = new JsonBoolean(source, line, startColumn, parent, true);
        }
        else if (LITERAL_FALSE.equals(word))
        {
            value = new JsonBoolean(source, line, startColumn, parent, false);
        }
        else if (LITERAL_NULL.equals(word))
        {
            value = new JsonNull(source, line, startColumn, parent);
        }
        else if (LITERAL_NAN.equals(word) || LITERAL_INFINITY.equals(word))
        {
            throw errorAt(ERROR_NOT_FINITE, startColumn);
        }
        else
        {
            throw errorAt(ERROR_LITERAL, startColumn);
        }
        return value;
    }

    /**
     * Reads a number: an integer literal that fits a {@code long} is a {@code long}, any other number a
     * {@code double}.
     *
     * @param parent The enclosing object or array, {@code null} for the root.
     * @return The value.
     */
    private JsonValue parseNumber(JsonValue parent)
    {
        final int start = position;
        final int startColumn = column();
        if (chars[position] == '-')
        {
            position++;
        }
        scanInteger(startColumn);
        final boolean fraction = scanFraction(startColumn);
        final boolean exponent = scanExponent(startColumn);
        final JsonValue number;
        if (fraction || exponent)
        {
            number = new JsonNumber(source, line, startColumn, parent, toDouble(start, startColumn));
        }
        else
        {
            number = toInteger(start, startColumn, parent);
        }
        return number;
    }

    /**
     * Skips the integer part of a number.
     *
     * @param startColumn The column of the number, for errors.
     */
    private void scanInteger(int startColumn)
    {
        if (position == length)
        {
            throw errorAt(ERROR_NUMBER, startColumn);
        }
        final char c = chars[position];
        if (c == '0')
        {
            position++;
            if (position < length && JsonPath.isDigit(chars[position]))
            {
                throw errorAt(ERROR_LEADING_ZERO, startColumn);
            }
        }
        else if (JsonPath.isDigit(c))
        {
            skipDigits();
        }
        else if (c == 'I')
        {
            throw errorAt(ERROR_NOT_FINITE, startColumn);
        }
        else
        {
            throw errorAt(ERROR_NUMBER, startColumn);
        }
    }

    /**
     * Skips the fraction of a number, if any.
     *
     * @param startColumn The column of the number, for errors.
     * @return {@code true} if the number has a fraction.
     */
    private boolean scanFraction(int startColumn)
    {
        final boolean fraction = position < length && chars[position] == '.';
        if (fraction)
        {
            position++;
            requireDigits(startColumn);
        }
        return fraction;
    }

    /**
     * Skips the exponent of a number, if any.
     *
     * @param startColumn The column of the number, for errors.
     * @return {@code true} if the number has an exponent.
     */
    private boolean scanExponent(int startColumn)
    {
        final boolean exponent = position < length && (chars[position] == 'e' || chars[position] == 'E');
        if (exponent)
        {
            position++;
            if (position < length && (chars[position] == '+' || chars[position] == '-'))
            {
                position++;
            }
            requireDigits(startColumn);
        }
        return exponent;
    }

    /**
     * Skips one or more digits.
     *
     * @param startColumn The column of the number, for errors.
     */
    private void requireDigits(int startColumn)
    {
        if (position == length || !JsonPath.isDigit(chars[position]))
        {
            throw errorAt(ERROR_NUMBER, startColumn);
        }
        skipDigits();
    }

    /**
     * Skips digits.
     */
    private void skipDigits()
    {
        while (position < length && JsonPath.isDigit(chars[position]))
        {
            position++;
        }
    }

    /**
     * Converts the number just read to a {@code double}.
     *
     * @param start The index of its first character.
     * @param startColumn The column of the number, for errors.
     * @return The value, finite.
     */
    private double toDouble(int start, int startColumn)
    {
        final double value = Double.parseDouble(new String(chars, start, position - start));
        if (Double.isInfinite(value))
        {
            throw errorAt(ERROR_NUMBER_RANGE, startColumn);
        }
        return value;
    }

    /**
     * Converts the integer literal just read: a {@code long} if it fits, a {@code double} otherwise.
     *
     * @param start The index of its first character.
     * @param startColumn The column of the number, for errors.
     * @param parent The enclosing object or array, {@code null} for the root.
     * @return The value.
     */
    private JsonValue toInteger(int start, int startColumn, JsonValue parent)
    {
        final boolean negative = chars[start] == '-';
        int first = start;
        long limit = -Long.MAX_VALUE;
        if (negative)
        {
            first++;
            limit = Long.MIN_VALUE;
        }
        final long multiplyLimit = limit / DECIMAL_RADIX;
        long result = 0L;
        boolean overflow = false;
        for (int i = first; i < position && !overflow; i++)
        {
            final int digit = chars[i] - '0';
            overflow = result < multiplyLimit || result * DECIMAL_RADIX < limit + digit;
            result = result * DECIMAL_RADIX - digit;
        }
        final JsonValue number;
        if (overflow)
        {
            number = new JsonNumber(source, line, startColumn, parent, toDouble(start, startColumn));
        }
        else if (negative)
        {
            number = new JsonNumber(source, line, startColumn, parent, result);
        }
        else
        {
            number = new JsonNumber(source, line, startColumn, parent, -result);
        }
        return number;
    }

    /**
     * Creates an error at the current character.
     *
     * @param message The error description.
     * @return The error, to throw.
     */
    private DefinitionException error(String message)
    {
        return new DefinitionException(message, source, line, column(), path.toString());
    }

    /**
     * Creates an error at a column of the current line.
     *
     * @param message The error description.
     * @param errorColumn The column, from 1.
     * @return The error, to throw.
     */
    private DefinitionException errorAt(String message, int errorColumn)
    {
        return new DefinitionException(message, source, line, errorColumn, path.toString());
    }
}
