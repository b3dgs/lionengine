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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.UtilRandom;

/**
 * An unordered map that uses int keys. This implementation is a cuckoo hash map using 3 hashes, random walking, and a
 * small stash for problematic keys. Null values are allowed. No allocation is done except when growing the table size.
 * 
 * @author Nathan Sweet
 * @param <V> The value type.
 */
// CHECKSTYLE OFF: MagicNumber|NPath|ReturnCount|ExecutableStatementCount|NCSS|CyclomaticComplexity|NestedIfDepth
public class IntMap<V>
{
    private static final int PRIME2 = 0xb4b82e39;
    private static final int PRIME3 = 0xced1c241;
    private static final int EMPTY = 0;

    /**
     * Returns the next power of two. Returns the specified value if the value is already a power of two.
     * 
     * @param value The value.
     * @return The next value.
     */
    private static int nextPowerOfTwo(int value)
    {
        if (value == 0)
        {
            return 1;
        }
        int v = value;
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return v + 1;
    }

    private int size;

    private int[] keyTable;
    private V[] valueTable;
    private int capacity;
    private int stashSize;
    private V zeroValue;
    private boolean hasZeroValue;

    private float loadFactor;
    private int hashShift;
    private int mask;
    private int threshold;
    private int stashCapacity;
    private int pushIterations;

    /**
     * Creates a new map with an initial capacity of 51 and a load factor of 0.8.
     */
    public IntMap()
    {
        this(51, 0.8f);
    }

    /**
     * Creates a new map with a load factor of 0.8.
     * 
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
     */
    public IntMap(int initialCapacity)
    {
        this(initialCapacity, 0.8f);
    }

    /**
     * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items
     * before growing the backing table.
     * 
     * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
     * @param loadFactor The load factor.
     */
    @SuppressWarnings("unchecked")
    public IntMap(int initialCapacity, float loadFactor)
    {
        int initial = initialCapacity;
        if (initial < 0)
        {
            throw new IllegalArgumentException("initialCapacity must be >= 0: " + initial);
        }
        initial = nextPowerOfTwo((int) Math.ceil(initial / loadFactor));
        if (initial > 1 << 30)
        {
            throw new IllegalArgumentException("initialCapacity is too large: " + initial);
        }
        capacity = initial;

        if (loadFactor <= 0)
        {
            throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
        }
        this.loadFactor = loadFactor;

        threshold = (int) (capacity * loadFactor);
        mask = capacity - 1;
        hashShift = 31 - Integer.numberOfTrailingZeros(capacity);
        stashCapacity = Math.max(3, (int) Math.ceil(Math.log(capacity)) * 2);
        pushIterations = Math.max(Math.min(capacity, 8), (int) Math.sqrt(capacity) / 8);

        keyTable = new int[capacity + stashCapacity];
        valueTable = (V[]) new Object[keyTable.length];
    }

    /**
     * Put value.
     * 
     * @param key The key.
     * @param value The value.
     * @return The old value.
     */
    public V put(int key, V value)
    {
        if (key == 0)
        {
            final V oldValue = zeroValue;
            zeroValue = value;
            if (!hasZeroValue)
            {
                hasZeroValue = true;
                size++;
            }
            return oldValue;
        }

        final int[] keyTable = this.keyTable;

        // Check for existing keys.
        final int index1 = key & mask;
        final int key1 = keyTable[index1];
        if (key1 == key)
        {
            final V oldValue = valueTable[index1];
            valueTable[index1] = value;
            return oldValue;
        }

        final int index2 = hash2(key);
        final int key2 = keyTable[index2];
        if (key2 == key)
        {
            final V oldValue = valueTable[index2];
            valueTable[index2] = value;
            return oldValue;
        }

        final int index3 = hash3(key);
        final int key3 = keyTable[index3];
        if (key3 == key)
        {
            final V oldValue = valueTable[index3];
            valueTable[index3] = value;
            return oldValue;
        }

        // Update key in the stash.
        for (int i = capacity, n = i + stashSize; i < n; i++)
        {
            if (keyTable[i] == key)
            {
                final V oldValue = valueTable[i];
                valueTable[i] = value;
                return oldValue;
            }
        }

        // Check for empty buckets.
        if (key1 == EMPTY)
        {
            keyTable[index1] = key;
            valueTable[index1] = value;
            if (size++ >= threshold)
            {
                resize(capacity << 1);
            }
            return null;
        }

        if (key2 == EMPTY)
        {
            keyTable[index2] = key;
            valueTable[index2] = value;
            if (size++ >= threshold)
            {
                resize(capacity << 1);
            }
            return null;
        }

        if (key3 == EMPTY)
        {
            keyTable[index3] = key;
            valueTable[index3] = value;
            if (size++ >= threshold)
            {
                resize(capacity << 1);
            }
            return null;
        }

        push(key, value, index1, key1, index2, key2, index3, key3);
        return null;
    }

    private void putResize(int key, V value)
    {
        if (key == 0)
        {
            zeroValue = value;
            hasZeroValue = true;
            return;
        }

        // Check for empty buckets.
        final int index1 = key & mask;
        final int key1 = keyTable[index1];
        if (key1 == EMPTY)
        {
            keyTable[index1] = key;
            valueTable[index1] = value;
            if (size++ >= threshold)
            {
                resize(capacity << 1);
            }
            return;
        }

        final int index2 = hash2(key);
        final int key2 = keyTable[index2];
        if (key2 == EMPTY)
        {
            keyTable[index2] = key;
            valueTable[index2] = value;
            if (size++ >= threshold)
            {
                resize(capacity << 1);
            }
            return;
        }

        final int index3 = hash3(key);
        final int key3 = keyTable[index3];
        if (key3 == EMPTY)
        {
            keyTable[index3] = key;
            valueTable[index3] = value;
            if (size++ >= threshold)
            {
                resize(capacity << 1);
            }
            return;
        }

        push(key, value, index1, key1, index2, key2, index3, key3);
    }

    private void push(int ik, V iv, int i1, int k1, int i2, int k2, int i3, int k3)
    {
        final int[] keyTable = this.keyTable;

        int index1 = i1;
        int key1 = k1;
        int index2 = i2;
        int key2 = k2;
        int index3 = i3;
        int key3 = k3;
        int insertKey = ik;
        V insertValue = iv;

        final V[] valueTable = this.valueTable;
        final int mask = this.mask;

        // Push keys until an empty bucket is found.
        int evictedKey;
        V evictedValue;
        int i = 0;
        final int pushIterations = this.pushIterations;
        do
        {
            // Replace the key and value for one of the hashes.
            switch (UtilRandom.getRandomInteger(2))
            {
                case 0:
                    evictedKey = key1;
                    evictedValue = valueTable[index1];
                    keyTable[index1] = insertKey;
                    valueTable[index1] = insertValue;
                    break;
                case 1:
                    evictedKey = key2;
                    evictedValue = valueTable[index2];
                    keyTable[index2] = insertKey;
                    valueTable[index2] = insertValue;
                    break;
                default:
                    evictedKey = key3;
                    evictedValue = valueTable[index3];
                    keyTable[index3] = insertKey;
                    valueTable[index3] = insertValue;
                    break;
            }

            // If the evicted key hashes to an empty bucket, put it there and stop.
            index1 = evictedKey & mask;
            key1 = keyTable[index1];
            if (key1 == EMPTY)
            {
                keyTable[index1] = evictedKey;
                valueTable[index1] = evictedValue;
                if (size++ >= threshold)
                {
                    resize(capacity << 1);
                }
                return;
            }

            index2 = hash2(evictedKey);
            key2 = keyTable[index2];
            if (key2 == EMPTY)
            {
                keyTable[index2] = evictedKey;
                valueTable[index2] = evictedValue;
                if (size++ >= threshold)
                {
                    resize(capacity << 1);
                }
                return;
            }

            index3 = hash3(evictedKey);
            key3 = keyTable[index3];
            if (key3 == EMPTY)
            {
                keyTable[index3] = evictedKey;
                valueTable[index3] = evictedValue;
                if (size++ >= threshold)
                {
                    resize(capacity << 1);
                }
                return;
            }

            if (++i == pushIterations)
            {
                break;
            }

            insertKey = evictedKey;
            insertValue = evictedValue;
        }
        while (true);

        putStash(evictedKey, evictedValue);
    }

    private void putStash(int key, V value)
    {
        if (stashSize == stashCapacity)
        {
            // Too many pushes occurred and the stash is full, increase the table size.
            resize(capacity << 1);
            putResize(key, value);
            return;
        }
        // Store key in the stash.
        final int index = capacity + stashSize;
        keyTable[index] = key;
        valueTable[index] = value;
        stashSize++;
        size++;
    }

    /**
     * Get value.
     * 
     * @param key The key.
     * @return The value.
     */
    public V get(int key)
    {
        if (key == 0)
        {
            if (!hasZeroValue)
            {
                return null;
            }
            return zeroValue;
        }
        int index = key & mask;
        if (keyTable[index] != key)
        {
            index = hash2(key);
            if (keyTable[index] != key)
            {
                index = hash3(key);
                if (keyTable[index] != key)
                {
                    return getStash(key, null);
                }
            }
        }
        return valueTable[index];
    }

    private V getStash(int key, V defaultValue)
    {
        final int[] keyTable = this.keyTable;
        for (int i = capacity, n = i + stashSize; i < n; i++)
        {
            if (keyTable[i] == key)
            {
                return valueTable[i];
            }
        }
        return defaultValue;
    }

    /**
     * Clear values.
     */
    public void clear()
    {
        if (size == 0)
        {
            return;
        }
        final int[] keyTable = this.keyTable;
        final V[] valueTable = this.valueTable;
        for (int i = capacity + stashSize; i-- > 0;)
        {
            keyTable[i] = EMPTY;
            valueTable[i] = null;
        }
        size = 0;
        stashSize = 0;
        zeroValue = null;
        hasZeroValue = false;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newSize)
    {
        final int oldEndIndex = capacity + stashSize;

        capacity = newSize;
        threshold = (int) (newSize * loadFactor);
        mask = newSize - 1;
        hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
        stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
        pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

        final int[] oldKeyTable = keyTable;
        final V[] oldValueTable = valueTable;

        keyTable = new int[newSize + stashCapacity];
        valueTable = (V[]) new Object[newSize + stashCapacity];

        final int oldSize = size;
        size = hasZeroValue ? 1 : 0;
        stashSize = 0;
        if (oldSize > 0)
        {
            for (int i = 0; i < oldEndIndex; i++)
            {
                final int key = oldKeyTable[i];
                if (key != EMPTY)
                {
                    putResize(key, oldValueTable[i]);
                }
            }
        }
    }

    private int hash2(int h)
    {
        final int p = h * PRIME2;
        return (p ^ p >>> hashShift) & mask;
    }

    private int hash3(int h)
    {
        final int p = h * PRIME3;
        return (p ^ p >>> hashShift) & mask;
    }
}
