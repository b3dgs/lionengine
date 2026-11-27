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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.FrameScaling;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.Viewport;

/**
 * Runs {@code render-api} on Android API 21 (ADR-0044): command buffer entries, defaults and presentation order, the
 * scaling function and the fallback order of the output filters. Expected values are the ones of the JVM tests (CommandBufferTest, FrameScalingTest).
 */
@RunWith(AndroidJUnit4.class)
public final class RenderTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 240;
    /** Texture of the tests. */
    private static final int TEXTURE = 7;
    /** Size of a source rectangle. */
    private static final int SIZE = 16;
    /** Scale of the tests. */
    private static final double THREE = 3.0;

    /**
     * Creates the test.
     */
    public RenderTest()
    {
        super();
    }

    /**
     * An entry keeps its source rectangle, gets the default values, and the chained setters complete it.
     */
    @Test
    public void testEntries()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 1);
        buffer.add(TEXTURE, 1, 2, SIZE, SIZE + 1);
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE)
              .position(1.5F, -1.5F)
              .scale(-1.0F, 2.0F)
              .rotation(90.0F)
              .tint(0x80FF0000)
              .alpha(0.5F)
              .layer(3);

        assertEquals(2, buffer.size());
        assertEquals(TEXTURE, buffer.getTexture(0));
        assertEquals(SIZE + 1, buffer.getSourceHeight(0));
        assertEquals(1.0F, buffer.getScaleX(0), 0.0F);
        assertEquals(CommandBuffer.NO_TINT, buffer.getTint(0));
        assertEquals(1.0F, buffer.getAlpha(0), 0.0F);
        assertEquals(WIDTH, buffer.getViewportWidth(0));
        assertEquals(-1.5F, buffer.getY(1), 0.0F);
        assertEquals(2.0F, buffer.getScaleY(1), 0.0F);
        assertEquals(90.0F, buffer.getRotation(1), 0.0F);
        assertEquals(0x80FF0000, buffer.getTint(1));
        assertEquals(3, buffer.getLayer(1));
    }

    /**
     * Entries are presented by ascending layer, then submission order; the buffer grows past its capacity.
     */
    @Test
    public void testPresentationOrder()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 2);
        final int[] layers = {2, 0, 1, 0, 2, -1, 1};
        for (final int layer : layers)
        {
            buffer.add(TEXTURE, 0, 0, SIZE, SIZE).layer(layer);
        }
        final int[] order = new int[buffer.size()];
        for (int rank = 0; rank < order.length; rank++)
        {
            order[rank] = buffer.getEntry(rank);
        }

        assertArrayEquals(new int[] {5, 1, 3, 2, 6, 0, 4}, order);
    }

    /**
     * A viewport applies to the entries added after it.
     */
    @Test
    public void testViewport()
    {
        final CommandBuffer buffer = new CommandBuffer(WIDTH, HEIGHT, 2);
        buffer.setViewport(new Viewport(10, 20, 100, 50));
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);
        buffer.resetViewport();
        buffer.add(TEXTURE, 0, 0, SIZE, SIZE);

        assertEquals(10, buffer.getViewportX(0));
        assertEquals(50, buffer.getViewportHeight(0));
        assertEquals(0, buffer.getViewportX(1));
        assertEquals(HEIGHT, buffer.getViewportHeight(1));
        assertThrows(IllegalArgumentException.class, () -> new Viewport(0, 0, 0, 1));
    }

    /**
     * Each scaling policy gives the rectangle and scales of the JVM, including the {@code INTEGER} fallback.
     */
    @Test
    public void testScaling()
    {
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 160, 0, 960, 720, THREE, THREE),
                     FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 1280, 720));
        final double scale = 1001.0 / WIDTH;
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 24, 1001, 751, scale, scale),
                     FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 1001, 800));
        assertEquals(new FrameScaling(ScalingPolicy.INTEGER, 81, 3, 640, 480, 2.0, 2.0),
                     FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 803, 487));
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 87, 300, 225, 300.0 / WIDTH, 300.0 / WIDTH),
                     FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 300, 400));
        assertEquals(new FrameScaling(ScalingPolicy.STRETCH, 0, 0, 1280, 720, 4.0, THREE),
                     FrameScaling.of(ScalingPolicy.STRETCH, WIDTH, HEIGHT, 1280, 720));
        assertSame(ScalingPolicy.FIT, FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 0, 0).policy());
    }

    /**
     * The fallback order of the output filters and the standard identifiers are the ones of the JVM (OutputFilterTest,
     * ADR-0062, ADR-0064).
     */
    @Test
    public void testFilterFallback()
    {
        assertSame(OutputFilter.BILINEAR, OutputFilter.HQ2X.fallback());
        assertSame(OutputFilter.NONE, OutputFilter.BILINEAR.fallback());
        assertSame(OutputFilter.NONE, new OutputFilter("game:sharp", FilterKind.SCALING).fallback());
        assertNull(OutputFilter.NONE.fallback());
        assertNull(OutputFilter.CRT.fallback());
        assertTrue(OutputFilter.HQ2X.isStandard());
        assertFalse(new OutputFilter("game:glow", FilterKind.POST).isStandard());
    }
}
