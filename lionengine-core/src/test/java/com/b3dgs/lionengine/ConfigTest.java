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

import com.b3dgs.lionengine.core.Medias;

/**
 * Test the config class.
 */
public class ConfigTest
{
    /** Resolution. */
    private static final Resolution OUTPUT = new Resolution(320, 240, 60);
    /** Config. */
    private static final Config CONFIG = new Config(OUTPUT, 32, true);
    /** Icon test. */
    private final String ICON = "image.png";

    /**
     * Test the config failure resolution.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureResolution()
    {
        Assert.assertNotNull(new Config(null, 1, true));
    }

    /**
     * Test the config failure depth.
     */
    @Test(expected = LionEngineException.class)
    public void testFailureDepth()
    {
        Assert.assertNotNull(new Config(OUTPUT, 0, true));
    }

    /**
     * Test the config getter.
     */
    @Test
    public void testGetter()
    {
        Assert.assertEquals(32, CONFIG.getDepth());
        Assert.assertTrue(CONFIG.isWindowed());
        Assert.assertEquals(OUTPUT, CONFIG.getOutput());
    }

    /**
     * Test the config source.
     */
    @Test
    public void testSource()
    {
        CONFIG.setSource(OUTPUT);
        Assert.assertEquals(OUTPUT.getWidth(), CONFIG.getSource().getWidth());
        Assert.assertEquals(OUTPUT.getHeight(), CONFIG.getSource().getHeight());
        Assert.assertEquals(OUTPUT.getRate(), CONFIG.getSource().getRate());
    }

    /**
     * Test the get applet with <code>null</code> argument.
     */
    @Test(expected = LionEngineException.class)
    public void testGetAppletNullArgument()
    {
        Assert.assertNull(CONFIG.getApplet((Class<AppletMock>) null));
    }

    /**
     * Test the config applet.
     */
    @Test
    public void testApplet()
    {
        CONFIG.setApplet(null);
        Assert.assertFalse(CONFIG.getApplet(AppletMock.class).isPresent());
        Assert.assertFalse(CONFIG.hasApplet());

        CONFIG.setApplet(new AppletMock());
        Assert.assertTrue(CONFIG.getApplet(AppletMock.class).isPresent());
        Assert.assertTrue(CONFIG.hasApplet());
    }

    /**
     * Test the config icon.
     */
    @Test
    public void testIcon()
    {
        Assert.assertFalse(CONFIG.getIcon().isPresent());
        final Media icon = Medias.create(ICON);
        final Config config = new Config(CONFIG.getOutput(), CONFIG.getDepth(), CONFIG.isWindowed(), icon);
        Assert.assertEquals(icon, config.getIcon().get());
    }

    /**
     * Test the default windowed config.
     */
    @Test
    public void testDefaultWindowed()
    {
        final Config config = Config.windowed(OUTPUT);
        Assert.assertTrue(config.isWindowed());
        Assert.assertEquals(OUTPUT, config.getOutput());
        Assert.assertEquals(32, config.getDepth());
    }

    /**
     * Test the default windowed config.
     */
    @Test
    public void testDefaultFullscreen()
    {
        final Config config = Config.fullscreen(OUTPUT);
        Assert.assertFalse(config.isWindowed());
        Assert.assertEquals(OUTPUT, config.getOutput());
        Assert.assertEquals(32, config.getDepth());
    }
}
