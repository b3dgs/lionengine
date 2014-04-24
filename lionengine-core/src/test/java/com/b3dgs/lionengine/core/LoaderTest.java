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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;
import com.b3dgs.lionengine.mock.SequenceArgumentsMock;
import com.b3dgs.lionengine.mock.SequenceFailMock;
import com.b3dgs.lionengine.mock.SequenceMock;

/**
 * Test the loader class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class LoaderTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        EngineCore.start("Test", Version.create(1, 0, 0), Verbose.CRITICAL, new FactoryGraphicMock(),
                new FactoryMediaMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        EngineCore.terminate();
    }

    /**
     * Test the loader failure cases.
     */
    @Test
    public void testLoaderFail()
    {
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);

        try
        {
            final Loader loader = new Loader(null);
            Assert.assertNotNull(loader);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            final Loader loader = new Loader(config);
            loader.start(null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Loader loader = new Loader(config);
        Verbose.info("********* EXCEPTED EXCEPTION *********");
        loader.start(SequenceFailMock.class);
        Assert.assertNull(loader.renderer.nextSequence);
        try
        {
            loader.renderer.join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }
        Verbose.info("**************************************");
    }

    /**
     * Test the loader.
     */
    @Test
    public void testLoader()
    {
        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final Loader loader = new Loader(config);
        loader.start(SequenceMock.class);
        try
        {
            loader.renderer.join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }

        final Loader loader2 = new Loader(config);
        loader2.start(SequenceArgumentsMock.class, new Object());
        try
        {
            loader2.renderer.join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }

        try
        {
            loader2.start(SequenceArgumentsMock.class, new Object());
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        final Resolution output3 = new Resolution(640, 480, 0);
        final Config config3 = new Config(output3, 16, false, Filter.HQ2X);
        final Loader loader3 = new Loader(config3);
        loader3.start(SequenceMock.class);
        try
        {
            loader3.renderer.join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }

        final Resolution output4 = new Resolution(960, 720, 60);
        final Config config4 = new Config(output4, 16, false, Filter.HQ3X);
        final Loader loader4 = new Loader(config4);
        loader4.start(SequenceMock.class);
        try
        {
            loader4.renderer.join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }

        final Resolution output5 = new Resolution(320, 240, 0);
        final Config config5 = new Config(output5, 16, true, Filter.BILINEAR);
        final Loader loader5 = new Loader(config5);
        loader5.start(SequenceMock.class);
        try
        {
            loader5.renderer.join();
        }
        catch (final InterruptedException exception)
        {
            Assert.fail();
        }

    }
}
