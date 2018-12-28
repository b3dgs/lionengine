/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Config}.
 */
public final class ConfigTest
{
    /**
     * Test <code>null</code> resolution.
     */
    @Test
    public void testResolutionNull()
    {
        assertThrows(() -> new Config(null, 1, true), Check.ERROR_NULL);
    }

    /**
     * Test failure depth.
     */
    @Test
    public void testDepthInvalid()
    {
        assertThrows(() -> new Config(new Resolution(320, 240, 60), 0, true),
                     Check.ERROR_ARGUMENT + 0 + Check.ERROR_SUPERIOR_STRICT + 0);
    }

    /**
     * Test getter.
     */
    @Test
    public void testGetter()
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
    public void testIcon()
    {
        assertFalse(Config.windowed(new Resolution(320, 240, 60)).getIcon().isPresent());

        final Media icon = Medias.create("image.png");

        assertEquals(icon, new Config(new Resolution(320, 240, 60), 32, true, icon).getIcon().get());
    }

    /**
     * Test default windowed config.
     */
    @Test
    public void testDefaultWindowed()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.windowed(output);

        assertTrue(config.isWindowed());
        assertEquals(output, config.getOutput());
        assertEquals(32, config.getDepth());
    }

    /**
     * Test default fullscreen config.
     */
    @Test
    public void testDefaultFullscreen()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.fullscreen(output);

        assertFalse(config.isWindowed());
        assertEquals(output, config.getOutput());
        assertEquals(32, config.getDepth());
    }
}
