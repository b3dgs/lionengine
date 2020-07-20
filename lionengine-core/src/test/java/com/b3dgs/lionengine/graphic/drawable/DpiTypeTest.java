/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.graphic.DpiType;

/**
 * Test {@link DpiType}.
 */
final class DpiTypeTest
{
    /**
     * Get a factor resolution.
     * 
     * @param baseline The baseline resolution.
     * @param factor The applied factor.
     * @return The resolution with factor.
     */
    private static Resolution get(Resolution baseline, double factor)
    {
        return new Resolution((int) (baseline.getWidth() * factor), (int) (baseline.getHeight() * factor), 60);
    }

    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(DpiType.class);
    }

    /**
     * Test the from function.
     */
    @Test
    void testFrom()
    {
        final Resolution baseline = new Resolution(640, 480, 60);

        assertEquals(DpiType.LDPI, DpiType.from(baseline, get(baseline, 0.75)));
        assertEquals(DpiType.MDPI, DpiType.from(baseline, get(baseline, 1.0)));
        assertEquals(DpiType.HDPI, DpiType.from(baseline, get(baseline, 1.5)));
        assertEquals(DpiType.XHDPI, DpiType.from(baseline, get(baseline, 2.0)));
        assertEquals(DpiType.XXHDPI, DpiType.from(baseline, get(baseline, 3.0)));

        assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(320, 200, 60)));
        assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(320, 240, 60)));

        assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(200, 320, 60)));
        assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(240, 320, 60)));
        assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(400, 640, 60)));
        assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(480, 640, 60)));

        assertEquals(DpiType.MDPI, DpiType.from(baseline, baseline));
        assertEquals(DpiType.MDPI, DpiType.from(baseline, new Resolution(800, 600, 60)));
        assertEquals(DpiType.MDPI, DpiType.from(baseline, new Resolution(720, 1280, 60)));
        assertEquals(DpiType.MDPI, DpiType.from(baseline, new Resolution(800, 1280, 60)));

        assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1280, 720, 60)));
        assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1280, 800, 60)));
        assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(960, 1280, 60)));
        assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1080, 1920, 60)));
        assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1200, 1920, 60)));

        assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1280, 960, 60)));
        assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1600, 2560, 60)));
        assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1920, 1080, 60)));
        assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1920, 1200, 60)));

        assertEquals(DpiType.XXHDPI, DpiType.from(baseline, new Resolution(2560, 1600, 60)));
        assertEquals(DpiType.XXHDPI, DpiType.from(baseline, new Resolution(2560, 1920, 60)));
        assertEquals(DpiType.XXHDPI, DpiType.from(baseline, new Resolution(1920, 2560, 60)));
    }
}
