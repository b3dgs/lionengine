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
package com.b3dgs.lionengine.game.state;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the state handler class.
 */
public class StateHandlerTest
{
    private final StateFactory factory = new StateFactory();
    private final StateHandler handler = new StateHandler(factory);

    /**
     * Test the state handling.
     */
    @Test
    public void testHandler()
    {
        final StateTest idle = new StateIdle();
        factory.addState(idle);

        final StateTest walk = new StateWalk();
        factory.addState(walk);

        handler.update(1.0);

        Assert.assertFalse(handler.isState(StateType.IDLE));
        Assert.assertFalse(handler.isState(StateType.WALK));
        Assert.assertFalse(idle.isEntered());
        Assert.assertFalse(walk.isEntered());

        handler.changeState(StateType.IDLE);

        Assert.assertTrue(handler.isState(StateType.IDLE));
        Assert.assertTrue(idle.isEntered());
        Assert.assertFalse(idle.isUpdated());

        handler.update(1.0);

        Assert.assertTrue(handler.isState(StateType.IDLE));
        Assert.assertTrue(idle.isEntered());
        Assert.assertTrue(idle.isUpdated());
        Assert.assertFalse(idle.isExited());
        Assert.assertFalse(walk.isEntered());
        Assert.assertFalse(walk.isUpdated());

        handler.changeState(StateType.WALK);

        Assert.assertTrue(handler.isState(StateType.WALK));
        Assert.assertTrue(idle.isExited());
        Assert.assertTrue(walk.isEntered());
        Assert.assertFalse(walk.isUpdated());

        handler.update(1.0);

        Assert.assertTrue(walk.isUpdated());
        Assert.assertFalse(walk.isExited());

        handler.changeState(StateType.IDLE);
        handler.update(1.0);

        Assert.assertTrue(handler.isState(StateType.IDLE));
        Assert.assertTrue(walk.isExited());
    }

    /**
     * Test the state transitions.
     */
    @Test
    public void testTransition()
    {
        final State idle = new StateIdle();
        factory.addState(idle);
        factory.addState(new StateWalk());

        handler.addInput(new InputDirectionalMock());
        handler.addInput(new InputPointerMock());

        handler.changeState(StateType.IDLE);

        Assert.assertTrue(handler.isState(StateType.IDLE));

        handler.update(1.0);

        Assert.assertTrue(handler.isState(StateType.WALK));

        handler.update(1.0);

        Assert.assertTrue(handler.isState(StateType.IDLE));

        idle.clearTransitions();
        handler.update(1.0);

        Assert.assertTrue(handler.isState(StateType.IDLE));
    }

    /**
     * Test is state with invalid parameter.
     */
    @Test(expected = LionEngineException.class)
    public void testNullArgument()
    {
        handler.changeState(null);
    }
}
