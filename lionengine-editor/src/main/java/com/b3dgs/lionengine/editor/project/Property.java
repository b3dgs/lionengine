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

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
/**
 * Properties type list.
 * 
 * @author Pierre-Alexandre
 */
public enum Property
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
    LEVEL("lrm"),
    /** Map implementation property. */
    MAP_IMPL(MapTile.class),
    /** Factory implementation property. */
    FACTORY_IMPL(FactoryObjectGame.class),
    /** Class file. */
    CLASS(Property.EXTENSION_CLASS);

    /** Java class file extension. */
    public static final String EXTENSION_CLASS = "class";

    /** Extension list. */
    private final String[] extensions;
    /** Class parent. */
    private final Class<?> parent;

    /**
     * Constructor.
     * 
     * @param extensions The extensions list associated to the property.
     */
    private Property(String... extensions)
    {
        this(null, extensions);
    }

    /**
     * Constructor.
     * 
     * @param parent The excepted parent class.
     */
    private Property(Class<?> parent)
    {
        this(parent, "class");
    }

    /**
     * Constructor.
     * 
     * @param extensions The extensions list associated to the property.
     * @param parent The excepted parent class.
     */
    private Property(Class<?> parent, String... extensions)
    {
        this.parent = parent;
        this.extensions = extensions;
    }

    /**
     * Check if the file is this type.
     * 
     * @param file The file to test.
     * @return <code>true</code> if this is this type, <code>false</code> else.
     */
    public boolean is(Media file)
    {
        if (parent == null)
        {
            return isExtension(file);
        }
        return isExtension(file) && isParent(file);
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

    /**
     * Check if the file is this parent.
     * 
     * @param file The file to test.
     * @return <code>true</code> if this is parent, <code>false</code> else.
     */
    private boolean isParent(Media file)
    {
        final String name = file.getPath().replace("." + Property.EXTENSION_CLASS, "").replace(File.separator, ".");
        try
        {
            final Class<?> clazz = Project.getActive().getClass(name);
            return parent.isAssignableFrom(clazz);
        }
        catch (final NoClassDefFoundError exception)
        {
            return false;
        }
    }
}
