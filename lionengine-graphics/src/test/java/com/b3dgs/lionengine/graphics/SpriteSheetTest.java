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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.testsupport.RecordingBackend;
import com.sun.management.ThreadMXBean;

/**
 * Test {@link SpriteSheet}: grid index math, margin, spacing, derived and given counts, out-of-range indexes
 * (REQ-SHEET-001, ADR-0033).
 */
final class SpriteSheetTest
{
    /** Id of the sheets. */
    private static final ResourceKey ID = new ResourceKey("sheets/tiles.json");
    /** Cell lookups per allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /** The image cache. */
    private ImageCache cache;
    /** The provider of the images. */
    private FakeResourceProvider provider;

    /**
     * Creates the cache of each test.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        cache = new ImageCache(new Resources(provider), new RecordingBackend());
    }

    /**
     * Loads an image of a size.
     *
     * @param width The width.
     * @param height The height.
     * @return The image.
     */
    private Image image(int width, int height)
    {
        final ResourceKey key = new ResourceKey("images/" + width + "x" + height + ".png");
        provider.put(key, PngWriter.rgba(width, height, new int[width * height]).write());
        return cache.load(key);
    }

    /**
     * A sheet added in code has no template; an unknown or {@code null} template name is refused.
     */
    @Test
    void testNoTemplate()
    {
        final SpriteSheet sheet = new SpriteSheet(ID, image(16, 16), new SheetGrid(16, 16), 0.0, 0.0);

        assertTrue(sheet.getTemplateNames().isEmpty());
        assertFalse(sheet.hasTemplate("red"));
        final IllegalStateException error = assertThrows(IllegalStateException.class, () -> sheet.getTemplate("red"));
        assertEquals("Template not in the sheet sheets/tiles.json: red", error.getMessage());
        assertThrows(IllegalArgumentException.class, () -> sheet.getTemplate(null));
        assertThrows(IllegalArgumentException.class, () -> sheet.hasTemplate(null));
    }

    /**
     * The templates of a definition are kept in the order of the file and cannot be changed.
     */
    @Test
    void testTemplates()
    {
        final Map<String, ColorMapping> templates = new LinkedHashMap<>();
        templates.put("red", ColorMapping.NONE.with(0xFFFF00FF, 0xFFFF0000));
        templates.put("blue", ColorMapping.NONE.with(0xFFFF00FF, 0xFF0000FF));
        final SpriteSheet sheet = new SpriteSheet(ID,
                                                  image(16, 16),
                                                  new SheetGrid(16, 16),
                                                  0.0,
                                                  0.0,
                                                  Collections.unmodifiableMap(templates));

        assertEquals(List.of("red", "blue"), sheet.getTemplateNames());
        assertTrue(sheet.hasTemplate("blue"));
        assertEquals(0xFF0000FF, sheet.getTemplate("blue").apply(0xFFFF00FF));
        assertThrows(UnsupportedOperationException.class, () -> sheet.getTemplateNames().clear());
    }

    /**
     * Cells without margin or spacing tile the image row by row from the top-left.
     */
    @Test
    void testPlainGrid()
    {
        final Image image = image(64, 32);
        final SpriteSheet sheet = new SpriteSheet(ID, image, new SheetGrid(16, 16), 0.0, 0.0);

        assertSame(ID, sheet.getId());
        assertSame(image, sheet.getImage());
        assertEquals(4, sheet.getColumns());
        assertEquals(2, sheet.getRows());
        assertEquals(8, sheet.getCellCount());
        assertEquals(16, sheet.getCellWidth());
        assertEquals(16, sheet.getCellHeight());
        assertEquals(0, sheet.getCellX(0));
        assertEquals(0, sheet.getCellY(0));
        assertEquals(48, sheet.getCellX(3));
        assertEquals(0, sheet.getCellY(3));
        assertEquals(0, sheet.getCellX(4));
        assertEquals(16, sheet.getCellY(4));
        assertEquals(48, sheet.getCellX(7));
        assertEquals(16, sheet.getCellY(7));
    }

    /**
     * The margin moves the first cell, the spacing separates the cells; the counts derived from the image follow the
     * formula of Tiled and Godot, {@code (size - margin + spacing) / (cell + spacing)}, so the right and bottom margins
     * may be missing.
     *
     * @param width The image width.
     * @param height The image height.
     * @param columns The expected columns.
     * @param rows The expected rows.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "38, 21, 3, 2", "39, 22, 3, 2", "37, 21, 2, 2", "54, 30, 4, 2"
    })
    void testMarginAndSpacing(int width, int height, int columns, int rows)
    {
        final SheetGrid grid = new SheetGrid(10, 8, 0, 0, 2, 3);
        final SpriteSheet sheet = new SpriteSheet(ID, image(width, height), grid, 0.0, 0.0);

        assertEquals(2, sheet.getMargin());
        assertEquals(3, sheet.getSpacing());
        assertEquals(columns, sheet.getColumns());
        assertEquals(rows, sheet.getRows());
        assertEquals(2, sheet.getCellX(0));
        assertEquals(2, sheet.getCellY(0));
        assertEquals(15, sheet.getCellX(1));
        assertEquals(13, sheet.getCellY(columns));
    }

    /**
     * Given counts are kept when they fit, also when they leave part of the image unused.
     */
    @Test
    void testGivenCounts()
    {
        final SpriteSheet sheet = new SpriteSheet(ID, image(64, 32), new SheetGrid(16, 16, 2, 1, 0, 0), 0.0, 0.0);

        assertEquals(2, sheet.getColumns());
        assertEquals(1, sheet.getRows());
        assertEquals(2, sheet.getCellCount());
        assertEquals(16, sheet.getCellX(1));
    }

    /**
     * A grid that does not fit is refused, with the id in the message: no derived cell fits, or given counts exceed
     * the image.
     *
     * @param columns The columns.
     * @param rows The rows.
     * @param margin The margin.
     * @param spacing The spacing.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "5, 0, 0, 0", "0, 3, 0, 0", "4, 0, 1, 0", "0, 0, 49, 0", "4, 2, 0, 1", "0, 0, 20, 0"
    })
    void testGridOutside(int columns, int rows, int margin, int spacing)
    {
        final Image image = image(64, 32);
        final SheetGrid grid = new SheetGrid(16, 16, columns, rows, margin, spacing);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                                () -> new SpriteSheet(ID, image, grid, 0.0, 0.0));
        assertTrue(exception.getMessage().contains(ID.path()), exception.getMessage());
    }

    /**
     * An index outside the sheet is refused.
     *
     * @param index The index.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        -1, 8, Integer.MAX_VALUE, Integer.MIN_VALUE
    })
    void testIndexOutside(int index)
    {
        final SpriteSheet sheet = new SpriteSheet(ID, image(64, 32), new SheetGrid(16, 16), 0.0, 0.0);

        assertThrows(IndexOutOfBoundsException.class, () -> sheet.getCellX(index));
        assertThrows(IndexOutOfBoundsException.class, () -> sheet.getCellY(index));
    }

    /**
     * Cell lookups allocate nothing (REQ-RT-006): sprites read them at every frame.
     */
    @Test
    void testLookupAllocatesNothing()
    {
        final SpriteSheet sheet = new SpriteSheet(ID, image(64, 32), new SheetGrid(16, 16), 0.0, 0.0);
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        int sum = 0;

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < ITERATIONS; i++)
        {
            final int index = i % sheet.getCellCount();
            sum += sheet.getCellX(index) + sheet.getCellY(index);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
        assertEquals(3_200_000, sum);
    }
}
