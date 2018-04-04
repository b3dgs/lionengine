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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link FactoryMedia}.
 */
public final class FactoryMediaTest
{
    /** Factory. */
    private static final FactoryMedia FACTORY = new FactoryMediaDefault();

    /**
     * Test create media from resources directory.
     */
    @Test
    public void testCreateMediaResources()
    {
        final Media media = FACTORY.create("/", "rsc", "test.txt");
        Assert.assertEquals("", media.getParentPath());
        Assert.assertEquals("test.txt", media.getPath());
        Assert.assertEquals("rsc" + java.io.File.separator + "test.txt", media.getFile().getPath());
    }

    /**
     * Test create media from loader.
     */
    @Test
    public void testCreateMediaLoader()
    {
        final Media media = FACTORY.create("/", FactoryMediaTest.class, "test.txt");
        Assert.assertEquals("", media.getParentPath());
        Assert.assertEquals("test.txt", media.getPath());
        Assert.assertTrue(media.getFile().getPath().endsWith("test.txt"));
    }
}
