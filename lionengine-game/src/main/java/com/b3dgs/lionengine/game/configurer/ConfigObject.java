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
package com.b3dgs.lionengine.game.configurer;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the object configuration data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 */
public final class ConfigObject
{
    /** Class node name. */
    public static final String CLASS = Configurer.PREFIX + "class";
    /** Setup node name. */
    public static final String SETUP = Configurer.PREFIX + "setup";

    /**
     * Create the object data from node.
     * 
     * @param configurer The configurer reference.
     * @return The object data.
     * @throws LionEngineException If unable to read node.
     */
    public static ConfigObject create(Configurer configurer) throws LionEngineException
    {
        return new ConfigObject(configurer.getText(CLASS), configurer.getText(SETUP));
    }

    /** Object class name. */
    private final String clazz;
    /** Setup class name. */
    private final String setup;

    /**
     * Disabled constructor.
     */
    private ConfigObject()
    {
        throw new RuntimeException();
    }

    /**
     * Create an object configuration.
     * 
     * @param clazz The object class name.
     * @param setup The setup class name.
     */
    private ConfigObject(String clazz, String setup)
    {
        this.clazz = clazz;
        this.setup = setup;
    }

    /**
     * Get the class name node value.
     * 
     * @return The class name node value.
     */
    public String getClassName()
    {
        return clazz;
    }

    /**
     * Get the setup class name node value.
     * 
     * @return The setup class name node value.
     */
    public String getSetupName()
    {
        return setup;
    }
}
