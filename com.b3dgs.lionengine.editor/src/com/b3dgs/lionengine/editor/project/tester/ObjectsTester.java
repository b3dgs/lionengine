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
package com.b3dgs.lionengine.editor.project.tester;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.utility.UtilClass;
import com.b3dgs.lionengine.game.object.ObjectGame;

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
            final Class<?> clazz = UtilClass.get(media);
            return ObjectGame.class.isAssignableFrom(clazz);
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
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
        if (ObjectsTester.PROPERTY_ADD_OBJECT.equals(property))
        {
            result = file.isDirectory();
        }
        else if (ObjectsTester.PROPERTY_IS_OBJECT.equals(property))
        {
            result = ObjectsTester.isObjectFile(selection);
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
        // Nothing to do
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final Project project = Project.getActive();
        if (project != null)
        {
            final Media selection = ProjectModel.INSTANCE.getSelection();
            if (selection != null)
            {
                return check(selection, property);
            }
        }
        return false;
    }
}
