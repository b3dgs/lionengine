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
package com.b3dgs.lionengine.render.awt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.CpuFilter;
import com.b3dgs.lionengine.render.CustomFilter;
import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.sun.management.ThreadMXBean;

/**
 * Golden images of the CPU chain of custom filters, drawn headless on an off-screen surface (REQ-FILTER-003,
 * ADR-0064).
 */
final class AwtFiltersTest
{
    /** Logical frame width. */
    private static final int WIDTH = 2;
    /** Logical frame height. */
    private static final int HEIGHT = 2;
    /** Red, without alpha as in the logical frame. */
    private static final int RED = 0xFF0000;
    /** Blue. */
    private static final int BLUE = 0x0000FF;
    /** Black of the surface outside the geometry. */
    private static final int BLACK = 0x000000;
    /** Bytes tolerated for the allocation measurement itself. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** Post filter inverting the colors and writing a transparent alpha, which is ignored. */
    private static final OutputFilter INVERT = new OutputFilter("game:invert", FilterKind.POST);
    /** Post filter keeping the red channel only. */
    private static final OutputFilter RED_ONLY = new OutputFilter("game:red", FilterKind.POST);
    /** Scaling filter painting every target pixel blue. */
    private static final OutputFilter PAINT = new OutputFilter("game:paint", FilterKind.SCALING);

    /**
     * Creates a custom filter.
     *
     * @param filter The identifier.
     * @param cpu The CPU implementation, {@code null} if none.
     * @return The custom filter.
     */
    private static CustomFilter custom(OutputFilter filter, CpuFilter cpu)
    {
        return new CustomFilter()
        {
            @Override
            public OutputFilter getFilter()
            {
                return filter;
            }

            @Override
            public ResourceKey getFragmentShader()
            {
                return null;
            }

            @Override
            public CpuFilter getCpuFilter()
            {
                return cpu;
            }
        };
    }

    /**
     * Creates the logical frame: red on the left column, blue on the right one.
     *
     * @return The frame.
     */
    private static BufferedImage frame()
    {
        final BufferedImage frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++)
        {
            frame.setRGB(0, y, RED);
            frame.setRGB(1, y, BLUE);
        }
        return frame;
    }

    /** Filters of the test. */
    private final AwtFilters filters = new AwtFilters(WIDTH, HEIGHT);
    /** Source alpha seen by the inverting filter, {@code 0} until it runs. */
    private int sourceAlpha;
    /** Surface of the test, 8 by 6, the geometry 4 by 4 at (2, 1). */
    private final BufferedImage surface = new BufferedImage(8, 6, BufferedImage.TYPE_INT_RGB);
    /** Graphics of the surface. */
    private final Graphics2D graphics = surface.createGraphics();
    /** Geometry of the test: a scale of 2 at (2, 1). */
    private final PresentationGeometry geometry = new PresentationGeometry(2, 1, 4, 4, 2.0, 2.0);

    /**
     * Creates the test and registers its filters.
     */
    AwtFiltersTest()
    {
        filters.register(custom(INVERT, (source, sw, sh, target, tw, th) ->
        {
            sourceAlpha = source[0] >>> 24;
            for (int i = 0; i < tw * th; i++)
            {
                target[i] = ~source[i] & 0x00FFFFFF;
            }
        }));
        filters.register(custom(RED_ONLY, (source, sw, sh, target, tw, th) ->
        {
            for (int i = 0; i < tw * th; i++)
            {
                target[i] = source[i] & 0xFFFF0000;
            }
        }));
        filters.register(custom(PAINT, (source, sw, sh, target, tw, th) ->
        {
            for (int i = 0; i < tw * th; i++)
            {
                target[i] = 0xFF000000 | BLUE;
            }
        }));
    }

    /**
     * Returns a pixel of the surface.
     *
     * @param x The horizontal position.
     * @param y The vertical position.
     * @return The color, without alpha.
     */
    private int pixel(int x, int y)
    {
        return surface.getRGB(x, y) & 0xFFFFFF;
    }

    /**
     * Releases the graphics.
     */
    @AfterEach
    void dispose()
    {
        graphics.dispose();
    }

    /**
     * The standard scaling filters and the custom filters with a CPU implementation are supported, a custom one only
     * with the kind it was registered with; CRT and a filter without CPU implementation are not.
     */
    @Test
    void testSupported()
    {
        final OutputFilter gpu = new OutputFilter("game:gpu", FilterKind.POST);
        filters.register(custom(gpu, null));

        assertTrue(filters.isSupported(OutputFilter.NONE));
        assertTrue(filters.isSupported(OutputFilter.BILINEAR));
        assertTrue(filters.isSupported(OutputFilter.HQ2X));
        assertFalse(filters.isSupported(OutputFilter.CRT));
        assertTrue(filters.isSupported(INVERT));
        assertFalse(filters.isSupported(new OutputFilter(INVERT.id(), FilterKind.SCALING)));
        assertFalse(filters.isSupported(gpu));
        final CustomFilter standard = custom(OutputFilter.NONE, null);
        assertThrows(IllegalArgumentException.class, () -> filters.register(standard));
    }

    /**
     * A post filter runs on the frame scaled by {@code NONE}, at the geometry size and position, on opaque pixels;
     * the alpha it writes is ignored; the surface outside the geometry is untouched.
     */
    @Test
    void testPostFilter()
    {
        filters.draw(graphics, frame(), geometry, List.of(INVERT));

        assertEquals(0xFF, sourceAlpha);
        assertEquals(~RED & 0xFFFFFF, pixel(2, 1));
        assertEquals(~RED & 0xFFFFFF, pixel(3, 4));
        assertEquals(~BLUE & 0xFFFFFF, pixel(4, 1));
        assertEquals(~BLUE & 0xFFFFFF, pixel(5, 4));
        assertEquals(BLACK, pixel(1, 1));
        assertEquals(BLACK, pixel(6, 1));
        assertEquals(BLACK, pixel(2, 0));
        assertEquals(BLACK, pixel(2, 5));
    }

    /**
     * Post filters run in the chain order, each on the output of the previous one, after a standard scaling filter.
     */
    @Test
    void testPostOrder()
    {
        filters.draw(graphics, frame(), geometry, List.of(OutputFilter.NONE, INVERT, RED_ONLY));

        assertEquals(0x000000, pixel(2, 1));
        assertEquals(0xFF0000, pixel(4, 1));

        filters.draw(graphics, frame(), geometry, List.of(RED_ONLY, INVERT));

        assertEquals(0x00FFFF, pixel(2, 1));
        assertEquals(0xFFFFFF, pixel(4, 1));
    }

    /**
     * A custom scaling filter writes the whole geometry from the logical frame, then the post filters run.
     */
    @Test
    void testCustomScaling()
    {
        filters.draw(graphics, frame(), geometry, List.of(PAINT));

        assertEquals(BLUE, pixel(2, 1));
        assertEquals(BLUE, pixel(5, 4));
        assertEquals(BLACK, pixel(1, 1));

        filters.draw(graphics, frame(), geometry, List.of(PAINT, INVERT));

        assertEquals(~BLUE & 0xFFFFFF, pixel(3, 2));
    }

    /**
     * A chain of standard filters is drawn without the CPU chain; an empty geometry draws nothing.
     */
    @Test
    void testStandardAndEmpty()
    {
        filters.draw(graphics, frame(), geometry, List.of());

        assertEquals(RED, pixel(2, 1));
        assertEquals(BLUE, pixel(5, 4));

        filters.draw(graphics, frame(), new PresentationGeometry(0, 0, 0, 0, 1.0, 1.0), List.of(INVERT));

        assertEquals(RED, pixel(2, 1));
    }

    /**
     * Once the buffers of a geometry size exist, a frame of the CPU chain allocates nothing.
     */
    @Test
    void testAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final BufferedImage frame = frame();
        final List<OutputFilter> chain = List.of(INVERT, RED_ONLY);
        for (int i = 0; i < 20; i++)
        {
            filters.draw(graphics, frame, geometry, chain);
        }

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < 10; i++)
        {
            filters.draw(graphics, frame, geometry, chain);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < 10 * ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }
}
