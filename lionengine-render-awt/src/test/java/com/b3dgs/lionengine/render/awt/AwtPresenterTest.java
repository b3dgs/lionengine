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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.OutputFilter;
import com.sun.management.ThreadMXBean;

/**
 * Golden images of the scaling filters of {@link AwtPresenter}, drawn headless on an off-screen image
 * (REQ-FILTER-004, ADR-0063).
 */
final class AwtPresenterTest
{
    /** Logical frame width. */
    private static final int WIDTH = 2;
    /** Logical frame height. */
    private static final int HEIGHT = 2;
    /** First color, without alpha as in the logical frame. */
    private static final int A = 0x000000;
    /** Second color. */
    private static final int B = 0xFFFFFF;
    /** Bytes tolerated for the allocation measurement itself. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Creates the checkerboard logical frame {@code A B / B A}.
     *
     * @return The frame.
     */
    private static BufferedImage frame()
    {
        final BufferedImage frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        frame.setRGB(0, 0, A);
        frame.setRGB(1, 0, B);
        frame.setRGB(0, 1, B);
        frame.setRGB(1, 1, A);
        return frame;
    }

    /** Presenter of the test. */
    private final AwtPresenter presenter = new AwtPresenter(WIDTH, HEIGHT);
    /** Surface of the test. */
    private BufferedImage surface;
    /** Graphics of the surface. */
    private Graphics2D graphics;

    /**
     * Draws the checkerboard on a new surface with a scaling filter.
     *
     * @param scale The integer scale on both axes.
     * @param filter The scaling filter.
     */
    private void draw(int scale, OutputFilter filter)
    {
        surface = new BufferedImage(WIDTH * scale, HEIGHT * scale, BufferedImage.TYPE_INT_RGB);
        graphics = surface.createGraphics();
        presenter.draw(graphics,
                       frame(),
                       new PresentationGeometry(0, 0, WIDTH * scale, HEIGHT * scale, scale, scale),
                       filter);
    }

    /**
     * Returns a pixel of the surface, without alpha.
     *
     * @param x The horizontal position.
     * @param y The vertical position.
     * @return The color.
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
        if (graphics != null)
        {
            graphics.dispose();
        }
    }

    /**
     * The scaling filter of a chain is its first filter if it scales, {@code NONE} otherwise.
     */
    @Test
    void testScalingFilter()
    {
        assertEquals(OutputFilter.NONE, AwtPresenter.getScalingFilter(List.of()));
        assertEquals(OutputFilter.NONE, AwtPresenter.getScalingFilter(List.of(OutputFilter.CRT)));
        assertEquals(OutputFilter.BILINEAR,
                     AwtPresenter.getScalingFilter(List.of(OutputFilter.BILINEAR, OutputFilter.CRT)));
        assertEquals(FilterKind.SCALING, AwtPresenter.getScalingFilter(List.of(OutputFilter.HQ2X)).kind());
    }

    /**
     * {@code NONE} repeats each pixel: blocks of one color.
     */
    @Test
    void testNone()
    {
        draw(4, OutputFilter.NONE);

        assertEquals(A, pixel(0, 0));
        assertEquals(A, pixel(3, 3));
        assertEquals(B, pixel(4, 0));
        assertEquals(B, pixel(3, 4));
        assertEquals(A, pixel(7, 7));
    }

    /**
     * Bilinear interpolation mixes the colors near the edges of the source pixels.
     */
    @Test
    void testBilinear()
    {
        draw(4, OutputFilter.BILINEAR);

        final int edge = pixel(3, 1) & 0xFF;
        assertTrue(edge > 0 && edge < 0xFF, () -> Integer.toHexString(pixel(3, 1)));
        assertEquals(pixel(3, 1), pixel(4, 6));
    }

    /**
     * {@code HQ2X} at a scale of 2 is the {@code Scale2x} image of the frame, pixel for pixel.
     */
    @Test
    void testHq2xAsScale2x()
    {
        draw(2, OutputFilter.HQ2X);

        final int[] expected =
        {
            A, A, B, B,
            A, B, A, B,
            B, A, B, A,
            B, B, A, A
        };
        for (int y = 0; y < 2 * HEIGHT; y++)
        {
            for (int x = 0; x < 2 * WIDTH; x++)
            {
                assertEquals(expected[y * 2 * WIDTH + x], pixel(x, y), x + "," + y);
            }
        }
    }

    /**
     * Below a scale of 2, {@code HQ2X} is drawn as {@code NONE}.
     */
    @Test
    void testHq2xBelowTwo()
    {
        surface = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        graphics = surface.createGraphics();
        presenter.draw(graphics, frame(), new PresentationGeometry(0, 0, 3, 3, 1.5, 1.5), OutputFilter.HQ2X);
        final BufferedImage none = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        final Graphics2D other = none.createGraphics();
        presenter.draw(other, frame(), new PresentationGeometry(0, 0, 3, 3, 1.5, 1.5), OutputFilter.NONE);
        other.dispose();

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                assertEquals(none.getRGB(x, y), surface.getRGB(x, y), x + "," + y);
            }
        }
    }

    /**
     * Once the doubled buffer exists, an {@code HQ2X} frame allocates nothing in the presenter: the pixels are copied
     * into the arrays it keeps.
     */
    @Test
    void testHq2xAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final AwtPresenter large = new AwtPresenter(64, 48);
        final BufferedImage frame = new BufferedImage(64, 48, BufferedImage.TYPE_INT_RGB);
        surface = new BufferedImage(128, 96, BufferedImage.TYPE_INT_RGB);
        graphics = surface.createGraphics();
        final PresentationGeometry geometry = new PresentationGeometry(0, 0, 128, 96, 2.0, 2.0);
        for (int i = 0; i < 20; i++)
        {
            large.draw(graphics, frame, geometry, OutputFilter.HQ2X);
        }

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < 10; i++)
        {
            large.draw(graphics, frame, geometry, OutputFilter.HQ2X);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < 10 * ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }
}
