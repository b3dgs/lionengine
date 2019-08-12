/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;

import org.junit.jupiter.api.Test;

/**
 * Test {@link RenderableVoid}.
 */
public final class RenderableVoidTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(RenderableVoid.class);
    }

    /**
     * Test the get instance.
     */
    @Test
    public void testGetInstance()
    {
        assertNotNull(RenderableVoid.getInstance());
    }

    /**
     * Test the render.
     */
    @Test
    public void testRender()
    {
        RenderableVoid.getInstance().render(null);
    }
}