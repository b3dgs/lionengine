/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Check resource type for double click action and icon. Must provide the default constructor.
 */
public interface ResourceChecker
{
    /** Extension ID. */
    String EXTENSION_ID = Activator.PLUGIN_ID + ".resourceChecker";

    /**
     * Check the media type.
     * 
     * @param shell The shell parent.
     * @param media The media reference.
     * @return <code>true</code> if handled, <code>false</code> else.
     */
    boolean check(Shell shell, Media media);

    /**
     * Get the associated icon.
     * 
     * @param media The media reference.
     * @return The associated icon, <code>null</code> if none.
     */
    Image getIcon(Media media);
}
