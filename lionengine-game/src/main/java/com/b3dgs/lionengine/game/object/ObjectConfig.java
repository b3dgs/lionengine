/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the object configuration data.
 * 
 * @see Configurer
 */
public final class ObjectConfig
{
    /** Default file name. */
    public static final String DEFAULT_FILENAME = "object.xml";
    /** Object node name. */
    public static final String NODE_OBJECT = Constant.XML_PREFIX + "object";
    /** Class attribute name. */
    public static final String CLASS = Constant.XML_PREFIX + "class";
    /** Setup attribute name. */
    public static final String SETUP = Constant.XML_PREFIX + "setup";

    /**
     * Import the object data from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The object data.
     * @throws LionEngineException If unable to read node.
     */
    public static ObjectConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Import the object data from node.
     * 
     * @param root The root node reference.
     * @return The object data.
     * @throws LionEngineException If unable to read node.
     */
    public static ObjectConfig imports(XmlNode root)
    {
        final String clazz = root.getChild(CLASS).getText();
        final String setup = root.getChild(SETUP).getText();

        return new ObjectConfig(clazz, setup);
    }

    /**
     * Export the object node from class data.
     * 
     * @param clazz The class name.
     * @return The class node.
     * @throws LionEngineException If unable to export node.
     */
    public static XmlNode exportClass(String clazz)
    {
        final XmlNode node = Xml.create(CLASS);
        node.setText(clazz);
        return node;
    }

    /**
     * Export the object node from setup data.
     * 
     * @param setup The setup name.
     * @return The setup node.
     * @throws LionEngineException If unable to export node.
     */
    public static XmlNode exportSetup(String setup)
    {
        final XmlNode node = Xml.create(SETUP);
        node.setText(setup);
        return node;
    }

    /** Object class name. */
    private final String clazz;
    /** Setup class name. */
    private final String setup;

    /**
     * Create an object configuration.
     * 
     * @param clazz The object class name.
     * @param setup The setup class name.
     */
    public ObjectConfig(String clazz, String setup)
    {
        Check.notNull(clazz);
        Check.notNull(setup);

        this.clazz = clazz;
        this.setup = setup;
    }

    /**
     * Get the class name node value.
     * 
     * @return The class name node value.
     */
    public String getClassName()
    {
        return clazz;
    }

    /**
     * Get the setup class name node value.
     * 
     * @return The setup class name node value.
     */
    public String getSetupName()
    {
        return setup;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + clazz.hashCode();
        result = prime * result + setup.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof ObjectConfig))
        {
            return false;
        }
        final ObjectConfig other = (ObjectConfig) obj;
        return other.getClassName().equals(getClassName()) && other.getSetupName().equals(getSetupName());
    }
}
