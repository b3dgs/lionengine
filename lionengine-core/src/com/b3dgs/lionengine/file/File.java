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

import java.io.IOException;

import com.b3dgs.lionengine.Media;

/**
 * File factory. Can create the following elements:
 * <ul>
 * <li>{@link FileReading}</li>
 * <li>{@link FileWriting}</li>
 * <li>{@link XmlParser}</li>
 * <li>{@link XmlNode}</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class File
{
    /**
     * Open a binary file as read only.
     * 
     * @param media The media file.
     * @return The created reader.
     * @throws IOException If open failed.
     */
    public static FileReading createFileReading(Media media) throws IOException
    {
        return new FileReadingImpl(media);
    }

    /**
     * Open a binary file as write only.
     * 
     * @param media The media file.
     * @return The created writer.
     * @throws IOException If open failed.
     */
    public static FileWriting createFileWriting(Media media) throws IOException
    {
        return new FileWritingImpl(media);
    }

    /**
     * Create an XML parser, in order to load an XML node from a file.
     * 
     * @return The created parser.
     */
    public static XmlParser createXmlParser()
    {
        return new XmlParserImpl();
    }

    /**
     * Create an XML node from a name.
     * 
     * @param name The node name.
     * @return The created node.
     */
    public static XmlNode createXmlNode(String name)
    {
        return new XmlNodeImpl(name);
    }

    /**
     * Private constructor.
     */
    private File()
    {
        throw new RuntimeException();
    }
}
