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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the featurable configuration data.
 */
public final class FeaturableConfig
{
    /** Default file name. */
    public static final String DEFAULT_FILENAME = "featurable.xml";
    /** Featurable node name. */
    public static final String NODE_FEATURABLE = Constant.XML_PREFIX + "featurable";
    /** Class attribute name. */
    public static final String CLASS = Constant.XML_PREFIX + "class";
    /** Setup attribute name. */
    public static final String SETUP = Constant.XML_PREFIX + "setup";
    /** Feature node. */
    public static final String NODE_FEATURE = Constant.XML_PREFIX + "feature";

    /**
     * Import the featurable data from configurer.
     * 
     * @param configurer The configurer reference.
     * @return The featurable data.
     * @throws LionEngineException If unable to read node.
     */
    public static FeaturableConfig imports(Configurer configurer)
    {
        return imports(configurer.getRoot());
    }

    /**
     * Import the featurable data from node.
     * 
     * @param root The root node reference.
     * @return The featurable data.
     * @throws LionEngineException If unable to read node.
     */
    public static FeaturableConfig imports(XmlNode root)
    {
        final String clazz;
        if (root.hasChild(CLASS))
        {
            clazz = root.getChild(CLASS).getText();
        }
        else
        {
            clazz = FeaturableModel.class.getName();
        }

        final String setup;
        if (root.hasChild(SETUP))
        {
            setup = root.getChild(SETUP).getText();
        }
        else
        {
            setup = Constant.EMPTY_STRING;
        }

        return new FeaturableConfig(clazz, setup);
    }

    /**
     * Export the featurable node from class data.
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
     * Export the featurable node from setup data.
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

    /** Featurable class name. */
    private final String clazz;
    /** Setup class name. */
    private final String setup;

    /**
     * Create an featurable configuration.
     * 
     * @param clazz The featurable class name.
     * @param setup The setup class name (empty if undefined).
     */
    public FeaturableConfig(String clazz, String setup)
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
     * @return The setup class name node value (empty if undefined).
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
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final FeaturableConfig other = (FeaturableConfig) object;
        return other.getClassName().equals(getClassName()) && other.getSetupName().equals(getSetupName());
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [clazz=")
                                  .append(clazz)
                                  .append(", setup=")
                                  .append(setup)
                                  .append("]")
                                  .toString();
    }
}
