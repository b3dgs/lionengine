/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Test the state factory.
 */
public class StateFactoryTest
{
    /**
     * Test the state creation.
     */
    @Test
    public void testCreate()
    {
        final StateFactory factory = new StateFactory();
        final State idle = new StateIdle();
        final State walk = new StateWalk();
        factory.addState(idle);
        factory.addState(walk);

        Assert.assertEquals(idle, factory.getState(StateType.IDLE));
        Assert.assertEquals(walk, factory.getState(StateType.WALK));

        factory.clear();

        try
        {
            Assert.assertNull(factory.getState(StateType.IDLE));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
