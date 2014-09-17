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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Stream factory. Can create the following elements:
 * <ul>
 * <li>{@link FileReading}</li>
 * <li>{@link FileWriting}</li>
 * <li>{@link XmlNode}</li>
 * </ul>
 * It can also performs the following operations:
 * <ul>
 * <li>{@link #saveXml(XmlNode, Media)}</li>
 * <li>{@link #loadXml(Media)}</li>
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Stream
{
    /**
     * Open a binary file as read only.
     * 
     * @param media The media file.
     * @return The created reader.
     * @throws LionEngineException If error when opening the media.
     */
    public static FileReading createFileReading(Media media) throws LionEngineException
    {
        return new FileReadingImpl(media);
    }

    /**
     * Open a binary file as write only.
     * 
     * @param media The media file.
     * @return The created writer.
     * @throws LionEngineException If error when opening the media.
     */
    public static FileWriting createFileWriting(Media media) throws LionEngineException
    {
        return new FileWritingImpl(media);
    }

    /**
     * Load an XML file.
     * 
     * @param media The XML media path.
     * @return The XML root node.
     * @throws LionEngineException If error when opening the media.
     */
    public static XmlNode loadXml(Media media) throws LionEngineException
    {
        return XmlFactory.load(media);
    }

    /**
     * Save an XML tree to a file.
     * 
     * @param root The XML root node.
     * @param media The output media path.
     * @throws LionEngineException If error when opening the media.
     */
    public static void saveXml(XmlNode root, Media media) throws LionEngineException
    {
        XmlFactory.save(root, media);
    }

    /**
     * Create an XML node from a name.
     * 
     * @param name The node name.
     * @return The created node.
     * @throws LionEngineException If error when creating the node.
     */
    public static XmlNode createXmlNode(String name) throws LionEngineException
    {
        return new XmlNodeImpl(name);
    }

    /**
     * Private constructor.
     */
    private Stream()
    {
        throw new RuntimeException();
    }
}
