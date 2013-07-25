package com.b3dgs.lionengine.file;

import java.util.List;
import java.util.Map;

/**
 * Describe an xml node, which can be modified (reading & writing). All primitive types are written as string inside any
 * xml file. XmlNode can be save in a file, using an XmlParser.
 * <p>
 * Note: Special case for the string stored as <code>null</code> which is in fact stored as {@link XmlNode#NULL}. When
 * read, the {@link XmlNode#NULL} string is return if the stored string was <code>null</code>.
 * </p>
 * 
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
     * Write an integer.
     * 
     * @param attribute The integer name.
     * @param content The integer value.
     */
    void writeInteger(String attribute, int content);

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
     * @param attribute The double name.
     * @param content The double value.
     */
    void writeDouble(String attribute, double content);

    /**
     * Write a string. If the content is equal to <code>null</code>, {@link XmlNode#NULL} is wrote instead.
     * 
     * @param attribute The string name.
     * @param content The string value.
     */
    void writeString(String attribute, String content);

    /**
     * Write a boolean.
     * 
     * @param attribute The boolean name.
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
     * @throws XmlNodeNotFoundException Thrown if no node if found at this child name.
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
