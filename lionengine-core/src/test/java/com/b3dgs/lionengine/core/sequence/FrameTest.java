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
package com.b3dgs.lionengine.core.sequence;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the frame.
 */
public class FrameTest
{
    /**
     * Test the frame default function.
     */
    @Test
    public void testFrame()
    {
        final AtomicBoolean result = new AtomicBoolean();
        final Frame frame = new Frame()
        {
            @Override
            public void update(double extrp)
            {
                result.set(true);
            }

            @Override
            public void render()
            {
                result.set(true);
            }

            @Override
            public void computeFrameRate(double lastTime, double currentTime)
            {
                result.set(true);
            }
        };
        frame.check();

        Assert.assertFalse(result.get());
    }
}
