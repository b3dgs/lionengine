/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.game.Range;

/**
 * Test range class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
@SuppressWarnings("static-method")
public class RangeTest
{
    /**
     * Test range functions.
     */
    @Test
    public void testRange()
    {
        final Range range = new Range();
        Assert.assertTrue(range.getMin() == 0);
        Assert.assertTrue(range.getMax() == 0);

        final Range rangeA = new Range(1, 3);
        Assert.assertTrue(rangeA.getMin() == 1);
        Assert.assertTrue(rangeA.getMax() == 3);

        rangeA.setMin(0);
        rangeA.setMax(4);
        Assert.assertTrue(rangeA.getMin() == 0);
        Assert.assertTrue(rangeA.getMax() == 4);
    }
}
