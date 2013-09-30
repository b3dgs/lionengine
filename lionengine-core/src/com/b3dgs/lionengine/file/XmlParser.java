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
