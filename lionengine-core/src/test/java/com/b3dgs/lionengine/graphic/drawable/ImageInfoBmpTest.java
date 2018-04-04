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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.MediaMock;
import com.b3dgs.lionengine.Verbose;

/**
 * Test {@link ImageHeaderBmp}.
 */
public final class ImageInfoBmpTest
{
    /**
     * Test constructor.
     * 
     * @throws IOException The expected exception.
     */
    @Test(expected = IOException.class)
    public void testBmp() throws IOException
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        Assert.assertFalse(new ImageHeaderBmp().is(new MediaMock()));
        Assert.assertNull(new ImageHeaderBmp().readHeader(new MediaMock().getInputStream()));
        Verbose.info("****************************************************************************************");
    }
}
