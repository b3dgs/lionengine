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
package com.b3dgs.lionengine.io;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.util.UtilReflection;
import com.b3dgs.lionengine.util.UtilStream;
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

    /**
     * Test missing feature.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testMissingFeature() throws Exception
    {
        final Object old = UtilReflection.getField(DocumentFactory.class, "documentFactory");
        final Field field = DocumentFactory.class.getDeclaredField("documentFactory");
        UtilReflection.setAccessible(field, true);
        field.set(DocumentFactory.class, null);

        final String oldFactory = System.getProperty(DocumentBuilderFactory.class.getName());
        System.setProperty(DocumentBuilderFactory.class.getName(), Factory.class.getName());

        try
        {
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            try
            {
                Assert.assertNull(DocumentFactory.createDocument());
                Assert.fail();
            }
            catch (final LionEngineException exception)
            {
                Assert.assertTrue(exception.getCause() instanceof ParserConfigurationException);
            }
            Verbose.info("****************************************************************************************");
        }
        finally
        {
            if (oldFactory != null)
            {
                System.setProperty(DocumentBuilderFactory.class.getName(), oldFactory);
            }
            else
            {
                System.setProperty(DocumentBuilderFactory.class.getName(), "");
            }
            field.set(DocumentFactory.class, old);
            UtilReflection.setAccessible(field, false);
        }
    }

    /**
     * Mock factory.
     */
    public static final class Factory extends DocumentBuilderFactory
    {
        /**
         * Constructor.
         */
        public Factory()
        {
            super();
        }

        @Override
        public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException
        {
            throw new ParserConfigurationException();
        }

        @Override
        public void setAttribute(String name, Object value) throws IllegalArgumentException
        {
            // Mock
        }

        @Override
        public Object getAttribute(String name) throws IllegalArgumentException
        {
            return null;
        }

        @Override
        public void setFeature(String name, boolean value) throws ParserConfigurationException
        {
            throw new ParserConfigurationException();
        }

        @Override
        public boolean getFeature(String name) throws ParserConfigurationException
        {
            return false;
        }
    }
}
