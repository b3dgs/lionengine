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

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link UtilSequence}.
 */
public final class UtilSequenceTest
{
    private static final Context CONTEXT = new Context()
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
    };

    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilSequence.class);
    }

    /**
     * Test sequence creation.
     */
    @Test
    public void testCreateSequence()
    {
        assertNotNull(UtilSequence.create(SequenceSingleMock.class, CONTEXT));
    }

    /**
     * Test sequence creation with argument.
     */
    @Test
    public void testCreateSequenceArgument()
    {
        assertNotNull(UtilSequence.create(SequenceArgumentsMock.class, CONTEXT, new Object()));
    }

    /**
     * Test sequence creation with invalid argument.
     */
    @Test
    public void testCreateSequenceArgumentFail()
    {
        final String message = NoSuchMethodException.class.getName()
                               + ": No compatible constructor found for "
                               + SequenceSingleMock.class.getName()
                               + " with: [class com.b3dgs.lionengine.graphic.engine.UtilSequenceTest$1, "
                               + "class java.lang.Object]";
        assertThrows(() -> UtilSequence.create(SequenceSingleMock.class, CONTEXT, new Object()), message);
    }

    /**
     * Test pause.
     */
    @Test
    public void testPause()
    {
        final Timing timing = new Timing();

        assertTrue(timing.elapsed() == 0, String.valueOf(timing.elapsed()));

        timing.start();

        UtilSequence.pause(Constant.HUNDRED);

        assertTrue(timing.elapsed() > 0, String.valueOf(timing.elapsed()));
    }

    /**
     * Test pause interrupted.
     */
    @Test
    public void testPauseInterrupted()
    {
        final Timing timing = new Timing();
        timing.start();

        final Thread thread = new Thread(() -> UtilSequence.pause(Constant.THOUSAND));
        thread.start();
        thread.interrupt();

        assertTrue(timing.elapsed() < Constant.THOUSAND, String.valueOf(timing.elapsed()));
    }
}
