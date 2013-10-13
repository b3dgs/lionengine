/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * XML parser implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class XmlParserImpl
        implements XmlParser
{
    /**
     * Create an XML parser.
     */
    XmlParserImpl()
    {
        // Nothing to do
    }

    /*
     * XmlParser
     */

    @Override
    public XmlNode load(Media media)
    {
        Media.exist(media);
        final Element root;
        final String file = media.getPath();
        try
        {
            final SAXBuilder builder = new SAXBuilder();
            final Document gamesave = builder.build(Media.getStream(media, "XmlNode", false));
            root = gamesave.getRootElement();
        }
        catch (final JDOMException exception)
        {
            throw new LionEngineException(exception, "The XmlParser was unable to parse the following file \"", file,
                    "\"");
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "An error occured while reading the following file: \"", file,
                    "\"");
        }
        return new XmlNodeImpl(root);
    }

    @Override
    public void save(XmlNode root, Media media)
    {
        final String file = media.getPath();
        final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

        try (OutputStream outputStream = new FileOutputStream(file);)
        {
            outputter.output(new Document(((XmlNodeImpl) root).getElement()), outputStream);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "An error occured while reading the following file: \"", file,
                    "\"");
        }
    }
}
