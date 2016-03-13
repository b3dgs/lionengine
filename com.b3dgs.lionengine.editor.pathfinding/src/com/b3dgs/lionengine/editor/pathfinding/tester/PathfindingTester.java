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
package com.b3dgs.lionengine.editor.pathfinding.tester;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.pathfinding.PathfindingConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test if the folder contains pathfinding file.
 */
public final class PathfindingTester extends PropertyTester
{
    /** Can add pathfinding property. */
    private static final String PROPERTY_ADD_PATHFINDING = "addPathfinding";
    /** Can edit pathfinding property. */
    private static final String PROPERTY_EDIT_PATHFINDING = "editPathfinding";

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
        if (PROPERTY_EDIT_PATHFINDING.equals(property))
        {
            result = isPathfindingFile(selection);
        }
        else if (PROPERTY_ADD_PATHFINDING.equals(property))
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
     * Check if the media is a pathfinding descriptor.
     * 
     * @param media The media to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isPathfindingFile(Media media)
    {
        try
        {
            final XmlNode root = Xml.load(media);
            return PathfindingConfig.PATHFINDING.equals(root.getNodeName());
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Create tester.
     */
    public PathfindingTester()
    {
        super();
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
