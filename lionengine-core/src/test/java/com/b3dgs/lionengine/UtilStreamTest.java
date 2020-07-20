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

import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.io.InputStreamMock;
import com.b3dgs.lionengine.io.OutputStreamMock;

/**
 * Test {@link UtilStream}.
 */
final class UtilStreamTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(UtilStreamTest.class);
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
        assertPrivateConstructor(UtilStream.class);
    }

    /**
     * Test copy.
     * 
     * @throws IOException If error.
     */
    @Test
    void testCopy() throws IOException
    {
        final Path temp1 = Files.createTempFile("temp", ".tmp");
        final Path temp2 = Files.createTempFile("temp", ".tmp");

        try (InputStream input = new FileInputStream(temp1.toFile());
             OutputStream output = new FileOutputStream(temp2.toFile()))
        {
            output.write(1);
            output.flush();
            UtilStream.copy(input, output);
        }
        finally
        {
            Files.delete(temp1);
            Files.delete(temp2);
        }
    }

    /**
     * Test copy with <code>null</code> input.
     * 
     * @throws IOException If error.
     */
    @Test
    void testCopyNullInput() throws IOException
    {
        try (OutputStream output = new OutputStreamMock())
        {
            assertThrows(() -> UtilStream.copy(null, output), Check.ERROR_NULL);
        }
    }

    /**
     * Test copy with <code>null</code> output.
     * 
     * @throws IOException If error.
     */
    @Test
    void testCopyNullOutput() throws IOException
    {
        try (InputStream input = new InputStreamMock())
        {
            assertThrows(() -> UtilStream.copy(input, null), Check.ERROR_NULL);
        }
    }

    /**
     * Test input stream copy.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetCopy() throws IOException
    {
        try (InputStream input = new InputStreamMock())
        {
            assertTrue(UtilStream.getCopy("te", input).delete());
            assertTrue(UtilStream.getCopy("temp", input).delete());
            assertTrue(UtilStream.getCopy("temp.tmp", input).delete());
        }
    }

    /**
     * Test input stream copy with <code>null</code> name.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetCopyNullName() throws IOException
    {
        try (InputStream input = new InputStreamMock())
        {
            assertThrows(() -> UtilStream.getCopy(null, input), Check.ERROR_NULL);
        }
    }

    /**
     * Test input stream copy with <code>null</code> name.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetCopyNullStream() throws IOException
    {
        assertThrows(() -> UtilStream.getCopy("temp", null), Check.ERROR_NULL);
    }

    /**
     * Test get copy error.
     */
    @Test
    void testGetCopyError()
    {
        assertThrows(() -> UtilStream.getCopy("copy", new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException();
            }
        }), UtilStream.ERROR_TEMP_FILE + "copy");
    }
}
