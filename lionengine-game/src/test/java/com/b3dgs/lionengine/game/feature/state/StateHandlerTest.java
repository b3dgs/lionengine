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

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link StateHandler}.
 */
public final class StateHandlerTest
{
    private final StateHandler handler = new StateHandler();

    /**
     * Test the state handling.
     */
    @Test
    public void testHandler()
    {
        assertFalse(handler.isState(StateBase.class));
        assertFalse(StateBase.entered);
        assertFalse(StateBase.updated);
        assertFalse(StateBase.exited);

        handler.update(1.0);

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

        assertFalse(StateBase.entered);
        assertTrue(StateBase.updated);
        assertFalse(StateBase.exited);

        assertFalse(StateNext.entered);
        assertFalse(StateNext.updated);
        assertFalse(StateNext.exited);

        StateBase.reset();
        StateBase.check = true;
        handler.update(1.0);

        assertFalse(StateBase.entered);
        assertTrue(StateBase.updated);
        assertTrue(StateBase.exited);

        assertTrue(StateNext.entered);
        assertFalse(StateNext.updated);
        assertFalse(StateNext.exited);

        StateBase.reset();
        handler.update(1.0);

        assertFalse(StateBase.entered);
        assertFalse(StateBase.updated);
        assertFalse(StateBase.exited);

        assertTrue(StateNext.updated);
        assertFalse(StateNext.exited);
    }

    /**
     * Test is state with invalid parameter.
     */
    @Test
    public void testNullArgument()
    {
        assertThrows(() -> handler.changeState(null), "Unexpected null argument !");
    }
}
