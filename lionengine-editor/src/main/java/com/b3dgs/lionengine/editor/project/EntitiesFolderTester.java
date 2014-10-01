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
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.xsd.XsdLoader;

/**
 * Test if the folder contains entities.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntitiesFolderTester
        extends PropertyTester
{
    /** Type file name. */
    private static final String TYPE_FILE_NAME = "type.xml";
    /** Can edit category property. */
    private static final String PROPERTY_CATEGORY = "category";
    /** Can add entity property. */
    private static final String PROPERTY_ADD_ENTITY = "addEntity";
    /** Is entity property. */
    private static final String PROPERTY_IS_ENTITY = "isEntity";
    /** Can edit entity property. */
    private static final String PROPERTY_EDIT_ENTITY = "editEntity";

    /**
     * Check if the file is an entity descriptor.
     * 
     * @param file The file to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isEntityFile(File file)
    {
        return EntitiesFolderTester.is(file, XsdLoader.XSD_ENTITY);
    }

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
        return EntitiesFolderTester.is(file, XsdLoader.XSD_FOLDER_TYPE);
    }

    /**
     * Check if can add entity in the selected folder.
     * 
     * @param selection The selected folder.
     * @return <code>true</code> if can add entity, <code>false</code> else.
     */
    private static boolean canAddEntity(Media selection)
    {
        final File file = selection.getFile();
        if (file.isDirectory())
        {
            final File parent = file.getParentFile();
            for (final File current : parent.listFiles())
            {
                if (current.isFile() && EntitiesFolderTester.TYPE_FILE_NAME.equals(current.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the file is a folder type descriptor.
     * 
     * @param file The file to test.
     * @param xsd The expected XSD type.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean is(File file, String xsd)
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
     * Check if is entity in the selected folder.
     * 
     * @param selection The selected folder.
     * @return <code>true</code> if can add entity, <code>false</code> else.
     */
    private static boolean isEntity(Media selection)
    {
        final File file = selection.getFile();
        return EntitiesFolderTester.isEntityFile(file);
    }

    /**
     * Check if is entity in the selected folder.
     * 
     * @param selection The selected folder.
     * @return <code>true</code> if can add entity, <code>false</code> else.
     */
    private static boolean canEditEntity(Media selection)
    {
        return EntitiesFolderTester.isEntity(selection);
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
                if (EntitiesFolderTester.PROPERTY_CATEGORY.equals(property))
                {
                    return EntitiesFolderTester.isFolderType(selection.getFile());
                }
                else if (EntitiesFolderTester.PROPERTY_ADD_ENTITY.equals(property))
                {
                    return EntitiesFolderTester.canAddEntity(selection);
                }
                else if (EntitiesFolderTester.PROPERTY_IS_ENTITY.equals(property))
                {
                    return EntitiesFolderTester.isEntity(selection);
                }
                else if (EntitiesFolderTester.PROPERTY_EDIT_ENTITY.equals(property))
                {
                    return EntitiesFolderTester.canEditEntity(selection);
                }
            }
        }
        return false;
    }
}
