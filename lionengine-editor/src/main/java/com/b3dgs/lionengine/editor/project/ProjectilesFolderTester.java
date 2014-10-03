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

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.xsd.XsdLoader;

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
     * Check if the folder contains projectiles.
     * 
     * @param folder The folder to check.
     * @return <code>true</code> if contains projectiles, <code>false</code> else.
     */
    public static boolean isProjectilesFolder(File folder)
    {
        if (folder.isDirectory())
        {
            for (final File file : folder.listFiles())
            {
                if (ProjectilesFolderTester.isProjectileFile(file))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the file is an projectile descriptor.
     * 
     * @param file The file to test.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    public static boolean isProjectileFile(File file)
    {
        return FolderTypeTester.is(file, XsdLoader.XSD_PROJECTILE);
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
                    return ProjectilesFolderTester.isProjectilesFolder(selection.getFile())
                            && !FolderTypeTester.isFolderType(selection.getFile());
                }
                else if (ProjectilesFolderTester.PROPERTY_IS_PROJECTILE.equals(property))
                {
                    return ProjectilesFolderTester.isProjectileFile(file);
                }
            }
        }
        return false;
    }
}
