/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.state;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.UtilTransformable;

/**
 * Test {@link StateHandler}.
 */
final class StateHandlerTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Medias.setLoadFromJar(StateHandlerTest.class);
        config = UtilTransformable.createMedia(StateHandlerTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

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
    void testHandler()
    {
        final StateHandler handler = new StateHandler(services, setup);

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
        handler.postUpdate();

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
    void testHandlerConverter()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        final StateHandler handler;
        handler = featurable.addFeatureAndGet(new StateHandler(services,
                                                               new Setup(Medias.create("Object.xml")),
                                                               Class::getName));
        handler.prepare(featurable);
        handler.changeState(StateIdle.class);

        assertCause(() -> handler.postUpdate(), "Animation not found: " + StateIdle.class.getName());
    }

    /**
     * Test the state clear transition.
     */
    @Test
    void testClear()
    {
        final StateHandler handler = new StateHandler(services, setup);
        handler.changeState(StateClear.class);
        handler.update(1.0);

        assertFalse(handler.isState(StateClear.class));

        handler.postUpdate();

        assertTrue(handler.isState(StateClear.class));
    }

    /**
     * Test state with configuration.
     */
    @Test
    void testWithConfig()
    {
        final Featurable featurable = new FeaturableModel(services, setup);
        final StateHandler handler;
        handler = featurable.addFeatureAndGet(new StateHandler(services, new Setup(Medias.create("Object.xml"))));
        handler.prepare(featurable);
        handler.changeState(StateIdle.class);
        handler.postUpdate();

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
    void testNullArgument()
    {
        final StateHandler handler = new StateHandler(services, setup);

        assertThrows(() -> handler.changeState(null), "Unexpected null argument !");
    }

    /**
     * Test is state with invalid parameter.
     */
    @Test
    void testUnknownState()
    {
        final StateHandler handler = new StateHandler(services, setup);
        handler.changeState(State.class);

        assertCause(() -> handler.postUpdate(), NoSuchMethodException.class);
    }

    /**
     * Test transition with last state.
     */
    @Test
    void testLast()
    {
        final AtomicReference<Class<? extends State>> old = new AtomicReference<>();
        final AtomicReference<Class<? extends State>> next = new AtomicReference<>();

        final StateHandler handler = new StateHandler(services, setup);
        handler.addListener((o, n) ->
        {
            old.set(o);
            next.set(n);
        });

        handler.changeState(StateMock.class);
        handler.postUpdate();
        handler.postUpdate();

        assertNull(old.get());
        assertEquals(StateMock.class, next.get());

        old.set(null);
        next.set(null);

        handler.changeState(StateMock.class);
        handler.postUpdate();

        assertEquals(StateMock.class, old.get());
        assertEquals(StateMock.class, next.get());
    }

    /**
     * Test last state instance.
     * 
     * @throws Exception If error.
     */
    @Test
    void testLastInstance() throws Exception
    {
        final StateLast state = UtilReflection.create(StateLast.class, new Class[] {});
        state.enter();
        state.update(1.0);
    }

    /**
     * Test is state with listener.
     */
    @Test
    void testListener()
    {
        final AtomicReference<Class<? extends State>> old = new AtomicReference<>();
        final AtomicReference<Class<? extends State>> next = new AtomicReference<>();

        final Featurable featurable = new FeaturableModel(services, setup);
        final StateHandler handler;
        handler = featurable.addFeatureAndGet(new StateHandler(services, new Setup(Medias.create("Object.xml"))));
        handler.prepare(featurable);
        final StateTransitionListener listener = (o, n) ->
        {
            old.set(o);
            next.set(n);
        };
        handler.addListener(listener);

        handler.changeState(StateIdle.class);
        handler.postUpdate();

        assertNull(old.get());
        assertEquals(StateIdle.class, next.get());

        handler.changeState(StateWalk.class);
        handler.postUpdate();

        assertEquals(StateIdle.class, old.get());
        assertEquals(StateWalk.class, next.get());

        handler.removeListener(listener);
        old.set(null);
        next.set(null);
        handler.changeState(StateIdle.class);
        handler.postUpdate();

        assertNull(old.get());
        assertNull(next.get());
    }

    /**
     * Test state with transition to itself.
     */
    @Test
    void testAddTransitionItself()
    {
        final StateMock mock = new StateMock();
        assertThrows(() -> mock.addTransition(StateMock.class, () -> true), StateAbstract.ERROR_ADD_ITSELF);
    }

    private static class StateMock extends StateAbstract
    {
        private StateMock()
        {
            super();

            addTransition(StateLast.class, () -> true);
        }

        @Override
        public void update(double extrp)
        {
            // Nothing to do
        }

        @Override
        public void enter()
        {
            // Nothing to do
        }
    }
}
