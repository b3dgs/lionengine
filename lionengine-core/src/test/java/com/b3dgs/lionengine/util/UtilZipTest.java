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
package com.b3dgs.lionengine.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the utility ZIP class.
 */
public class UtilZipTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(UtilZipTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilZip.class);
    }

    /**
     * Test the ZIP get entries by extension.
     */
    @Test
    public void testEntriesByExtension()
    {
        final File jar = Medias.create("resources.jar").getFile();
        final String path = UtilZipTest.class.getPackage().getName().replace(Constant.DOT, Medias.getSeparator());
        final Collection<ZipEntry> entries = UtilZip.getEntriesByExtension(jar, path, "png");

        Assert.assertEquals(path + Medias.getSeparator() + "image.png", entries.iterator().next().getName());
    }

    /**
     * Test the ZIP get entries by extension with wrong JAR.
     */
    @Test(expected = LionEngineException.class)
    public void testEntriesByExtensionWrongJar()
    {
        Assert.assertNull(UtilZip.getEntriesByExtension(new File("void"),
                                                        Constant.EMPTY_STRING,
                                                        Constant.EMPTY_STRING));
    }

    /**
     * Test the close error.
     * 
     * @throws Throwable If error.
     */
    @Test
    public void testCloseError() throws Throwable
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        final Method method = UtilZip.class.getDeclaredMethod("closeZip", ZipFile.class);
        UtilReflection.setAccessible(method, true);
        method.invoke(UtilZip.class, new ZipFile(Medias.create("resources.jar").getFile())
        {
            @Override
            public void close() throws IOException
            {
                throw new IOException();
            }
        });
        Verbose.info("****************************************************************************************");
    }
}
