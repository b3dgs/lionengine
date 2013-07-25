package com.b3dgs.lionengine.file;

import com.b3dgs.lionengine.Media;

/**
 * Describe an xml parser, which is able to perform save and load operations.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * // Create a tree and its nodes
 * final XmlNode node1 = File.createXmlNode(&quot;root&quot;);
 * final XmlNode node2 = File.createXmlNode(&quot;foo&quot;);
 * node2.writeBoolean(&quot;myBoolean&quot;, true);
 * node1.add(node2);
 * 
 * // Save the tree
 * final Media file = Media.get(&quot;file.xml&quot;);
 * final XmlParser parser = File.createXmlParser();
 * parser.save(node1, file);
 * 
 * // Load and read the tree
 * final XmlNode root = parser.load(file);
 * final XmlNode foo = root.getChild(&quot;foo&quot;);
 * Assert.assertTrue(foo.readBoolean(&quot;myBoolean&quot;));
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
