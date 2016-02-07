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
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;

/**
 * XML parser implementation.
 */
public final class Xml
{
    /** Error when reading the file. */
    private static final String ERROR_READING = "An error occured while reading";
    /** Error when writing into file. */
    private static final String ERROR_WRITING = "An error occured while writing";
    /** Header attribute. */
    private static final String HEADER_ATTRIBUTE = "xmlns:lionengine";
    /** Header value. */
    private static final String HEADER_VALUE = "http://lionengine.b3dgs.com";
    /** Property indent. */
    private static final String PROPERTY_INDENT = "{http://xml.apache.org/xslt}indent-amount";

    /**
     * Load an XML file.
     * 
     * @param media The XML media path.
     * @return The XML root node.
     * @throws LionEngineException If error when loading media.
     */
    public static XmlNode load(Media media)
    {
        Check.notNull(media);

        final InputStream input = media.getInputStream();
        try
        {
            final Document document = DocumentFactory.createDocument(input);
            final Element root = document.getDocumentElement();
            return new XmlNodeImpl(document, root);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, media, ERROR_READING);
        }
    }

    /**
     * Save an XML tree to a file.
     * 
     * @param root The XML root node.
     * @param media The output media path.
     * @throws LionEngineException If error when saving media.
     */
    public static void save(XmlNode root, Media media)
    {
        Check.notNull(root);
        Check.notNull(media);

        final OutputStream output = media.getOutputStream();
        try
        {
            final Transformer transformer = DocumentFactory.createTransformer();
            if (root instanceof XmlNodeImpl)
            {
                final XmlNodeImpl node = (XmlNodeImpl) root;
                node.normalize();
                root.writeString(HEADER_ATTRIBUTE, HEADER_VALUE);
                final DOMSource source = new DOMSource(node.getElement());
                final StreamResult result = new StreamResult(output);
                final String yes = "yes";
                transformer.setOutputProperty(OutputKeys.INDENT, yes);
                transformer.setOutputProperty(OutputKeys.STANDALONE, yes);
                transformer.setOutputProperty(PROPERTY_INDENT, "4");
                transformer.transform(source, result);
            }
        }
        catch (final TransformerException exception)
        {
            throw new LionEngineException(exception, media, ERROR_WRITING);
        }
        finally
        {
            UtilFile.safeClose(output);
        }
    }

    /**
     * Create an XML node from a name.
     * 
     * @param name The node name.
     * @return The created node.
     * @throws LionEngineException If error when creating the node.
     */
    public static XmlNode create(String name)
    {
        return new XmlNodeImpl(name);
    }

    /**
     * Private constructor.
     */
    private Xml()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
