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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.configurable.Configurable;

/**
 * Define a structure used to create configurable objects.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurable
 */
public class SetupGame
{
    /** Configurable reference. */
    protected final Configurable configurable;
    /** Config file name. */
    protected final Media configFile;

    /**
     * Constructor.
     * 
     * @param config The config media.
     */
    public SetupGame(Media config)
    {
        configurable = new Configurable();
        configurable.load(config);
        configFile = config;
    }

    /**
     * Get the configurable reference.
     * 
     * @return The configurable reference.
     */
    public Configurable getConfigurable()
    {
        return configurable;
    }

    /**
     * Get the configuration file.
     * 
     * @return The configuration file.
     */
    public Media getConfigFile()
    {
        return configFile;
    }
}
