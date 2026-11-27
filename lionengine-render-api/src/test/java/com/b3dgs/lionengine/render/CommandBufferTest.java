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
package com.b3dgs.lionengine.render;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link CommandBuffer}: entries, defaults, presentation order, viewports, growth and allocation
 * (REQ-SPRITE-008, REQ-SPRITE-010).
 */
final class CommandBufferTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 240;
    /** Bytes tolerated for the allocation measurement itself. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** Texture of the tests. */
    private static final int TEXTURE = 7;
    /** Size of a source rectangle. */
    private static final int SIZE = 16;
    /** Value of the setters. */
    private static final float VALUE = 1.5F;

    /**
     * Returns the entries in presentation order.
     *
     * @param buffer The buffer.
     * @return The entry indexes by rank.
     */
    private static int[] order(CommandBuffer buffer)
    {
        final int[] order = new int[buffer.size()];
        for (int rank = 0; rank < order.length; rank++)
        {
            order[rank] = buffer.getEntry(rank);
        }
        return order;
    }

    /**
     * An entry keeps its source rectangle and gets the default values.
     */
    @Test
    void testDefaults()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);

        assertSame(buffer, buffer.add(TEXTURE, 1, 2, SIZE, SIZE + 1));

        assertEquals(1, buffer.size());
        assertEquals(TEXTURE, buffer.getTexture(0));
        assertEquals(1, buffer.getSourceX(0));
        assertEquals(2, buffer.getSourceY(0));
        assertEquals(SIZE, buffer.getSourceWidth(0));
        assertEquals(SIZE + 1, buffer.getSourceHeight(0));
        assertEquals(0.0F, buffer.getX(0));
        assertEquals(0.0F, buffer.getY(0));
        assertEquals(1.0F, buffer.getScaleX(0));
        assertEquals(1.0F, buffer.getScaleY(0));
        assertEquals(0.0F, buffer.getRotation(0));
        assertEquals(0.0F, buffer.getPivotX(0));
        assertEquals(0.0F, buffer.getPivotY(0));
        assertEquals(CommandBuffer.NO_TINT, buffer.getTint(0));
        assertEquals(1.0F, buffer.getAlpha(0));
        assertEquals(0, buffer.getLayer(0));
        assertEquals(0, buffer.getViewportX(0));
        assertEquals(0, buffer.getViewportY(0));
        assertEquals(WIDTH, buffer.getViewportWidth(0));
        assertEquals(HEIGHT, buffer.getViewportHeight(0));
    }

    /**
     * The chained setters complete the last entry only.
     */
    @Test
    void testSetters()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE)
              .position(VALUE, -VALUE)
              .scale(-1.0F, 2.0F)
              .rotation(90.0F)
              .pivot(8.0F, 4.0F)
              .tint(0x80FF0000)
              .alpha(0.5F)
              .layer(3);

        assertEquals(VALUE, buffer.getX(1));
        assertEquals(-VALUE, buffer.getY(1));
        assertEquals(-1.0F, buffer.getScaleX(1));
        assertEquals(2.0F, buffer.getScaleY(1));
        assertEquals(90.0F, buffer.getRotation(1));
        assertEquals(8.0F, buffer.getPivotX(1));
        assertEquals(4.0F, buffer.getPivotY(1));
        assertEquals(0x80FF0000, buffer.getTint(1));
        assertEquals(0.5F, buffer.getAlpha(1));
        assertEquals(3, buffer.getLayer(1));
        assertEquals(0.0F, buffer.getX(0));
        assertEquals(0, buffer.getLayer(0));
    }

    /**
     * The setters can be called in any order, a second call replaces the first value, and a setter never changes an
     * entry added before the last one.
     */
    @Test
    void testSettersOrderAndReplacement()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE).layer(2).position(VALUE, VALUE).layer(1);
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE).position(-VALUE, 0.0F);

        assertEquals(1, buffer.getLayer(0));
        assertEquals(VALUE, buffer.getX(0));
        assertEquals(VALUE, buffer.getY(0));
        assertEquals(0, buffer.getLayer(1));
        assertEquals(-VALUE, buffer.getX(1));
    }

    /**
     * Entries are presented by ascending layer, then submission order.
     */
    @Test
    void testPresentationOrder()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 2);
        final int[] layers =
        {
            2, 0, 1, 0, 2, -1, 1
        };
        for (final int layer : layers)
        {
            buffer.add(TEXTURE, 0, 0, SIZE, SIZE).layer(layer);
        }

        assertArrayEquals(new int[]
        {
            5, 1, 3, 2, 6, 0, 4
        }, order(buffer));

        buffer.add(TEXTURE, 0, 0, SIZE, SIZE).layer(-1);
        assertEquals(7, buffer.getEntry(1));
    }

    /**
     * Entries of one layer keep the submission order, and large buffers are sorted stably.
     */
    @Test
    void testStableSort()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        final int count = 1000;
        for (int i = 0; i < count; i++)
        {
            buffer.add(i, 0, 0, SIZE, SIZE).layer(i % 3 == 0 ? 1 : 0);
        }

        int previousLayer = Integer.MIN_VALUE;
        int previousTexture = -1;
        for (int rank = 0; rank < count; rank++)
        {
            final int entry = buffer.getEntry(rank);
            final int layer = buffer.getLayer(entry);
            assertTrue(layer >= previousLayer);
            if (layer == previousLayer)
            {
                assertTrue(buffer.getTexture(entry) > previousTexture);
            }
            previousLayer = layer;
            previousTexture = buffer.getTexture(entry);
        }
    }

    /**
     * A viewport applies to the entries added after it, until the next one or a reset; a clear resets it.
     */
    @Test
    void testViewports()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);
        for (int i = 0; i < 5; i++)
        {
            buffer.setViewport(new Viewport(i, 10 + i, 100, 50));
        }
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);
        buffer.resetViewport();
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);

        assertEquals(WIDTH, buffer.getViewportWidth(0));
        assertEquals(4, buffer.getViewportX(1));
        assertEquals(14, buffer.getViewportY(1));
        assertEquals(100, buffer.getViewportWidth(1));
        assertEquals(50, buffer.getViewportHeight(1));
        assertEquals(0, buffer.getViewportX(2));
        assertEquals(HEIGHT, buffer.getViewportHeight(2));

        buffer.setViewport(new Viewport(1, 1, 1, 1));
        buffer.clear();
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);
        assertEquals(WIDTH, buffer.getViewportWidth(0));
        assertThrows(IllegalArgumentException.class, () -> buffer.setViewport(null));
    }

    /**
     * The capacity doubles when full and is kept by a clear; entries survive the growth.
     */
    @Test
    void testGrowth()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 2);
        for (int i = 0; i < 5; i++)
        {
            buffer.add(i, 0, 0, SIZE, SIZE).layer(-i);
        }

        assertEquals(8, buffer.getCapacity());
        assertEquals(5, buffer.size());
        assertEquals(4, buffer.getEntry(0));
        assertEquals(3, buffer.getTexture(3));

        buffer.clear();
        assertEquals(0, buffer.size());
        assertEquals(8, buffer.getCapacity());
    }

    /**
     * Invalid arguments and accesses are rejected.
     */
    @Test
    void testInvalid()
    {
        assertThrows(IllegalArgumentException.class, () -> new CommandBuffer(0, HEIGHT, 1));
        assertThrows(IllegalArgumentException.class, () -> new CommandBuffer(WIDTH, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new CommandBuffer(WIDTH, HEIGHT, 0));

        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        assertThrows(IllegalStateException.class, () -> buffer.position(0.0F, 0.0F));
        assertThrows(IllegalStateException.class, () -> buffer.layer(1));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getEntry(0));
        assertThrows(IllegalArgumentException.class, () -> buffer.add(TEXTURE, 0, 0, 0, SIZE));
        assertThrows(IllegalArgumentException.class, () -> buffer.add(TEXTURE, 0, 0, SIZE, 0));

        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);
        assertThrows(IllegalArgumentException.class, () -> buffer.alpha(-0.1F));
        assertThrows(IllegalArgumentException.class, () -> buffer.alpha(1.1F));
        assertThrows(IllegalArgumentException.class, () -> buffer.alpha(Float.NaN));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getTexture(1));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getTexture(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getEntry(1));
    }

    /**
     * Frames that fit the capacity allocate nothing, sorting and viewports included (ADR-0019), measured with the
     * per-thread allocation counter of the JVM.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        final Viewport viewport = new Viewport(0, 0, 160, 120);
        long checksum = frames(buffer, viewport);

        final long before = bean.getCurrentThreadAllocatedBytes();
        checksum += frames(buffer, viewport);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(checksum > 0L);
    }

    /**
     * Writes and reads many frames.
     *
     * @param buffer The buffer.
     * @param viewport The viewport set in the middle of each frame.
     * @return A checksum of what was read.
     */
    private static long frames(CommandBuffer buffer, Viewport viewport)
    {
        long checksum = 0L;
        for (int frame = 0; frame < 1000; frame++)
        {
            buffer.clear();
            for (int i = 0; i < 64; i++)
            {
                if (i == 32)
                {
                    buffer.setViewport(viewport);
                }
                buffer.add(i, 0, 0, SIZE, SIZE).position((float) i, (float) frame).layer(63 - i);
            }
            for (int rank = 0; rank < buffer.size(); rank++)
            {
                final int entry = buffer.getEntry(rank);
                checksum += buffer.getTexture(entry) + buffer.getViewportWidth(entry);
            }
        }
        return checksum;
    }
}
