/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;

/**
 * Represents the featurable configuration data.
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @param clazz The featurable class name.
 * @param setup The setup class name.
 */
public record FeaturableConfig(String clazz, String setup)
{
    /** Default file name. */
    public static final String DEFAULT_FILENAME = "featurable.xml";
    /** Featurable node name. */
    public static final String NODE_FEATURABLE = Constant.XML_PREFIX + "featurable";
    /** Setup attribute name. */
    public static final String ATT_SETUP = Constant.XML_PREFIX + "setup";
    /** Features node. */
    public static final String NODE_FEATURES = Constant.XML_PREFIX + "features";
    /** Feature node. */
    public static final String NODE_FEATURE = Constant.XML_PREFIX + "feature";
    /** Feature priority update attribute name. */
    public static final String ATT_PRIORITY_UPDATE = "priorityUpdate";
    /** Feature priority render attribute name. */
    public static final String ATT_PRIORITY_RENDER = "priorityRender";

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
    public static FeaturableConfig imports(AttributesReader root)
    {
        Check.notNull(root);

        final String clazz = UtilFeaturable.getClass(root);

        final String setup;
        if (root.hasNode(ATT_SETUP))
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

        final Xml node = new Xml(UtilFeaturable.ATT_CLASS);
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
     * @param loader The class loader reference.
     * @param featurable The featurable reference.
     * @param services The services reference.
     * @param setup The setup reference.
     * @throws LionEngineException If invalid class.
     */
    public static void addFeatures(ClassLoader loader, Featurable featurable, Services services, Setup setup)
    {
        addFeatures(loader, featurable, services, setup, null);
    }

    /**
     * Get all available features.
     * Default constructor of each feature must be available or with {@link Setup} as single parameter.
     * 
     * @param loader The class loader reference.
     * @param featurable The featurable reference.
     * @param services The services reference.
     * @param setup The setup reference.
     * @param filter The type filter (can be <code>null</code> if none).
     * @throws LionEngineException If invalid class.
     */
    public static void addFeatures(ClassLoader loader,
                                   Featurable featurable,
                                   Services services,
                                   Setup setup,
                                   Class<?> filter)
    {
        final List<? extends AttributesReader> children;
        final AttributesReader root = setup.getRoot();
        if (root.hasNode(NODE_FEATURES))
        {
            children = setup.getRoot()
                            .getChild(FeaturableConfig.NODE_FEATURES)
                            .getChildren(FeaturableConfig.NODE_FEATURE);
        }
        else
        {
            children = Collections.emptyList();
        }

        final int n = children.size();
        for (int i = 0; i < n; i++)
        {
            final AttributesReader node = children.get(i);
            final String className = node.getText();
            final Class<? extends Feature> clazz = UtilFeaturable.getClass(loader, className);
            if (filter == null || filter.isAssignableFrom(clazz))
            {
                UtilFeaturable.createAndAdd(clazz, featurable, services, setup, node);
            }
        }
        children.clear();
    }

    /**
     * Create configuration.
     * 
     * @param clazz The featurable class name (must not be <code>null</code>).
     * @param setup The setup class name, {@link Constant#EMPTY_STRING} if undefined (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public FeaturableConfig
    {
        Check.notNull(clazz);
        Check.notNull(setup);
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
}
