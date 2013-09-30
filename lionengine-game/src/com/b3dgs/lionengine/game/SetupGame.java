/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Define a structure used to create configurable objects.
 */
public class SetupGame
{
    /** Configurable reference. */
    public final Configurable configurable;
    /** Config file name. */
    public final Media configFile;

    /**
     * Create a new entity setup.
     * 
     * @param config The config media.
     */
    public SetupGame(Media config)
    {
        this(new ConfigurableModel(), config);
    }

    /**
     * Create a new entity setup.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     */
    public SetupGame(Configurable configurable, Media config)
    {
        Check.notNull(configurable, "The configurable must bot be null !");
        Media.exist(config);
        this.configurable = configurable;
        this.configurable.loadData(config);
        configFile = config;
    }
}
