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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Define a structure used to create multiple entity, sharing the same data and {@link ImageBuffer}.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurable
 */
public class SetupSurfaceGame
        extends SetupGame
{
    /** Surface reference. */
    public final ImageBuffer surface;
    /** Surface file name. */
    public final Media surfaceFile;

    /**
     * Constructor.
     * 
     * @param config The config media.
     */
    public SetupSurfaceGame(Media config)
    {
        this(new ConfigurableModel(), config, false);
    }

    /**
     * Constructor.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     * @param alpha The alpha use flag.
     */
    public SetupSurfaceGame(Configurable configurable, Media config, boolean alpha)
    {
        super(configurable, config);
        final String conf = config.getPath();
        surfaceFile = Core.MEDIA.create(conf.substring(0, conf.lastIndexOf(Core.MEDIA.getSeparator()) + 1)
                + configurable.getDataString("surface"));
        surface = Core.GRAPHIC.getImageBuffer(surfaceFile, alpha);
    }
}
