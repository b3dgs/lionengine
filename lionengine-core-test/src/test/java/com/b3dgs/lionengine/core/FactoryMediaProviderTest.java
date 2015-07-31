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
package com.b3dgs.lionengine.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.test.FactoryMediaMock;
import com.b3dgs.lionengine.test.MediaMock;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the factory media provider class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryMediaProviderTest
{
    /** Resources path. */
    private static final String PATH = "graphic";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setFactoryMedia(new FactoryMediaMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setFactoryMedia(null);
    }

    /**
     * Test the constructor.
     * 
     * @throws Throwable If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Throwable
    {
        UtilTests.testPrivateConstructor(Medias.class);
    }

    /**
     * Test the create media.
     */
    @Test
    public void testCreateMedia()
    {
        Assert.assertEquals(PATH, new MediaMock(PATH, true).getPath());
        Assert.assertNotNull(Medias.create("../test/file1.txt"));
    }

    /**
     * Test the create media path.
     */
    @Test
    public void testCreateMediaPath()
    {
        Assert.assertEquals(PATH, new MediaMock("graphic", true).getPath());
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
