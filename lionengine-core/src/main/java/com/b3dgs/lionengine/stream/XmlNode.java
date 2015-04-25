/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;

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
 * final XmlNode node = Stream.createXmlNode(&quot;node&quot;);
 * node.writeBoolean(&quot;value&quot;, true);
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
     * @throws LionEngineException If error when adding the node.
     */
    void add(XmlNode node) throws LionEngineException;

    /**
     * Set the text inside the node.
     * 
     * @param text The text content.
     * @throws LionEngineException If error when setting the node text.
     */
    void setText(String text) throws LionEngineException;

    /**
     * Write a byte.
     * 
     * @param attribute The attribute name.
     * @param content The byte value.
     * @throws LionEngineException If error when writing.
     */
    void writeByte(String attribute, byte content) throws LionEngineException;

    /**
     * Write a short.
     * 
     * @param attribute The attribute name.
     * @param content The short value.
     * @throws LionEngineException If error when writing.
     */
    void writeShort(String attribute, short content) throws LionEngineException;

    /**
     * Write an integer.
     * 
     * @param attribute The attribute name.
     * @param content The integer value.
     * @throws LionEngineException If error when writing.
     */
    void writeInteger(String attribute, int content) throws LionEngineException;

    /**
     * Write a long.
     * 
     * @param attribute The attribute name.
     * @param content The long value.
     * @throws LionEngineException If error when writing.
     */
    void writeLong(String attribute, long content) throws LionEngineException;

    /**
     * Write a float.
     * 
     * @param attribute The float name.
     * @param content The float value.
     * @throws LionEngineException If error when writing.
     */
    void writeFloat(String attribute, float content) throws LionEngineException;

    /**
     * Write a double.
     * 
     * @param attribute The attribute name.
     * @param content The double value.
     * @throws LionEngineException If error when writing.
     */
    void writeDouble(String attribute, double content) throws LionEngineException;

    /**
     * Write a string. If the content is equal to <code>null</code>, {@link XmlNode#NULL} is wrote instead.
     * 
     * @param attribute The attribute name.
     * @param content The string value.
     * @throws LionEngineException If error when writing.
     */
    void writeString(String attribute, String content) throws LionEngineException;

    /**
     * Write a boolean.
     * 
     * @param attribute The attribute name.
     * @param content The boolean value.
     * @throws LionEngineException If error when writing.
     */
    void writeBoolean(String attribute, boolean content) throws LionEngineException;

    /**
     * Remove attribute.
     * 
     * @param attribute The attribute to remove.
     */
    void removeAttribute(String attribute);

    /**
     * Remove child.
     * 
     * @param child The child to remove.
     */
    void removeChild(String child);

    /**
     * Remove child.
     * 
     * @param child The child to remove.
     */
    void removeChild(XmlNode child);

    /**
     * Remove all children.
     * 
     * @param children The children to remove.
     */
    void removeChildren(String children);

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
     * @throws LionEngineException If no node is found at this child name.
     */
    XmlNode getChild(String name) throws LionEngineException;

    /**
     * Get the list of all children with this name.
     * 
     * @param name The children name.
     * @return The children list.
     */
    Collection<XmlNode> getChildren(String name);

    /**
     * Get list of all children.
     * 
     * @return The children list.
     */
    Collection<XmlNode> getChildren();

    /**
     * Get all attributes.
     * 
     * @return The attributes map reference.
     */
    Map<String, String> getAttributes();

    /**
     * Check if node has the following attribute.
     * 
     * @param attribute The attribute name.
     * @return <code>true</code> if attribute exists, <code>false</code> else.
     */
    boolean hasAttribute(String attribute);

    /**
     * Check if node has the following child.
     * 
     * @param child The child name.
     * @return <code>true</code> if child exists, <code>false</code> else.
     */
    boolean hasChild(String child);
}
