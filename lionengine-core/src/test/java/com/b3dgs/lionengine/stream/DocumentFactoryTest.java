/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.stream;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.TransformerConfigurationException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilStream;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the document factory.
 */
public class DocumentFactoryTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(DocumentFactory.class);
    }

    /**
     * Test the create document.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testCreateDocument() throws IOException
    {
        Assert.assertNotNull(DocumentFactory.createDocument());

        InputStream input = null;
        try
        {
            input = DocumentFactoryTest.class.getResourceAsStream("type.xml");
            Assert.assertNotNull(DocumentFactory.createDocument(input));
        }
        finally
        {
            UtilStream.close(input);
        }
    }

    /**
     * Test the create document malformed.
     * 
     * @throws IOException If error.
     */
    @Test(expected = IOException.class)
    public void testCreateDocumentMalformed() throws IOException
    {
        Assert.assertNotNull(DocumentFactory.createDocument());

        InputStream input = null;
        try
        {
            input = DocumentFactoryTest.class.getResourceAsStream("malformed.xml");
            Assert.assertNotNull(DocumentFactory.createDocument(input));
        }
        finally
        {
            UtilStream.close(input);
        }
    }

    /**
     * Test the create document with <code>null</code> stream.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateDocumentNullStream() throws IOException
    {
        Assert.assertNotNull(DocumentFactory.createDocument(null));
    }

    /**
     * Test the create transformer.
     * 
     * @throws TransformerConfigurationException If error.
     */
    @Test
    public void testCreateTransformer() throws TransformerConfigurationException
    {
        Assert.assertNotNull(DocumentFactory.createTransformer());
    }
}
