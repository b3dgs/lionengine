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
package com.b3dgs.lionengine.test.core;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the medias factory class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MediasTest
{
    /** Resources path. */
    private static final String PATH = "graphic";

    /**
     * Test the constructor.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws ReflectiveOperationException
    {
        UtilTests.testPrivateConstructor(Medias.class);
    }

    /**
     * Test the create media.
     */
    @Test
    public void testCreateMedia()
    {
        Assert.assertEquals(PATH, Medias.create(PATH).getPath());
        Assert.assertNotNull(Medias.create("../test/file1.txt"));
    }

    /**
     * Test the create media path.
     */
    @Test
    public void testCreateMediaPath()
    {
        Assert.assertEquals(PATH, Medias.create("graphic").getPath());
    }

    /**
     * Test the separator.
     */
    @Test
    public void testSeparator()
    {
        final String old = Medias.getSeparator();
        Medias.setSeparator(Constant.PERCENT);
        Assert.assertEquals("test%toto", Medias.create("test", "toto").getPath());
        Medias.setSeparator(old);
    }
}
