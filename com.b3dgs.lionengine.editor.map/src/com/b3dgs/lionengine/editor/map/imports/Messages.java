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
package com.b3dgs.lionengine.editor.map.imports;

import java.util.Locale;

import org.eclipse.osgi.util.NLS;

/**
 * Messages internationalization.
 */
public final class Messages extends NLS
{
    /** Dialog title. */
    public static String Title;
    /** Dialog progress title. */
    public static String Title_Progress;
    /** Title header. */
    public static String HeaderTitle;
    /** Description header. */
    public static String HeaderDesc;
    /** Level rip location. */
    public static String LevelRipLocation;
    /** Sheets location. */
    public static String SheetsLocation;
    /** Groups location. */
    public static String GroupsLocation;
    /** Error level rip. */
    public static String ErrorLevelRip;
    /** Error sheets. */
    public static String ErrorSheets;
    /** Error groups. */
    public static String ErrorGroups;
    /** Import map progress. */
    public static String Progress;

    /**
     * Initialize.
     */
    static
    {
        NLS.initializeMessages(Messages.class.getName().toLowerCase(Locale.ENGLISH), Messages.class);
    }
}
