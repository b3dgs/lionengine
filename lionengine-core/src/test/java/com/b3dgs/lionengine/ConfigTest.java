/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Config}.
 */
final class ConfigTest
{
    /**
     * Test <code>null</code> resolution.
     */
    @Test
    void testResolutionNull()
    {
        assertThrows(() -> new Config(null, 1, true), Check.ERROR_NULL);
    }

    /**
     * Test failure depth.
     */
    @Test
    void testDepthInvalid()
    {
        assertThrows(() -> new Config(new Resolution(320, 240, 60), -2, true),
                     Check.ERROR_ARGUMENT + -2 + Check.ERROR_SUPERIOR + -1);
    }

    /**
     * Test getter.
     */
    @Test
    void testGetter()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 32, true);

        assertEquals(32, config.getDepth());
        assertTrue(config.isWindowed());
        assertEquals(output, config.getOutput());
    }

    /**
     * Test icon.
     */
    @Test
    void testIcons()
    {
        assertTrue(Config.windowed(new Resolution(320, 240, 60)).getIcons().isEmpty());

        final Media icon = Medias.create("image.png");

        assertEquals(icon, new Config(new Resolution(320, 240, 60), 32, true, icon).getIcons().iterator().next());
    }

    /**
     * Test default windowed config.
     */
    @Test
    void testDefaultWindowed()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.windowed(output);

        assertTrue(config.isWindowed());
        assertEquals(output, config.getOutput());
        assertEquals(32, config.getDepth());
    }

    /**
     * Test default windowed config with icons.
     */
    @Test
    void testDefaultWindowedIcons()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.windowed(output, new Media[0]);

        assertTrue(config.isWindowed());
        assertEquals(output, config.getOutput());
        assertEquals(32, config.getDepth());
    }

    /**
     * Test default fullscreen config.
     */
    @Test
    void testDefaultFullscreen()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.fullscreen(output);

        assertFalse(config.isWindowed());
        assertEquals(output, config.getOutput());
        assertEquals(32, config.getDepth());
    }
}
