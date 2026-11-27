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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * Strict JSON reader of definition files (ADR-0028, ADR-0032, REQ-DEF-001, REQ-DEF-002).
 *
 * <p>
 * Standard JSON only (RFC 8259), from UTF-8 bytes or from characters; a leading byte-order mark is ignored. Errors:
 * duplicate keys, comments, trailing commas, {@code NaN}, {@code Infinity}, invalid escapes or UTF-8, unterminated
 * values, content after the root value, and exceeding a limit. An integer literal (no fraction, no exponent) that fits
 * a {@code long} is read as {@link JsonValue.Kind#LONG}, any other number as {@link JsonValue.Kind#DOUBLE}. Object
 * members keep their document order.
 * </p>
 *
 * <p>
 * Every error is a {@link DefinitionException} with the source given by the caller (the resource key of the file),
 * the line and column (both from 1) and the JSON path of the faulty value, for example
 * {@code Duplicate key (sprites/hero.json:3:7, $.animations.walk)}.
 * </p>
 *
 * <p>
 * Limits, set at construction: document size ({@link #DEFAULT_MAX_DOCUMENT_SIZE}), nesting depth
 * ({@link #DEFAULT_MAX_DEPTH}) and string length ({@link #DEFAULT_MAX_STRING_LENGTH}). Opening resources is not the
 * reader's job: the caller reads the bytes through {@code resources}.
 * </p>
 *
 * <p>
 * Immutable and thread-safe: a loading worker may read with a shared instance (ADR-0019). Each read allocates its
 * parsing state and the values.
 * </p>
 *
 * @since 11.0.0
 */
public final class JsonReader
{
    /** Default document size limit: 16 MiB, in bytes (or characters for character input). */
    public static final int DEFAULT_MAX_DOCUMENT_SIZE = 16 * 1024 * 1024;
    /** Default nesting limit: 64 objects and arrays inside one another. */
    public static final int DEFAULT_MAX_DEPTH = 64;
    /** Default string length limit: 1 MiB, in characters, for member names and string values. */
    public static final int DEFAULT_MAX_STRING_LENGTH = 1024 * 1024;

    /** Error start: document beyond the size limit. */
    static final String ERROR_DOCUMENT_SIZE = "Document larger than the limit of ";
    /** Error: malformed UTF-8 input. */
    static final String ERROR_ENCODING = "Invalid UTF-8";

    /** Byte-order mark, ignored at the start of the document. */
    private static final char BYTE_ORDER_MARK = 0xFEFF;
    /** End of the size error for byte input. */
    private static final String BYTES = " bytes";
    /** End of the size error for character input. */
    private static final String CHARACTERS = " characters";

    /**
     * Checks a limit given at construction.
     *
     * @param value The limit.
     * @param name The limit name, for the error message.
     * @throws IllegalArgumentException If the limit is not positive.
     */
    private static void checkLimit(int value, String name)
    {
        if (value < 1)
        {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
    }

    /**
     * Checks the arguments of a read.
     *
     * @param input The input.
     * @param source The source name.
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    private static void checkArguments(Object input, String source)
    {
        if (input == null)
        {
            throw new IllegalArgumentException("Input must not be null");
        }
        if (source == null)
        {
            throw new IllegalArgumentException("Source must not be null");
        }
    }

    /**
     * Decodes strict UTF-8.
     *
     * @param utf8 The bytes.
     * @param source The source name, for errors.
     * @return The characters, from index 0 to the buffer position.
     * @throws DefinitionException If the bytes are not valid UTF-8.
     */
    private static CharBuffer decode(byte[] utf8, String source)
    {
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                                                             .onMalformedInput(CodingErrorAction.REPORT)
                                                             .onUnmappableCharacter(CodingErrorAction.REPORT);
        final CharBuffer chars = CharBuffer.allocate(utf8.length);
        CoderResult result = decoder.decode(ByteBuffer.wrap(utf8), chars, true);
        if (!result.isError())
        {
            result = decoder.flush(chars);
        }
        if (result.isError())
        {
            throw encodingError(chars, source);
        }
        return chars;
    }

    /**
     * Creates the error of malformed UTF-8, located after the last character decoded.
     *
     * @param chars The characters decoded before the error.
     * @param source The source name.
     * @return The error, to throw.
     */
    private static DefinitionException encodingError(CharBuffer chars, String source)
    {
        final char[] decoded = chars.array();
        final int end = chars.position();
        int line = 1;
        int lineStart = 0;
        if (end > 0 && decoded[0] == BYTE_ORDER_MARK)
        {
            lineStart = 1;
        }
        for (int i = 0; i < end; i++)
        {
            final char c = decoded[i];
            if (c == '\n' || c == '\r' && (i + 1 == end || decoded[i + 1] != '\n'))
            {
                line++;
                lineStart = i + 1;
            }
        }
        return new DefinitionException(ERROR_ENCODING, source, line, end - lineStart + 1, null);
    }

    /** Document size limit, in bytes or characters. */
    private final int maxDocumentSize;
    /** Nesting limit. */
    private final int maxDepth;
    /** String length limit, in characters. */
    private final int maxStringLength;

    /**
     * Creates a reader with the default limits.
     */
    public JsonReader()
    {
        this(DEFAULT_MAX_DOCUMENT_SIZE, DEFAULT_MAX_DEPTH, DEFAULT_MAX_STRING_LENGTH);
    }

    /**
     * Creates a reader with the given limits.
     *
     * @param maxDocumentSize The document size limit, in bytes (or characters for character input), positive.
     * @param maxDepth The nesting limit, positive: the number of objects and arrays allowed inside one another.
     * @param maxStringLength The string length limit, in characters, positive.
     * @throws IllegalArgumentException If a limit is not positive.
     */
    public JsonReader(int maxDocumentSize, int maxDepth, int maxStringLength)
    {
        checkLimit(maxDocumentSize, "Document size limit");
        checkLimit(maxDepth, "Depth limit");
        checkLimit(maxStringLength, "String length limit");

        this.maxDocumentSize = maxDocumentSize;
        this.maxDepth = maxDepth;
        this.maxStringLength = maxStringLength;
    }

    /**
     * Returns the document size limit.
     *
     * @return The limit, in bytes (or characters for character input).
     */
    public int getMaxDocumentSize()
    {
        return maxDocumentSize;
    }

    /**
     * Returns the nesting limit.
     *
     * @return The number of objects and arrays allowed inside one another.
     */
    public int getMaxDepth()
    {
        return maxDepth;
    }

    /**
     * Returns the string length limit.
     *
     * @return The limit, in characters.
     */
    public int getMaxStringLength()
    {
        return maxStringLength;
    }

    /**
     * Reads a document encoded in UTF-8.
     *
     * @param utf8 The document bytes (never {@code null}); not modified and not kept.
     * @param source The resource key of the document, or a caller-given name, reported in errors (never
     *            {@code null}).
     * @return The root value (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws DefinitionException If the document is not valid JSON or UTF-8, or exceeds a limit.
     */
    public JsonValue read(byte[] utf8, String source)
    {
        checkArguments(utf8, source);
        if (utf8.length > maxDocumentSize)
        {
            throw new DefinitionException(ERROR_DOCUMENT_SIZE + maxDocumentSize + BYTES,
                                          source,
                                          DefinitionException.UNKNOWN,
                                          DefinitionException.UNKNOWN,
                                          null);
        }
        final CharBuffer chars = decode(utf8, source);
        return new JsonParser(this, source, chars.array(), chars.position()).parse();
    }

    /**
     * Reads a document given as characters.
     *
     * @param text The document text (never {@code null}); not kept.
     * @param source The resource key of the document, or a caller-given name, reported in errors (never
     *            {@code null}).
     * @return The root value (never {@code null}).
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws DefinitionException If the document is not valid JSON, or exceeds a limit.
     */
    public JsonValue read(CharSequence text, String source)
    {
        checkArguments(text, source);
        if (text.length() > maxDocumentSize)
        {
            throw new DefinitionException(ERROR_DOCUMENT_SIZE + maxDocumentSize + CHARACTERS,
                                          source,
                                          DefinitionException.UNKNOWN,
                                          DefinitionException.UNKNOWN,
                                          null);
        }
        final char[] chars = text.toString().toCharArray();
        return new JsonParser(this, source, chars, chars.length).parse();
    }
}
