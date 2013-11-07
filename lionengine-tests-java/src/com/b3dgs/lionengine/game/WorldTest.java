/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Loader;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;

/**
 * Test the world class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldTest
{
    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test the world.
     */
    @Test
    public void testWorld()
    {
        Engine.start("WorldTest", Version.create(1, 0, 0), "resources");
        final Graphic g = UtilityImage.createImageBuffer(320, 240, Transparency.BITMASK).createGraphic();
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader(config);
        final Scene scene = new Scene(loader);
        final World world = new World(scene);

        final Media media = Media.create("test");
        try
        {
            world.saveToFile(media);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }

        world.loadFromFile(Media.create("type.xml"));
        world.update(0);
        world.render(g);
    }

    /**
     * Test the world.
     */
    @Test
    public void testWorldFail()
    {
        Engine.start("WorldTest", Version.create(1, 0, 0), "resources");
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader(config);
        final Scene scene = new Scene(loader);
        final WorldFail world = new WorldFail(scene);

        try
        {
            world.saveToFile(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Media media = Media.create("test");
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
            world.loadFromFile(Media.create("type.xml"));
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
