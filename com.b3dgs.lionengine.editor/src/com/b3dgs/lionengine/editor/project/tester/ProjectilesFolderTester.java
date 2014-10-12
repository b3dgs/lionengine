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
package com.b3dgs.lionengine.editor.project.tester;

import java.io.File;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.game.projectile.ProjectileGame;

/**
 * Test if the folder contains projectiles.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ProjectilesFolderTester
        extends PropertyTester
{
    /** Can add projectile property. */
    private static final String PROPERTY_ADD_PROJECTILE = "addProjectile";
    /** Is projectile property. */
    private static final String PROPERTY_IS_PROJECTILE = "isProjectile";

    /**
     * Check if the file is a projectile descriptor.
     * 
     * @param media The media to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isProjectileFile(Media media)
    {
        try
        {
            final Class<?> clazz = Tools.getClass(media);
            return ProjectileGame.class.isAssignableFrom(clazz);
        }
        catch (final LionEngineException exception)
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
                final File file = selection.getFile();
                if (ProjectilesFolderTester.PROPERTY_ADD_PROJECTILE.equals(property))
                {
                    return file.isDirectory() && !FolderTypeTester.isFolderType(file);
                }
                else if (ProjectilesFolderTester.PROPERTY_IS_PROJECTILE.equals(property))
                {
                    return ProjectilesFolderTester.isProjectileFile(selection);
                }
            }
        }
        return false;
    }
}
