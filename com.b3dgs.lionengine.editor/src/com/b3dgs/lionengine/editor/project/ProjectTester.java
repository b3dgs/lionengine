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
package com.b3dgs.lionengine.editor.project;

import org.eclipse.core.expressions.PropertyTester;

/**
 * Test if there is an active project.
 */
public final class ProjectTester extends PropertyTester
{
    /** Active project presence. */
    private static final String PROPERTY_ACTIVE = "active";

    /**
     * Create tester.
     */
    public ProjectTester()
    {
        super();
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (PROPERTY_ACTIVE.equals(property))
        {
            return ProjectModel.INSTANCE.getProject() != null;
        }
        return false;
    }
}
