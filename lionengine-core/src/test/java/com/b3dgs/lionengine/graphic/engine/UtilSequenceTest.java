/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphic.engine;

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.ContextMock;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link UtilSequence}.
 */
final class UtilSequenceTest
{
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
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilSequence.class);
    }

    /**
     * Test sequence creation.
     */
    @Test
    void testCreateSequence()
    {
        assertNotNull(UtilSequence.create(SequenceSingleMock.class, new ContextMock()));
    }

    /**
     * Test sequence creation with argument.
     */
    @Test
    void testCreateSequenceArgument()
    {
        assertNotNull(UtilSequence.create(SequenceArgumentsMock.class, new ContextMock(), new Object()));
    }

    /**
     * Test sequence creation with invalid argument.
     */
    @Test
    void testCreateSequenceArgumentFail()
    {
        final String message = NoSuchMethodException.class.getName()
                               + ": No compatible constructor found for "
                               + SequenceSingleMock.class.getName()
                               + " with: [class "
                               + ContextMock.class.getName()
                               + ", "
                               + "class java.lang.Object]";
        assertThrows(() -> UtilSequence.create(SequenceSingleMock.class, new ContextMock(), new Object()), message);
    }

    /**
     * Test pause.
     */
    @Test
    void testPause()
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
    void testPauseInterrupted()
    {
        final Timing timing = new Timing();
        timing.start();

        final Thread thread = new Thread(() -> UtilSequence.pause(Constant.THOUSAND));
        thread.start();
        thread.interrupt();

        assertTrue(timing.elapsed() < Constant.THOUSAND, String.valueOf(timing.elapsed()));
    }
}
