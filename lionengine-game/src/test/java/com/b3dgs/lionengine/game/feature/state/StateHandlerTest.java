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
package com.b3dgs.lionengine.game.feature.state;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;

/**
 * Test {@link StateHandler}.
 */
public final class StateHandlerTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(StateHandlerTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        StateBase.reset();
    }

    /**
     * Test the state handling.
     */
    @Test
    public void testHandler()
    {
        final StateHandler handler = new StateHandler();

        assertFalse(handler.isState(StateBase.class));
        assertFalse(StateBase.entered);
        assertFalse(StateBase.updated);
        assertFalse(StateBase.exited);

        handler.update(1.0);
        handler.postUpdate();

        assertFalse(handler.isState(StateBase.class));
        assertFalse(StateBase.entered);
        assertFalse(StateBase.updated);
        assertFalse(StateBase.exited);

        handler.changeState(StateBase.class);

        assertTrue(handler.isState(StateBase.class));
        assertTrue(StateBase.entered);
        assertFalse(StateBase.updated);
        assertFalse(StateBase.exited);

        StateBase.reset();
        handler.update(1.0);
        handler.postUpdate();

        assertFalse(StateBase.entered);
        assertTrue(StateBase.updated);
        assertFalse(StateBase.exited);

        assertFalse(StateNext.entered);
        assertFalse(StateNext.updated);
        assertFalse(StateNext.exited);

        StateBase.reset();
        StateBase.check = true;
        handler.update(1.0);
        handler.postUpdate();

        assertFalse(StateBase.entered);
        assertTrue(StateBase.updated);
        assertTrue(StateBase.exited);

        assertTrue(StateNext.entered);
        assertFalse(StateNext.updated);
        assertFalse(StateNext.exited);

        StateBase.reset();
        handler.update(1.0);
        handler.postUpdate();

        assertFalse(StateBase.entered);
        assertFalse(StateBase.updated);
        assertFalse(StateBase.exited);

        assertTrue(StateNext.updated);
        assertFalse(StateNext.exited);
    }

    /**
     * Test the state handling with custom converter.
     */
    @Test
    public void testHandlerConverter()
    {
        final Featurable featurable = new FeaturableModel();
        final StateHandler handler;
        handler = featurable.addFeatureAndGet(new StateHandler(new Configurer(Medias.create("object.xml")),
                                                               state -> state.getName()));
        handler.prepare(featurable);
        assertThrows(() -> handler.changeState(StateIdle.class), "Animation not found: " + StateIdle.class.getName());
    }

    /**
     * Test the state clear transition.
     */
    @Test
    public void testClear()
    {
        final StateHandler handler = new StateHandler();
        handler.changeState(StateClear.class);
        handler.update(1.0);

        assertTrue(handler.isState(StateClear.class));

        handler.update(1.0);

        assertTrue(handler.isState(StateClear.class));
    }

    /**
     * Test state with configuration.
     */
    @Test
    public void testWithConfig()
    {
        final Featurable featurable = new FeaturableModel();
        final StateHandler handler;
        handler = featurable.addFeatureAndGet(new StateHandler(new Configurer(Medias.create("object.xml"))));
        handler.prepare(featurable);
        handler.changeState(StateIdle.class);

        assertEquals(new Animation(StateIdle.class.getSimpleName(), 1, 1, 0.125, false, false), StateIdle.animation);
        assertNull(StateWalk.animation);
        assertTrue(handler.isState(StateIdle.class));

        handler.update(1.0);

        assertNull(StateWalk.animation);

        handler.postUpdate();

        assertEquals(new Animation(StateWalk.class.getSimpleName(), 2, 2, 0.125, false, false), StateWalk.animation);
        assertTrue(handler.isState(StateWalk.class));

        handler.update(1.0);
        handler.postUpdate();

        assertTrue(handler.isState(StateIdle.class));
    }

    /**
     * Test is state with invalid parameter.
     */
    @Test
    public void testNullArgument()
    {
        final StateHandler handler = new StateHandler();

        assertThrows(() -> handler.changeState(null), "Unexpected null argument !");
    }

    /**
     * Test is state with invalid parameter.
     */
    @Test
    public void testUnknownState()
    {
        final StateHandler handler = new StateHandler();

        assertCause(() -> handler.changeState(State.class), NoSuchMethodException.class);
    }
}
