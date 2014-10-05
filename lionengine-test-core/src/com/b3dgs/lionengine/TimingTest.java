/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the timing class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TimingTest
{
    /**
     * Test the timing class.
     * 
     * @throws InterruptedException If error.
     */
    @Test
    public void testTiming() throws InterruptedException
    {
        final Timing timer = new Timing();
        Assert.assertFalse(timer.isStarted());
        Assert.assertTrue(timer.elapsed() == 0);
        Assert.assertFalse(timer.elapsed(1000L));
        timer.start();
        timer.restart();
        timer.start();
        Assert.assertTrue(timer.isStarted());
        Thread.sleep(100);
        Assert.assertTrue(timer.isStarted());
        Assert.assertTrue(timer.elapsed(50));
        Assert.assertTrue(timer.elapsed() >= 50);
        timer.pause();
        Thread.sleep(50);
        timer.unpause();
        Assert.assertFalse(timer.elapsed(2000));
        timer.stop();
        Assert.assertFalse(timer.isStarted());
        Assert.assertTrue(timer.get() >= 0);

        timer.stop();
        timer.set(1000L);
        Assert.assertTrue(timer.isStarted());
        Assert.assertEquals(timer.get(), 1000L);
    }
}
