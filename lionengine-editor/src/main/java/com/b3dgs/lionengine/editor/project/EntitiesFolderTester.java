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
import java.util.List;

import javax.xml.bind.ValidationException;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
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
    /** Can edit category property. */
    private static final String PROPERTY_CATEGORY = "category";
    /** Can add entity property. */
    private static final String PROPERTY_ADD_ENTITY = "addEntity";

    /**
     * Check if the file is an entity descriptor.
     * 
     * @param file The file to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean isEntityFile(File file)
    {
        try
        {
            UtilFile.validateXml(XsdLoader.get(XsdLoader.XSD_ENTITY), file);
            return true;
        }
        catch (final ValidationException exception)
        {
            return false;
        }
    }

    /**
     * Check if can edit property folder.
     * 
     * @param selection The selected folder.
     * @return <code>true</code> if can edit, <code>false</code> else.
     */
    private static boolean canEditProperty(Media selection)
    {
        final boolean hasFolders = UtilFile.getDirsList(selection.getFile().getPath()).length != 0;
        if (hasFolders)
        {
            final List<File> files = UtilFile.getFilesByExtension(selection.getFile().getPath(),
                    FactoryObjectGame.FILE_DATA_EXTENSION);
            for (final File file : files)
            {

                if (EntitiesFolderTester.isEntityFile(file))
                {
                    return true;
                }
            }
        }
        return false;
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
                if (current.isFile() && "type.xml".equals(current.getName()))
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
                if (EntitiesFolderTester.PROPERTY_CATEGORY.equals(property))
                {
                    return EntitiesFolderTester.canEditProperty(selection);
                }
                else if (EntitiesFolderTester.PROPERTY_ADD_ENTITY.equals(property))
                {
                    return EntitiesFolderTester.canAddEntity(selection);
                }
            }
        }
        return false;
    }
}
