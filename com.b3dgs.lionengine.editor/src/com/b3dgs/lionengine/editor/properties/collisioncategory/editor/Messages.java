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
package com.b3dgs.lionengine.editor.properties.collisioncategory.editor;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".properties.collisioncategory.editor.messages";

    /** Collision category editor title. */
    public static String CollisionCategoryEditor_Title;
    /** Collision category axis. */
    public static String CollisionCategoryEditor_Axis;
    /** Collision category offset x. */
    public static String CollisionCategoryEditor_OffsetX;
    /** Collision category offset y. */
    public static String CollisionCategoryEditor_OffsetY;
    /** Collision category groups. */
    public static String CollisionCategoryEditor_Groups;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    /**
     * Private constructor.
     */
    private Messages()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
