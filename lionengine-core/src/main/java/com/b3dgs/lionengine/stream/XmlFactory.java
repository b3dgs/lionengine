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
package com.b3dgs.lionengine.stream;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * XML parser implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class XmlFactory
{
    /** Error when reading the file. */
    private static final String ERROR_READING = "An error occured while reading the following file: ";
    /** Error when writing into file. */
    private static final String ERROR_WRITING = "An error occured while writing the following file: ";
    /** Header attribute. */
    private static final String HEADER_ATTRIBUTE = "xmlns:lionengine";
    /** Header value. */
    private static final String HEADER_VALUE = "http://lionengine.b3dgs.com";

    /** Load factory. */
    private static DocumentBuilderFactory documentFactory;
    /** Save factory. */
    private static TransformerFactory transformerFactory;

    /**
     * Load an XML file.
     * 
     * @param media The XML media path.
     * @return The XML root node.
     */
    public static XmlNode load(Media media)
    {
        final String file = media.getPath();
        try
        {
            final DocumentBuilder builder = XmlFactory.getDocumentFactory().newDocumentBuilder();
            builder.setErrorHandler(null);
            final Document document = builder.parse(media.getInputStream());
            final Element root = document.getDocumentElement();
            return new XmlNodeImpl(root);
        }
        catch (final IOException
                     | SAXException
                     | IllegalArgumentException
                     | ParserConfigurationException exception)
        {
            throw new LionEngineException(exception, XmlFactory.ERROR_READING, "\"", file, "\"");
        }
    }

    /**
     * Save an XML tree to a file.
     * 
     * @param root The XML root node.
     * @param media The output media path.
     */
    public static void save(XmlNode root, Media media)
    {
        final String file = media.getPath();
        try (OutputStream outputStream = media.getOutputStream();)
        {
            final Transformer transformer = XmlFactory.getTransformerFactory().newTransformer();
            if (root instanceof XmlNodeImpl)
            {
                root.writeString(XmlFactory.HEADER_ATTRIBUTE, XmlFactory.HEADER_VALUE);
                final DOMSource source = new DOMSource(((XmlNodeImpl) root).getElement());
                final StreamResult result = new StreamResult(outputStream);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(source, result);
            }
        }
        catch (final IOException
                     | TransformerException
                     | LionEngineException exception)
        {
            throw new LionEngineException(exception, XmlFactory.ERROR_WRITING, "\"", file, "\"");
        }
    }

    /**
     * Get the document factory.
     * 
     * @return The document factory.
     */
    static DocumentBuilderFactory getDocumentFactory()
    {
        synchronized (XmlFactory.class)
        {
            if (XmlFactory.documentFactory == null)
            {
                XmlFactory.documentFactory = DocumentBuilderFactory.newInstance();
            }
        }
        return XmlFactory.documentFactory;
    }

    /**
     * Get the transformer factory.
     * 
     * @return The transformer factory.
     */
    private static TransformerFactory getTransformerFactory()
    {
        synchronized (XmlFactory.class)
        {
            if (XmlFactory.transformerFactory == null)
            {
                XmlFactory.transformerFactory = TransformerFactory.newInstance();
            }
        }
        return XmlFactory.transformerFactory;
    }

    /**
     * Private constructor.
     */
    private XmlFactory()
    {
        throw new RuntimeException();
    }
}
