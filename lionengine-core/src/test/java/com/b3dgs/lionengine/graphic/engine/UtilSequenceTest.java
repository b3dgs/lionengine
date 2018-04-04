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
package com.b3dgs.lionengine.graphic.engine;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link UtilSequence}.
 */
public final class UtilSequenceTest
{
    /**
     * Prepare the test.
     */
    @BeforeClass
    public static void prepareTest()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilSequence.class);
    }

    /**
     * Test sequence creation.
     */
    @Test
    public void testCreateSequence()
    {
        Assert.assertNotNull(UtilSequence.create(SequenceSingleMock.class, new Context()
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
                return new Config(new Resolution(320, 240, 60), 32, true);
            }
        }));
    }

    /**
     * Test sequence creation with argument.
     */
    @Test
    public void testCreateSequenceArgument()
    {
        Assert.assertNotNull(UtilSequence.create(SequenceArgumentsMock.class, new Context()
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
                return new Config(new Resolution(320, 240, 60), 32, true);
            }
        }, new Object()));
    }

    /**
     * Test sequence creation with invalid argument.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateSequenceArgumentFail()
    {
        Assert.assertNotNull(UtilSequence.create(SequenceSingleMock.class, new Context()
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
                return new Config(new Resolution(320, 240, 60), 32, true);
            }
        }, new Object()));
    }

    /**
     * Test pause.
     */
    @Test
    public void testPause()
    {
        final Timing timing = new Timing();

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() == 0);

        timing.start();

        UtilSequence.pause(Constant.HUNDRED);

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() > 0);
    }

    /**
     * Test pause interrupted.
     */
    @Test
    public void testPauseInterrupted()
    {
        final Timing timing = new Timing();
        timing.start();

        final Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                UtilSequence.pause(Constant.THOUSAND);
            }
        };
        thread.start();
        thread.interrupt();

        Assert.assertTrue(String.valueOf(timing.elapsed()), timing.elapsed() < Constant.THOUSAND);
    }
}
