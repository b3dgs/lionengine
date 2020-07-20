/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link FactoryMedia}.
 */
final class FactoryMediaTest
{
    /**
     * Test create media from resources directory.
     */
    @Test
    void testCreateMediaResources()
    {
        final FactoryMedia factory = new FactoryMediaDefault();
        final Media media = factory.create(java.io.File.separator, "rsc", "test.txt");

        assertEquals("", media.getParentPath());
        assertEquals("test.txt", media.getPath());
        assertEquals("rsc" + java.io.File.separator + "test.txt", media.getFile().getPath());
    }

    /**
     * Test create media from loader.
     */
    @Test
    void testCreateMediaLoader()
    {
        final FactoryMedia factory = new FactoryMediaDefault();
        final Media media = factory.create(java.io.File.separator, FactoryMediaTest.class, "test.txt");

        assertEquals("", media.getParentPath());
        assertEquals("test.txt", media.getPath());
        assertTrue(media.getFile().getPath().endsWith("test.txt"));
    }
}
