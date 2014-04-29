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

import com.b3dgs.lionengine.UtilityFile;

/**
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
/**
 * Properties type list.
 * 
 * @author Pierre-Alexandre
 */
enum Property
{
    /** Music property. */
    MUSIC("midi", "mp3", "ogg", "sc68"),
    /** Sound property. */
    SOUND("wav"),
    /** Image property. */
    IMAGE("bmp", "png", "gif"),
    /** Data property. */
    DATA("xml"),
    /** Level property. */
    LEVEL("lrm");

    /** Extension list. */
    private final String[] extensions;

    /**
     * Constructor.
     * 
     * @param extensions The extensions list associated to the property.
     */
    private Property(String... extensions)
    {
        this.extensions = extensions;
    }

    /**
     * Check if the file is this type.
     * 
     * @param file The file to test.
     * @return <code>true</code> if this is this type, <code>false</code> else.
     */
    public boolean is(File file)
    {
        final String extension = UtilityFile.getExtension(file);
        for (final String value : extensions)
        {
            if (extension.equals(value))
            {
                return true;
            }
        }
        return false;
    }
}
