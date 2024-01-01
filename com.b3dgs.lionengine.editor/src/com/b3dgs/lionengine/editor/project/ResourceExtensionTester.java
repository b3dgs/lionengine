/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Locale;

import org.eclipse.core.expressions.PropertyTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Media;

/**
 * Test the resources extension.
 */
public final class ResourceExtensionTester extends PropertyTester
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceExtensionTester.class);

    /**
     * Create tester.
     */
    public ResourceExtensionTester()
    {
        super();
    }

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
            LOGGER.error("test error", exception);
            return false;
        }
    }
}
