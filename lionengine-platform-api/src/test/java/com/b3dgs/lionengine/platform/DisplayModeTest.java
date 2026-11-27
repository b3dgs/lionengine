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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test {@link DisplayMode} (REQ-DISPLAY-003, ADR-0022).
 */
final class DisplayModeTest
{
    /**
     * Known refresh rate and bit depth.
     */
    @Test
    void testKnown()
    {
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);

        assertTrue(mode.isRefreshRateKnown());
        assertTrue(mode.isBitDepthKnown());
    }

    /**
     * {@link DisplayMode#UNKNOWN} means unknown or multiple.
     */
    @Test
    void testUnknown()
    {
        final DisplayMode mode = new DisplayMode(1920, 1080, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN);

        assertFalse(mode.isRefreshRateKnown());
        assertFalse(mode.isBitDepthKnown());
    }

    /**
     * Invalid values are rejected.
     *
     * @param width The width.
     * @param height The height.
     * @param rate The refresh rate.
     * @param depth The bit depth.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 1080, 60, 32", "1920, 0, 60, 32", "1920, 1080, -1, 32", "1920, 1080, 60, -1"
    })
    void testInvalid(int width, int height, int rate, int depth)
    {
        assertThrows(IllegalArgumentException.class, () -> new DisplayMode(width, height, rate, depth));
    }
}
