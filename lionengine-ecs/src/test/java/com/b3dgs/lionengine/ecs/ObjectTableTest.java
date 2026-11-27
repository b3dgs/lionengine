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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests of {@link ObjectTable}: ID packing, allocation, reuse and retirement (REQ-ECS-002, REQ-ECS-011, ADR-0030,
 * ADR-0074).
 */
final class ObjectTableTest
{
    /**
     * The generation is in the high 32 bits and the index in the low 32 bits; unpacking gives them back.
     *
     * @param generation The generation.
     * @param index The index.
     * @param id The expected ID.
     */
    @ParameterizedTest
    @CsvSource({"1, 0, 4294967296", "1, 5, 4294967301", "2, 0, 8589934592", "3, 7, 12884901895",
                "2147483647, 1073741823, 9223372033633550335"})
    void testPack(int generation, int index, long id)
    {
        assertEquals(id, ObjectTable.pack(generation, index));
        assertEquals(generation, ObjectTable.generation(id));
        assertEquals(index, ObjectTable.index(id));
    }

    /**
     * The first IDs take the first indices with the first generation; they are pending, then alive once activated.
     */
    @Test
    void testAllocateActivate()
    {
        final ObjectTable table = new ObjectTable(4, ObjectTable.MAX_GENERATION);
        final long first = table.allocate();
        final long second = table.allocate();

        assertEquals(ObjectTable.pack(1, 0), first);
        assertEquals(ObjectTable.pack(1, 1), second);
        assertTrue(table.isPending(first));
        assertFalse(table.isAlive(first));

        table.activate(first);

        assertTrue(table.isAlive(first));
        assertFalse(table.isPending(first));
        assertTrue(table.isPending(second));
        assertEquals(1, table.getAlive());
    }

    /**
     * A released index takes the next generation and is reused, the oldest free index first.
     */
    @Test
    void testReuseOldestFirst()
    {
        final ObjectTable table = new ObjectTable(4, ObjectTable.MAX_GENERATION);
        final long[] ids = new long[3];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = table.allocate();
            table.activate(ids[i]);
        }
        assertTrue(table.release(ids[2]));
        assertTrue(table.release(ids[0]));
        assertFalse(table.release(ids[0]));

        assertEquals(ObjectTable.pack(2, 2), table.allocate());
        assertEquals(ObjectTable.pack(2, 0), table.allocate());
        assertEquals(ObjectTable.pack(1, 3), table.allocate());
        assertEquals(1, table.getAlive());
    }

    /**
     * An index at the highest generation is retired: never reused.
     */
    @Test
    void testRetirement()
    {
        final ObjectTable table = new ObjectTable(2, 2);
        final long first = table.allocate();
        table.activate(first);
        table.release(first);
        final long second = table.allocate();
        assertEquals(ObjectTable.pack(2, 0), second);
        table.activate(second);
        table.release(second);

        assertFalse(table.isAlive(second));
        assertFalse(table.isPending(second));
        assertEquals(ObjectTable.pack(1, 1), table.allocate());
        assertEquals(ObjectTable.pack(1, 2), table.allocate());
    }

    /**
     * Indices allocated beyond the capacity are pending with the first generation and stay pending after a growth; the
     * free queue keeps its order across the growth.
     */
    @Test
    void testBeyondCapacityAndGrowth()
    {
        final ObjectTable table = new ObjectTable(2, ObjectTable.MAX_GENERATION);
        final long first = table.allocate();
        final long second = table.allocate();
        table.activate(first);
        table.activate(second);
        table.release(second);
        table.release(first);
        table.allocate();
        table.allocate();
        final long third = table.allocate();

        assertEquals(ObjectTable.pack(1, 2), third);
        assertTrue(table.isPending(third));
        assertFalse(table.isPending(ObjectTable.pack(2, 2)));
        assertFalse(table.isAlive(third));
        assertFalse(table.isPending(ObjectTable.pack(1, 3)));

        table.growToIssued();

        assertEquals(4, table.getCapacity());
        assertTrue(table.isPending(third));
        table.activate(third);
        assertTrue(table.isAlive(third));
    }

    /**
     * A growth keeps the order of the free queue, also when the queue wraps around its ring.
     */
    @Test
    void testGrowthKeepsFreeOrder()
    {
        final ObjectTable table = new ObjectTable(3, ObjectTable.MAX_GENERATION);
        final long[] ids = new long[3];
        for (int i = 0; i < ids.length; i++)
        {
            ids[i] = table.allocate();
            table.activate(ids[i]);
        }
        table.release(ids[0]);
        table.release(ids[1]);
        final long reused = table.allocate();
        table.activate(reused);
        table.release(ids[2]);
        table.release(reused);

        table.grow(8);
        table.grow(4);

        assertEquals(8, table.getCapacity());
        assertEquals(ObjectTable.pack(2, 1), table.allocate());
        assertEquals(ObjectTable.pack(2, 2), table.allocate());
        assertEquals(ObjectTable.pack(3, 0), table.allocate());
        assertEquals(ObjectTable.pack(1, 3), table.allocate());
    }

    /**
     * Liveness never throws, whatever the value.
     *
     * @param id The ID.
     */
    @ParameterizedTest
    @CsvSource({"0", "-1", "1", "4294967295", "4294967296", "-9223372036854775808", "9223372036854775807"})
    void testNeverValid(long id)
    {
        final ObjectTable table = new ObjectTable(2, ObjectTable.MAX_GENERATION);

        assertFalse(table.isAlive(id));
        assertFalse(table.isPending(id));
    }
}
