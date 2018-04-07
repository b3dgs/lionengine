/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Config}.
 */
public final class ConfigTest
{
    /** Icon test. */
    private final String ICON = "image.png";

    /**
     * Test <code>null</code> resolution.
     */
    @Test(expected = LionEngineException.class)
    public void testResolutionNull()
    {
        Assert.assertNotNull(new Config(null, 1, true));
    }

    /**
     * Test failure depth.
     */
    @Test(expected = LionEngineException.class)
    public void testDepthInvalid()
    {
        Assert.assertNotNull(new Config(new Resolution(320, 240, 60), 0, true));
    }

    /**
     * Test getter.
     */
    @Test
    public void testGetter()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 32, true);

        Assert.assertEquals(32, config.getDepth());
        Assert.assertTrue(config.isWindowed());
        Assert.assertEquals(output, config.getOutput());
    }

    /**
     * Test source.
     */
    @Test
    public void testSource()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 32, true);
        config.setSource(output);

        Assert.assertEquals(output.getWidth(), config.getSource().getWidth());
        Assert.assertEquals(output.getHeight(), config.getSource().getHeight());
        Assert.assertEquals(output.getRate(), config.getSource().getRate());
    }

    /**
     * Test <code>null</code> source.
     */
    @Test(expected = LionEngineException.class)
    public void testSourceNull()
    {
        new Config(new Resolution(320, 240, 60), 32, true).setSource(null);
    }

    /**
     * Test icon.
     */
    @Test
    public void testIcon()
    {
        Assert.assertFalse(Config.windowed(new Resolution(320, 240, 60)).getIcon().isPresent());

        final Media icon = Medias.create(ICON);

        Assert.assertEquals(icon, new Config(new Resolution(320, 240, 60), 32, true, icon).getIcon().get());
    }

    /**
     * Test default windowed config.
     */
    @Test
    public void testDefaultWindowed()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.windowed(output);

        Assert.assertTrue(config.isWindowed());
        Assert.assertEquals(output, config.getOutput());
        Assert.assertEquals(32, config.getDepth());
    }

    /**
     * Test default fullscreen config.
     */
    @Test
    public void testDefaultFullscreen()
    {
        final Resolution output = new Resolution(320, 240, 60);
        final Config config = Config.fullscreen(output);

        Assert.assertFalse(config.isWindowed());
        Assert.assertEquals(output, config.getOutput());
        Assert.assertEquals(32, config.getDepth());
    }
}
