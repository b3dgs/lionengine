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
package com.b3dgs.lionengine.graphic.drawable;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test {@link FontCharData}.
 */
public final class FontCharDataTest
{
    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        final FontCharData data = new FontCharData(0, 1, 2);

        Assert.assertEquals(0, data.getId());
        Assert.assertEquals(1, data.getWidth());
        Assert.assertEquals(2, data.getHeight());
    }

    /**
     * Test constructor with invalid id.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorInvalidId()
    {
        Assert.assertNull(new FontCharData(-1, 1, 2));
    }

    /**
     * Test constructor with invalid with.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorInvalidWidth()
    {
        Assert.assertNull(new FontCharData(0, -1, 2));
    }

    /**
     * Test constructor with invalid height.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorInvalidheight()
    {
        Assert.assertNull(new FontCharData(0, 1, -1));
    }
}
