package com.b3dgs.lionengine.file;

import com.b3dgs.lionengine.Media;

/**
 * Describe an xml parser, which is able to perform save and load operations.
 * <p>
 * Example of use:
 * </p>
 * 
 * <pre>
 * import static com.b3dgs.lionengine.File.FILE;
 * 
 * XmlParser parser = FILE.createXMLParser();
 * XmlNode root = FILE.createXMLNode("game");
 * root.writeInteger("state", 1);
 * XmlNode children = FILE.createXMLNode("sub");
 * children.writeBoolean("active", true);
 * root.add(children);
 * parser.save(root, Media.get("output.xml"));
 * </pre>
 */
public interface XmlParser
{
    /**
     * Load an xml file.
     * 
     * @param media The xml media path.
     * @return The xml root node.
     */
    XmlNode load(Media media);

    /**
     * Save an xml tree to a file.
     * 
     * @param root The xml root node.
     * @param media The output media path.
     */
    void save(XmlNode root, Media media);
}
