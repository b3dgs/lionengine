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

import javax.xml.bind.ValidationException;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.xsd.XsdLoader;

/**
 * Test if the folder contains tile sheets.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TilesheetsFolderTester
        extends PropertyTester
{
    /** Can edit tile sheets property. */
    private static final String PROPERTY_EDIT_TILESHEETS = "editTilesheets";

    /**
     * Check if the file is a tile sheets descriptor.
     * 
     * @param file The file to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isTilesheetsFile(File file)
    {
        try
        {
            UtilFile.validateXml(XsdLoader.get(XsdLoader.XSD_TILE_SHEETS), file);
            return true;
        }
        catch (final ValidationException exception)
        {
            return false;
        }
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
                if (TilesheetsFolderTester.PROPERTY_EDIT_TILESHEETS.equals(property))
                {
                    return TilesheetsFolderTester.isTilesheetsFile(selection.getFile());
                }
            }
        }
        return false;
    }
}
