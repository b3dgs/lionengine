/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.pathfinding.project;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindingConfig;

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
            return media.getFile().isFile() && PathfindingConfig.NODE_PATHFINDING.equals(new Xml(media).getNodeName());
        }
        catch (@SuppressWarnings("unused") final LionEngineException exception)
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

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final Project project = ProjectModel.INSTANCE.getProject();
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
