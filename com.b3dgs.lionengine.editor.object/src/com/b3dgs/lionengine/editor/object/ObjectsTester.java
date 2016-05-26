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
package com.b3dgs.lionengine.editor.object;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test if the folder contains objects.
 */
public final class ObjectsTester extends PropertyTester
{
    /** Can add object property. */
    private static final String PROPERTY_ADD_OBJECT = "addObject";
    /** Is object property. */
    private static final String PROPERTY_IS_OBJECT = "isObject";

    /**
     * Check if the file is an object descriptor.
     * 
     * @param media The media to test (can be <code>null</code>).
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isObjectFile(Media media)
    {
        try
        {
            final Class<?> clazz = get(media);
            return Featurable.class.isAssignableFrom(clazz);
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Get the class from media file, by reading the attribute {@link FeaturableConfig#CLASS} attribute.
     * 
     * @param media The media descriptor.
     * @return The class reference.
     * @throws LionEngineException If not able to create the class.
     */
    private static Class<?> get(Media media)
    {
        final XmlNode root = Xml.load(media);
        final String className = root.getChild(FeaturableConfig.CLASS).getText();
        return ProjectModel.INSTANCE.getProject().getLoader().getClass(className);
    }

    /**
     * Check result depending of selection.
     * 
     * @param selection The selection reference.
     * @param property The property to check.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(Media selection, String property)
    {
        final boolean result;
        final File file = selection.getFile();
        if (PROPERTY_ADD_OBJECT.equals(property))
        {
            result = file.isDirectory();
        }
        else if (PROPERTY_IS_OBJECT.equals(property))
        {
            result = isObjectFile(selection);
        }
        else
        {
            result = false;
        }
        return result;
    }

    /**
     * Create tester.
     */
    public ObjectsTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final Media selection = ProjectModel.INSTANCE.getSelection();
        if (selection != null)
        {
            return check(selection, property);
        }
        return false;
    }
}
