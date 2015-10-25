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

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.configurer.ConfigTileSheets;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test if the folder contains tile sheets.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SheetsTester extends PropertyTester
{
    /** Can add sheets property. */
    private static final String PROPERTY_ADD_SHEETS = "addSheets";
    /** Can edit sheets property. */
    private static final String PROPERTY_EDIT_SHEETS = "editSheets";

    /**
     * Check if the media is a sheets descriptor.
     * 
     * @param media The media to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isSheetsFile(Media media)
    {
        try
        {
            final XmlNode root = Stream.loadXml(media);
            return ConfigTileSheets.FILENAME.equals(root.getNodeName());
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
        if (PROPERTY_EDIT_SHEETS.equals(property))
        {
            result = isSheetsFile(selection);
        }
        else if (PROPERTY_ADD_SHEETS.equals(property))
        {
            result = selection.getFile().isDirectory();
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
    public SheetsTester()
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
