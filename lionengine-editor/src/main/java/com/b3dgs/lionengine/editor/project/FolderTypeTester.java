/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.ValidationException;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.xsd.XsdLoader;

/**
 * Test if the folder is a folder type.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FolderTypeTester
        extends PropertyTester
{
    /** Can edit category property. */
    private static final String PROPERTY_CATEGORY = "category";

    /**
     * Check if the path is a folder type descriptor.
     * 
     * @param path The path to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isFolderType(File path)
    {
        try
        {
            return Tools.getFolderTypeFile(path).isFile();
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
        return FolderTypeTester.is(file, XsdLoader.XSD_FOLDER_TYPE);
    }

    /**
     * Check if the file is a folder type descriptor.
     * 
     * @param file The file to test.
     * @param xsd The expected XSD type.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean is(File file, String xsd)
    {
        try
        {
            if (file.isFile() && file.getName().endsWith(FactoryObjectGame.FILE_DATA_EXTENSION))
            {
                UtilFile.validateXml(XsdLoader.get(xsd), file);
                return true;
            }
            return false;
        }
        catch (final ValidationException exception)
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
            for (final File file : folder.listFiles())
            {
                if (Property.LEVEL.is(UtilityMedia.get(file)))
                {
                    return true;
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
            for (final File file : folder.listFiles())
            {
                if (FolderTypeTester.is(file, XsdLoader.XSD_TILE_SHEETS))
                {
                    return true;
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
