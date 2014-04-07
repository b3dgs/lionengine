/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Ratio;
import com.b3dgs.lionengine.Resolution;

/**
 * Test the config class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ConfigTest
{
    /**
     * Test the config failures.
     * 
     * @param resolution The resolution.
     * @param depth The depth.
     * @param filter The filter.
     */
    private static void testConfigFailure(Resolution resolution, int depth, Filter filter)
    {
        try
        {
            final Config config = new Config(resolution, depth, true, filter);
            Assert.assertNotNull(config);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        ConfigTest.testConfigFailure(null, 1, Filter.NONE);
        ConfigTest.testConfigFailure(new Resolution(320, 240, 60), 0, Filter.NONE);
        ConfigTest.testConfigFailure(new Resolution(320, 240, 60), 1, null);

        final Resolution output = new Resolution(320, 240, 60);
        final Config config = new Config(output, 32, true);
        Assert.assertEquals(32, config.getDepth());
        Assert.assertTrue(config.isWindowed());
        Assert.assertEquals(Filter.NONE, config.getFilter());
        Assert.assertEquals(output, config.getOutput());

        config.setSource(output);
        Assert.assertEquals(output.getWidth(), config.getSource().getWidth());

        config.setRatio(Ratio.R16_10);
        Assert.assertEquals(384, config.getOutput().getWidth());
        Assert.assertEquals(240, config.getOutput().getHeight());

        config.setSource(output);
        Assert.assertEquals(output.getWidth(), config.getSource().getWidth());

        config.setApplet(null);
        Assert.assertNull(config.getApplet());

        config.setApplet(new AppletMock());
        Assert.assertNotNull(config.getApplet());
    }
}
