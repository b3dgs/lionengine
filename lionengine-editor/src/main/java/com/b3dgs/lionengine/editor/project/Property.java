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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.core.Media;
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
    JAVA_MAP_IMPL(MapTile.class);

    /**
     * Get the class from its file.
     * 
     * @param clazz The class to cast to.
     * @param file The class file.
     * @return The class instance.
     * @throws LionEngineException If not able to create the class.
     */
    public static <C> C getClass(Class<C> clazz, Media file) throws LionEngineException
    {
        final String name = file.getPath().replace(".java", "").replace(java.io.File.separator, ".");
        try
        {
            final Class<?> clazzRef = Class.forName(name);
            return clazz.cast(clazzRef);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception);
        }
    }

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
        this(parent, "java");
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
        return isParent(file);
    }

    /**
     * Check if the file is this type.
     * 
     * @param file The file to test.
     * @return <code>true</code> if this is this type, <code>false</code> else.
     */
    private boolean isExtension(Media file)
    {
        final String extension = UtilityFile.getExtension(file.getPath());
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
        try
        {
            final String name = file.getPath().replace(".java", "").replace(java.io.File.separator, ".");
            final Class<?> clazz = Class.forName(name);
            return clazz.isAssignableFrom(parent);
        }
        catch (final ClassNotFoundException exception)
        {
            // TODO: Load into classpath the loaded project classes
            return false;
        }
    }
}
