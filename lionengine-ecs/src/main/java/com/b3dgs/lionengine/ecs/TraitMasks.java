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
package com.b3dgs.lionengine.ecs;

/**
 * The trait mask of every object index: one bit per registered trait type, set while the object has a trait of that
 * type (ADR-0011, decision 4, ADR-0075).
 *
 * <p>
 * The bits are kept in words of 64 trait types: word {@code w} holds the types {@code 64 w} to {@code 64 w + 63}, one
 * {@code long} per object index, so any number of trait types is supported. A world has as many words as its
 * registered types need; registering a type that needs one more word allocates it, outside the steps. The masks are
 * changed at the flush only; they are the seam of the queries of the next batch, which test an object against include
 * and exclude masks word by word.
 * </p>
 *
 * <p>
 * Not thread-safe: owned by the runtime thread with its world.
 * </p>
 */
final class TraitMasks
{
    /** Trait types per word. */
    static final int TYPES_PER_WORD = Long.SIZE;

    /** Shift from a type ID to its word. */
    private static final int WORD_SHIFT = 6;

    /** Words per mask word index, then object index; a word array has the object capacity. */
    private long[][] words;
    /** Number of words in use. */
    private int wordCount;
    /** Object capacity. */
    private int capacity;

    /**
     * Creates masks without any trait type.
     *
     * @param capacity The object capacity, positive.
     */
    TraitMasks(int capacity)
    {
        this.capacity = capacity;
        words = new long[1][];
    }

    /**
     * Makes room for a new trait type, adding a word when it is the first type of a word.
     *
     * @param typeId The type ID, the number of types registered before it.
     */
    void addType(int typeId)
    {
        final int word = typeId >>> WORD_SHIFT;
        if (word == wordCount)
        {
            if (word == words.length)
            {
                final long[][] newWords = new long[words.length * 2][];
                System.arraycopy(words, 0, newWords, 0, words.length);
                words = newWords;
            }
            words[word] = new long[capacity];
            wordCount++;
        }
    }

    /**
     * Grows the masks to an object capacity. Never shrinks.
     *
     * @param newCapacity The object capacity.
     */
    void grow(int newCapacity)
    {
        if (newCapacity > capacity)
        {
            for (int w = 0; w < wordCount; w++)
            {
                final long[] grown = new long[newCapacity];
                System.arraycopy(words[w], 0, grown, 0, capacity);
                words[w] = grown;
            }
            capacity = newCapacity;
        }
    }

    /**
     * Sets the bit of a trait type.
     *
     * @param index The object index.
     * @param typeId The type ID.
     */
    void set(int index, int typeId)
    {
        words[typeId >>> WORD_SHIFT][index] |= 1L << typeId;
    }

    /**
     * Clears the bit of a trait type.
     *
     * @param index The object index.
     * @param typeId The type ID.
     */
    void clear(int index, int typeId)
    {
        words[typeId >>> WORD_SHIFT][index] &= ~(1L << typeId);
    }

    /**
     * Tells whether the bit of a trait type is set.
     *
     * @param index The object index.
     * @param typeId The type ID, registered.
     * @return {@code true} if the object has a trait of the type.
     */
    boolean has(int index, int typeId)
    {
        return (words[typeId >>> WORD_SHIFT][index] & 1L << typeId) != 0L;
    }

    /**
     * Returns a word of a mask, for the queries.
     *
     * @param index The object index.
     * @param word The word, in [0, {@link #getWordCount()}).
     * @return The bits of the types {@code 64 word} to {@code 64 word + 63}.
     */
    long getWord(int index, int word)
    {
        return words[word][index];
    }

    /**
     * Returns the number of words in use.
     *
     * @return The words the registered types need.
     */
    int getWordCount()
    {
        return wordCount;
    }
}
