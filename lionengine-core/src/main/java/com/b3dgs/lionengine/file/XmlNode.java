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
package com.b3dgs.lionengine.file;

import java.util.List;
import java.util.Map;

/**
 * Describe an XML node, which can be modified (reading & writing). All primitive types are written as string inside any
 * XML file. XmlNode can be save in a file, using an XmlParser.
 * <p>
 * Note: Special case for the string stored as <code>null</code> which is in fact stored as {@link XmlNode#NULL}. When
 * read, the {@link XmlNode#NULL} string is return if the stored string was <code>null</code>.
 * </p>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final XmlNode node = File.createXmlNode(&quot;node&quot;);
 * node.writeBoolean(&quot;value&quot;, true);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see XmlParser
 */
public interface XmlNode
        extends XmlNodeReader
{
    /** Null string (represents a string stored as <code>null</code>). */
    String NULL = "null";

    /**
     * Add a child node.
     * 
     * @param node The child node.
     */
    void add(XmlNode node);

    /**
     * Write a byte.
     * 
     * @param attribute The attribute name.
     * @param content The byte value.
     */
    void writeByte(String attribute, byte content);

    /**
     * Write a short.
     * 
     * @param attribute The attribute name.
     * @param content The short value.
     */
    void writeShort(String attribute, short content);

    /**
     * Write an integer.
     * 
     * @param attribute The attribute name.
     * @param content The integer value.
     */
    void writeInteger(String attribute, int content);

    /**
     * Write a long.
     * 
     * @param attribute The attribute name.
     * @param content The long value.
     */
    void writeLong(String attribute, long content);

    /**
     * Write a float.
     * 
     * @param attribute The float name.
     * @param content The float value.
     */
    void writeFloat(String attribute, float content);

    /**
     * Write a double.
     * 
     * @param attribute The attribute name.
     * @param content The double value.
     */
    void writeDouble(String attribute, double content);

    /**
     * Write a string. If the content is equal to <code>null</code>, {@link XmlNode#NULL} is wrote instead.
     * 
     * @param attribute The attribute name.
     * @param content The string value.
     */
    void writeString(String attribute, String content);

    /**
     * Write a boolean.
     * 
     * @param attribute The attribute name.
     * @param content The boolean value.
     */
    void writeBoolean(String attribute, boolean content);

    /**
     * Return the text inside the node.
     * 
     * @return The text.
     */
    String getText();

    /**
     * Get a child node from its name.
     * 
     * @param name The child name.
     * @return The child node reference.
     * @throws XmlNodeNotFoundException If no node is found at this child name.
     */
    XmlNode getChild(String name) throws XmlNodeNotFoundException;

    /**
     * Get the list of all children with this name.
     * 
     * @param name The children name.
     * @return The children list.
     */
    List<XmlNode> getChildren(String name);

    /**
     * Get list of all children.
     * 
     * @return The children list.
     */
    List<XmlNode> getChildren();

    /**
     * Get all attributes.
     * 
     * @return The attributes map reference.
     */
    Map<String, String> getAttributes();
}
