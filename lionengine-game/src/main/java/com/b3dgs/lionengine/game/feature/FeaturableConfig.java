/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;

/**
 * Represents the featurable configuration data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class FeaturableConfig
{
    /** Default file name. */
    public static final String DEFAULT_FILENAME = "featurable.xml";
    /** Featurable node name. */
    public static final String NODE_FEATURABLE = Constant.XML_PREFIX + "featurable";
    /** Class attribute name. */
    public static final String ATT_CLASS = Constant.XML_PREFIX + "class";
    /** Setup attribute name. */
    public static final String ATT_SETUP = Constant.XML_PREFIX + "setup";
    /** Feature node. */
    public static final String NODE_FEATURE = Constant.XML_PREFIX + "feature";
    /** Class not found error. */
    static final String ERROR_CLASS_PRESENCE = "Class not found: ";
    /** Default class name. */
    private static final String DEFAULT_CLASS_NAME = FeaturableModel.class.getName();
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 35;
    /** Class loader. */
    private static final ClassLoader LOADER = Configurer.class.getClassLoader();
    /** Class cache. */
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

    /**
     * Clear classes cache.
     */
    public static void clearCache()
    {
        CLASS_CACHE.clear();
    }

    /**
     * Import the featurable data from configurer.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The featurable data.
     * @throws LionEngineException If unable to read node.
     */
    public static FeaturableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        return imports(configurer.getRoot());
    }

    /**
     * Import the featurable data from node.
     * 
     * @param root The root node reference (must not be <code>null</code>).
     * @return The featurable data.
     * @throws LionEngineException If unable to read node.
     */
    public static FeaturableConfig imports(Xml root)
    {
        Check.notNull(root);

        final String clazz;
        if (root.hasChild(ATT_CLASS))
        {
            clazz = root.getChild(ATT_CLASS).getText();
        }
        else
        {
            clazz = DEFAULT_CLASS_NAME;
        }

        final String setup;
        if (root.hasChild(ATT_SETUP))
        {
            setup = root.getChild(ATT_SETUP).getText();
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
     * @param clazz The class name (must not be <code>null</code>).
     * @return The class node.
     * @throws LionEngineException If unable to export node.
     */
    public static Xml exportClass(String clazz)
    {
        Check.notNull(clazz);

        final Xml node = new Xml(ATT_CLASS);
        node.setText(clazz);

        return node;
    }

    /**
     * Export the featurable node from setup data.
     * 
     * @param setup The setup name (must not be <code>null</code>).
     * @return The setup node.
     * @throws LionEngineException If unable to export node.
     */
    public static Xml exportSetup(String setup)
    {
        Check.notNull(setup);

        final Xml node = new Xml(ATT_SETUP);
        node.setText(setup);

        return node;
    }

    /**
     * Get all available features.
     * Default constructor of each feature must be available or with {@link Setup} as single parameter.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @return The available features.
     * @throws LionEngineException If invalid class.
     */
    public static List<Feature> getFeatures(Services services, Setup setup)
    {
        final Collection<Xml> children = setup.getRoot().getChildren(FeaturableConfig.NODE_FEATURE);
        final List<Feature> features = new ArrayList<>(children.size());
        for (final Xml featureNode : children)
        {
            final String className = featureNode.getText();
            final Feature feature;
            try
            {
                final Class<? extends Feature> clazz = getClass(className);
                feature = UtilReflection.createReduce(clazz, services, setup);
            }
            catch (final NoSuchMethodException exception)
            {
                throw new LionEngineException(exception);
            }
            features.add(feature);
        }
        return features;
    }

    /**
     * Get the class reference from its name using cache.
     * 
     * @param <T> The class type.
     * @param className The class name.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(String className)
    {
        if (CLASS_CACHE.containsKey(className))
        {
            return (Class<T>) CLASS_CACHE.get(className);
        }
        try
        {
            final Class<?> clazz = LOADER.loadClass(className);
            CLASS_CACHE.put(className, clazz);
            return (Class<T>) clazz;
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_PRESENCE + className);
        }
    }

    /** Featurable class name. */
    private final String clazz;
    /** Setup class name. */
    private final String setup;

    /**
     * Create an featurable configuration.
     * 
     * @param clazz The featurable class name (must not be <code>null</code>).
     * @param setup The setup class name, {@link Constant#EMPTY_STRING} if undefined (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public FeaturableConfig(String clazz, String setup)
    {
        super();

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
     * @return The setup class name node value, {@link Constant#EMPTY_STRING} if undefined.
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
        return clazz.equals(other.clazz) && setup.equals(other.setup);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [clazz=")
                                            .append(clazz)
                                            .append(", setup=")
                                            .append(setup)
                                            .append("]")
                                            .toString();
    }
}
