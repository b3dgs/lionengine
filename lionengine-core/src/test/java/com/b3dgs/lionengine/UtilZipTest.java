/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.io.File;
import java.util.Collection;
import java.util.zip.ZipEntry;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilZip}.
 */
final class UtilZipTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(UtilZipTest.class);
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilZip.class);
    }

    /**
     * Test get entries by extension.
     */
    @Test
    void testEntriesByExtension()
    {
        final File jar = Medias.create("resources.jar").getFile();
        final String path = UtilZipTest.class.getPackage().getName().replace(Constant.DOT, Medias.getSeparator());
        final Collection<ZipEntry> entries = UtilZip.getEntriesByExtension(jar, path, "png");

        assertEquals(path + Medias.getSeparator() + "image.png", entries.iterator().next().getName());
    }

    /**
     * Test get entries by extension with wrong JAR.
     */
    @Test
    void testEntriesByExtensionWrongJar()
    {
        assertThrows(() -> UtilZip.getEntriesByExtension(new File("void"),
                                                         Constant.EMPTY_STRING,
                                                         Constant.EMPTY_STRING),
                     UtilZip.ERROR_OPEN_ZIP + new File("void").getAbsolutePath());
    }
}
