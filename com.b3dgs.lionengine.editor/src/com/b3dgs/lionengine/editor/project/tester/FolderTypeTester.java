/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.io.FileNotFoundException;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.editor.project.Property;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test if the folder is a folder type.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FolderTypeTester
        extends PropertyTester
{
    /** Folder type node. */
    public static final String NODE_FOLDER_TYPE = "lionengine:folderType";
    /** Folder type name node. */
    public static final String NODE_NAME = "lionengine:name";
    /** Can edit category property. */
    private static final String PROPERTY_CATEGORY = "category";

    /**
     * Check if the path is a folder type descriptor or contains objects.
     * 
     * @param path The path to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isFolderType(File path)
    {
        try
        {
            if (Tools.getFolderTypeFile(path).isFile())
            {
                return true;
            }
            final File[] files = path.listFiles();
            if (files != null)
            {
                for (final File file : files)
                {
                    if (ObjectsTester.isObjectFile(UtilityMedia.get(file)))
                    {
                        return true;
                    }
                }
            }
            return false;
        }
        catch (final FileNotFoundException exception)
        {
            return false;
        }
    }

    /**
     * Check if the file is a folder type descriptor.
     * 
     * @param file The file to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isFolderTypeFile(File file)
    {
        try
        {
            final XmlNode root = Stream.loadXml(UtilityMedia.get(file));
            return root.getChild(FolderTypeTester.NODE_NAME) != null;
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Check if the folder contains levels.
     * 
     * @param folder The folder to check.
     * @return <code>true</code> if contains levels, <code>false</code> else.
     */
    public static boolean isLevelsFolder(File folder)
    {
        if (folder.isDirectory())
        {
            final File[] files = folder.listFiles();
            if (files != null)
            {
                for (final File file : files)
                {
                    if (Property.LEVEL.is(UtilityMedia.get(file)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if the folder contains tiles.
     * 
     * @param folder The folder to check.
     * @return <code>true</code> if contains tiles, <code>false</code> else.
     */
    public static boolean isTilesFolder(File folder)
    {
        if (folder.isDirectory())
        {
            final File[] files = folder.listFiles();
            if (files != null)
            {
                for (final File file : files)
                {
                    if (SheetsTester.isSheetsFile(UtilityMedia.get(file)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
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
            final Media selection = ProjectsModel.INSTANCE.getSelection();
            if (selection != null)
            {
                if (FolderTypeTester.PROPERTY_CATEGORY.equals(property))
                {
                    return FolderTypeTester.isFolderType(selection.getFile());
                }
            }
        }
        return false;
    }
}
