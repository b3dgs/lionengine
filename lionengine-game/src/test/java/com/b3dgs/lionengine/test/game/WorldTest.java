/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.Media;

/**
 * Test the world class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
    }

    /**
     * Test the world.
     */
    @Test
    public void testWorld()
    {
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        config.setSource(output);
        final World world = new World(config);

        final Media media = new MediaMock("test", true);
        try
        {
            world.saveToFile(media);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }

        world.update(0);
        world.render(null);
    }

    /**
     * Test the world.
     */
    @Test
    public void testWorldFail()
    {
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        config.setSource(output);
        final WorldFail world = new WorldFail(config);

        try
        {
            world.saveToFile(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Media media = new MediaMock("test", true);
        try
        {
            world.saveToFile(media);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }

        try
        {
            world.loadFromFile(new MediaMock("type.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            world.loadFromFile(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
