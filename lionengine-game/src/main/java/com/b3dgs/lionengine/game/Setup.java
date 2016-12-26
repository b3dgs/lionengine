/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Define a structure used to create configurer objects.
 */
public class Setup extends Configurer
{
    /** Class error. */
    private static final String ERROR_CLASS = "Class not found for: ";

    /** Class reference. */
    private Class<?> clazz;

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media.
     */
    public Setup(Media config)
    {
        super(config);
    }

    /**
     * Get the class mapped to the setup. Lazy call (load class only first time, and keep its reference after).
     * 
     * @param classLoader The class loader used.
     * @return The class mapped to the setup.
     * @throws LionEngineException If the class was not found by the class loader.
     */
    public final Class<?> getConfigClass(ClassLoader classLoader)
    {
        if (clazz == null)
        {
            final FeaturableConfig config = FeaturableConfig.imports(this);
            try
            {
                clazz = classLoader.loadClass(config.getClassName());
            }
            catch (final ClassNotFoundException exception)
            {
                throw new LionEngineException(exception, Setup.ERROR_CLASS, getMedia().getPath());
            }
        }
        return clazz;
    }
}
