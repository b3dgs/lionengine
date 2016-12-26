/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.core.drawable;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.graphic.DpiType;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the DPI type.
 */
public class DpiTypeTest
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
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(DpiType.class);
    }

    /**
     * Test the from function.
     */
    @Test
    public void testFrom()
    {
        final Resolution baseline = new Resolution(640, 480, 60);

        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, get(baseline, 0.75)));
        Assert.assertEquals(DpiType.MDPI, DpiType.from(baseline, get(baseline, 1.0)));
        Assert.assertEquals(DpiType.HDPI, DpiType.from(baseline, get(baseline, 1.5)));
        Assert.assertEquals(DpiType.XHDPI, DpiType.from(baseline, get(baseline, 2.0)));
        Assert.assertEquals(DpiType.XXHDPI, DpiType.from(baseline, get(baseline, 3.0)));

        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(320, 200, 60)));
        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(320, 240, 60)));

        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(200, 320, 60)));
        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(240, 320, 60)));
        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(400, 640, 60)));
        Assert.assertEquals(DpiType.LDPI, DpiType.from(baseline, new Resolution(480, 640, 60)));

        Assert.assertEquals(DpiType.MDPI, DpiType.from(baseline, baseline));
        Assert.assertEquals(DpiType.MDPI, DpiType.from(baseline, new Resolution(800, 600, 60)));
        Assert.assertEquals(DpiType.MDPI, DpiType.from(baseline, new Resolution(720, 1280, 60)));
        Assert.assertEquals(DpiType.MDPI, DpiType.from(baseline, new Resolution(800, 1280, 60)));

        Assert.assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1280, 720, 60)));
        Assert.assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1280, 800, 60)));
        Assert.assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(960, 1280, 60)));
        Assert.assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1080, 1920, 60)));
        Assert.assertEquals(DpiType.HDPI, DpiType.from(baseline, new Resolution(1200, 1920, 60)));

        Assert.assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1280, 960, 60)));
        Assert.assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1600, 2560, 60)));
        Assert.assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1920, 1080, 60)));
        Assert.assertEquals(DpiType.XHDPI, DpiType.from(baseline, new Resolution(1920, 1200, 60)));

        Assert.assertEquals(DpiType.XXHDPI, DpiType.from(baseline, new Resolution(2560, 1600, 60)));
        Assert.assertEquals(DpiType.XXHDPI, DpiType.from(baseline, new Resolution(2560, 1920, 60)));
        Assert.assertEquals(DpiType.XXHDPI, DpiType.from(baseline, new Resolution(1920, 2560, 60)));
    }
}
