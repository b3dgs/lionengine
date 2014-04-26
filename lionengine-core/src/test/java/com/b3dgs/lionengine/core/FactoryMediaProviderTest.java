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
package com.b3dgs.lionengine.core;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the factory media provider class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryMediaProviderTest
{
    /** Resources path. */
    private static final String PATH = UtilityFile.getPath("src", "test", "resources", "graphic");

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryMediaProvider.setFactoryMedia(null);
    }

    /**
     * Test the create media.
     */
    @Test
    public void testCreateMedia()
    {
        Assert.assertEquals(FactoryMediaProviderTest.PATH, Core.MEDIA.create(FactoryMediaProviderTest.PATH).getPath());
    }

    /**
     * Test the create media path.
     */
    @Test
    public void testCreateMediaPath()
    {
        Assert.assertEquals(FactoryMediaProviderTest.PATH, Core.MEDIA.create("src", "test", "resources", "graphic")
                .getPath());
    }

    /**
     * Test the separator.
     */
    @Test
    public void testSeparator()
    {
        final String old = Core.MEDIA.getSeparator();
        Core.MEDIA.setSeparator("%");
        Assert.assertEquals("test%toto", Core.MEDIA.create("test", "toto").getPath());
        Core.MEDIA.setSeparator(old);
    }
}
