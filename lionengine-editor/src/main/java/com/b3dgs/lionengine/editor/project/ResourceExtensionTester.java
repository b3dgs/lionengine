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

import java.util.Locale;

import org.eclipse.core.expressions.PropertyTester;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Test the resources extension.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ResourceExtensionTester
        extends PropertyTester
{
    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        final Media selection = ProjectsModel.INSTANCE.getSelection();
        try
        {
            final Property type = Property.valueOf(property.toUpperCase(Locale.ENGLISH));
            return type.is(selection);
        }
        catch (final IllegalArgumentException exception)
        {
            Verbose.exception(ResourceExtensionTester.class, "test", exception);
            return false;
        }
    }
}
