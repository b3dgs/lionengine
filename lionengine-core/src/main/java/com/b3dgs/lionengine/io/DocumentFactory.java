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
package com.b3dgs.lionengine.io;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * Document factory implementation for DOM.
 */
final class DocumentFactory
{
    /** Load factory. */
    private static DocumentBuilder documentFactory;
    /** Save factory. */
    private static TransformerFactory transformerFactory;

    /**
     * Create a blank document.
     * 
     * @return The created document.
     * @throws LionEngineException If unable to create document.
     */
    public static Document createDocument()
    {
        return getDocumentFactory().newDocument();
    }

    /**
     * Create a document from an input stream.
     * 
     * @param input The input stream.
     * @return The created document.
     * @throws IOException If malformed document.
     * @throws LionEngineException If unable to create document.
     */
    public static Document createDocument(InputStream input) throws IOException
    {
        Check.notNull(input);
        try
        {
            return getDocumentFactory().parse(input);
        }
        catch (final SAXException exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Create a transformer.
     * 
     * @return The created transformer.
     * @throws TransformerConfigurationException If error.
     */
    public static Transformer createTransformer() throws TransformerConfigurationException
    {
        return getTransformerFactory().newTransformer();
    }

    /**
     * Get the document factory.
     * 
     * @return The document factory.
     * @throws LionEngineException If unable to create builder.
     */
    private static synchronized DocumentBuilder getDocumentFactory()
    {
        if (documentFactory == null)
        {
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringElementContentWhitespace(true);
            try
            {
                documentBuilderFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            }
            catch (final ParserConfigurationException exception)
            {
                Verbose.exception(exception);
            }
            try
            {
                documentFactory = documentBuilderFactory.newDocumentBuilder();
                documentFactory.setErrorHandler(null);
            }
            catch (final ParserConfigurationException exception)
            {
                throw new LionEngineException(exception);
            }
        }
        return documentFactory;
    }

    /**
     * Get the transformer factory.
     * 
     * @return The transformer factory.
     */
    private static synchronized TransformerFactory getTransformerFactory()
    {
        if (transformerFactory == null)
        {
            transformerFactory = TransformerFactory.newInstance();
        }
        return transformerFactory;
    }

    /**
     * Private constructor.
     */
    private DocumentFactory()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
