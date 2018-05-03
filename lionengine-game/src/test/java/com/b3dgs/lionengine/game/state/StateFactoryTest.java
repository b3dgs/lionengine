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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link StateFactory}.
 */
public final class StateFactoryTest
{
    /**
     * Test add state.
     */
    @Test
    public void testAddState()
    {
        final StateFactory factory = new StateFactory();

        final State idle = new StateIdle();
        factory.addState(idle);

        final State walk = new StateWalk();
        factory.addState(walk);

        assertEquals(idle, factory.getState(StateType.IDLE));
        assertEquals(walk, factory.getState(StateType.WALK));
    }

    /**
     * Test clear.
     */
    @Test
    public void testClear()
    {
        final StateFactory factory = new StateFactory();
        factory.addState(new StateIdle());
        factory.addState(new StateWalk());
        factory.clear();

        assertThrows(() -> factory.getState(StateType.IDLE), "Unknown enum: " + StateType.IDLE);
    }
}
