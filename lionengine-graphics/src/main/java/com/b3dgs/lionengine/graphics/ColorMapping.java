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
package com.b3dgs.lionengine.graphics;

import java.util.Arrays;

/**
 * Template color mapping: exact key colors replaced by target colors, such as the team colors of one sprite
 * (ADR-0026, ADR-0057, REQ-SPRITE-005).
 *
 * <p>
 * Colors are packed {@code 0xAARRGGBB}, straight alpha. A pixel equal to a key color on all four channels becomes its
 * target color, alpha included; any other pixel is kept. Every pixel is mapped once, from the source colors: with
 * {@code A → B} and {@code B → C}, a pixel {@code A} becomes {@code B}, and a pixel {@code B} becomes {@code C}.
 * </p>
 *
 * <p>
 * A mapping is the static part of a {@link ColorTransform} ({@link ColorTransform#withMapping(ColorMapping)}), applied
 * before the grading, and baked into a variant of an image by {@link ImageCache#bake(Image, ColorTransform)}; the
 * templates of a sheet definition are read as mappings ({@link SpriteSheet#getTemplate(String)}).
 * </p>
 *
 * <p>
 * Immutable and thread-safe: {@link #with(int, int)} returns a new mapping. Entries are kept by ascending key color
 * read as an unsigned number; an entry mapping a color to itself changes nothing and is not kept, so two mappings
 * that change the same colors the same way are equal. The canonical text ({@link #toString()}) names the cached
 * variant of a bake.
 * </p>
 *
 * @since 11.0.0
 */
public final class ColorMapping
{
    /** The mapping that changes nothing. */
    public static final ColorMapping NONE = new ColorMapping(new int[0], new int[0]);

    /** Bit flipped to compare colors as unsigned numbers with a signed comparison. */
    private static final int UNSIGNED = Integer.MIN_VALUE;
    /** Digits of a color in the text. */
    private static final int DIGITS = 8;
    /** Hexadecimal radix. */
    private static final int HEX = 16;
    /** Bits of a hexadecimal digit. */
    private static final int DIGIT_BITS = 4;
    /** Mask of a hexadecimal digit. */
    private static final int DIGIT_MASK = HEX - 1;

    /**
     * Appends a color to a text, {@code #AARRGGBB} in upper case.
     *
     * @param text The text.
     * @param argb The color.
     */
    private static void appendColor(StringBuilder text, int argb)
    {
        text.append('#');
        for (int i = DIGITS - 1; i >= 0; i--)
        {
            text.append(Character.toUpperCase(Character.forDigit((argb >>> (i * DIGIT_BITS)) & DIGIT_MASK, HEX)));
        }
    }

    /**
     * Finds a key color.
     *
     * @param keys The key colors, ascending as unsigned numbers.
     * @param argb The color searched.
     * @return The index of the color, or {@code -(insertion point) - 1} if absent.
     */
    private static int find(int[] keys, int argb)
    {
        final int searched = argb ^ UNSIGNED;
        int low = 0;
        int high = keys.length - 1;
        while (low <= high)
        {
            final int middle = (low + high) >>> 1;
            final int value = keys[middle] ^ UNSIGNED;
            if (value < searched)
            {
                low = middle + 1;
            }
            else if (value > searched)
            {
                high = middle - 1;
            }
            else
            {
                return middle;
            }
        }
        return -(low + 1);
    }

    /** The key colors, ascending as unsigned numbers. */
    private final int[] keys;
    /** The target colors, in the order of the keys. */
    private final int[] targets;

    /**
     * Creates a mapping from checked entries.
     *
     * @param keys The key colors, ascending as unsigned numbers, each different from its target.
     * @param targets The target colors, in the order of the keys.
     */
    private ColorMapping(int[] keys, int[] targets)
    {
        super();

        this.keys = keys;
        this.targets = targets;
    }

    /**
     * Returns this mapping with one more entry: a key color replaced by a target color. An entry already given for
     * the key color is replaced; a key color mapped to itself removes the entry of the key color.
     *
     * @param key The key color, packed {@code 0xAARRGGBB}: matched exactly, alpha included.
     * @param target The target color, packed {@code 0xAARRGGBB}.
     * @return The new mapping.
     */
    public ColorMapping with(int key, int target)
    {
        final int index = find(keys, key);
        final ColorMapping mapping;
        if (index >= 0 && key == target)
        {
            mapping = remove(index);
        }
        else if (index >= 0)
        {
            final int[] newTargets = targets.clone();
            newTargets[index] = target;
            mapping = new ColorMapping(keys, newTargets);
        }
        else if (key == target)
        {
            mapping = this;
        }
        else
        {
            mapping = insert(-index - 1, key, target);
        }
        return mapping;
    }

    /**
     * Returns the number of entries.
     *
     * @return The number of key colors that change, {@code 0} for {@link #NONE}.
     */
    public int size()
    {
        return keys.length;
    }

    /**
     * Tells whether the mapping changes nothing.
     *
     * @return {@code true} if it has no entry.
     */
    public boolean isIdentity()
    {
        return keys.length == 0;
    }

    /**
     * Returns the key color of an entry.
     *
     * @param index The index of the entry, entries ascending by key color read as an unsigned number.
     * @return The key color, packed {@code 0xAARRGGBB}.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, size())}.
     */
    public int getKey(int index)
    {
        checkIndex(index);
        return keys[index];
    }

    /**
     * Returns the target color of an entry.
     *
     * @param index The index of the entry, entries ascending by key color read as an unsigned number.
     * @return The target color, packed {@code 0xAARRGGBB}.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, size())}.
     */
    public int getTarget(int index)
    {
        checkIndex(index);
        return targets[index];
    }

    /**
     * Maps a pixel, on the CPU, allocating nothing.
     *
     * @param argb The pixel, packed {@code 0xAARRGGBB}, straight alpha.
     * @return The target color if the pixel is a key color, the pixel otherwise.
     */
    public int apply(int argb)
    {
        final int index = find(keys, argb);
        final int result;
        if (index >= 0)
        {
            result = targets[index];
        }
        else
        {
            result = argb;
        }
        return result;
    }

    /**
     * Checks an entry index.
     *
     * @param index The index.
     * @throws IndexOutOfBoundsException If the index is outside {@code [0, size())}.
     */
    private void checkIndex(int index)
    {
        if (index < 0 || index >= keys.length)
        {
            throw new IndexOutOfBoundsException("Color mapping entry: " + index + " of " + keys.length);
        }
    }

    /**
     * Returns this mapping without an entry.
     *
     * @param index The index of the entry.
     * @return The new mapping.
     */
    private ColorMapping remove(int index)
    {
        final int count = keys.length - 1;
        final int[] newKeys = new int[count];
        final int[] newTargets = new int[count];
        System.arraycopy(keys, 0, newKeys, 0, index);
        System.arraycopy(targets, 0, newTargets, 0, index);
        System.arraycopy(keys, index + 1, newKeys, index, count - index);
        System.arraycopy(targets, index + 1, newTargets, index, count - index);
        return new ColorMapping(newKeys, newTargets);
    }

    /**
     * Returns this mapping with a new entry.
     *
     * @param index The index of the entry, keeping the keys ascending.
     * @param key The key color.
     * @param target The target color.
     * @return The new mapping.
     */
    private ColorMapping insert(int index, int key, int target)
    {
        final int count = keys.length;
        final int[] newKeys = new int[count + 1];
        final int[] newTargets = new int[count + 1];
        System.arraycopy(keys, 0, newKeys, 0, index);
        System.arraycopy(targets, 0, newTargets, 0, index);
        newKeys[index] = key;
        newTargets[index] = target;
        System.arraycopy(keys, index, newKeys, index + 1, count - index);
        System.arraycopy(targets, index, newTargets, index + 1, count - index);
        return new ColorMapping(newKeys, newTargets);
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof final ColorMapping other
               && Arrays.equals(keys, other.keys)
               && Arrays.equals(targets, other.targets);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(keys) * 31 + Arrays.hashCode(targets);
    }

    /**
     * Returns the canonical text of the mapping: its entries by ascending key color, each key then {@code =} then
     * target in {@code #AARRGGBB}, upper case, separated by commas, inside {@code mapping(...)}; for example
     * {@code mapping(#FFFF00FF=#FFFF0000)}, and {@code mapping()} for {@link #NONE}. It names the cached variant of a
     * bake.
     *
     * @return The canonical text.
     */
    @Override
    public String toString()
    {
        final StringBuilder text = new StringBuilder("mapping(");
        for (int i = 0; i < keys.length; i++)
        {
            if (i > 0)
            {
                text.append(',');
            }
            appendColor(text, keys[i]);
            text.append('=');
            appendColor(text, targets[i]);
        }
        return text.append(')').toString();
    }
}
