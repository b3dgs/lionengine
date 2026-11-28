/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests of {@link Jumpable} and {@link Jumpable#TYPE} (REQ-STD-004, ADR-0083, ADR-0084).
 */
final class JumpableTest
{
    /**
     * A new instance has no impulse, is not grounded, has the default times and cut factor, and no variable height.
     */
    @Test
    void testNew()
    {
        final Jumpable jumpable = Jumpable.TYPE.create();

        assertEquals(0.0, jumpable.getImpulse());
        assertFalse(jumpable.isGrounded());
        assertEquals(0.1, Jumpable.DEFAULT_COYOTE_TIME);
        assertEquals(0.1, Jumpable.DEFAULT_JUMP_BUFFER);
        assertEquals(0.5, Jumpable.DEFAULT_CUT_FACTOR);
        assertEquals(Jumpable.DEFAULT_COYOTE_TIME, jumpable.getCoyoteTime());
        assertEquals(Jumpable.DEFAULT_JUMP_BUFFER, jumpable.getJumpBuffer());
        assertEquals(Jumpable.DEFAULT_CUT_FACTOR, jumpable.getCutFactor());
        assertFalse(jumpable.isVariableHeight());
        assertFalse(jumpable.isJumpHeld());
        assertEquals(0.0, jumpable.getCoyoteRemaining());
        assertEquals(0.0, jumpable.getBufferRemaining());
    }

    /**
     * The setters change their fields, bounds included; the reset gives back the state of a new instance.
     */
    @Test
    void testSetAndReset()
    {
        final Jumpable jumpable = new Jumpable();
        jumpable.setImpulse(7.5);
        jumpable.setGrounded(true);
        jumpable.setCoyoteTime(0.2);
        jumpable.setJumpBuffer(0.0);
        jumpable.setCutFactor(1.0);
        jumpable.setVariableHeight(true);
        jumpable.setJumpHeld(true);

        assertTrue(jumpable.isJumpHeld());
        assertEquals(7.5, jumpable.getImpulse());
        assertTrue(jumpable.isGrounded());
        assertEquals(0.2, jumpable.getCoyoteTime());
        assertEquals(0.0, jumpable.getJumpBuffer());
        assertEquals(1.0, jumpable.getCutFactor());
        assertTrue(jumpable.isVariableHeight());
        jumpable.setCutFactor(0.0);
        assertEquals(0.0, jumpable.getCutFactor());
        jumpable.setJumpBuffer(0.3);
        jumpable.setGrounded(false);
        assertEquals(-1.0, jumpable.update(-1.0, 0.05));
        assertEquals(0.3, jumpable.getBufferRemaining());

        Jumpable.TYPE.reset(jumpable);
        assertFalse(jumpable.isJumpHeld());
        assertEquals(0.0, jumpable.getCoyoteRemaining());
        assertEquals(0.0, jumpable.getBufferRemaining());
        assertEquals(0.0, jumpable.getImpulse());
        assertFalse(jumpable.isGrounded());
        assertEquals(Jumpable.DEFAULT_COYOTE_TIME, jumpable.getCoyoteTime());
        assertEquals(Jumpable.DEFAULT_JUMP_BUFFER, jumpable.getJumpBuffer());
        assertEquals(Jumpable.DEFAULT_CUT_FACTOR, jumpable.getCutFactor());
        assertFalse(jumpable.isVariableHeight());

        jumpable.setImpulse(4.0);
        jumpable.setGrounded(true);
        jumpable.setJumpHeld(true);
        assertEquals(4.0, jumpable.update(0.0, 0.05), "The press is seen after the reset");
    }

    /**
     * Negative or non-finite impulses and times, and cut factors outside [0, 1], are refused and change nothing.
     */
    @Test
    void testInvalid()
    {
        final Jumpable jumpable = new Jumpable();
        for (final double invalid : new double[]
        {
            -0.5, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        })
        {
            assertThrows(IllegalArgumentException.class, () -> jumpable.setImpulse(invalid));
            assertThrows(IllegalArgumentException.class, () -> jumpable.setCoyoteTime(invalid));
            assertThrows(IllegalArgumentException.class, () -> jumpable.setJumpBuffer(invalid));
            assertThrows(IllegalArgumentException.class, () -> jumpable.setCutFactor(invalid));
        }
        assertThrows(IllegalArgumentException.class, () -> jumpable.setCutFactor(1.5));
        assertThrows(IllegalArgumentException.class, () -> jumpable.setCutFactor(-0.0001));

        assertEquals(0.0, jumpable.getImpulse());
        assertEquals(Jumpable.DEFAULT_COYOTE_TIME, jumpable.getCoyoteTime());
        assertEquals(Jumpable.DEFAULT_JUMP_BUFFER, jumpable.getJumpBuffer());
        assertEquals(Jumpable.DEFAULT_CUT_FACTOR, jumpable.getCutFactor());
    }
}
