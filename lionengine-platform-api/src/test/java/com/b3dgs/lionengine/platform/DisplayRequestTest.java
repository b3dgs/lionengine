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
package com.b3dgs.lionengine.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Test {@link DisplayRequest} and {@link EffectiveResult} (REQ-DISPLAY-008, ADR-0046).
 */
final class DisplayRequestTest
{
    /**
     * The filter chain is copied and cannot be modified.
     */
    @Test
    void testFiltersCopied()
    {
        final List<OutputFilter> filters = new ArrayList<>(Arrays.asList(OutputFilter.BILINEAR, OutputFilter.CRT));
        final DisplayRequest request = new DisplayRequest(ScreenMode.WINDOWED, null, ScalingPolicy.FIT, filters);
        filters.clear();

        assertEquals(List.of(OutputFilter.BILINEAR, OutputFilter.CRT), request.filters());
        assertNull(request.displayMode());
        final List<OutputFilter> chain = request.filters();
        assertThrows(UnsupportedOperationException.class, chain::clear);
    }

    /**
     * Requests with the same fields are equal.
     */
    @Test
    void testEquality()
    {
        final DisplayMode mode = new DisplayMode(1280, 720, 60, 32);

        assertEquals(new DisplayRequest(ScreenMode.FULL_SCREEN, mode, ScalingPolicy.INTEGER, List.of()),
                     new DisplayRequest(ScreenMode.FULL_SCREEN, mode, ScalingPolicy.INTEGER, List.of()));
    }

    /**
     * Missing mandatory fields and {@code null} filters are rejected.
     */
    @Test
    void testInvalid()
    {
        final List<OutputFilter> empty = List.of();
        final List<OutputFilter> withNull = Collections.singletonList(null);

        assertThrows(IllegalArgumentException.class, () -> new DisplayRequest(null, null, ScalingPolicy.FIT, empty));
        assertThrows(IllegalArgumentException.class,
                     () -> new DisplayRequest(ScreenMode.WINDOWED, null, null, empty));
        assertThrows(IllegalArgumentException.class,
                     () -> new DisplayRequest(ScreenMode.WINDOWED, null, ScalingPolicy.FIT, null));
        assertThrows(IllegalArgumentException.class,
                     () -> new DisplayRequest(ScreenMode.WINDOWED, null, ScalingPolicy.FIT, withNull));
    }

    /**
     * An effective result keeps both requests, the flag and the reason.
     */
    @Test
    void testEffectiveResult()
    {
        final List<OutputFilter> none = List.of();
        final DisplayRequest requested = new DisplayRequest(ScreenMode.FULL_SCREEN, null, ScalingPolicy.INTEGER, none);
        final DisplayRequest effective = new DisplayRequest(ScreenMode.FULL_WINDOW, null, ScalingPolicy.FIT, none);
        final EffectiveResult result = new EffectiveResult(requested, effective, true, "exclusive mode refused");

        assertEquals(requested, result.requested());
        assertEquals(effective, result.effective());
        assertTrue(result.degraded());
        assertEquals("exclusive mode refused", result.reason());
        assertThrows(IllegalArgumentException.class, () -> new EffectiveResult(requested, effective, false, null));
        assertThrows(IllegalArgumentException.class, () -> new EffectiveResult(null, effective, false, ""));
        assertThrows(IllegalArgumentException.class, () -> new EffectiveResult(requested, null, false, ""));
    }
}
