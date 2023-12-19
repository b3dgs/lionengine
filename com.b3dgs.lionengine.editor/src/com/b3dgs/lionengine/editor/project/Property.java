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

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilFile;

/**
 * Properties type list.
 */
public enum Property
{
    /** Music property. */
    MUSIC("midi", "mid", "mp3", "ogg", "sc68", "lds"),
    /** Sound property. */
    SOUND("wav"),
    /** Image property. */
    IMAGE("bmp", "png", "gif", "jpg", "jpeg"),
    /** Data property. */
    DATA("xml"),
    /** Level property. */
    LEVEL("lel"),
    /** Class file. */
    CLASS(Property.EXTENSION_CLASS);

    /** Java class file extension. */
    public static final String EXTENSION_CLASS = ".class";

    /** Extension list. */
    private final Collection<String> extensions;

    /**
     * Private constructor.
     * 
     * @param extensions The extensions list associated to the property.
     */
    Property(String... extensions)
    {
        this.extensions = new HashSet<>(extensions.length);
        for (final String extension : extensions)
        {
            this.extensions.add(extension.replace(Constant.DOT, Constant.EMPTY_STRING));
        }
    }

    /**
     * Check if the file is this type.
     * 
     * @param file The file to test.
     * @return <code>true</code> if this is this type, <code>false</code> else.
     */
    public boolean is(Media file)
    {
        return isExtension(file);
    }

    /**
     * Register an extension used for file detection.
     * 
     * @param extension The extension to add.
     */
    public void addExtension(String extension)
    {
        extensions.add(extension);
    }

    /**
     * Unregister an extension used for file detection.
     * 
     * @param extension The extension to remove.
     */
    public void removeExtension(String extension)
    {
        extensions.remove(extension);
    }

    /**
     * Check if the file is this type.
     * 
     * @param file The file to test.
     * @return <code>true</code> if this is this type, <code>false</code> else.
     */
    private boolean isExtension(Media file)
    {
        final String extension = UtilFile.getExtension(file.getFile());
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
