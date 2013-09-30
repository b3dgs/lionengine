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
 * Handle files manipulation, reading and writing.
 */
public final class File
{
    /**
     * Open a file as read only.
     * 
     * @param media The media file.
     * @return The created FileReader.
     * @throws IOException If open failed.
     */
    public static FileReading createFileReading(Media media) throws IOException
    {
        return new FileReadingImpl(media);
    }

    /**
     * Open a file as write only.
     * 
     * @param media The media file.
     * @return The created FileWriter.
     * @throws IOException If write failed.
     */
    public static FileWriting createFileWriting(Media media) throws IOException
    {
        return new FileWritingImpl(media);
    }

    /**
     * Create an xml parser, in order to load an xml node from a file.
     * 
     * @return The parser reference.
     */
    public static XmlParser createXmlParser()
    {
        return new XmlParserImpl();
    }

    /**
     * Create an xml node from a name.
     * 
     * @param name The node name.
     * @return The node reference.
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
