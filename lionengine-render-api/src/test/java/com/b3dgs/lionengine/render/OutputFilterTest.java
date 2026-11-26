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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link OutputFilter} (REQ-FILTER-001, REQ-FILTER-003).
 */
final class OutputFilterTest
{
    /**
     * The standard filters have their ids and kinds.
     */
    @Test
    void testStandard()
    {
        assertEquals("lionengine:none", OutputFilter.NONE.id());
        assertEquals("lionengine:bilinear", OutputFilter.BILINEAR.id());
        assertEquals("lionengine:hq2x", OutputFilter.HQ2X.id());
        assertEquals("lionengine:crt", OutputFilter.CRT.id());
        assertEquals(FilterKind.SCALING, OutputFilter.NONE.kind());
        assertEquals(FilterKind.SCALING, OutputFilter.BILINEAR.kind());
        assertEquals(FilterKind.SCALING, OutputFilter.HQ2X.kind());
        assertEquals(FilterKind.POST, OutputFilter.CRT.kind());
    }

    /**
     * The fallback order of ADR-0006 and ADR-0062: {@code HQ2X} to bilinear, bilinear and custom scaling filters to
     * {@code NONE}; none after {@code NONE} or a post filter.
     */
    @Test
    void testFallback()
    {
        assertEquals(OutputFilter.BILINEAR, OutputFilter.HQ2X.fallback());
        assertEquals(OutputFilter.NONE, OutputFilter.BILINEAR.fallback());
        assertEquals(OutputFilter.NONE, new OutputFilter("game:sharp", FilterKind.SCALING).fallback());
        assertNull(OutputFilter.NONE.fallback());
        assertNull(OutputFilter.CRT.fallback());
        assertNull(new OutputFilter("game:glow", FilterKind.POST).fallback());
    }

    /**
     * The four standard filters are standard, a custom one is not; the id decides, so a standard id under another kind
     * is standard too and cannot be registered as a custom filter.
     */
    @Test
    void testIsStandard()
    {
        assertTrue(OutputFilter.NONE.isStandard());
        assertTrue(OutputFilter.BILINEAR.isStandard());
        assertTrue(OutputFilter.HQ2X.isStandard());
        assertTrue(OutputFilter.CRT.isStandard());
        assertFalse(new OutputFilter("game:glow", FilterKind.POST).isStandard());
        assertTrue(new OutputFilter("lionengine:crt", FilterKind.SCALING).isStandard());
    }

    /**
     * Valid custom ids are accepted.
     *
     * @param id The id.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "game:sepia", "my-game:old_tv2", "a:b"
    })
    void testValidCustom(String id)
    {
        assertEquals(id, new OutputFilter(id, FilterKind.POST).id());
    }

    /**
     * Invalid ids are rejected.
     *
     * @param id The id.
     */
    @ParameterizedTest
    @ValueSource(strings =
    {
        "", "sepia", ":sepia", "game:", "Game:sepia", "game:Sepia", "1game:sepia", "game:1sepia", "game:se:pia",
        "game :sepia", "_game:sepia"
    })
    void testInvalidId(String id)
    {
        assertThrows(IllegalArgumentException.class, () -> new OutputFilter(id, FilterKind.SCALING));
    }

    /**
     * {@code null} arguments are rejected.
     */
    @Test
    void testNull()
    {
        assertThrows(IllegalArgumentException.class, () -> new OutputFilter(null, FilterKind.SCALING));
        assertThrows(IllegalArgumentException.class, () -> new OutputFilter("game:sepia", null));
    }
}
