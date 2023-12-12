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

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertThrowsIo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test {@link DocumentFactory}.
 */
final class DocumentFactoryTest
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFactoryTest.class);

    /**
     * Test the constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(DocumentFactory.class);
    }

    /**
     * Test create document.
     * 
     * @throws IOException If error.
     */
    @Test
    void testCreateDocument() throws IOException
    {
        assertNotNull(DocumentFactory.createDocument());

        try (InputStream input = DocumentFactoryTest.class.getResourceAsStream("type.xml"))
        {
            assertNotNull(DocumentFactory.createDocument(input));
        }
    }

    /**
     * Test create document malformed.
     * 
     * @throws IOException If error.
     */
    @Test
    void testCreateDocumentMalformed() throws IOException
    {
        try (InputStream input = DocumentFactoryTest.class.getResourceAsStream("malformed.xml"))
        {
            final String message = "org.xml.sax.SAXParseException; lineNumber: 4; columnNumber: 7; ";
            assertThrowsIo(() -> DocumentFactory.createDocument(input), message);
        }
    }

    /**
     * Test create document with <code>null</code> stream.
     * 
     * @throws IOException If error.
     */
    @Test
    void testCreateDocumentNullStream() throws IOException
    {
        assertThrows(() -> DocumentFactory.createDocument(null), Check.ERROR_NULL);
    }

    /**
     * Test create transformer.
     * 
     * @throws TransformerConfigurationException If error.
     */
    @Test
    void testCreateTransformer() throws TransformerConfigurationException
    {
        assertNotNull(DocumentFactory.createTransformer());
        assertNotNull(DocumentFactory.createTransformer()); // Get cached instance
    }

    /**
     * Test missing feature.
     * 
     * @throws Exception If error.
     */
    @Test
    void testMissingFeature() throws Exception
    {
        final Object old = UtilTests.getField(DocumentFactory.class, "documentBuilder");
        final Field field = DocumentFactory.class.getDeclaredField("documentBuilder");
        UtilReflection.setAccessible(field, true);
        field.set(DocumentFactory.class, null);

        final String oldFactory = System.getProperty(DocumentBuilderFactory.class.getName());
        System.setProperty(DocumentBuilderFactory.class.getName(), Factory.class.getName());

        try
        {
            LOGGER.info("*********************************** EXPECTED VERBOSE ***********************************");
            assertCause(DocumentFactory::createDocument, ParserConfigurationException.class);
            LOGGER.info("****************************************************************************************");
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
