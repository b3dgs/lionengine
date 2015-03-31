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
package com.b3dgs.lionengine.editor.components.dialogs;

import org.eclipse.osgi.util.NLS;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Messages internationalization.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Messages
        extends NLS
{
    /** Bundle name. */
    private static final String BUNDLE_NAME = Activator.PLUGIN_ID + ".components.dialogs.messages"; //$NON-NLS-1$

    /** Map properties dialog title. */
    public static String MapPropertiesDialog_Title;
    /** Map properties title header. */
    public static String MapPropertiesDialog_HeaderTitle;
    /** Map properties description header. */
    public static String MapPropertiesDialog_HeaderDesc;
    /** Map properties tile width. */
    public static String MapPropertiesDialog_TileWidth;
    /** Map properties tile height. */
    public static String MapPropertiesDialog_TileHeight;
    /** Map properties add feature. */
    public static String MapPropertiesDialog_AddFeature;
    /** Map properties remove feature. */
    public static String MapPropertiesDialog_RemoveFeature;

    /** Map feature selection dialog title. */
    public static String MapFeatureSelectionDialog_Title;
    /** Map feature selection title header. */
    public static String MapFeatureSelectionDialog_HeaderTitle;
    /** Map feature selection description header. */
    public static String MapFeatureSelectionDialog_HeaderDesc;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
    }

    /**
     * Private constructor.
     */
    private Messages()
    {
        throw new RuntimeException();
    }
}
