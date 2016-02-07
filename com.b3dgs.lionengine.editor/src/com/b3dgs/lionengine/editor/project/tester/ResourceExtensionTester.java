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
package com.b3dgs.lionengine.editor.project.tester;

import java.util.Locale;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.project.Property;

/**
 * Test the resources extension.
 */
public final class ResourceExtensionTester extends PropertyTester
{
    /**
     * Create tester.
     */
    public ResourceExtensionTester()
    {
        // Nothing to do
    }

    /*
     * PropertyTester
     */

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final Media selection = ProjectModel.INSTANCE.getSelection();
        try
        {
            final Property type = Property.valueOf(property.toUpperCase(Locale.ENGLISH));
            return type.is(selection);
        }
        catch (final IllegalArgumentException exception)
        {
            Verbose.exception(exception);
            return false;
        }
    }
}
