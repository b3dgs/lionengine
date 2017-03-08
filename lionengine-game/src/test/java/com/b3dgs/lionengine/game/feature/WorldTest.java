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
package com.b3dgs.lionengine.game.feature;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test the world class.
 */
public class WorldTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(WorldTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
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
        final World world = new World(new Context()
        {
            @Override
            public int getX()
            {
                return 0;
            }

            @Override
            public int getY()
            {
                return 0;
            }

            @Override
            public <T extends InputDevice> T getInputDevice(Class<T> type)
            {
                return null;
            }

            @Override
            public Config getConfig()
            {
                return config;
            }
        });

        final Media media = Medias.create("test");
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
        final WorldFail world = new WorldFail(new Context()
        {
            @Override
            public int getX()
            {
                return 0;
            }

            @Override
            public int getY()
            {
                return 0;
            }

            @Override
            public <T extends InputDevice> T getInputDevice(Class<T> type)
            {
                return null;
            }

            @Override
            public Config getConfig()
            {
                return config;
            }
        });

        try
        {
            world.saveToFile(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }

        final Media media = Medias.create("test");
        try
        {
            world.saveToFile(media);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }

        try
        {
            world.loadFromFile(Medias.create("type.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }

        try
        {
            world.loadFromFile(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }
}
